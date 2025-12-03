package com.greenbasket.common.event.payload;

import com.greenbasket.common.event.EventPayload;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDeletedEventPayload implements EventPayload {
    private Long id;
    private String orderId;
    private String userId;
    private Integer totalPrice;
    private Long userPk;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder.Default
    private List<OrderItemPayload> orderItems = new ArrayList<>();

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemPayload {
        private Long catalogId;
        private String productId;
        private String productName;
        private Integer qty;
        private Integer unitPrice;
        private Integer totalPrice;

        /**
         * 정적 팩토리 메서드 - OrderItem 필드들로부터 생성
         */
        public static OrderItemPayload from(Long catalogId, String productId, String productName,
                                           Integer qty, Integer unitPrice, Integer totalPrice) {
            return OrderItemPayload.builder()
                .catalogId(catalogId)
                .productId(productId)
                .productName(productName)
                .qty(qty)
                .unitPrice(unitPrice)
                .totalPrice(totalPrice)
                .build();
        }
    }
}
