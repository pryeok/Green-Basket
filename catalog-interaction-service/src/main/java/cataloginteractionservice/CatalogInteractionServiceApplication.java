package cataloginteractionservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EntityScan(basePackages = {
        "cataloginteractionservice",
        "com.greenbasket.common.outboxmessagerelay"
})
@EnableJpaRepositories(basePackages = {
        "cataloginteractionservice",
        "com.greenbasket.common.outboxmessagerelay"
})

@SpringBootApplication
@EnableJpaAuditing
@EnableFeignClients
public class CatalogInteractionServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(CatalogInteractionServiceApplication.class, args);
    }
}
