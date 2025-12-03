package hotcatalogservice.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class NewHotCatalogListRepository {
    private final StringRedisTemplate redisTemplate;

    // hot-catalog::new-list::{yyyyMMdd} - 그 주의 월요일 날짜
    // 예: 2025-12-02(월) ~ 2025-12-08(일) → hot-catalog::new-list::20251202
    // 이번 주에 생성된 신상품 중 인기 있는 catalog
    private static final String KEY_FORMAT = "hot-catalog::new-list::%s";

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    public void add(Long catalogId, LocalDateTime time, Long score, Long limit, Duration ttl) {
        redisTemplate.executePipelined((RedisCallback<?>) action -> {
            StringRedisConnection conn = (StringRedisConnection) action;
            String key = generateKey(time);
            conn.zAdd(key, score, String.valueOf(catalogId));
            conn.zRemRange(key, 0, - limit - 1);
            conn.expire(key, ttl.toSeconds());
            return null;
        } );
    }

    public void remove(Long catalogId, LocalDateTime time) {
        redisTemplate.opsForZSet().remove(generateKey(time), String.valueOf(catalogId));
    }

    private String generateKey(LocalDateTime time) {
        // 해당 날짜가 속한 주의 월요일 날짜를 키로 사용
        LocalDateTime startOfWeek = time.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        return generateKey(TIME_FORMATTER.format(startOfWeek));
    }

    private String generateKey(String dateStr) {
        return KEY_FORMAT.formatted(dateStr);
    }

    public List<Long> readAll(String dateStr) {
        return redisTemplate.opsForZSet()
                .reverseRangeWithScores(generateKey(dateStr), 0, -1).stream()
                .peek(tuple ->
                        log.info("[NewHotCatalogListRepository.readAll] catalogId={}, score={}", tuple.getValue(), tuple.getScore()))
                .map(ZSetOperations.TypedTuple::getValue)
                .map(Long::valueOf)
                .toList();
    }
}
