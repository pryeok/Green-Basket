package hotcatalogservice.repository;


import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class CatalogViewCountRepository {
    private final StringRedisTemplate redisTemplate;

    // hot-catalog::catalog::{catalogId}::view-count
    private static final String KEY_FORMAT = "hot-catalog::catalog::%s::view-count";

    public void createOrUpdate(Long catalogId, Long viewCount, Duration ttl) {
        redisTemplate.opsForValue().set(generateKey(catalogId), String.valueOf(viewCount), ttl);
    }

    public Long read(Long catalogId) {
        String result = redisTemplate.opsForValue().get(generateKey(catalogId));
        return result == null ? 0L : Long.valueOf(result);
    }

    private String generateKey(Long catalogId) {
        return KEY_FORMAT.formatted(catalogId);
    }
}
