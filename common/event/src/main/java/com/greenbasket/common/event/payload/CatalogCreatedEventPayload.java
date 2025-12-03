package com.greenbasket.common.event.payload;

import com.greenbasket.common.event.EventPayload;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CatalogCreatedEventPayload implements EventPayload {
    private Long id;
    private String productId;
    private String productName;
    private Integer stock;
    private Integer unitPrice;
    private String userId;
    private Long categoryId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
