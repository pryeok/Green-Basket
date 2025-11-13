package catalogservice.service.catalog.response;

import catalogservice.entity.Catalog;
import lombok.Getter;


@Getter
public class CatalogResponse {
    private Long id;
    private String productId;
    private String productName;
    private Integer stock;
    private Integer unitPrice;
    private String userId;

    public static CatalogResponse from(Catalog catalog) {
        CatalogResponse response = new CatalogResponse();
        response.id = catalog.getId();
        response.productId = catalog.getProductId();
        response.productName = catalog.getProductName();
        response.stock = catalog.getStock();
        response.unitPrice = catalog.getUnitPrice();
        response.userId = catalog.getUserId();
        return response;
    }
}
