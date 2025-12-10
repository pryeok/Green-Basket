package catalogreadservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "catalog-interaction-service")
public interface ViewClient {

    /**
     * 특정 상품의 조회 수 조회
     *
     * @param catalogId 상품 ID (productId가 아닌 catalog 테이블의 PK)
     * @return 조회 수
     */
    @GetMapping("/catalogs/{catalogId}/views")
    Long getViewCount(@PathVariable("catalogId") Long catalogId);
}
