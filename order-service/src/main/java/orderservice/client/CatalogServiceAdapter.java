package orderservice.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import orderservice.client.response.CatalogResponse;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Catalog Service 호출을 Circuit Breaker로 보호하는 어댑터
 * Self-invocation 문제를 해결하기 위해 별도 컴포넌트로 분리
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CatalogServiceAdapter {
    private final CatalogServiceClient catalogClient;
    private final CatalogServiceFallbackHandler fallbackHandler;

    /**
     * Circuit Breaker + Retry로 보호된 상품 조회
     */
    @CircuitBreaker(name = "catalog-order", fallbackMethod = "getCatalogProductsFallback")
    @Retry(name = "catalog-order")
    public List<CatalogResponse> getCatalogProducts(List<String> productIds) {
        log.debug("상품 조회 시도: productIds={}", productIds);
        return catalogClient.getProducts(productIds);
    }

    /**
     * Fallback 위임 to CatalogServiceFallbackHandler
     */
    public List<CatalogResponse> getCatalogProductsFallback(List<String> productIds, Exception e) {
        return fallbackHandler.getCatalogProductsFallback(productIds, e);
    }

    /**
     * Circuit Breaker + Retry로 보호된 재고 차감
     */
    @CircuitBreaker(name = "catalog-order", fallbackMethod = "decreaseCatalogStockFallback")
    @Retry(name = "catalog-order")
    public void decreaseCatalogStock(String productId, Integer quantity) {
        log.debug("재고 차감 시도: productId={}, quantity={}", productId, quantity);
        catalogClient.decreaseStock(productId, quantity);
    }

    /**
     * Fallback 위임 to CatalogServiceFallbackHandler
     */
    public void decreaseCatalogStockFallback(String productId, Integer quantity, Exception e) {
        fallbackHandler.decreaseCatalogStockFallback(productId, quantity, e);
    }
}
