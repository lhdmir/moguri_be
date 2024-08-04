package ync.likelion.moguri_be.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info; // 올바른 Info 클래스 import
import io.swagger.v3.oas.models.info.Contact; // Contact 추가
import io.swagger.v3.oas.models.info.License; // License 추가
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(apiInfo());
    }

    private Info apiInfo() {
        return new Info()
                .title("Springdoc 테스트")
                .description("Springdoc을 사용한 Swagger UI 테스트")
                .version("1.0.0")
                .contact(new Contact()
                        .name("개발자 이름")
                        .url("http://example.com")
                        .email("developer@example.com"))
                .license(new License()
                        .name("MIT License")
                        .url("https://opensource.org/licenses/MIT"));
    }
}
