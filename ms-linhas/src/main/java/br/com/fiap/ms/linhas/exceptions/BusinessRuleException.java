package br.com.fiap.ms.linhas.exceptions;

/** Regra de negócio violada: vira HTTP 409. */
public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String mensagem) {
        super(mensagem);
    }
}
