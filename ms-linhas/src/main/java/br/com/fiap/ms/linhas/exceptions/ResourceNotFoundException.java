package br.com.fiap.ms.linhas.exceptions;

/** Recurso inexistente: vira HTTP 404. */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String mensagem) {
        super(mensagem);
    }
}
