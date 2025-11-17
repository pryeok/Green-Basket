package orderservice.service.order;

import com.greenbasket.common.idgenerator.IdGenerator;
import com.greenbasket.common.snowflake.Snowflake;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final Snowflake snowflake;
    private final OrderRepository orderRepository;
    private final CatalogServiceClient catalogClient;

    private static final int MAX_RETRY_COUNT = 3;
    private static final long RETRY_DELAY_MS = 100;

    @Override
    @Transactional
    public OrderResponse createOrder(OrderDto orderDto) {
        List<String> productIds = orderDto.getOrderItems().stream()
                .map(OrderItemDto::getProductId)
                .toList();

        List<CatalogResponse> catalogs = catalogClient.getProducts(productIds);
        Map<String, CatalogResponse> catalogMap = catalogs.stream()
                .collect(Collectors.toMap(CatalogResponse::getProductId, catalog -> catalog));

        List<String> decreasedProductIds = new ArrayList<>();
        try {
            for (OrderItemDto itemDto : orderDto.getOrderItems()) {
                CatalogResponse catalog = catalogMap.get(itemDto.getProductId());
                decreaseStockWithRetry(catalog.getProductId(), itemDto.getQty());
                decreasedProductIds.add(catalog.getProductId());
            }
        } catch (OutOfStockException e) {
            throw e;
        } catch (Exception e) {
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
                orderDto.getUserId()
        );

        orderDto.getOrderItems().forEach(itemDto -> {
            CatalogResponse catalog = catalogMap.get(itemDto.getProductId());
            OrderItem orderItem = OrderItem.create(
                    snowflake.nextId(),
                    order,
                    catalog.getProductId(),
                    catalog.getProductName(),
                    itemDto.getQty(),
                    catalog.getUnitPrice()
            );
            order.addOrderItem(orderItem);
        });

        Order savedOrder = orderRepository.save(order);
        return OrderResponse.from(savedOrder);
    }

    /**
     * 낙관적 락 재시도 로직
     */
    private void decreaseStockWithRetry(String productId, Integer quantity) {
        int retryCount = 0;
        while (retryCount < MAX_RETRY_COUNT) {
            try {
                catalogClient.decreaseStock(productId, quantity);
                return;
            } catch (ResponseStatusException e) {
                if (e.getStatusCode() == HttpStatus.CONFLICT) {
                    // 재고 부족인 경우 - 재시도하지 않고 바로 던지기
                    String reason = e.getReason();
                    if (reason != null && reason.contains("재고")) {
                        throw new OutOfStockException(reason);
                    }

                    // 낙관적 락 충돌인 경우 - 재시도
                    if (retryCount < MAX_RETRY_COUNT - 1) {
                        retryCount++;
                        log.warn("재고 차감시 낙관적 락 충돌 발생, 재시도 {}/{}", retryCount, MAX_RETRY_COUNT);
                        try {
                            Thread.sleep(RETRY_DELAY_MS);
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            throw new OrderCreationFailedException("재시도 중 인터럽트 발생");
                        }
                    } else {
                        throw new OrderCreationFailedException("재고 차감 실패: 최대 재시도 횟수 초과");
                    }
                } else {
                    throw new OrderCreationFailedException("재고 차감 실패: " + e.getMessage());
                }
            } catch (Exception e) {
                log.error("예상치 못한 예외 발생 - productId: {}, type: {}, message: {}", productId, e.getClass().getName(), e.getMessage(), e);
                throw new OrderCreationFailedException("재고 차감 실패: " + e.getMessage());
            }
        }
    }

    /**
     * 보상 트랜잭션: 재고 복구
     * @return 모든 재고 복구 성공 시 true, 하나라도 실패 시 false
     */
    private boolean rollbackStockDecrease(OrderDto orderDto, List<String> decreasedProductIds, Map<String, CatalogResponse> catalogMap) {
        boolean allSuccess = true;

        for (String productId : decreasedProductIds) {
            try {
                // 복구할 수량 찾기
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
