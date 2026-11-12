package br.com.fiap.transitasp.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Rotas do gateway: http://localhost:8082/<nome-do-servico>/... vai para o microsserviço
 * de mesmo nome. O prefixo "lb://" faz o gateway descobrir as instâncias no Eureka e
 * distribuir as chamadas entre elas (Load Balancer), sem nenhuma URL fixa.
 * O stripPrefix(1) retira o nome do serviço do caminho antes de encaminhar.
 */
@Configuration
public class RotasConfig {

    @Bean
    public RouteLocator rotas(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("ms-linhas", r -> r
                        .path("/ms-linhas/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://ms-linhas"))
                .route("ms-ocorrencias", r -> r
                        .path("/ms-ocorrencias/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://ms-ocorrencias"))
                .build();
    }
}
