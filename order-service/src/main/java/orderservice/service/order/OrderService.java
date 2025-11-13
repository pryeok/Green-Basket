package orderservice.service.order;

import orderservice.dto.OrderDto;
import orderservice.service.order.response.OrderResponse;

import java.util.List;

public interface OrderService {

    OrderResponse createOrder(OrderDto orderDto);

    List<OrderResponse> getOrdersByUserId(String userId);
}
