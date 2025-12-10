package catalogreadservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.Limit;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

/**
 * 카테고리별 Catalog ID 목록 저장/조회 Repository
 * Key Format: catalog-read::category::{categoryId}::catalog-ids
 * Sorted Set으로 관리 (최신순 정렬)
 */
@Repository
@RequiredArgsConstructor
public class CatalogIdListRepository {
    private final StringRedisTemplate redisTemplate;

    // catalog-read::board::{boardId}::catalog-list
    private static final String KEY_FORMAT = "catalog-read::board::%s::catalog-list";

    public void add(Long categoryId, Long catalogId, Long limit) {
        redisTemplate.executePipelined((RedisCallback<?>) action -> {
            StringRedisConnection conn = (StringRedisConnection) action;
            String key = generateKey(categoryId);
            conn.zAdd(key, 0, toPaddedString(catalogId));
            conn.zRemRange(key, 0, - limit - 1);
            return null;
        });
    }

    public void delete(Long categoryId, Long catalogId) {
        redisTemplate.opsForZSet().remove(generateKey(categoryId), toPaddedString(catalogId));
    }

    public List<Long> readAll(Long categoryId, Long offset, Long limit) {
        return redisTemplate.opsForZSet()
                .reverseRange(generateKey(categoryId), offset, offset + limit - 1)
                .stream().map(Long::valueOf).toList();
    }

//    public List<Long> readAllInfiniteScroll(Long boardId, Long lastArticleId, Long limit) {
//        return redisTemplate.opsForZSet().reverseRangeByLex(
//                generateKey(boardId),
//                lastArticleId == null ?
//                        Range.unbounded() :
//                        Range.leftUnbounded(Range.Bound.exclusive(toPaddedString(lastArticleId))),
//                Limit.limit().count(limit.intValue())
//        ).stream().map(Long::valueOf).toList();
//    }

    private String toPaddedString(Long catalogId) {
        return "%019d".formatted(catalogId);
        // 1234 -> 0000000000000001234
    }

    private String generateKey(Long categoryId) {
        return KEY_FORMAT.formatted(categoryId);
    }
}
