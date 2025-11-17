package catalogservice.exception;

import com.greenbasket.common.exception.BaseException;
import org.springframework.http.HttpStatus;

import java.util.List;

public class ProductNotFoundException extends BaseException {
    public ProductNotFoundException(List<String> notFoundIds) {
        super(HttpStatus.NOT_FOUND,
                "catalog-service.exception.products-not-found",
                notFoundIds);
    }
}
