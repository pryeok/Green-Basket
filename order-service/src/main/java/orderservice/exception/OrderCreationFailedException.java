package orderservice.exception;

import com.greenbasket.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class OrderCreationFailedException extends BaseException {
    public OrderCreationFailedException(String details) {
        super(HttpStatus.BAD_REQUEST,
                "order-service.exception.order-creation-decrease-failed",
                details);
    }
}
