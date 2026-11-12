package br.com.fiap.ms.ocorrencias.exceptions;

/** Outro microsserviço não respondeu: vira HTTP 503. */
public class ServiceUnavailableException extends RuntimeException {

    public ServiceUnavailableException(String mensagem) {
        super(mensagem);
    }
}
