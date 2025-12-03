package catalogservice.service.catalog;

import catalogservice.dto.CatalogDto;
import catalogservice.entity.Catalog;
import catalogservice.entity.CategoryCatalogCount;
import catalogservice.repository.CatalogRepository;
import catalogservice.repository.CategoryCatalogCountRepository;
import catalogservice.service.catalog.response.CatalogPageResponse;
import catalogservice.service.catalog.response.CatalogResponse;
import com.greenbasket.common.event.EventType;
import com.greenbasket.common.event.payload.CatalogCreatedEventPayload;
import com.greenbasket.common.event.payload.CatalogDeletedEventPayload;
import com.greenbasket.common.outboxmessagerelay.OutboxEventPublisher;
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
    private final OutboxEventPublisher outboxEventPublisher;
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

        outboxEventPublisher.publish(
                EventType.CATALOG_CREATED,
                CatalogCreatedEventPayload.builder()
                        .id(savedCatalog.getId())
                        .productId(savedCatalog.getProductId())
                        .productName(savedCatalog.getProductName())
                        .stock(savedCatalog.getStock())
                        .unitPrice(savedCatalog.getUnitPrice())
                        .userId(savedCatalog.getUserId())
                        .categoryId(savedCatalog.getCategoryId())
                        .createdAt(savedCatalog.getCreatedAt())
                        .updatedAt(savedCatalog.getUpdatedAt())
                        .build(),
                savedCatalog.getCategoryId()
        );

        return CatalogResponse.from(savedCatalog);
    }

    @Override
    @Transactional
    public CatalogResponse updateCatalog(String productId, CatalogDto catalogDto) {
        Catalog catalog = catalogRepository.findByProductId(productId)
                .orElseThrow(() -> new ProductNotFoundException(List.of(productId)));

        catalog.update(
                catalogDto.getProductName(),
                catalogDto.getStock(),
                catalogDto.getUnitPrice()
        );

        return CatalogResponse.from(catalog);
    }

    @Override
    @Transactional
    public void deleteCatalog(String productId) {
        Catalog catalog = catalogRepository.findByProductId(productId)
                .orElseThrow(() -> new ProductNotFoundException(List.of(productId)));

        outboxEventPublisher.publish(
                EventType.CATALOG_DELETED,
                CatalogDeletedEventPayload.builder()
                        .id(catalog.getId())
                        .productId(catalog.getProductId())
                        .productName(catalog.getProductName())
                        .stock(catalog.getStock())
                        .unitPrice(catalog.getUnitPrice())
                        .userId(catalog.getUserId())
                        .categoryId(catalog.getCategoryId())
                        .createdAt(catalog.getCreatedAt())
                        .updatedAt(catalog.getUpdatedAt())
                        .build(),
                catalog.getCategoryId()  // shardKey
        );

        categoryCatalogCountRepository.decrease(catalog.getCategoryId());

        catalogRepository.delete(catalog);
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
    public List<CatalogResponse> getCatalogsByUserId(String userId) {
        List<Catalog> catalogs = catalogRepository.findByUserId(userId);
        return catalogs.stream()
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
    @Transactional(readOnly = true)
    public CatalogPageResponse readAll(Long categoryId, Long page, Long pageSize) {
        return CatalogPageResponse.of(
                catalogRepository.findAll(categoryId, (page - 1) * pageSize, pageSize).stream()
                        .map(CatalogResponse::from)
                        .toList(),
                catalogRepository.countByCategoryId(
                        categoryId,
                        PageLimitCalculator.calculatePageLimit(page, pageSize, 10L)
                )
        );
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
