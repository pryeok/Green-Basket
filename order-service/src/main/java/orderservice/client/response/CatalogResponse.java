package orderservice.client.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class CatalogResponse {
    private Long id;
    private String productId;
    private String productName;
    private Integer unitPrice;
    private Integer stock;
}
