package catalogreadservice.client;

import catalogreadservice.client.response.CatalogPageResponse;
import catalogreadservice.client.response.CatalogResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Catalog Service 호출을 위한 Feign Client
 * 상품 기본 정보 조회
 */
@FeignClient(name = "catalog-service")
public interface CatalogClient {

    /**
     * 단일 상품 조회 (productId)
     */
    @GetMapping("/catalogs/{productId}")
    CatalogResponse getCatalog(@PathVariable("productId") String productId);

    /**
     * 전체 상품 목록 조회
     */
    @GetMapping("/catalogs")
    List<CatalogResponse> getAllCatalogs();

    /**
     * 카테고리별 페이징 조회
     */
    @GetMapping("/catalogs/category")
    CatalogPageResponse getCatalogsByCategory(
            @RequestParam("categoryId") Long categoryId,
            @RequestParam(value = "page", defaultValue = "1") Long page,
            @RequestParam(value = "pageSize", defaultValue = "20") Long pageSize
    );

    /**
     * 여러 상품 ID로 배치 조회 (CQRS Read Model 구성 시 사용)
     */
    @PostMapping("/catalogs/batch")
    List<CatalogResponse> getCatalogsByProductIds(@RequestBody List<String> productIds);
}
