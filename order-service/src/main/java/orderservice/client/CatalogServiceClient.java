package orderservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import orderservice.client.response.CatalogResponse;

import java.util.List;

@FeignClient(name = "catalog-service")
public interface CatalogServiceClient {

    @PostMapping("/catalogs/batch")
    List<CatalogResponse> getProducts(@RequestBody List<String> productIds);

    @PutMapping("/catalogs/{productId}/decrease-stock")
    void decreaseStock(@PathVariable("productId") String productId,
                       @RequestParam("quantity") Integer quantity);

    @PutMapping("/catalogs/{productId}/increase-stock")
    void increaseStock(@PathVariable("productId") String productId,
                       @RequestParam("quantity") Integer quantity);

}
