package catalogservice.entity;

import catalogservice.entity.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Table(name = "catalog")
@Getter
@Entity
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Catalog extends BaseEntity {

    @Id
    private Long id;

    @Version
    private Long version;

    @Column(nullable = false, length = 120, unique = true)
    private String productId;
    @Column(nullable = false)
    private String productName;
    @Column(nullable = false)
    private Integer stock;
    @Column(nullable = false)
    private Integer unitPrice;
    @Column(nullable = false)
    private String userId;
    @Column(nullable = false) // Shard Key
    private Long categoryId;

    public static Catalog create(Long id, String productId, String productName, Integer stock, Integer unitPrice, String userId, Long categoryId) {
        Catalog catalog = new Catalog();
        catalog.id = id;
        catalog.version = 0L;
        catalog.productId = productId;
        catalog.productName = productName;
        catalog.stock = stock;
        catalog.unitPrice = unitPrice;
        catalog.userId = userId;
        catalog.categoryId = categoryId;
        return catalog;
    }

    // 재고 차감
    public void decreaseStock(Integer quantity) {
        this.stock -= quantity;
    }
    // 재고 복구
    public void increaseStock(Integer quantity) {
        this.stock += quantity;
    }

}
