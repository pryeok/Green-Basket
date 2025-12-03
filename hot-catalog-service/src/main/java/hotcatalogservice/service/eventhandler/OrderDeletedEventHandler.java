package hotcatalogservice.service.eventhandler;

import com.greenbasket.common.event.Event;
import com.greenbasket.common.event.EventType;
import com.greenbasket.common.event.payload.OrderDeletedEventPayload;
import hotcatalogservice.repository.OrderCountRepository;
import hotcatalogservice.util.TimeCalculatorUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderDeletedEventHandler implements EventHandler<OrderDeletedEventPayload> {
    private final OrderCountRepository orderCountRepository;

    @Override
    public void handle(Event<OrderDeletedEventPayload> event) {
        OrderDeletedEventPayload payload = event.getPayload();

        // OrderItem에서 catalogId 리스트 추출
        List<String> catalogIds = payload.getOrderItems().stream()
                .map(OrderDeletedEventPayload.OrderItemPayload::getCatalogId)
                .map(String::valueOf)
                .collect(Collectors.toList());

        // Pipeline + Hash로 한 번에 감소
        orderCountRepository.decrementBatch(
                catalogIds,
                payload.getCreatedAt(),
                TimeCalculatorUtils.calculateDurationToEndOfWeek()
        );
    }

    @Override
    public boolean supports(Event<OrderDeletedEventPayload> event) {
        return EventType.ORDER_DELETED == event.getType();
    }

    @Override
    public Long findCatalogId(Event<OrderDeletedEventPayload> event) {
        // Order는 여러 catalog를 포함하므로 첫 번째 catalog 반환
        return event.getPayload().getOrderItems().isEmpty()
                ? null
                : event.getPayload().getOrderItems().get(0).getCatalogId();
    }
}
