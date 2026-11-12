package br.com.fiap.ms.ocorrencias.config;

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
                        .title("TransitaSP - Microsserviço de Ocorrências")
                        .description("API responsável pelas ocorrências nas linhas. Abrir, alterar ou encerrar uma ocorrência muda o status da linha no ms-linhas. Protótipo acadêmico com dados simulados.")
                        .version("v1"))
                // As chamadas do Swagger passam pelo API Gateway: http://localhost:8082/ms-ocorrencias/...
                .servers(List.of(
                        new Server().url("/ms-ocorrencias")
                ));
    }
}
