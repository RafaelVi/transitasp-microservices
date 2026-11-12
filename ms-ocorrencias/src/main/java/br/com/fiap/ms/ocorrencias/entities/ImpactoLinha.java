package br.com.fiap.ms.ocorrencias.entities;

/**
 * Efeito da ocorrência sobre a linha. A ordem importa: quanto mais abaixo, mais grave.
 * Os nomes são os mesmos do status da linha no ms-linhas.
 */
public enum ImpactoLinha {
    VELOCIDADE_REDUZIDA,
    PARALISADA
}
