package catalogservice.service.catalog;

import catalogservice.dto.CatalogDto;
import catalogservice.entity.Catalog;
import catalogservice.entity.CategoryCatalogCount;
import catalogservice.repository.CatalogRepository;
import catalogservice.repository.CategoryCatalogCountRepository;
import catalogservice.service.catalog.response.CatalogResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.greenbasket.common.snowflake.Snowflake;
import static com.greenbasket.common.idgenerator.IdGenerator.generateProductId;
import catalogservice.exception.InvalidProductIdException;
import catalogservice.exception.OutOfStockException;
import catalogservice.exception.ProductNotFoundException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class CatalogServiceImpl implements CatalogService {
    private final Snowflake snowflake;
    private final CatalogRepository catalogRepository;
    private final CategoryCatalogCountRepository categoryCatalogCountRepository;

    @Override
    @Transactional
    public CatalogResponse createCatalog(CatalogDto catalogDto) {
        Catalog catalog = Catalog.create(
                snowflake.nextId(),
                generateProductId(),
                catalogDto.getProductName(),
                catalogDto.getStock(),
                catalogDto.getUnitPrice(),
                catalogDto.getUserId(),
                catalogDto.getCategoryId()
        );
        Catalog savedCatalog = catalogRepository.save(catalog);
        int updated = categoryCatalogCountRepository.increase(catalogDto.getCategoryId());
        if (updated == 0) {
            categoryCatalogCountRepository.save(CategoryCatalogCount.init(catalogDto.getCategoryId(), 1L));
        }

        return CatalogResponse.from(savedCatalog);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CatalogResponse> getCatalogsByUserId(String userId) {
        List<Catalog> catalogs = catalogRepository.findByUserId(userId);
        return catalogs.stream()
                .map(CatalogResponse::from)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CatalogResponse> getAllCatalogs() {
        return catalogRepository.findAll().stream()
                .map(CatalogResponse::from)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CatalogResponse> getCatalogsByProductIds(List<String> productIds) {
        if (productIds == null || productIds.isEmpty() ||
                productIds.stream().anyMatch(id -> id == null || id.isBlank())) {
            throw new InvalidProductIdException();
        }

        List<Catalog> catalogs = catalogRepository.findByProductIdIn(productIds);

        if (catalogs.size() != productIds.size()) {
            Set<String> foundIds = catalogs.stream()
                    .map(Catalog::getProductId)
                    .collect(Collectors.toSet());

            List<String> notFoundIds = productIds.stream()
                    .filter(id -> !foundIds.contains(id))
                    .toList();

            throw new ProductNotFoundException(notFoundIds);
        }

        return catalogs.stream()
                .map(CatalogResponse::from)
                .toList();
    }

    @Override
    @Transactional
    public void decreaseStock(String productId, Integer quantity) {
        Catalog catalog = catalogRepository.findByProductId(productId)
                .orElseThrow(() -> new ProductNotFoundException(List.of(productId)));

        if (catalog.getStock() < quantity) {
            throw new OutOfStockException(
                    catalog.getProductId(),
                    quantity,
                    catalog.getStock());
        }
        catalog.decreaseStock(quantity);
    }

    @Override
    @Transactional
    public void increaseStock(String productId, Integer quantity) {
        Catalog catalog = catalogRepository.findByProductId(productId)
                .orElseThrow(() -> new ProductNotFoundException(List.of(productId)));

        catalog.increaseStock(quantity);
    }
}
