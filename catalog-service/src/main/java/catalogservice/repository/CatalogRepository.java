package catalogservice.repository;

import catalogservice.entity.Catalog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CatalogRepository extends JpaRepository<Catalog, Long> {
    List<Catalog> findByUserId(String userId);

    List<Catalog> findByProductIdIn(List<String> productIds);

    Optional<Catalog> findByProductId(String productId);
}
