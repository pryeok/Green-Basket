package orderservice.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderDto {
    private String userId;
    private Long userPk;
    private List<OrderItemDto> orderItems;
}
