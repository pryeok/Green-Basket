package orderservice.config;

import feign.codec.ErrorDecoder;
import orderservice.error.FeignErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class FeignConfig {

    @Bean
    public ErrorDecoder errorDecoder(Environment env) {
        return new FeignErrorDecoder(env);
    }
}
