package br.com.fiap.ms.ocorrencias;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MsOcorrenciasApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsOcorrenciasApplication.class, args);
    }
}
