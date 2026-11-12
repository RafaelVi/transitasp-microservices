package br.com.fiap.ms.ocorrencias.exceptions.dto;

/** Corpo padrão das respostas de erro da API. */
public record CustomErrorDTO(
        String timestamp,
        Integer status,
        String error,
        String path
) {
}
