package orderservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EntityScan(basePackages = {
        "orderservice",
        "com.greenbasket.common.outboxmessagerelay"
})
@EnableJpaRepositories(basePackages = {
        "orderservice",
        "com.greenbasket.common.outboxmessagerelay"
})
@SpringBootApplication
@EnableJpaAuditing
@EnableFeignClients
public class OrderServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}
