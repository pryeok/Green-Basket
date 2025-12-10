package hotcatalogservice.service;

import com.greenbasket.common.event.Event;
import com.greenbasket.common.event.EventPayload;
import com.greenbasket.common.event.EventType;
import hotcatalogservice.service.event.handler.EventHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HotCatalogServiceImpl implements HotCatalogService {
    private final List<EventHandler> eventHandlers;
    private final HotCatalogScoreCalculator hotCatalogScoreCalculator;
    private final HotCatalogScoreUpdater hotCatalogScoreUpdater;

    public void handleEvent(Event<EventPayload> event) {
        EventHandler<EventPayload> eventHandler = findEventHandler(event);
        if (eventHandler == null) {
            return;
        }

        if (isCatalogCreatedOrDeleted(event)) {
            eventHandler.handle(event);
        } else {
            hotCatalogScoreUpdater.update(event, eventHandler);
        }
    }

    private EventHandler<EventPayload> findEventHandler(Event<EventPayload> event) {
        return eventHandlers.stream()  // 7개의 핸들러 순회
                .filter(eventHandler -> eventHandler.supports(event))
                .findAny()  // True인 핸들러 반환
                .orElse(null);
    }

    private boolean isCatalogCreatedOrDeleted(Event<EventPayload> event) {
        return EventType.CATALOG_CREATED == event.getType() || EventType.CATALOG_DELETED == event.getType();
    }

}
