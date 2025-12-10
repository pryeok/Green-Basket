package hotcatalogservice.service.event.handler;

import com.greenbasket.common.event.Event;
import com.greenbasket.common.event.EventType;
import com.greenbasket.common.event.payload.CatalogUnlikedEventPayload;
import hotcatalogservice.repository.CatalogLikeCountRepository;
import hotcatalogservice.util.TimeCalculatorUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CatalogUnlikedEventHandler implements EventHandler<CatalogUnlikedEventPayload> {
    private final CatalogLikeCountRepository catalogLikeCountRepository;

    @Override
    public void handle(Event<CatalogUnlikedEventPayload> event) {
        CatalogUnlikedEventPayload payload = event.getPayload();
        catalogLikeCountRepository.createOrUpdate(
                payload.getCatalogId(),
                payload.getLikeCount(),
                TimeCalculatorUtils.calculateDurationToEndOfWeek()
        );
    }

    @Override
    public boolean supports(Event<CatalogUnlikedEventPayload> event) {
        return EventType.CATALOG_UNLIKED == event.getType();
    }

    @Override
    public Long findCatalogId(Event<CatalogUnlikedEventPayload> event) {
        return event.getPayload().getCatalogId();
    }
}
