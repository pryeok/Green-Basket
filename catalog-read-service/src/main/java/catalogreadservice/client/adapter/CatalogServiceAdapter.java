package catalogreadservice.client.adapter;

import catalogreadservice.client.CatalogClient;
import catalogreadservice.client.fallback.CatalogServiceFallbackHandler;
import catalogreadservice.client.response.CatalogPageResponse;
import catalogreadservice.client.response.CatalogResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Catalog Service 호출을 Circuit Breaker로 보호하는 Adapter
 * Self-invocation 문제를 해결하기 위해 별도 컴포넌트로 분리
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CatalogServiceAdapter {
    private final CatalogClient catalogClient;
    private final CatalogServiceFallbackHandler fallbackHandler;

    /**
     * Circuit Breaker + Retry로 보호된 단일 상품 조회 (productId)
     */
    @CircuitBreaker(name = "catalog-read", fallbackMethod = "getCatalogFallback")
    @Retry(name = "catalog-read")
    public CatalogResponse getCatalog(String productId) {
        log.debug("단일 상품 조회 시도: productId={}", productId);
        return catalogClient.getCatalog(productId);
    }

    public CatalogResponse getCatalogFallback(String productId, Exception e) {
        return fallbackHandler.getCatalogFallback(productId, e);
    }

    /**
     * Circuit Breaker + Retry로 보호된 전체 상품 목록 조회
     */
    @CircuitBreaker(name = "catalog-read", fallbackMethod = "getAllCatalogsFallback")
    @Retry(name = "catalog-read")
    public List<CatalogResponse> getAllCatalogs() {
        log.debug("전체 상품 조회 시도");
        return catalogClient.getAllCatalogs();
    }

    public List<CatalogResponse> getAllCatalogsFallback(Exception e) {
        return fallbackHandler.getAllCatalogsFallback(e);
    }

    /**
     * Circuit Breaker + Retry로 보호된 카테고리별 페이징 조회
     */
    @CircuitBreaker(name = "catalog-read", fallbackMethod = "getCatalogsByCategoryFallback")
    @Retry(name = "catalog-read")
    public CatalogPageResponse getCatalogsByCategory(Long categoryId, Long page, Long pageSize) {
        log.debug("카테고리별 상품 조회 시도: categoryId={}, page={}, pageSize={}", categoryId, page, pageSize);
        return catalogClient.getCatalogsByCategory(categoryId, page, pageSize);
    }

    public CatalogPageResponse getCatalogsByCategoryFallback(Long categoryId, Long page, Long pageSize, Exception e) {
        return fallbackHandler.getCatalogsByCategoryFallback(categoryId, page, pageSize, e);
    }

    /**
     * Circuit Breaker + Retry로 보호된 배치 조회
     */
    @CircuitBreaker(name = "catalog-read", fallbackMethod = "getCatalogsByProductIdsFallback")
    @Retry(name = "catalog-read")
    public List<CatalogResponse> getCatalogsByProductIds(List<String> productIds) {
        log.debug("배치 상품 조회 시도: productIds={}", productIds);
        return catalogClient.getCatalogsByProductIds(productIds);
    }

    public List<CatalogResponse> getCatalogsByProductIdsFallback(List<String> productIds, Exception e) {
        return fallbackHandler.getCatalogsByProductIdsFallback(productIds, e);
    }
}
