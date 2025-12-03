package hotcatalogservice.service.eventhandler;

import com.greenbasket.common.event.Event;
import com.greenbasket.common.event.EventType;
import com.greenbasket.common.event.payload.OrderCreatedEventPayload;
import hotcatalogservice.repository.OrderCountRepository;
import hotcatalogservice.util.TimeCalculatorUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderCreatedEventHandler implements EventHandler<OrderCreatedEventPayload> {
    private final OrderCountRepository orderCountRepository;

    @Override
    public void handle(Event<OrderCreatedEventPayload> event) {
        OrderCreatedEventPayload payload = event.getPayload();

        // OrderItem에서 catalogId 리스트 추출
        List<String> catalogIds = payload.getOrderItems().stream()
                .map(OrderCreatedEventPayload.OrderItemPayload::getCatalogId)
                .map(String::valueOf)
                .collect(Collectors.toList());

        // Pipeline + Hash로 한 번에 증가
        orderCountRepository.incrementBatch(
                catalogIds,
                payload.getCreatedAt(),
                TimeCalculatorUtils.calculateDurationToEndOfWeek()
        );
    }

    @Override
    public boolean supports(Event<OrderCreatedEventPayload> event) {
        return EventType.ORDER_CREATED == event.getType();
    }

    @Override
    public Long findCatalogId(Event<OrderCreatedEventPayload> event) {
        // Order는 여러 catalog를 포함하므로 첫 번째 catalog 반환
        // (HotCatalogScoreUpdater에서 사용)
        return event.getPayload().getOrderItems().isEmpty()
                ? null
                : event.getPayload().getOrderItems().get(0).getCatalogId();
    }
}
