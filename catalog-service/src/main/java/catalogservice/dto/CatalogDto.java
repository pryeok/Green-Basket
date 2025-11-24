package catalogservice.dto;

import lombok.Data;

@Data
public class CatalogDto {
    private String productName;
    private Integer stock;
    private Integer unitPrice;
    private String userId;
    private Long categoryId;
}
