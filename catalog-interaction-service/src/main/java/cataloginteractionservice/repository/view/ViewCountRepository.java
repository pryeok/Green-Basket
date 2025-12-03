package cataloginteractionservice.repository.view;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ViewCountRepository {
    private final StringRedisTemplate redisTemplate;

    private static final String KEY_FORMAT = "view::catalog::%s::view_count";

    public Long read(Long catalogId) {
        String result = redisTemplate.opsForValue().get(generateKey(catalogId));
        return result == null ? 0L : Long.valueOf(result);
    }

    public Long increase(Long catalogId) {
        return redisTemplate.opsForValue().increment(generateKey(catalogId));
    }

    private String generateKey(Long catalogId) {
        return KEY_FORMAT.formatted(catalogId);
    }


}
