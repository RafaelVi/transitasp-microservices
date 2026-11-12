package br.com.fiap.ms.linhas.entities;

/**
 * Situação operacional da linha. É alterada pelo ms-ocorrencias,
 * por comunicação síncrona, quando uma ocorrência é aberta ou encerrada.
 */
public enum StatusLinha {
    NORMAL,
    VELOCIDADE_REDUZIDA,
    PARALISADA
}
