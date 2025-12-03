package cataloginteractionservice.controller.like;

import cataloginteractionservice.dto.like.LikeDto;
import cataloginteractionservice.service.like.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class LikeController {
    private final LikeService likeService;

    // 좋아요 추가
    @PostMapping("/catalogs/{catalogId}/likes")
    public void likePessmisticLock(
            @PathVariable("catalogId") Long catalogId,
            @RequestHeader("X-User-Id") String userId) {
        LikeDto likeDto = new LikeDto();
        likeDto.setCatalogId(catalogId);
        likeDto.setUserId(userId);
        likeService.likePessimisticLock(likeDto);
    }

    // 좋아요 취소
    @DeleteMapping("/catalogs/{catalogId}/likes")
    public void unlikePessmisticLock(
            @PathVariable("catalogId") Long catalogId,
            @RequestHeader("X-User-Id") String userId) {
        LikeDto likeDto = new LikeDto();
        likeDto.setCatalogId(catalogId);
        likeDto.setUserId(userId);
        likeService.unlikePessimisticLock(likeDto);
    }
}
