package catalogservice.exception;

import com.greenbasket.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class OutOfStockException extends BaseException {
    public OutOfStockException(String productId, Integer requestedQty, Integer availableStock) {
        super(HttpStatus.CONFLICT,
                "catalog-service.exception.out-of-stock",
                String.format("Product: %s, Requested: %d, Available: %d",
                             productId, requestedQty, availableStock));
    }
}
