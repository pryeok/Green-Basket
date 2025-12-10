package hotcatalogservice.service.event.handler;

import com.greenbasket.common.event.Event;
import com.greenbasket.common.event.EventPayload;

public interface EventHandler<T extends EventPayload> {
    void handle(Event<T> event);
    boolean supports(Event<T> event);
    Long findCatalogId(Event<T> event);

}
