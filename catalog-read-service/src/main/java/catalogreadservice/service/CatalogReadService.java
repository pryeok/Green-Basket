package catalogreadservice.service;

import catalogreadservice.client.adapter.CatalogInteractionServiceAdapter;
import catalogreadservice.client.adapter.CatalogServiceAdapter;
import catalogreadservice.client.response.CatalogPageResponse;
import catalogreadservice.client.response.CatalogResponse;
import catalogreadservice.model.CatalogQueryModel;
import catalogreadservice.repository.CatalogIdListRepository;
import catalogreadservice.repository.CatalogQueryModelRepository;
import catalogreadservice.repository.CategoryCatalogCountRepository;
import catalogreadservice.service.response.CatalogReadPageResponse;
import catalogreadservice.service.response.CatalogReadResponse;
import com.greenbasket.common.event.Event;
import com.greenbasket.common.event.EventPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.beans.EventHandler;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Catalog Read Service (CQRS Read Model)
 * - Redis 기반 Cache-Aside 패턴
 * - Cache Miss 시 원본 서비스(catalog-service, catalog-interaction-service)에서 데이터 조회
 * - Circuit Breaker와 Retry 패턴 적용 (Adapter 계층)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CatalogReadService {
    private final CatalogServiceAdapter catalogServiceAdapter;
    private final CatalogInteractionServiceAdapter interactionServiceAdapter;

    private final CatalogQueryModelRepository catalogQueryModelRepository;
    private final CatalogIdListRepository catalogIdListRepository;
    private final CategoryCatalogCountRepository categoryCatalogCountRepository;
    private final List<EventHandler> eventHandlers;

    private static final Duration DEFAULT_TTL = Duration.ofDays(1);

//    public void handleEvent(Event<EventPayload> event) {
//        for (EventHandler eventHandler : eventHandlers) {
//            if (eventHandler.supports(event)) {
//                eventHandler.handle(event);
//            }
//        }
//    }

    public CatalogReadResponse read(String productId) {
        CatalogQueryModel catalogQueryModel = catalogQueryModelRepository.read(productId)
                .or(() -> fetch(productId))
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다. productId: " + productId));

        // productId → catalogId 변환 후 ViewCount 조회
        return CatalogReadResponse.from(
                catalogQueryModel,
                getViewCount(catalogQueryModel.getId())
        );
    }

    /**
     * Cache Miss 시 원본 데이터 조회 및 Redis 저장 (productId 기반)
     * productId → CatalogResponse 조회 → catalogId 획득
     */
    private Optional<CatalogQueryModel> fetch(String productId) {
        log.info("[CatalogReadService.fetch] Cache Miss, fetching from catalog-service. productId={}", productId);

        // CatalogServiceAdapter로 단일 조회 (productId → CatalogResponse with catalogId)
        CatalogResponse catalog = catalogServiceAdapter.getCatalog(productId);

        // catalogId 획득 후 Interaction 서비스 호출
        Long catalogId = catalog.getId();
        CatalogQueryModel catalogQueryModel = CatalogQueryModel.create(
                catalog,
//                getCommentCount(catalogId),
                getLikeCount(catalogId),
                0L  // viewCount는 실시간 조회하므로 저장하지 않음
        );

        catalogQueryModelRepository.create(catalogQueryModel, DEFAULT_TTL);
        log.info("[CatalogReadService.fetch] Cache Miss → Fetched data. productId={}, catalogId={}", productId, catalogId);
        return Optional.of(catalogQueryModel);
    }

    public CatalogReadPageResponse readAll(Long categoryId, Long page, Long pageSize) {
        return CatalogReadPageResponse.of(
                readAll(
                        readAllCatalogIds(categoryId, page, pageSize)
                ),
                count(categoryId)
        );
    }

    private List<CatalogReadResponse> readAll(List<Long> catalogIds) {
        Map<Long, CatalogQueryModel> catalogQueryModelMap = catalogQueryModelRepository.readAll(catalogIds);

        return catalogIds.stream()
                .map(catalogId -> catalogQueryModelMap.containsKey(catalogId) ?
                        catalogQueryModelMap.get(catalogId) :
                        fetchByCatalogId(catalogId).orElse(null))
                .filter(Objects::nonNull)
                .map(catalogQueryModel ->
                        CatalogReadResponse.from(
                                catalogQueryModel,
                                getViewCount(catalogQueryModel.getId())
                        ))
                .toList();
    }

    /**
     * Cache Miss 시 catalogId 기반으로 데이터 조회 (목록 조회 전용)
     * TODO: catalog-service에 catalogId 기반 조회 API 추가 필요
     */
    private Optional<CatalogQueryModel> fetchByCatalogId(Long catalogId) {
        log.warn("[CatalogReadService.fetchByCatalogId] catalogId 기반 조회는 비효율적, catalogId={}", catalogId);

        // 임시: 전체 조회 후 필터링 (매우 비효율적)
        // TODO: catalog-service에 GET /catalogs/by-id/{catalogId} API 추가 권장
        return Optional.empty();
    }

    private List<Long> readAllCatalogIds(Long categoryId, Long page, Long pageSize) {
        List<Long> catalogIds = catalogIdListRepository.readAll(categoryId, (page - 1) * pageSize, pageSize);

        if (pageSize == catalogIds.size()) {
            log.info("[CatalogReadService.readAllCatalogIds] return redis data.");
            return catalogIds;
        }

        log.info("[CatalogReadService.readAllCatalogIds] return origin data.");
        CatalogPageResponse catalogPageResponse = catalogServiceAdapter.getCatalogsByCategory(categoryId, page, pageSize);

        // Redis에 저장 (다음 요청에서 Cache Hit)
        List<Long> originCatalogIds = catalogPageResponse.getCatalogs().stream()
                .map(CatalogResponse::getId)
                .toList();

        if (!originCatalogIds.isEmpty()) {
//            catalogIdListRepository.createOrUpdate(categoryId, originCatalogIds, DEFAULT_TTL);
        }

        return originCatalogIds;
    }

    private long count(Long categoryId) {
        Long cachedCount = categoryCatalogCountRepository.read(categoryId);
        if (cachedCount != null) {
            return cachedCount;
        }

        // 원본 데이터 조회
        CatalogPageResponse catalogPageResponse = catalogServiceAdapter.getCatalogsByCategory(categoryId, 1L, 1L);
        long count = catalogPageResponse.getCatalogCount();

        categoryCatalogCountRepository.createOrUpdate(categoryId, count);
        return count;
    }
//    /**
//     * 댓글 수 조회 (catalog-interaction-service)
//     * Circuit Breaker와 Retry 패턴 적용됨 (Adapter 계층)
//     */
//    private Long getCommentCount(Long catalogId) {
//        return interactionServiceAdapter.getCommentCount(catalogId);

//    }

    private Long getLikeCount(Long catalogId) {
        return interactionServiceAdapter.getLikeCount(catalogId);
    }

    private Long getViewCount(Long catalogId) {
        return interactionServiceAdapter.getViewCount(catalogId);
    }
}
