package apigateway.config;

import com.greenbasket.common.jwt.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {

    @Value("${token.secret}")
    private String secretKey;

    @Value("${token.access-expiration-time}")
    private long accessExpirationTime;

    @Value("${token.refresh-expiration-time}")
    private long refreshExpirationTime;

    @Bean
    public JwtUtil jwtUtil() {
        return new JwtUtil(secretKey, accessExpirationTime, refreshExpirationTime);
    }
}
