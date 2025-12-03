package hotcatalogservice.service.eventhandler;

import com.greenbasket.common.event.Event;
import com.greenbasket.common.event.EventType;
import com.greenbasket.common.event.payload.CatalogViewedEventPayload;
import hotcatalogservice.repository.CatalogViewCountRepository;
import hotcatalogservice.util.TimeCalculatorUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CatalogViewedEventHandler implements EventHandler<CatalogViewedEventPayload> {
    private final CatalogViewCountRepository catalogViewCountRepository;

    @Override
    public void handle(Event<CatalogViewedEventPayload> event) {
        CatalogViewedEventPayload payload = event.getPayload();
        catalogViewCountRepository.createOrUpdate(
                payload.getCatalogId(),
                payload.getViewCount(),
                TimeCalculatorUtils.calculateDurationToEndOfWeek()
        );
    }

    @Override
    public boolean supports(Event<CatalogViewedEventPayload> event) {
        return EventType.CATALOG_VIEWED == event.getType();
    }

    @Override
    public Long findCatalogId(Event<CatalogViewedEventPayload> event) {
        return event.getPayload().getCatalogId();
    }
}
