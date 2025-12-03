package hotcatalogservice.service.eventhandler;

import com.greenbasket.common.event.Event;
import com.greenbasket.common.event.EventType;
import com.greenbasket.common.event.payload.CatalogDeletedEventPayload;
import hotcatalogservice.repository.CatalogCreatedTimeRepository;
import hotcatalogservice.repository.HotCatalogListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CatalogDeletedEventHandler implements EventHandler<CatalogDeletedEventPayload> {
    private final HotCatalogListRepository hotCatalogListRepository;
    private final CatalogCreatedTimeRepository catalogCreatedTimeRepository;

    @Override
    public void handle(Event<CatalogDeletedEventPayload> event) {
        CatalogDeletedEventPayload payload = event.getPayload();
        catalogCreatedTimeRepository.deleted(payload.getId());
        hotCatalogListRepository.remove(payload.getId(), payload.getCreatedAt());
    }

    @Override
    public boolean supports(Event<CatalogDeletedEventPayload> event) {
        return EventType.CATALOG_DELETED == event.getType();
    }

    @Override
    public Long findCatalogId(Event<CatalogDeletedEventPayload> event) {
        return event.getPayload().getId();
    }
}
