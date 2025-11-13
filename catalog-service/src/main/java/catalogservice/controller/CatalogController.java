package catalogservice.controller;

import catalogservice.dto.CatalogDto;
import catalogservice.service.catalog.CatalogService;
import catalogservice.service.catalog.request.CatalogCreateRequest;
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

    @GetMapping("/catalogs/users/{userId}")
    public List<CatalogResponse> getCatalogsByUserId(@PathVariable("userId") String userId) {
        return catalogService.getCatalogsByUserId(userId);
    }

    @GetMapping("/catalogs")
    public List<CatalogResponse> getAllCatalogs() {
        return catalogService.getAllCatalogs();
    }
}
