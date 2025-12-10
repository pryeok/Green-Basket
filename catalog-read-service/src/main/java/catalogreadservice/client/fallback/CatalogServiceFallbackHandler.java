package catalogreadservice.client.fallback;

import catalogreadservice.client.response.CatalogPageResponse;
import catalogreadservice.client.response.CatalogResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Catalog Service 호출 실패 시 Fallback 처리
 */
@Slf4j
@Component
public class CatalogServiceFallbackHandler {

    /**
     * 단일 상품 조회 실패 Fallback
     */
    public CatalogResponse getCatalogFallback(String productId, Exception e) {
        log.error("[FALLBACK] 단일 상품 조회 실패 - productId: {}, 원인: {}", productId, e.getMessage());

        // 단일 조회 실패는 예외 전파 (필수 데이터)
        throw new IllegalArgumentException("상품 조회 실패: catalog-service 일시적 장애", e);
    }

    /**
     * 전체 상품 조회 실패 Fallback
     */
    public List<CatalogResponse> getAllCatalogsFallback(Exception e) {
        log.warn("[FALLBACK] 전체 상품 조회 실패 - 원인: {}", e.getMessage());

        // Read 작업은 빈 목록 반환으로 처리
        return List.of();
    }

    /**
     * 카테고리별 상품 조회 실패 Fallback
     */
    public CatalogPageResponse getCatalogsByCategoryFallback(Long categoryId, Long page, Long pageSize, Exception e) {
        log.warn("[FALLBACK] 카테고리별 상품 조회 실패 - categoryId: {}, page: {}, pageSize: {}, 원인: {}",
                 categoryId, page, pageSize, e.getMessage());

        // Read 작업은 빈 응답 반환
        return CatalogPageResponse.EMPTY;
    }

    /**
     * 배치 상품 조회 실패 Fallback
     */
    public List<CatalogResponse> getCatalogsByProductIdsFallback(List<String> productIds, Exception e) {
        log.warn("[FALLBACK] 배치 상품 조회 실패 - productIds: {}, 원인: {}",
                 productIds, e.getMessage());

        // Read 작업은 빈 목록 반환
        return List.of();
    }
}
