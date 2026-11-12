package br.com.fiap.ms.linhas.exceptions.dto;

/** Campo inválido e a mensagem de validação correspondente. */
public record FieldMessageDTO(
        String fieldName,
        String message
) {
}
