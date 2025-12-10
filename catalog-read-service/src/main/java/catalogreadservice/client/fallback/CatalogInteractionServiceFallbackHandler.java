package catalogreadservice.client.fallback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Catalog Interaction Service (Comment, Like, View) 호출 실패 시 Fallback 처리
 * NOTE: catalog-interaction-service가 아직 구현되지 않았으므로, 기본값 0L 반환
 */
@Slf4j
@Component
public class CatalogInteractionServiceFallbackHandler {

    /**
     * 댓글 수 조회 실패 Fallback
     * NOTE: Read 작업이므로 0 반환 (서비스 장애 시 사용자에게 0으로 표시)
     */
    public Long getCommentCountFallback(Long catalogId, Exception e) {
        log.warn("[FALLBACK] 댓글 수 조회 실패 (catalog-interaction-service 미구현 또는 장애) - catalogId: {}, 원인: {}",
                 catalogId, e.getMessage());

        return 0L;
    }

    /**
     * 좋아요 수 조회 실패 Fallback
     * NOTE: Read 작업이므로 0 반환
     */
    public Long getLikeCountFallback(Long catalogId, Exception e) {
        log.warn("[FALLBACK] 좋아요 수 조회 실패 (catalog-interaction-service 미구현 또는 장애) - catalogId: {}, 원인: {}",
                 catalogId, e.getMessage());

        return 0L;
    }

    /**
     * 조회 수 조회 실패 Fallback
     * NOTE: Read 작업이므로 0 반환
     */
    public Long getViewCountFallback(Long catalogId, Exception e) {
        log.warn("[FALLBACK] 조회 수 조회 실패 (catalog-interaction-service 미구현 또는 장애) - catalogId: {}, 원인: {}",
                 catalogId, e.getMessage());

        return 0L;
    }
}
