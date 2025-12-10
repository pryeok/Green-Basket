package catalogreadservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

/**
 * 카테고리별 Catalog 개수 저장/조회 Repository
 * Key Format: catalog-read::category::{categoryId}::count
 */
@Repository
@RequiredArgsConstructor
public class CategoryCatalogCountRepository {
    private final StringRedisTemplate redisTemplate;

    private static final String KEY_FORMAT = "catalog-read::category::%s::count";
    private static final Duration DEFAULT_TTL = Duration.ofDays(1);

    /**
     * 카테고리별 Catalog 개수 저장/업데이트
     */
    public void createOrUpdate(Long categoryId, Long count) {
        redisTemplate.opsForValue()
                .set(generateKey(categoryId), String.valueOf(count), DEFAULT_TTL);
    }

    /**
     * 카테고리별 Catalog 개수 조회
     */
    public Long read(Long categoryId) {
        String value = redisTemplate.opsForValue().get(generateKey(categoryId));
        return value != null ? Long.parseLong(value) : null;
    }

    /**
     * 카테고리별 Catalog 개수 증가 (상품 생성 시)
     */
    public void increment(Long categoryId) {
        redisTemplate.opsForValue().increment(generateKey(categoryId));
    }

    /**
     * 카테고리별 Catalog 개수 감소 (상품 삭제 시)
     */
    public void decrement(Long categoryId) {
        redisTemplate.opsForValue().decrement(generateKey(categoryId));
    }

    /**
     * 카테고리별 Catalog 개수 삭제
     */
    public void delete(Long categoryId) {
        redisTemplate.delete(generateKey(categoryId));
    }

    private String generateKey(Long categoryId) {
        return KEY_FORMAT.formatted(categoryId);
    }
}
