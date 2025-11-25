package cataloginteractionservice.entity;

import cataloginteractionservice.entity.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Table(name = "comment")
@Getter
@Entity
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseEntity {
    @Id
    private Long commentId;

    @Column(nullable = false)
    private String content;
    @Column(nullable = false)
    private Long parentCommentId;
    @Column(nullable = false) // Shard Key
    private Long catalogId;
    @Column(nullable = false)
    private String userId;
    private Boolean deleted;

    public static Comment create(Long commentId, String content, Long parentCommentId, Long catalogId, String userId) {
        Comment comment = new Comment();
        comment.commentId = commentId;
        comment.content = content;
        comment.parentCommentId = parentCommentId == null ? commentId : parentCommentId;
        comment.catalogId = catalogId;
        comment.userId = userId;
        comment.deleted = false;
        return comment;
    }

    public boolean isRoot() {
        return parentCommentId.longValue() == commentId;
    }

    public void delete() {
        this.deleted = true;
    }
}
