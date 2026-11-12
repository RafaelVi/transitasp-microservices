package br.com.fiap.ms.ocorrencias.client;

import br.com.fiap.ms.ocorrencias.client.dto.AlterarStatusLinhaRequestDTO;
import br.com.fiap.ms.ocorrencias.client.dto.LinhaClientResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Cliente síncrono do ms-linhas.
 * Não há URL fixa: o nome "ms-linhas" é resolvido no Eureka e as chamadas são
 * distribuídas entre as instâncias pelo Spring Cloud LoadBalancer. Cada chamada passa
 * por um Circuit Breaker; em caso de falha, quem responde é o LinhaClientFallbackFactory.
 */
@FeignClient(name = "ms-linhas", path = "/api/v1/linhas", fallbackFactory = LinhaClientFallbackFactory.class)
public interface LinhaClient {

    @GetMapping("/{id}")
    LinhaClientResponseDTO buscarPorId(@PathVariable("id") Long id);

    @PutMapping("/{id}/status")
    LinhaClientResponseDTO alterarStatus(@PathVariable("id") Long id, @RequestBody AlterarStatusLinhaRequestDTO request);
}
