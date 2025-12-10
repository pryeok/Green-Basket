package catalogreadservice.client.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Catalog Service로부터 받는 페이징된 상품 목록 응답
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CatalogPageResponse {
    private List<CatalogResponse> catalogs;
    private Long catalogCount;

    public static final CatalogPageResponse EMPTY = new CatalogPageResponse(List.of(), 0L);
}
