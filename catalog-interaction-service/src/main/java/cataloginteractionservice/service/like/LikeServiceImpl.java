package cataloginteractionservice.service.like;

import cataloginteractionservice.dto.like.LikeDto;
import cataloginteractionservice.entity.Like;
import cataloginteractionservice.entity.LikeCount;
import cataloginteractionservice.repository.like.LikeCountRepository;
import cataloginteractionservice.repository.like.LikeRepository;
import cataloginteractionservice.service.like.response.LikeResponse;
import com.greenbasket.common.event.EventType;
import com.greenbasket.common.event.payload.CatalogLikedEventPayload;
import com.greenbasket.common.outboxmessagerelay.OutboxEventPublisher;
import com.greenbasket.common.snowflake.Snowflake;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {
    private final Snowflake snowflake;
    private final OutboxEventPublisher outboxEventPublisher;
    private final LikeRepository likeRepository;
    private final LikeCountRepository likeCountRepository;

    @Transactional
    public void likePessimisticLock(LikeDto likeDto) {
        Like savedlike = likeRepository.save(
                Like.create(
                        snowflake.nextId(),
                        likeDto.getCatalogId(),
                        likeDto.getUserId()
                )
        );
        LikeCount likeCount = likeCountRepository.findLockedByCatalogId(likeDto.getCatalogId())
                .orElseGet(() -> LikeCount.init(likeDto.getCatalogId(), 0L));
        likeCount.increase();
        likeCountRepository.save(likeCount);

        outboxEventPublisher.publish(
                EventType.CATALOG_LIKED,
                CatalogLikedEventPayload.builder()
                        .likeId(savedlike.getLikeId())
                        .catalogId(savedlike.getCatalogId())
                        .userId(savedlike.getUserId())
                        .createdAt(savedlike.getCreatedAt())
                        .likeCount(count(savedlike.getCatalogId()))
                        .build(),
                savedlike.getCatalogId()
        );
    }

    @Transactional
    public void unlikePessimisticLock(LikeDto likeDto) {
        likeRepository.findByCatalogIdAndUserId(likeDto.getCatalogId(), likeDto.getUserId())
                .ifPresent(like -> {
                    likeRepository.delete(like);
                    LikeCount likeCount = likeCountRepository.findLockedByCatalogId(likeDto.getCatalogId()).orElseThrow();
                    likeCount.decrease();
                    outboxEventPublisher.publish(
                            EventType.CATALOG_UNLIKED,
                            CatalogLikedEventPayload.builder()
                                    .likeId(like.getLikeId())
                                    .catalogId(like.getCatalogId())
                                    .userId(like.getUserId())
                                    .createdAt(like.getCreatedAt())
                                    .likeCount(count(like.getCatalogId()))
                                    .build(),
                            like.getCatalogId()
                    );
                });
    }

    public Long count(Long catalogId) {
        return likeCountRepository.findById(catalogId)
                .map(LikeCount::getLikeCount)
                .orElse(0L);
    }
}
