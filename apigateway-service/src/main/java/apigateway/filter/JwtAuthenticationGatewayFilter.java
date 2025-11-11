package apigateway.filter;

import com.greenbasket.common.jwt.JwtUtil;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class JwtAuthenticationGatewayFilter extends AbstractGatewayFilterFactory<JwtAuthenticationGatewayFilter.Config> {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationGatewayFilter(JwtUtil jwtUtil) {
        super(Config.class);
        this.jwtUtil = jwtUtil;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            // Authorization 헤더 확인
            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return onError(exchange, "Missing Authorization header", HttpStatus.UNAUTHORIZED);
            }

            String authorizationHeader = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);

            if (!authorizationHeader.startsWith("Bearer ")) {
                return onError(exchange, "Invalid Authorization header format", HttpStatus.UNAUTHORIZED);
            }

            String token = authorizationHeader.substring(7);

            try {
                // JWT 검증
                if (!jwtUtil.validateToken(token)) {
                    return onError(exchange, "Invalid token", HttpStatus.UNAUTHORIZED);
                }

                // userId, role 추출
                String userId = jwtUtil.getUserIdFromToken(token);
                String role = jwtUtil.getRoleFromToken(token);

                // 내부 서비스로 전달할 Header 추가
                ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                        .header("X-User-Id", userId)
                        .header("X-User-Role", role)
                        .build();

                log.info("JWT 검증 성공: userId={}, role={}", userId, role);

                return chain.filter(exchange.mutate().request(modifiedRequest).build());

            } catch (JwtException e) {
                log.error("JWT 검증 실패: {}", e.getMessage());
                return onError(exchange, e.getMessage(), HttpStatus.UNAUTHORIZED);
            }
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        log.error("JWT Authentication Error: {}", message);
        return response.setComplete();
    }

    public static class Config {
        // Configuration properties if needed
    }
}
