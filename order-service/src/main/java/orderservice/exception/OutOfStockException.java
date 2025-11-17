package orderservice.exception;

import com.greenbasket.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class OutOfStockException extends BaseException {
    public OutOfStockException(String details) {
        super(HttpStatus.CONFLICT,
                "order-service.exception.out-of-stock",
                details);
    }
}
