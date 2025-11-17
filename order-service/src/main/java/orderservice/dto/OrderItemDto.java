package orderservice.dto;

import lombok.Data;

@Data
public class OrderItemDto {
    private String productId;
    private Integer qty;
}
