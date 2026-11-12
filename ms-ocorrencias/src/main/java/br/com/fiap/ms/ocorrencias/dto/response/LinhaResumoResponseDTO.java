package br.com.fiap.ms.ocorrencias.dto.response;

/** Dados da linha obtidos do ms-linhas por comunicação síncrona. */
public record LinhaResumoResponseDTO(
        Long id,
        Integer numero,
        String nome,
        String status
) {
}
