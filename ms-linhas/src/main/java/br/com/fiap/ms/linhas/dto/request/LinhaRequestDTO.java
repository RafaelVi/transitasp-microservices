package br.com.fiap.ms.linhas.dto.request;

import br.com.fiap.ms.linhas.entities.TipoLinha;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para cadastrar ou atualizar uma linha. O status não entra aqui: ele só muda pelo endpoint de status.")
public record LinhaRequestDTO(

        @Schema(description = "Número da linha", example = "6")
        @NotNull(message = "O número da linha é obrigatório")
        @Min(value = 1, message = "O número da linha deve ser no mínimo 1")
        @Max(value = 99, message = "O número da linha deve ser no máximo 99")
        Integer numero,

        @Schema(description = "Nome da linha", example = "Laranja")
        @NotBlank(message = "O nome da linha é obrigatório")
        @Size(max = 60, message = "O nome deve ter no máximo 60 caracteres")
        String nome,

        @Schema(description = "Tipo da linha", example = "METRO")
        @NotNull(message = "O tipo da linha é obrigatório (METRO ou TREM)")
        TipoLinha tipo,

        @Schema(description = "Cor da linha em hexadecimal", example = "#F58220")
        @NotBlank(message = "A cor da linha é obrigatória")
        @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "A cor deve estar no formato #RRGGBB")
        String corHex
) {
}
