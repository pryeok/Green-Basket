package orderservice.service.order;

import orderservice.dto.OrderDto;
import orderservice.service.order.response.OrderResponse;

import java.util.List;

public interface OrderService {

    OrderResponse createOrder(OrderDto orderDto);

    void delete(String orderId);

    List<OrderResponse> getOrdersByUserId(String userId);
}
