package br.com.fiap.ms.ocorrencias.client.dto;

import java.time.LocalDateTime;

/** Resposta do ms-linhas (mesmos campos do LinhaResponseDTO de lá). */
public record LinhaClientResponseDTO(
        Long id,
        Integer numero,
        String nome,
        String tipo,
        String corHex,
        StatusLinha status,
        LocalDateTime atualizadaEm
) {
}
