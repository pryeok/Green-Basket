package hotcatalogservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderCountRepository {
    private final StringRedisTemplate redisTemplate;

    // hot-catalog::order-count::{weekKey}
    // Hash: field(catalogId) → value(count)
    private static final String KEY_FORMAT = "hot-catalog::order-count::%s";
    private static final DateTimeFormatter WEEK_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    // Pipeline + Hash로 여러 catalog 한 번에 증가
    public void incrementBatch(List<String> catalogIds, LocalDateTime time, Duration ttl) {
        String weekKey = generateWeekKey(time);
        String hashKey = KEY_FORMAT.formatted(weekKey);

        redisTemplate.executePipelined((RedisCallback<?>) action -> {
            StringRedisConnection conn = (StringRedisConnection) action;

            for (String catalogId : catalogIds) {
                conn.hIncrBy(hashKey, catalogId, 1);
            }
            conn.expire(hashKey, ttl.toSeconds());
            return null;
        });
    }

    // Pipeline + Hash로 여러 catalog 한 번에 감소
    public void decrementBatch(List<String> catalogIds, LocalDateTime time, Duration ttl) {
        String weekKey = generateWeekKey(time);
        String hashKey = KEY_FORMAT.formatted(weekKey);

        redisTemplate.executePipelined((RedisCallback<?>) action -> {
            StringRedisConnection conn = (StringRedisConnection) action;

            for (String catalogId : catalogIds) {
                conn.hIncrBy(hashKey, catalogId, -1);  // -1로 감소
            }
            conn.expire(hashKey, ttl.toSeconds());
            return null;
        });
    }

    public Long read(Long catalogId, LocalDateTime time) {
        String weekKey = generateWeekKey(time);
        String hashKey = KEY_FORMAT.formatted(weekKey);
        Object result = redisTemplate.opsForHash().get(hashKey, String.valueOf(catalogId));
        return result == null ? 0L : Long.valueOf(result.toString());
    }

    private String generateWeekKey(LocalDateTime time) {
        // 해당 주의 월요일 날짜를 키로 사용
        LocalDateTime startOfWeek = time.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        return WEEK_FORMATTER.format(startOfWeek);
    }
}
