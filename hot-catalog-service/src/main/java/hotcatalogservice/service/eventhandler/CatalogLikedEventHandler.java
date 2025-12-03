package hotcatalogservice.service.eventhandler;

import com.greenbasket.common.event.Event;
import com.greenbasket.common.event.EventType;
import com.greenbasket.common.event.payload.CatalogLikedEventPayload;
import hotcatalogservice.repository.CatalogCreatedTimeRepository;
import hotcatalogservice.repository.CatalogLikeCountRepository;
import hotcatalogservice.repository.HotCatalogListRepository;
import hotcatalogservice.util.TimeCalculatorUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CatalogLikedEventHandler implements EventHandler<CatalogLikedEventPayload> {
    private final CatalogLikeCountRepository catalogLikeCountRepository;

    @Override
    public void handle(Event<CatalogLikedEventPayload> event) {
        CatalogLikedEventPayload payload = event.getPayload();
        catalogLikeCountRepository.createOrUpdate(
                payload.getCatalogId(),
                payload.getLikeCount(),
                TimeCalculatorUtils.calculateDurationToEndOfWeek()
        );
    }

    @Override
    public boolean supports(Event<CatalogLikedEventPayload> event) {
        return EventType.CATALOG_LIKED == event.getType();
    }

    @Override
    public Long findCatalogId(Event<CatalogLikedEventPayload> event) {
        return event.getPayload().getCatalogId();
    }
}
