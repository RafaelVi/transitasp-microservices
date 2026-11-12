package br.com.fiap.ms.ocorrencias.exceptions.dto;

import java.util.List;

/** Resposta de erro de validação: o erro padrão mais a lista de campos inválidos. */
public record ValidationErrorDTO(
        String timestamp,
        Integer status,
        String error,
        String path,
        List<FieldMessageDTO> errors
) {
}
