package orderservice.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.domain.Persistable;
import orderservice.entity.base.BaseEntity;

import java.util.ArrayList;
import java.util.List;

@Table(name = "orders")
@Getter
@Entity
@ToString(exclude = "orderItems")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseEntity implements Persistable<Long> {

    @Id
    private Long id;

    @Transient
    private boolean isNew = true;

    @Override
    public boolean isNew() {
        return isNew;
    }

    @PostPersist
    public void markNotNew() {
        this.isNew = false;
    }

    @Column(nullable = false, unique = true)
    private String orderId;
    @Column(nullable = false)
    private String userId;
    @Column(nullable = false)
    private Integer totalPrice;
    @Column(nullable = false) // Shard Key
    private Long userPk;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    public static Order create(Long id, String orderId, String userId, Long userPk) {
        Order order = new Order();
        order.id = id;
        order.orderId = orderId;
        order.userId = userId;
        order.userPk = userPk;
        order.totalPrice = 0;
        return order;
    }

    public void addOrderItem(OrderItem orderItem) {
        this.orderItems.add(orderItem);
        this.totalPrice += orderItem.getTotalPrice();
    }

    @Override
    public Long getId() {
        return id;
    }

}
