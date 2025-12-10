package catalogreadservice.client.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Catalog Service로부터 받는 상품 정보 응답
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CatalogResponse {
    private Long id;
    private String productId;
    private String productName;
    private Integer stock;
    private Integer unitPrice;
    private String userId;
    private Long categoryId;
}
