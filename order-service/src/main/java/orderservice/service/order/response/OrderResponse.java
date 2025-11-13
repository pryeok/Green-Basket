package orderservice.service.order.response;

import lombok.Getter;
import orderservice.entity.Order;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class OrderResponse {
    private Long id;
    private String orderId;
    private String userId;
    private Integer totalPrice;
    private List<OrderItemResponse> orderItems;

    public static OrderResponse from(Order order) {
        OrderResponse response = new OrderResponse();
        response.id = order.getId();
        response.orderId = order.getOrderId();
        response.userId = order.getUserId();
        response.totalPrice = order.getTotalPrice();
        response.orderItems = order.getOrderItems().stream()
                .map(OrderItemResponse::from)
                .collect(Collectors.toList());
        return response;
    }
}
