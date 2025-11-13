package orderservice.service.order.request;

import lombok.Data;
import orderservice.dto.OrderItemDto;

import java.util.List;

@Data
public class OrderCreateRequest {
    private List<OrderItemDto> orderItems;
}
