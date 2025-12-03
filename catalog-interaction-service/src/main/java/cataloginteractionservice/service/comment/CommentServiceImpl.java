package cataloginteractionservice.service.comment;

import cataloginteractionservice.dto.comment.CommentDto;
import cataloginteractionservice.entity.Comment;
import cataloginteractionservice.repository.comment.CommentRepository;
import cataloginteractionservice.service.comment.response.CommentPageResponse;
import cataloginteractionservice.service.comment.response.CommentResponse;
import com.greenbasket.common.snowflake.Snowflake;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static java.util.function.Predicate.not;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService{
    private final Snowflake snowflake;
    private final CommentRepository commentRepository;

    @Transactional
    public CommentResponse create(CommentDto commentDto) {
        Comment parent = findParent(commentDto);
        Comment comment = commentRepository.save(
                Comment.create(
                        snowflake.nextId(),
                        commentDto.getContent(),
                        parent == null ? null : parent.getCommentId(),
                        commentDto.getCatalogId(),
                        commentDto.getUserId()
                )
        );
        return CommentResponse.from(comment);
    }

    private Comment findParent(CommentDto commentDto) {
        Long parentCommentId = commentDto.getParentCommentId();
        if (parentCommentId == null) return null;
        return commentRepository.findById(parentCommentId)
                .filter(not(Comment::getDeleted))
                .filter(Comment::isRoot)
                .orElseThrow();
    }

    @Transactional
    public void delete(Long commentId) {
        commentRepository.findById(commentId)
                .filter(not(Comment::getDeleted))
                .ifPresent(comment -> {
                    if (hasChildren(comment)) {  // 대댓글이 2개인 경우 root 댓글 논리삭제
                        comment.delete();
                    } else {
                        delete(comment);  // 대댓글이 0~1개인 경우 root 댓글 물리삭제
                    }
                });
    }

    // depth가 2개인지 확인하는
    private boolean hasChildren(Comment comment) {
        return commentRepository.countBy(comment.getCatalogId(), comment.getCommentId(), 2L) == 2;
    }

    private void delete(Comment comment) {
        commentRepository.delete(comment);
        if (!comment.isRoot()) { // 루트댓글이 아니라 댓글이거나 대댓글인 경우
            commentRepository.findById(comment.getParentCommentId())
                    .filter(Comment::getDeleted)
                    .filter(not(this::hasChildren)) // haschildren 확인 후 delete 함수 재귀
                    .ifPresent(this::delete);
        }
    }

    @Override
    public CommentPageResponse readAll(Long catalogId, Long page, Long pageSize) {
        return CommentPageResponse.of(
                commentRepository.findAll(catalogId, (page - 1) * pageSize, pageSize).stream()
                        .map(CommentResponse::from)
                        .toList(),
                commentRepository.count(catalogId, PageLimitCalculator.calculatePageLimit(page, pageSize, 10L))
        );
    }

}
