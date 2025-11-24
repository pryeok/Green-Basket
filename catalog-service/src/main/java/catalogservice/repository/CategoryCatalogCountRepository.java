package catalogservice.repository;

import catalogservice.entity.CategoryCatalogCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CategoryCatalogCountRepository extends JpaRepository<CategoryCatalogCount, Long> {
    @Query(
            value = "UPDATE category_catalog_count SET catalog_count = catalog_count + 1 WHERE category_id = :categoryId",
            nativeQuery = true
    )
    @Modifying
    int increase(@Param("categoryId") Long categoryId);

    @Query(
            value = "UPDATE category_catalog_count SET catalog_count = catalog_count - 1 WHERE category_id = :categoryId",
            nativeQuery = true
    )
    @Modifying
    int decrease(@Param("categoryId") Long categoryId);
}
