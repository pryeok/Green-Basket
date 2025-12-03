package orderservice.controller;

import lombok.RequiredArgsConstructor;
import orderservice.dto.OrderDto;
import orderservice.service.order.OrderService;
import orderservice.service.order.request.OrderCreateRequest;
import orderservice.service.order.response.OrderResponse;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/orders")
    public OrderResponse createOrder(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Pk") Long userPk,
            @RequestBody OrderCreateRequest orderCreateRequest) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        OrderDto orderDto = mapper.map(orderCreateRequest, OrderDto.class);
        orderDto.setUserId(userId);
        orderDto.setUserPk(userPk);
        return orderService.createOrder(orderDto);
    }

    @DeleteMapping("/orders/{orderId}")
    public void delete(
            @PathVariable("orderId") String orderId,
            @RequestHeader("X-User-Id") String userId) {
        orderService.delete(orderId);
    }

    @GetMapping("/orders")
    public List<OrderResponse> getOrders(@RequestHeader("X-User-Id") String userId) {
        return orderService.getOrdersByUserId(userId);
    }

}
