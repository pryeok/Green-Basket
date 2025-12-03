package catalogservice.controller;

import catalogservice.dto.CatalogDto;
import catalogservice.service.catalog.CatalogService;
import catalogservice.service.catalog.request.CatalogCreateRequest;
import catalogservice.service.catalog.response.CatalogPageResponse;
import catalogservice.service.catalog.response.CatalogResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CatalogController {
    private final CatalogService catalogService;

    @PostMapping("/catalogs")
    public CatalogResponse createCatalog(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody CatalogCreateRequest catalogCreateRequest) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        CatalogDto catalogDto = mapper.map(catalogCreateRequest, CatalogDto.class);
        catalogDto.setUserId(userId);
        return catalogService.createCatalog(catalogDto);
    }

    @PutMapping("/catalogs/{productId}")
    public CatalogResponse updateCatalog(
            @PathVariable("productId") String productId,
            @RequestBody CatalogCreateRequest catalogCreateRequest
    ) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        CatalogDto catalogDto = mapper.map(catalogCreateRequest, CatalogDto.class);
        return catalogService.updateCatalog(productId, catalogDto);
    }

    @DeleteMapping("/catalogs/{productId}")
    public void deleteCatalog(@PathVariable("productId") String productId) {
        catalogService.deleteCatalog(productId);
    }

    @GetMapping("/catalogs/users/{userId}")
    public List<CatalogResponse> getCatalogsByUserId(@PathVariable("userId") String userId) {
        return catalogService.getCatalogsByUserId(userId);
    }

    @GetMapping("/catalogs")
    public List<CatalogResponse> getAllCatalogs() {
        return catalogService.getAllCatalogs();
    }

    @GetMapping("/catalogs/category")
    public CatalogPageResponse readAll(
            @RequestParam(value = "categoryId") Long categoryId,
            @RequestParam(value = "page", defaultValue = "1") Long page,
            @RequestParam(value = "pageSize", defaultValue = "20") Long pageSize) {
        return catalogService.readAll(categoryId, page, pageSize);
    }

    @PostMapping("/catalogs/batch")
    public List<CatalogResponse> getCatalogsByProductIds(@RequestBody List<String> productIds) {
        return catalogService.getCatalogsByProductIds(productIds);
    }

    @PutMapping("/catalogs/{productId}/decrease-stock")
    public void decreaseStock(
            @PathVariable("productId") String productId,
            @RequestParam("quantity") Integer quantity) {
        catalogService.decreaseStock(productId, quantity);
    }

    @PutMapping("/catalogs/{productId}/increase-stock")
    public void increaseStock(
            @PathVariable("productId") String productId,
            @RequestParam("quantity") Integer quantity) {
        catalogService.increaseStock(productId, quantity);
    }
}
