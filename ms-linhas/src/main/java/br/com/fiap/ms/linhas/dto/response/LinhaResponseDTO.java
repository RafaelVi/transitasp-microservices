package br.com.fiap.ms.linhas.dto.response;

import br.com.fiap.ms.linhas.entities.StatusLinha;
import br.com.fiap.ms.linhas.entities.TipoLinha;

import java.time.LocalDateTime;

public record LinhaResponseDTO(
        Long id,
        Integer numero,
        String nome,
        TipoLinha tipo,
        String corHex,
        StatusLinha status,
        LocalDateTime atualizadaEm
) {
}
