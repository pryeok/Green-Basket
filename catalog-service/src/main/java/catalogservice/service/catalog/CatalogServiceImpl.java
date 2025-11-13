package catalogservice.service.catalog;

import catalogservice.dto.CatalogDto;
import catalogservice.entity.Catalog;
import catalogservice.repository.CatalogRepository;
import catalogservice.service.catalog.response.CatalogResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.greenbasket.common.snowflake.Snowflake;
import static com.greenbasket.common.idgenerator.IdGenerator.generateProductId;
import java.util.List;


@Service
@RequiredArgsConstructor
public class CatalogServiceImpl implements CatalogService {
    private final Snowflake snowflake;
    private final CatalogRepository catalogRepository;

    @Override
    @Transactional
    public CatalogResponse createCatalog(CatalogDto catalogDto) {
        Catalog catalog = Catalog.create(
                snowflake.nextId(),
                generateProductId(),
                catalogDto.getProductName(),
                catalogDto.getStock(),
                catalogDto.getUnitPrice(),
                catalogDto.getUserId()
        );
        Catalog savedCatalog = catalogRepository.save(catalog);
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
}
