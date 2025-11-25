package cataloginteractionservice.dto.comment;

import lombok.Data;

@Data
public class CommentDto {
    private String content;
    private Long parentCommentId;
    private Long catalogId;
    private String userId;
}
