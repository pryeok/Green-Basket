package orderservice.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import orderservice.entity.base.BaseEntity;

import java.util.ArrayList;
import java.util.List;

@Table(name = "orders")
@Getter
@Entity
@ToString(exclude = "orderItems")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseEntity {

    @Id
    private Long id;

    @Column(nullable = false, unique = true)
    private String orderId;
    @Column(nullable = false)
    private String userId;
    @Column(nullable = false)
    private Integer totalPrice;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    public static Order create(Long id, String orderId, String userId) {
        Order order = new Order();
        order.id = id;
        order.orderId = orderId;
        order.userId = userId;
        order.totalPrice = 0;
        return order;
    }

    public void addOrderItem(OrderItem orderItem) {
        this.orderItems.add(orderItem);
        this.totalPrice += orderItem.getTotalPrice();
    }

}
