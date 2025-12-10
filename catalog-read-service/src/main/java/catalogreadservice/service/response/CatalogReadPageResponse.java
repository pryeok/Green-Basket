package catalogreadservice.service.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * Catalog 페이징 조회 응답 DTO
 */
@Getter
@AllArgsConstructor
public class CatalogReadPageResponse {
    private List<CatalogReadResponse> catalogs;
    private Long catalogCount;

    public static CatalogReadPageResponse of(List<CatalogReadResponse> catalogs, Long catalogCount) {
        return new CatalogReadPageResponse(catalogs, catalogCount);
    }

    public static final CatalogReadPageResponse EMPTY = new CatalogReadPageResponse(List.of(), 0L);
}
