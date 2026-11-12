package br.com.fiap.ms.ocorrencias.exceptions.handler;

import br.com.fiap.ms.ocorrencias.exceptions.BusinessRuleException;
import br.com.fiap.ms.ocorrencias.exceptions.ResourceNotFoundException;
import br.com.fiap.ms.ocorrencias.exceptions.ServiceUnavailableException;
import br.com.fiap.ms.ocorrencias.exceptions.dto.CustomErrorDTO;
import br.com.fiap.ms.ocorrencias.exceptions.dto.FieldMessageDTO;
import br.com.fiap.ms.ocorrencias.exceptions.dto.ValidationErrorDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;
import java.util.List;

/** Traduz as exceções da aplicação em respostas HTTP padronizadas. */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** Recurso inexistente: 404. */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<CustomErrorDTO> handleResourceNotFound(ResourceNotFoundException e,
                                                                 HttpServletRequest request) {
        return buildError(HttpStatus.NOT_FOUND, e.getMessage(), request);
    }

    /** Regra de negócio violada: 409. */
    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<CustomErrorDTO> handleBusinessRule(BusinessRuleException e,
                                                             HttpServletRequest request) {
        return buildError(HttpStatus.CONFLICT, e.getMessage(), request);
    }

    /** Outro microsserviço não respondeu (fallback acionado): 503. */
    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<CustomErrorDTO> handleServiceUnavailable(ServiceUnavailableException e,
                                                                   HttpServletRequest request) {
        return buildError(HttpStatus.SERVICE_UNAVAILABLE, e.getMessage(), request);
    }

    /** Campos do corpo reprovados na validação (Bean Validation): 422 com a lista de campos. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorDTO> handleValidation(MethodArgumentNotValidException e,
                                                               HttpServletRequest request) {
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
        List<FieldMessageDTO> errors = e.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> new FieldMessageDTO(fieldError.getField(), fieldError.getDefaultMessage()))
                .toList();
        ValidationErrorDTO body = new ValidationErrorDTO(
                Instant.now().toString(), status.value(), "Dados inválidos", request.getRequestURI(), errors);
        return ResponseEntity.status(status).body(body);
    }

    /** JSON ausente, malformado ou com valor fora da lista permitida (enum): 400. */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<CustomErrorDTO> handleNotReadable(HttpMessageNotReadableException e,
                                                            HttpServletRequest request) {
        return buildError(HttpStatus.BAD_REQUEST,
                "Corpo da requisição ausente ou inválido. Confira o JSON e os valores permitidos em cada campo",
                request);
    }

    /** Parâmetro de rota ou de consulta com tipo errado: 400. */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<CustomErrorDTO> handleTypeMismatch(MethodArgumentTypeMismatchException e,
                                                             HttpServletRequest request) {
        return buildError(HttpStatus.BAD_REQUEST, "Valor inválido para o parâmetro '" + e.getName() + "'", request);
    }

    private ResponseEntity<CustomErrorDTO> buildError(HttpStatus status, String message, HttpServletRequest request) {
        CustomErrorDTO body = new CustomErrorDTO(
                Instant.now().toString(), status.value(), message, request.getRequestURI());
        return ResponseEntity.status(status).body(body);
    }
}
