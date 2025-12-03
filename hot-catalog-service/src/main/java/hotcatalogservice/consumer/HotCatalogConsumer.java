package hotcatalogservice.consumer;

import com.greenbasket.common.event.Event;
import com.greenbasket.common.event.EventPayload;
import com.greenbasket.common.event.EventType;
import hotcatalogservice.service.HotCatalogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class HotCatalogConsumer {
    private final HotCatalogService hotCatalogService;

    @KafkaListener(
            topics = {
                    EventType.Topic.GREEN_BASKET_ORDER,
                    EventType.Topic.GREEN_BASKET_CATALOG,
                    EventType.Topic.GREEN_BASKET_CATALOG_VIEW,
                    EventType.Topic.GREEN_BASKET_CATALOG_LIKE
            },
            groupId = "hot-catalog-service"
    )
    public void listen(String message, Acknowledgment ack) {
        log.info("[HotCatalogEventConsumer.listen] received message={}", message);
        Event<EventPayload> event = Event.fromJson(message);
        if (event != null) {
            hotCatalogService.handleEvent(event);
        }
        ack.acknowledge();
    }
}
