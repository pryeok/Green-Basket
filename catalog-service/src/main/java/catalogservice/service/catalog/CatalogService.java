package catalogservice.service.catalog;

import catalogservice.dto.CatalogDto;
import catalogservice.service.catalog.response.CatalogResponse;

import java.util.List;

public interface CatalogService {

    CatalogResponse createCatalog(CatalogDto catalogDto);

    List<CatalogResponse> getCatalogsByUserId(String userId);

    List<CatalogResponse> getAllCatalogs();

}
