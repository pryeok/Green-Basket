package hotcatalogservice.service.event.handler;

import com.greenbasket.common.event.Event;
import com.greenbasket.common.event.EventType;
import com.greenbasket.common.event.payload.CatalogCreatedEventPayload;
import hotcatalogservice.repository.CatalogCreatedTimeRepository;
import hotcatalogservice.util.TimeCalculatorUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CatalogCreatedEventHandler implements EventHandler<CatalogCreatedEventPayload> {
    private final CatalogCreatedTimeRepository catalogCreatedTimeRepository;

    @Override
    public void handle(Event<CatalogCreatedEventPayload> event) {
        CatalogCreatedEventPayload payload = event.getPayload();
        catalogCreatedTimeRepository.createOrUpdate(
                payload.getId(),
                payload.getCreatedAt(),
                TimeCalculatorUtils.calculateDurationToEndOfWeek()
        );
    }

    @Override
    public boolean supports(Event<CatalogCreatedEventPayload> event) {
        return EventType.CATALOG_CREATED == event.getType();
    }

    @Override
    public Long findCatalogId(Event<CatalogCreatedEventPayload> event) {
        return event.getPayload().getId();
    }
}
