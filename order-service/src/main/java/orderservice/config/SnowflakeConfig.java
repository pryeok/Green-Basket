package orderservice.config;


import com.greenbasket.common.snowflake.Snowflake;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SnowflakeConfig {
    @Bean
    public Snowflake snowflake() {
        return new Snowflake();
    }
}
