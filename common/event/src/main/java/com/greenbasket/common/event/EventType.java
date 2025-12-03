package com.greenbasket.common.event;

import com.greenbasket.common.event.payload.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@RequiredArgsConstructor
public enum EventType {
    ORDER_CREATED(OrderCreatedEventPayload.class, Topic.GREEN_BASKET_ORDER),
    ORDER_DELETED(OrderDeletedEventPayload.class, Topic.GREEN_BASKET_ORDER),
    CATALOG_CREATED(CatalogCreatedEventPayload.class, Topic.GREEN_BASKET_CATALOG),
    CATALOG_DELETED(CatalogCreatedEventPayload.class, Topic.GREEN_BASKET_CATALOG),
    CATALOG_LIKED(CatalogLikedEventPayload.class, Topic.GREEN_BASKET_CATALOG_LIKE),
    CATALOG_UNLIKED(CatalogUnlikedEventPayload.class, Topic.GREEN_BASKET_CATALOG_LIKE),
    CATALOG_VIEWED(CatalogViewedEventPayload.class, Topic.GREEN_BASKET_CATALOG_VIEW);

    private final Class<? extends EventPayload> payloadClass;
    private final String topic;

    public static EventType from(String type) {
        try {
            return valueOf(type);
        } catch (Exception e) {
            log.error("[EventType.from] type={}", type, e);
            return null;
        }
    }

    public static class Topic {
        public static final String GREEN_BASKET_ORDER = "green-basket-order";
        public static final String GREEN_BASKET_CATALOG = "green-basket-catalog";
        public static final String GREEN_BASKET_CATALOG_LIKE = "green-basket-catalog-like";
        public static final String GREEN_BASKET_CATALOG_VIEW = "green-basket-catalog-view";
    }
}
