package userservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/redis-test")
@RequiredArgsConstructor
public class RedisTestController {

    private final StringRedisTemplate redisTemplate;

    @GetMapping("/ping")
    public String testRedis() {
        try {
            // Redis 연결 정보 출력
            String connectionInfo = redisTemplate.getConnectionFactory().getConnection().toString();
            log.info("Redis 연결 정보: {}", connectionInfo);

            // Redis에 테스트 값 저장
            redisTemplate.opsForValue().set("test_key", "test_value", 60, TimeUnit.SECONDS);
            log.info("Redis SET 성공: test_key = test_value");

            // Redis에서 값 조회
            String value = redisTemplate.opsForValue().get("test_key");
            log.info("Redis GET 성공: test_key = {}", value);

            return "Redis 연결 성공! 연결: " + connectionInfo + ", 값: " + value;
        } catch (Exception e) {
            log.error("Redis 연결 실패: {}", e.getMessage(), e);
            return "Redis 연결 실패: " + e.getMessage();
        }
    }
}
