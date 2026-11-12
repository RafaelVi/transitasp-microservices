package br.com.fiap.ms.linhas.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/** Como a porta é aleatória, mostra no console a porta desta instância e o endereço do Swagger no gateway. */
@Component
public class EnderecoSwaggerLogger {

    private static final Logger log = LoggerFactory.getLogger(EnderecoSwaggerLogger.class);

    private final Environment environment;

    public EnderecoSwaggerLogger(Environment environment) {
        this.environment = environment;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void mostrarEndereco() {
        String porta = environment.getProperty("local.server.port");
        log.info("Instância no ar na porta {}. Swagger pelo API Gateway: http://localhost:8082/swagger-ui/index.html", porta);
    }
}
