package orderservice.service.order.response;

import lombok.Getter;
import orderservice.entity.OrderItem;

@Getter
public class OrderItemResponse {
    private Long id;
    private String productId;
    private String productName;
    private Integer qty;
    private Integer unitPrice;
    private Integer totalPrice;

    public static OrderItemResponse from(OrderItem orderItem) {
        OrderItemResponse response = new OrderItemResponse();
        response.id = orderItem.getId();
        response.productId = orderItem.getProductId();
        response.productName = orderItem.getProductName();
        response.qty = orderItem.getQty();
        response.unitPrice = orderItem.getUnitPrice();
        response.totalPrice = orderItem.getTotalPrice();
        return response;
    }
}
