package orderservice.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import orderservice.entity.base.BaseEntity;

@Table(name = "order_items")
@Getter
@Entity
@ToString(exclude = "order")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem extends BaseEntity {

    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(nullable = false, length = 120)
    private String productId;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private Integer qty;

    @Column(nullable = false)
    private Integer unitPrice;

    @Column(nullable = false)
    private Integer totalPrice;

    public static OrderItem create(Long id, Order order, String productId, String productName, Integer qty, Integer unitPrice) {
        OrderItem orderItem = new OrderItem();
        orderItem.id = id;
        orderItem.order = order;
        orderItem.productId = productId;
        orderItem.productName = productName;
        orderItem.qty = qty;
        orderItem.unitPrice = unitPrice;
        orderItem.totalPrice = qty * unitPrice;
        return orderItem;
    }

    @Override
    public Long getId() {
        return id;
    }
}
