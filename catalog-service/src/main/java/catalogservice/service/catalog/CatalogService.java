package catalogservice.service.catalog;

import catalogservice.dto.CatalogDto;
import catalogservice.service.catalog.response.CatalogPageResponse;
import catalogservice.service.catalog.response.CatalogResponse;

import java.util.List;

public interface CatalogService {

    CatalogResponse createCatalog(CatalogDto catalogDto);

    CatalogResponse updateCatalog(String productId, CatalogDto catalogDto);

    void deleteCatalog(String productId);

    CatalogResponse getCatalog(String productId);

    List<CatalogResponse> getCatalogsByUserId(String userId);

    List<CatalogResponse> getAllCatalogs();

    List<CatalogResponse> getCatalogsByProductIds(List<String> productIds);

    CatalogPageResponse readAll(Long categoryId, Long page, Long pageSize);

    // 재고 차감
    void decreaseStock(String productId, Integer quantity);

    // 재고 복구
    void increaseStock(String productId, Integer quantity);

}
