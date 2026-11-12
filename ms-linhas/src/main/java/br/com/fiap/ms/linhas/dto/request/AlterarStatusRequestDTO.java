package br.com.fiap.ms.linhas.dto.request;

import br.com.fiap.ms.linhas.entities.StatusLinha;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Novo status da linha")
public record AlterarStatusRequestDTO(

        @Schema(description = "Status da linha", example = "VELOCIDADE_REDUZIDA")
        @NotNull(message = "O status é obrigatório (NORMAL, VELOCIDADE_REDUZIDA ou PARALISADA)")
        StatusLinha status
) {
}
