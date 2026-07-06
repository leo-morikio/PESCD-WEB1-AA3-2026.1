package br.ufscar.dc.dsw.pescd.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartException;

import java.io.IOException;

/**
 * Tratamento centralizado de erros para os endpoints REST (/api/**),
 * padronizando o corpo de resposta em caso de falha.
 * Cobre br.ufscar.dc.dsw.pescd.controller e seu subpacote controller.rest.
 */
@RestControllerAdvice(basePackages = "br.ufscar.dc.dsw.pescd.controller")
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleNaoEncontrado(RecursoNaoEncontradoException ex, HttpServletRequest req) {
        return corpo(HttpStatus.NOT_FOUND, ex, req);
    }

    @ExceptionHandler(NegocioException.class)
    public ResponseEntity<ErrorResponse> handleNegocio(NegocioException ex, HttpServletRequest req) {
        return corpo(HttpStatus.BAD_REQUEST, ex, req);
    }

    @ExceptionHandler({IOException.class, MultipartException.class})
    public ResponseEntity<ErrorResponse> handleArquivo(Exception ex, HttpServletRequest req) {
        return corpo(HttpStatus.BAD_REQUEST, ex, req);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleGenerico(RuntimeException ex, HttpServletRequest req) {
        // Regras de negócio legadas ainda lançam RuntimeException puro;
        // tratadas como erro de requisição até serem migradas para NegocioException.
        return corpo(HttpStatus.BAD_REQUEST, ex, req);
    }

    private ResponseEntity<ErrorResponse> corpo(HttpStatus status, Exception ex, HttpServletRequest req) {
        ErrorResponse erro = new ErrorResponse(status.value(), status.getReasonPhrase(), ex.getMessage(), req.getRequestURI());
        return ResponseEntity.status(status).body(erro);
    }
}
