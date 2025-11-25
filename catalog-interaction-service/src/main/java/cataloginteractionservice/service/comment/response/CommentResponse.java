package cataloginteractionservice.service.comment.response;

import cataloginteractionservice.entity.Comment;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class CommentResponse {
    private Long commentId;
    private String content;
    private Long parentCommentId;
    private Long catalogId;
    private String userId;
    private Boolean deleted;

    public static CommentResponse from(Comment comment) {
        CommentResponse response = new CommentResponse();
        response.commentId = comment.getCommentId();
        response.content = comment.getContent();
        response.parentCommentId = comment.getParentCommentId();
        response.catalogId = comment.getCatalogId();
        response.userId = comment.getUserId();
        response.deleted = comment.getDeleted();
        return response;
    }

}
