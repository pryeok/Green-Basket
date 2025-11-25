package catalogservice.service.catalog.response;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class CatalogPageResponse {
    private List<CatalogResponse> catalogs;
    private Long catalogCount;

    public static CatalogPageResponse of(List<CatalogResponse> catalogs, Long catalogCount) {
        CatalogPageResponse response = new CatalogPageResponse();
        response.catalogs = catalogs;
        response.catalogCount = catalogCount;
        return response;
    }
}
