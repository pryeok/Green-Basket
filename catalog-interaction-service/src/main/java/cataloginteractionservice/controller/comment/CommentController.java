package cataloginteractionservice.controller.comment;

import cataloginteractionservice.dto.comment.CommentDto;
import cataloginteractionservice.service.comment.CommentService;
import cataloginteractionservice.service.comment.request.CommentCreateRequest;
import cataloginteractionservice.service.comment.response.CommentPageResponse;
import cataloginteractionservice.service.comment.response.CommentResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    // 댓글 생성
    @PostMapping("/catalogs/comments")
    public CommentResponse create(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody CommentCreateRequest commentCreateRequest) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        CommentDto commentDto = mapper.map(commentCreateRequest, CommentDto.class);
        commentDto.setUserId(userId);
        return commentService.create(commentDto);
    }

    // 댓글 목록 조회 (페이지네이션)
    @GetMapping("/catalogs/{catalogId}/comments")
    public CommentPageResponse readAll(
            @PathVariable("catalogId") Long catalogId,
            @RequestParam(value = "page", defaultValue = "1") Long page,
            @RequestParam(value = "pageSize", defaultValue = "20") Long pageSize) {
        return commentService.readAll(catalogId, page, pageSize);
    }

    // 댓글 삭제
    @DeleteMapping("/catalogs/comments/{commentId}")
    public void delete(
            @PathVariable("commentId") Long commentId,
            @RequestHeader("X-User-Id") String userId) {
        commentService.delete(commentId);
    }
}
