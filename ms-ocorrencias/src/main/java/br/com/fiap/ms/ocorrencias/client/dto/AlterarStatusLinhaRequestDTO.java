package br.com.fiap.ms.ocorrencias.client.dto;

/** Corpo enviado ao ms-linhas para alterar o status de uma linha. */
public record AlterarStatusLinhaRequestDTO(StatusLinha status) {
}
