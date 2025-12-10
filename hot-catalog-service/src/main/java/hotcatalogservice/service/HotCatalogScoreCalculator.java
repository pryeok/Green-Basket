package hotcatalogservice.service;

import hotcatalogservice.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class HotCatalogScoreCalculator {
    private final CatalogLikeCountRepository catalogLikeCountRepository;
    private final CatalogViewCountRepository catalogViewCountRepository;
    private final OrderCountRepository orderCountRepository;

    private static final long ORDER_COUNT_WEIGHT = 5;
    private static final long CATALOG_LIKE_COUNT_WEIGHT = 3;
    private static final long CATALOG_VIEW_COUNT_WEIGHT = 1;

    public long calculate(Long catalogId) {
        Long orderCount = orderCountRepository.read(catalogId, LocalDateTime.now());
        Long likeCount = catalogLikeCountRepository.read(catalogId);
        Long viewCount = catalogViewCountRepository.read(catalogId);

        return (orderCount != null ? orderCount : 0) * ORDER_COUNT_WEIGHT
                + (likeCount != null ? likeCount : 0) * CATALOG_LIKE_COUNT_WEIGHT
                + (viewCount != null ? viewCount : 0) * CATALOG_VIEW_COUNT_WEIGHT;
    }
}
