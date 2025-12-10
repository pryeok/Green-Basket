package orderservice.exception;

import com.greenbasket.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class OrderDeletionFailedException extends BaseException {
    public OrderDeletionFailedException(String details) {
        super(HttpStatus.INTERNAL_SERVER_ERROR,
                "order-service.exception.order-deletion-failed",
                details);
    }
}
