package cataloginteractionservice.service.comment.request;

import lombok.Getter;

@Getter
public class CommentCreateRequest {
    private String content;
    private Long parentCommentId;
    private Long catalogId;
}
