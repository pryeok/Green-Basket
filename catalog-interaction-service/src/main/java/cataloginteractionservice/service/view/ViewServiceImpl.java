package cataloginteractionservice.service.view;

import cataloginteractionservice.repository.view.ViewCountRepository;
import cataloginteractionservice.repository.view.ViewDistributedLockRepository;
import com.greenbasket.common.event.EventType;
import com.greenbasket.common.event.payload.CatalogViewedEventPayload;
import com.greenbasket.common.outboxmessagerelay.OutboxEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class ViewServiceImpl implements ViewService {
    private final ViewCountRepository viewCountRepository;
    private final ViewCountBackUpProcessor viewCountBackUpProcessor;
    private final ViewDistributedLockRepository viewDistributedLockRepository;
    private final OutboxEventPublisher outboxEventPublisher;

    private static final int BACK_UP_BACH_SIZE = 100;
    private static final Duration TTL = Duration.ofMinutes(10);

    @Transactional
    public Long increase(Long catalogId, String userId) {
        if (!viewDistributedLockRepository.lock(catalogId, userId, TTL)) {
            return viewCountRepository.read(catalogId);
        }

        Long count = viewCountRepository.increase(catalogId);

        outboxEventPublisher.publish(
                EventType.CATALOG_VIEWED,
                CatalogViewedEventPayload.builder()
                        .catalogId(catalogId)
                        .viewCount(count)
                        .build(),
                catalogId
        );

        if (count % BACK_UP_BACH_SIZE == 0) {
            viewCountBackUpProcessor.backUp(catalogId, count);
        }
        return count;
    }

    public Long count(Long catalogId) {
        return viewCountRepository.read(catalogId);
    }
}
