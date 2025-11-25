package cataloginteractionservice.service.comment;

import cataloginteractionservice.dto.comment.CommentDto;
import cataloginteractionservice.service.comment.response.CommentPageResponse;
import cataloginteractionservice.service.comment.response.CommentResponse;

public interface CommentService {
    CommentResponse create(CommentDto commentDto);

    CommentPageResponse readAll(Long catalogId, Long page, Long pageSize);

    void delete(Long commentId);
}
