package catalogreadservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "catalog-interaction-service")
public interface LikeClient {

    @GetMapping("/catalogs/{catalogId}/likes/count")
    Long getLikeCount(@PathVariable("catalogId") Long catalogId);
}
