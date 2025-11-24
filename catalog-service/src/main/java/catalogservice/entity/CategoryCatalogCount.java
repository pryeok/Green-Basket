package catalogservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Table(name = "category_catalog_count")
@Entity
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CategoryCatalogCount {
    @Id
    private Long categoryId; // shard key
    private Long catalogCount;

    public static CategoryCatalogCount init(Long categoryId, Long catalogCount) {
        CategoryCatalogCount categoryCatalogCount = new CategoryCatalogCount();
        categoryCatalogCount.categoryId = categoryId;
        categoryCatalogCount.catalogCount = catalogCount;
        return categoryCatalogCount;
    }

    public void increase() {
        this.catalogCount++;
    }

    public void decrease() {
        if (this.catalogCount > 0) {
            this.catalogCount--;
        }
    }
}
