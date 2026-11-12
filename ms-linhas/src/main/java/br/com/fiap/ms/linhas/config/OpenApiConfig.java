package br.com.fiap.ms.linhas.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("TransitaSP - Microsserviço de Linhas")
                        .description("API responsável pelo cadastro e pelo status das linhas de metrô e trem. Protótipo acadêmico com dados simulados.")
                        .version("v1"))
                // As chamadas do Swagger passam pelo API Gateway: http://localhost:8082/ms-linhas/...
                .servers(List.of(
                        new Server().url("/ms-linhas")
                ));
    }
}
