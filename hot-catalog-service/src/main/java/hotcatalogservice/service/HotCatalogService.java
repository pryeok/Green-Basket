package hotcatalogservice.service;

import com.greenbasket.common.event.Event;
import com.greenbasket.common.event.EventPayload;

public interface HotCatalogService {
    void handleEvent(Event<EventPayload> event);
}
