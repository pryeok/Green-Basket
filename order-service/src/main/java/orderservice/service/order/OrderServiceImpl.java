package orderservice.service.order;

import com.greenbasket.common.idgenerator.IdGenerator;
import com.greenbasket.common.snowflake.Snowflake;
import lombok.RequiredArgsConstructor;
import orderservice.dto.OrderDto;
import orderservice.entity.Order;
import orderservice.entity.OrderItem;
import orderservice.repository.OrderRepository;
import orderservice.service.order.response.OrderResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final Snowflake snowflake;
    private final OrderRepository orderRepository;

    @Override
    @Transactional
    public OrderResponse createOrder(OrderDto orderDto) {
        Order order = Order.create(
                snowflake.nextId(),
                IdGenerator.generateOrderId(),
                orderDto.getUserId()
        );

        orderDto.getOrderItems().forEach(itemDto -> {
            OrderItem orderItem = OrderItem.create(
                    snowflake.nextId(),
                    order,
                    itemDto.getProductId(),
                    itemDto.getProductName(),
                    itemDto.getQty(),
                    itemDto.getUnitPrice()
            );
            order.addOrderItem(orderItem);
        });

        Order savedOrder = orderRepository.save(order);

        return OrderResponse.from(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByUserId(String userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        return orders.stream()
                .map(OrderResponse::from)
                .toList();
    }

}
