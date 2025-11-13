package com.greenbasket.common.idgenerator;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class IdGenerator {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    /**
     * Product ID 생성
     * 형식: PRD-yyyyMMdd-UUID8
     * 예: PRD-20251113-A1B2C3D4
     */
    public static String generateProductId() {
        String datePrefix = LocalDate.now().format(DATE_FORMATTER);
        String uuid = generateUuid8();
        return "PRD-" + datePrefix + "-" + uuid;
    }

    /**
     * Order ID 생성
     * 형식: ORD-yyyyMMdd-UUID8
     * 예: ORD-20251113-E5F6G7H8
     */
    public static String generateOrderId() {
        String datePrefix = LocalDate.now().format(DATE_FORMATTER);
        String uuid = generateUuid8();
        return "ORD-" + datePrefix + "-" + uuid;
    }

    /**
     * UUID 8자리 생성 (대문자)
     */
    private static String generateUuid8() {
        return UUID.randomUUID().toString()
                .replace("-", "")
                .substring(0, 8)
                .toUpperCase();
    }
}
