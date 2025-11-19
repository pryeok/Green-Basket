package orderservice.client;

import lombok.extern.slf4j.Slf4j;
import orderservice.client.response.CatalogResponse;
import orderservice.exception.OrderCreationFailedException;
import orderservice.exception.OutOfStockException;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Catalog Service 호출 실패 시 Fallback 처리
 */
@Slf4j
@Component
public class CatalogServiceFallbackHandler {

    /**
     * 상품 조회 실패 Fallback
     */
    public List<CatalogResponse> getCatalogProductsFallback(List<String> productIds, Exception e) {
        log.warn("[FALLBACK] 상품 조회 실패 - productIds: {}, 원인: {}",
                 productIds, e.getMessage());

        throw new OrderCreationFailedException(
            "일시적으로 주문을 처리할 수 없습니다. 상품 정보를 불러올 수 없습니다."
        );
    }

    /**
     * 재고 차감 실패 Fallback
     */
    public void decreaseCatalogStockFallback(String productId, Integer quantity, Exception e) {
        // OutOfStockException은 비즈니스 예외이므로 그대로 전파
        if (e instanceof OutOfStockException) {
            throw (OutOfStockException) e;
        }
        if (e.getCause() instanceof OutOfStockException) {
            throw (OutOfStockException) e.getCause();
        }

        // 인프라 장애는 일시적 오류로 처리
        log.warn("[FALLBACK] 재고 차감 실패 - productId: {}, quantity: {}, 원인: {}",
                 productId, quantity, e.getMessage());

        throw new OrderCreationFailedException(
            "일시적으로 주문을 처리할 수 없습니다. 잠시 후 다시 시도해주세요."
        );
    }
}
