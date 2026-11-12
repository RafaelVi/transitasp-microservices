package br.com.fiap.ms.ocorrencias.dto.request;

import br.com.fiap.ms.ocorrencias.entities.ImpactoLinha;
import br.com.fiap.ms.ocorrencias.entities.TipoOcorrencia;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para abrir uma ocorrência em uma linha")
public record OcorrenciaRequestDTO(

        @Schema(description = "Id da linha no ms-linhas", example = "1")
        @NotNull(message = "O id da linha é obrigatório")
        @Positive(message = "O id da linha deve ser positivo")
        Long linhaId,

        @Schema(description = "Tipo da ocorrência", example = "FALHA_TECNICA")
        @NotNull(message = "O tipo da ocorrência é obrigatório")
        TipoOcorrencia tipo,

        @Schema(description = "O que aconteceu", example = "Falha em trem na plataforma")
        @NotBlank(message = "A descrição é obrigatória")
        @Size(max = 255, message = "A descrição deve ter no máximo 255 caracteres")
        String descricao,

        @Schema(description = "Estação ou trecho afetado", example = "Estação Sé")
        @NotBlank(message = "O local é obrigatório")
        @Size(max = 80, message = "O local deve ter no máximo 80 caracteres")
        String local,

        @Schema(description = "Efeito sobre a linha; vira o status dela no ms-linhas", example = "VELOCIDADE_REDUZIDA")
        @NotNull(message = "O impacto é obrigatório (VELOCIDADE_REDUZIDA ou PARALISADA)")
        ImpactoLinha impacto
) {
}
