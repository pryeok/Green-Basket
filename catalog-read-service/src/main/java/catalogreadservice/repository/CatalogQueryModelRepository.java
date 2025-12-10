package catalogreadservice.repository;

import catalogreadservice.model.CatalogQueryModel;
import com.greenbasket.common.dataserializer.DataSerializer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

/**
 * CatalogQueryModel Redis Repository
 * Key Format: catalog-read::catalog::{productId}
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class CatalogQueryModelRepository {
    private final StringRedisTemplate redisTemplate;

    private static final String KEY_FORMAT = "catalog-read::catalog::%s";

    /**
     * Read Model 생성 (TTL 지정) - productId 기반
     */
    public void create(CatalogQueryModel catalogQueryModel, Duration ttl) {
        redisTemplate.opsForValue()
                .set(generateKey(catalogQueryModel), DataSerializer.serialize(catalogQueryModel), ttl);
    }

    /**
     * Read Model 업데이트 (키가 존재할 때만) - productId 기반
     */
    public void update(CatalogQueryModel catalogQueryModel) {
        redisTemplate.opsForValue()
                .setIfPresent(generateKey(catalogQueryModel), DataSerializer.serialize(catalogQueryModel));
    }

    /**
     * Read Model 삭제 - productId 기반
     */
    public void delete(String productId) {
        redisTemplate.delete(generateKey(productId));
    }

    /**
     * 단일 Read Model 조회 - productId 기반
     */
    public Optional<CatalogQueryModel> read(String productId) {
        return Optional.ofNullable(
                redisTemplate.opsForValue().get(generateKey(productId))
        ).map(json -> DataSerializer.deserialize(json, CatalogQueryModel.class));
    }

    /**
     * 여러 Read Model 배치 조회 (catalogId 목록 기반)
     * TODO: 비효율적 - productId 목록 기반으로 변경 권장
     */
    public Map<Long, CatalogQueryModel> readAll(List<Long> catalogIds) {
        // 모든 catalog-read::catalog::* 키 스캔 (비효율적)
        // TODO: productId 목록을 받는 방식으로 변경 필요
        log.warn("[CatalogQueryModelRepository.readAll] catalogId 기반 배치 조회는 비효율적");
        return Map.of();  // 임시: 빈 맵 반환
    }

    private String generateKey(CatalogQueryModel catalogQueryModel) {
        return generateKey(catalogQueryModel.getProductId());
    }

    private String generateKey(String productId) {
        return KEY_FORMAT.formatted(productId);
    }
}
