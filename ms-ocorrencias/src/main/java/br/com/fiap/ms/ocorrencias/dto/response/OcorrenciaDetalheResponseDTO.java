package br.com.fiap.ms.ocorrencias.dto.response;

/**
 * Ocorrência acompanhada da situação atual da linha no ms-linhas.
 * Se o ms-linhas estiver fora do ar na consulta, "linha" vem nulo,
 * "linhaDisponivel" vem false e "aviso" explica o motivo (fallback).
 */
public record OcorrenciaDetalheResponseDTO(
        OcorrenciaResponseDTO ocorrencia,
        LinhaResumoResponseDTO linha,
        boolean linhaDisponivel,
        String aviso
) {
}
