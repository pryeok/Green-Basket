package catalogreadservice.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "catalog-interaction-service")
public interface CommentClient {

    /**
     * 특정 상품의 댓글 수 조회
     *
     * @param catalogId 상품 ID (productId가 아닌 catalog 테이블의 PK)
     * @return 댓글 수
     */
    @GetMapping("/v1/catalog-comments/catalogs/{catalogId}/count")
    Long getCommentCount(@PathVariable("catalogId") Long catalogId);
}
