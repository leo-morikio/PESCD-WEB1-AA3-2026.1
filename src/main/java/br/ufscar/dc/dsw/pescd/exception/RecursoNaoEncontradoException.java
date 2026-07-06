package br.ufscar.dc.dsw.pescd.exception;

/**
 * Lançada quando um recurso solicitado (usuário, oferta, inscrição, etc.) não é encontrado.
 * Mapeada para HTTP 404 pelo GlobalExceptionHandler.
 */
public class RecursoNaoEncontradoException extends RuntimeException {
    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
