package br.ufscar.dc.dsw.pescd.exception;

/**
 * Lançada quando uma regra de negócio é violada (ex: e-mail duplicado, datas inválidas,
 * aluno já inscrito). Mapeada para HTTP 400 pelo GlobalExceptionHandler.
 */
public class NegocioException extends RuntimeException {
    public NegocioException(String mensagem) {
        super(mensagem);
    }
}
