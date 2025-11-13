package orderservice.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderDto {
    private String userId;
    private List<OrderItemDto> orderItems;
}
