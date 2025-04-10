package sem4.ea.ss25.battleship.assignment2.api_gateway.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI battleshipOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Battleship API Gateway")
                        .description("API Gateway for Battleship Microservices")
                        .version("1.0")
                        .contact(new Contact()
                                .name("Battleship Team")
                                .email("battleship@example.com")));
    }
}
