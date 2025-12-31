package catalogreadservice.consumer;

import catalogreadservice.service.CatalogReadService;
import com.greenbasket.common.event.Event;
import com.greenbasket.common.event.EventPayload;
import com.greenbasket.common.event.EventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.acks.AcknowledgmentCallback;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CatalogReadConsumer {
    private CatalogReadService catalogReadService;

    @KafkaListener(
            topics = {
                    EventType.Topic.GREEN_BASKET_CATALOG,
                    EventType.Topic.GREEN_BASKET_CATALOG_LIKE
            },
            groupId = "hot-read-service"
    )
    public void listen(String message, Acknowledgment ack) {
        log.info("[CatalogReadEventConsumer.listen] message={}", message);
        Event<EventPayload> event = Event.fromJson(message);
        if (event != null) {
            catalogReadService.handleEvent(event);
        ack.acknowledge();
    }

}
