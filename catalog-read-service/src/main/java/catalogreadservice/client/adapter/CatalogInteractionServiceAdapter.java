package catalogreadservice.client.adapter;

import catalogreadservice.client.CommentClient;
import catalogreadservice.client.LikeClient;
import catalogreadservice.client.ViewClient;
import catalogreadservice.client.fallback.CatalogInteractionServiceFallbackHandler;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Catalog Interaction Service (Comment, Like, View) 호출을 Circuit Breaker로 보호하는 Adapter
 * NOTE: catalog-interaction-service가 아직 구현되지 않았으므로, fallback은 기본값 반환
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CatalogInteractionServiceAdapter {
    private final CommentClient commentClient;
    private final LikeClient likeClient;
    private final ViewClient viewClient;
    private final CatalogInteractionServiceFallbackHandler fallbackHandler;

//    /**
//     * Circuit Breaker + Retry로 보호된 댓글 수 조회
//     */
//    @CircuitBreaker(name = "catalog-interaction", fallbackMethod = "getCommentCountFallback")
//    @Retry(name = "catalog-interaction")
//    public Long getCommentCount(Long catalogId) {
//        log.debug("댓글 수 조회 시도: catalogId={}", catalogId);
//        return commentClient.getCommentCount(catalogId);
//    }
//
//    public Long getCommentCountFallback(Long catalogId, Exception e) {
//        return fallbackHandler.getCommentCountFallback(catalogId, e);
//    }

    /**
     * Circuit Breaker + Retry로 보호된 좋아요 수 조회
     */
    @CircuitBreaker(name = "catalog-interaction", fallbackMethod = "getLikeCountFallback")
    @Retry(name = "catalog-interaction")
    public Long getLikeCount(Long catalogId) {
        log.debug("좋아요 수 조회 시도: catalogId={}", catalogId);
        return likeClient.getLikeCount(catalogId);
    }

    public Long getLikeCountFallback(Long catalogId, Exception e) {
        return fallbackHandler.getLikeCountFallback(catalogId, e);
    }

    /**
     * Circuit Breaker + Retry로 보호된 조회 수 조회
     */
    @CircuitBreaker(name = "catalog-interaction", fallbackMethod = "getViewCountFallback")
    @Retry(name = "catalog-interaction")
    public Long getViewCount(Long catalogId) {
        log.debug("조회 수 조회 시도: catalogId={}", catalogId);
        return viewClient.getViewCount(catalogId);
    }

    public Long getViewCountFallback(Long catalogId, Exception e) {
        return fallbackHandler.getViewCountFallback(catalogId, e);
    }
}
