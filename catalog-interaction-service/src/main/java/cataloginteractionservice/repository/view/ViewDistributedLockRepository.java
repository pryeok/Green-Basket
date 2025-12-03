package cataloginteractionservice.repository.view;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class ViewDistributedLockRepository {
    private final StringRedisTemplate redisTemplate;

    private static final String KEY_FORMAT = "view::catalog::%s::user::%s::lock";

    public boolean lock(Long catalogId, String userId, Duration ttl) {
        String key = generateKey(catalogId, userId);
        return redisTemplate.opsForValue().setIfAbsent(key, "", ttl);
    }

    private String generateKey(Long catalogId, String userId) {
        return KEY_FORMAT.formatted(catalogId, userId);
    }
}
