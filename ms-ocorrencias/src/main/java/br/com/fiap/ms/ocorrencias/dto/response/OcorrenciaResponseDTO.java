package br.com.fiap.ms.ocorrencias.dto.response;

import br.com.fiap.ms.ocorrencias.entities.ImpactoLinha;
import br.com.fiap.ms.ocorrencias.entities.StatusOcorrencia;
import br.com.fiap.ms.ocorrencias.entities.TipoOcorrencia;

import java.time.LocalDateTime;

public record OcorrenciaResponseDTO(
        Long id,
        Long linhaId,
        TipoOcorrencia tipo,
        String descricao,
        String local,
        ImpactoLinha impacto,
        StatusOcorrencia status,
        LocalDateTime abertaEm,
        LocalDateTime encerradaEm
) {
}
