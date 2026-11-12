package br.com.fiap.ms.ocorrencias.client;

import br.com.fiap.ms.ocorrencias.client.dto.AlterarStatusLinhaRequestDTO;
import br.com.fiap.ms.ocorrencias.client.dto.LinhaClientResponseDTO;
import br.com.fiap.ms.ocorrencias.exceptions.ResourceNotFoundException;
import br.com.fiap.ms.ocorrencias.exceptions.ServiceUnavailableException;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * Fallback do LinhaClient. É acionado quando a chamada falha, estoura o tempo limite
 * ou o Circuit Breaker está aberto.
 * - Se o ms-linhas respondeu 404, a linha não existe: repassa como "não encontrada".
 * - Em qualquer outro caso o serviço está indisponível: lança ServiceUnavailableException,
 *   que a camada Service trata (resposta degradada na consulta, HTTP 503 na escrita).
 */
@Component
public class LinhaClientFallbackFactory implements FallbackFactory<LinhaClient> {

    private static final Logger log = LoggerFactory.getLogger(LinhaClientFallbackFactory.class);

    @Override
    public LinhaClient create(Throwable causa) {
        return new LinhaClient() {

            @Override
            public LinhaClientResponseDTO buscarPorId(Long id) {
                throw traduzir(causa, id);
            }

            @Override
            public LinhaClientResponseDTO alterarStatus(Long id, AlterarStatusLinhaRequestDTO request) {
                throw traduzir(causa, id);
            }
        };
    }

    private RuntimeException traduzir(Throwable causa, Long linhaId) {
        FeignException erroFeign = procurarErroFeign(causa);
        if (erroFeign != null && erroFeign.status() == 404) {
            return new ResourceNotFoundException("Linha com id " + linhaId + " não encontrada no ms-linhas");
        }
        log.warn("Fallback do ms-linhas acionado (linha {}): {}", linhaId, String.valueOf(causa));
        return new ServiceUnavailableException(
                "O ms-linhas está indisponível no momento. Tente novamente em instantes");
    }

    /** A falha original pode vir encapsulada; percorre as causas até achar o erro do Feign. */
    private FeignException procurarErroFeign(Throwable causa) {
        Throwable atual = causa;
        int profundidade = 0;
        while (atual != null && profundidade < 10) {
            if (atual instanceof FeignException erroFeign) {
                return erroFeign;
            }
            atual = atual.getCause();
            profundidade++;
        }
        return null;
    }
}
