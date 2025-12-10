package catalogreadservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Catalog Read Service Application (CQRS Read Model)
 * - Spring Cloud OpenFeign 활성화
 * - Eureka Client 자동 활성화 (spring-cloud-starter-netflix-eureka-client 의존성)
 */
@SpringBootApplication
@EnableFeignClients
public class CatalogReadServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(CatalogReadServiceApplication.class, args);
    }
}
