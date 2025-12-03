package hotcatalogservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Repository
@RequiredArgsConstructor
public class CatalogCreatedTimeRepository {
    private final StringRedisTemplate redisTemplate;

    // hot-catalog::catalog::{catalogId}::created-time
    private static final String KEY_FORMAT = "hot-catalog::catalog::%s::created-time";

    public void createOrUpdate(Long catalogId, LocalDateTime createdAt, Duration ttl) {
        redisTemplate.opsForValue().set(
                generateKey(catalogId),
                String.valueOf(createdAt.toInstant(ZoneOffset.UTC).toEpochMilli()),
                ttl
        );

    }

    public void deleted(Long catalogId) {
        redisTemplate.delete(generateKey(catalogId));
    }

    public LocalDateTime read(Long catalogId) {
        String result = redisTemplate.opsForValue().get(generateKey(catalogId));
        if (result == null) {
            return null;
        }
        return LocalDateTime.ofInstant(
                Instant.ofEpochMilli(Long.valueOf(result)), ZoneOffset.UTC
        );
    }

    private String generateKey(Long catalogId) {
        return KEY_FORMAT.formatted(catalogId);
    }
}
