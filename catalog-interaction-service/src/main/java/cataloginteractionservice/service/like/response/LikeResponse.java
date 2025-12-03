package cataloginteractionservice.service.like.response;

import cataloginteractionservice.entity.Like;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class LikeResponse {
    private Long likeId;
    private Long catalogId;
    private String userId;

    public static LikeResponse from(Like like) {
        LikeResponse likeResponse = new LikeResponse();
        likeResponse.likeId = like.getLikeId();
        likeResponse.catalogId = like.getCatalogId();
        likeResponse.userId = like.getUserId();
        return likeResponse;
    }
}
