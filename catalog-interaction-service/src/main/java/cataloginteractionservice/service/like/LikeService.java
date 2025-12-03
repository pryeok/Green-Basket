package cataloginteractionservice.service.like;

import cataloginteractionservice.dto.like.LikeDto;

public interface LikeService {
    void likePessimisticLock(LikeDto likeDto);

    void unlikePessimisticLock(LikeDto likeDto);
}
