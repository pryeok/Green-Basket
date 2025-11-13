package catalogservice.service.catalog.request;

import lombok.Data;

@Data
public class CatalogCreateRequest {
    private String productName;
    private Integer stock;
    private Integer unitPrice;
}
