package catalogservice.exception;

import com.greenbasket.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class InvalidProductIdException extends BaseException {
    public InvalidProductIdException() {
        super(HttpStatus.BAD_REQUEST,
                "catalog-service.exception.invalid-product-id");
    }
}
