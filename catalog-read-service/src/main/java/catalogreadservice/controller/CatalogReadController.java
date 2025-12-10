package catalogreadservice.controller;

import catalogreadservice.service.CatalogReadService;
import catalogreadservice.service.response.CatalogReadPageResponse;
import catalogreadservice.service.response.CatalogReadResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CatalogReadController {
    private final CatalogReadService catalogReadService;

    /**
     * 단일 상품 조회 (productId 기반) - Read 전용
     */
    @GetMapping("/catalog-read/{productId}")
    public CatalogReadResponse read(@PathVariable("productId") String productId) {
        return catalogReadService.read(productId);
    }

    /**
     * 카테고리별 상품 목록 조회 (페이징) - Read 전용
     */
    @GetMapping("/catalog-read")
    public CatalogReadPageResponse readAll(
            @RequestParam("categoryId") Long categoryId,
            @RequestParam(value = "page", defaultValue = "1") Long page,
            @RequestParam(value = "pageSize", defaultValue = "20") Long pageSize
    ) {
        return catalogReadService.readAll(categoryId, page, pageSize);
    }
}
