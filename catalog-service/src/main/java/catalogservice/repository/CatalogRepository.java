package catalogservice.repository;

import catalogservice.entity.Catalog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CatalogRepository extends JpaRepository<Catalog, Long> {
    List<Catalog> findByUserId(String userId);

    List<Catalog> findByProductIdIn(List<String> productIds);

    Optional<Catalog> findByProductId(String productId);

    @Query(
            value = "select catalog.id, catalog.product_id, catalog.product_name, catalog.stock, catalog.unit_price, " +
                    "catalog.user_id, catalog.category_id, catalog.created_at, catalog.modified_at, catalog.version " +
                    "from (" +
                    "   select id from catalog " +
                    "   where category_id = :categoryId " +
                    "   order by id desc " +
                    "   limit :limit offset :offset " +
                    ") t left join catalog on t.id = catalog.id ",
            nativeQuery = true
    )
    List<Catalog> findAll(
            @Param("categoryId") Long categoryId,
            @Param("offset") Long offset,
            @Param("limit") Long limit
    );

    @Query(
            value = "select count(*) from (" +
                    "   select id from catalog where category_id = :categoryId limit :limit" +
                    ") t",
            nativeQuery = true
    )
    Long countByCategoryId(@Param("categoryId") Long categoryId, @Param("limit") Long limit);
}
