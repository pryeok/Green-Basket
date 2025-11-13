package orderservice.dto;

import lombok.Data;

@Data
public class OrderItemDto {
    private String productId;
    private String productName;
    private Integer qty;
    private Integer unitPrice;
}
