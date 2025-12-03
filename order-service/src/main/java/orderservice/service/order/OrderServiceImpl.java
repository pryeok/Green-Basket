package orderservice.service.order;

import com.greenbasket.common.event.EventType;
import com.greenbasket.common.event.payload.OrderCreatedEventPayload;
import com.greenbasket.common.event.payload.OrderDeletedEventPayload;
import com.greenbasket.common.idgenerator.IdGenerator;
import com.greenbasket.common.outboxmessagerelay.OutboxEventPublisher;
import com.greenbasket.common.snowflake.Snowflake;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import orderservice.client.CatalogServiceAdapter;
import orderservice.client.CatalogServiceClient;
import orderservice.client.response.CatalogResponse;
import orderservice.dto.OrderDto;
import orderservice.dto.OrderItemDto;
import orderservice.entity.Order;
import orderservice.entity.OrderItem;
import orderservice.exception.OrderCreationFailedException;
import orderservice.exception.OutOfStockException;
import orderservice.repository.OrderRepository;
import orderservice.service.order.response.OrderResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final Snowflake snowflake;
    private final OutboxEventPublisher outboxEventPublisher;
    private final OrderRepository orderRepository;
    private final CatalogServiceClient catalogClient;
    private final CatalogServiceAdapter catalogServiceAdapter;

    @Override
    @Transactional
    public OrderResponse createOrder(OrderDto orderDto) {
        List<String> productIds = orderDto.getOrderItems().stream()
                .map(OrderItemDto::getProductId)
                .toList();

        // Circuit Breaker로 보호된 상품 조회
        List<CatalogResponse> catalogs = catalogServiceAdapter.getCatalogProducts(productIds);
        Map<String, CatalogResponse> catalogMap = catalogs.stream()
                .collect(Collectors.toMap(CatalogResponse::getProductId, catalog -> catalog));

        List<String> decreasedProductIds = new ArrayList<>();
        try {
            for (OrderItemDto itemDto : orderDto.getOrderItems()) {
                CatalogResponse catalog = catalogMap.get(itemDto.getProductId());
                // Circuit Breaker로 보호된 재고 차감
                catalogServiceAdapter.decreaseCatalogStock(catalog.getProductId(), itemDto.getQty());
                decreasedProductIds.add(catalog.getProductId());
            }
        } catch (OutOfStockException e) {
            // 재고 부족은 재시도 없이 바로 던지기
            throw e;
        } catch (Exception e) {
            // 기타 예외는 보상 트랜잭션 실행
            log.warn("재고 차감 실패, 보상 트랜잭션 실행");
            boolean rollbackSuccess = rollbackStockDecrease(orderDto, decreasedProductIds, catalogMap);

            if (!rollbackSuccess) {
                log.error("재고 복구 실패, 수동 복구 필요. decreasedProductIds: {}", decreasedProductIds);
            }
            throw new OrderCreationFailedException("재고 차감 실패: " + e.getMessage());
        }

        Order order = Order.create(
                snowflake.nextId(),
                IdGenerator.generateOrderId(),
                orderDto.getUserId(),
                orderDto.getUserPk()
        );

        orderDto.getOrderItems().forEach(itemDto -> {
            CatalogResponse catalog = catalogMap.get(itemDto.getProductId());
            OrderItem orderItem = OrderItem.create(
                    snowflake.nextId(),
                    order,
                    catalog.getId(),        // catalogId
                    catalog.getProductId(),
                    catalog.getProductName(),
                    itemDto.getQty(),
                    catalog.getUnitPrice()
            );
            order.addOrderItem(orderItem);
        });

        Order savedOrder = orderRepository.save(order);

        outboxEventPublisher.publish(
                EventType.ORDER_CREATED,
                OrderCreatedEventPayload.builder()
                        .id(savedOrder.getId())
                        .orderId(savedOrder.getOrderId())
                        .userId(savedOrder.getUserId())
                        .totalPrice(savedOrder.getTotalPrice())
                        .userPk(savedOrder.getUserPk())
                        .createdAt(savedOrder.getCreatedAt())
                        .updatedAt(savedOrder.getUpdatedAt())
                        .orderItems(
                                savedOrder.getOrderItems().stream()
                                        .map(item -> OrderCreatedEventPayload.OrderItemPayload.from(
                                                item.getCatalogId(),
                                                item.getProductId(),
                                                item.getProductName(),
                                                item.getQty(),
                                                item.getUnitPrice(),
                                                item.getTotalPrice()
                                        ))
                                        .toList()
                        )
                        .build(),
                savedOrder.getUserPk()
        );
        return OrderResponse.from(savedOrder);
    }


    @Override
    @Transactional
    public void delete(String orderId) {
        // 1. orderId로 Order 조회 (orderItems 포함)
        Order order = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다. orderId: " + orderId));

        // 2. 삭제 이벤트 발행 (실제 삭제 전에 발행)
        outboxEventPublisher.publish(
                EventType.ORDER_DELETED,
                OrderDeletedEventPayload.builder()
                        .id(order.getId())
                        .orderId(order.getOrderId())
                        .userId(order.getUserId())
                        .totalPrice(order.getTotalPrice())
                        .userPk(order.getUserPk())
                        .createdAt(order.getCreatedAt())
                        .updatedAt(order.getUpdatedAt())
                        .orderItems(
                                order.getOrderItems().stream()
                                        .map(item -> OrderDeletedEventPayload.OrderItemPayload.from(
                                                item.getCatalogId(),
                                                item.getProductId(),
                                                item.getProductName(),
                                                item.getQty(),
                                                item.getUnitPrice(),
                                                item.getTotalPrice()
                                        ))
                                        .toList()
                        )
                        .build(),
                order.getUserPk()  // shardKey
        );

        orderRepository.delete(order);
        log.info("주문 삭제 완료: orderId={}, id={}", orderId, order.getId());
    }


    /**
     * 보상 트랜잭션: 재고 복구
     * @return 모든 재고 복구 성공 시 true, 하나라도 실패 시 false
     */
    private boolean rollbackStockDecrease(OrderDto orderDto, List<String> decreasedProductIds, Map<String, CatalogResponse> catalogMap) {
        boolean allSuccess = true;

        for (String productId : decreasedProductIds) {
            try {
                OrderItemDto itemDto = orderDto.getOrderItems().stream()
                        .filter(item -> item.getProductId().equals(productId))
                        .findFirst()
                        .orElseThrow();

                catalogClient.increaseStock(productId, itemDto.getQty());
                log.info("재고 복구 완료: productId={}, quantity={}", productId, itemDto.getQty());
            } catch (Exception rollbackException) {
                allSuccess = false;
                log.error("재고 복구 실패: productId={}, error={}", productId, rollbackException.getMessage());
            }
        }

        return allSuccess;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByUserId(String userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        return orders.stream()
                .map(OrderResponse::from)
                .toList();
    }

}
