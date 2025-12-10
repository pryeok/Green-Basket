package hotcatalogservice.service;

import com.greenbasket.common.event.Event;
import com.greenbasket.common.event.EventPayload;
import hotcatalogservice.repository.CatalogCreatedTimeRepository;
import hotcatalogservice.repository.HotCatalogListRepository;
import hotcatalogservice.repository.NewHotCatalogListRepository;
import hotcatalogservice.service.event.handler.EventHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;

@Component
@RequiredArgsConstructor
public class HotCatalogScoreUpdater {
    private final HotCatalogListRepository hotCatalogListRepository;
    private final NewHotCatalogListRepository newHotCatalogListRepository;
    private final HotCatalogScoreCalculator hotCatalogScoreCalculator;
    private final CatalogCreatedTimeRepository catalogCreatedTimeRepository;

    private static final long HOT_CATALOG_COUNT = 10;
    private static final Duration HOT_CATALOG_TTL = Duration.ofDays(14);

    public void update(Event<EventPayload> event, EventHandler<EventPayload> eventHandler) {

        Long catalogId = eventHandler.findCatalogId(event);
        LocalDateTime createdTime = catalogCreatedTimeRepository.read(catalogId);

        eventHandler.handle(event);

        long score = hotCatalogScoreCalculator.calculate(catalogId);
        LocalDateTime now = LocalDateTime.now();

        // 1. Hot Catalog (전체 상품 중 한 주 베스트)
        hotCatalogListRepository.add(
                catalogId,
                now,
                score,
                HOT_CATALOG_COUNT,
                HOT_CATALOG_TTL
        );

        // 2. New & Hot Catalog (신상품 중 한 주 베스트)
        if (isCatalogCreatedThisWeek(createdTime)) {
            newHotCatalogListRepository.add(
                    catalogId,
                    now,
                    score,
                    HOT_CATALOG_COUNT,
                    HOT_CATALOG_TTL
            );
        }
    }

    private boolean isCatalogCreatedThisWeek(LocalDateTime createdTime) {
        if (createdTime == null) {
            return false;
        }

        LocalDate now = LocalDate.now();
        LocalDate catalogDate = createdTime.toLocalDate();

        // 현재와 카탈로그 생성일이 같은 주(월요일 시작)에 속하는지 확인
        LocalDate currentWeekStart = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate catalogWeekStart = catalogDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        return currentWeekStart.equals(catalogWeekStart);
    }
}
