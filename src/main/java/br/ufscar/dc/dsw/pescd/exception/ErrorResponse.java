package br.ufscar.dc.dsw.pescd.exception;

import java.time.LocalDateTime;

/**
 * Corpo padrão de erro retornado pela API REST.
 */
public class ErrorResponse {

    private final LocalDateTime timestamp = LocalDateTime.now();
    private final int status;
    private final String erro;
    private final String mensagem;
    private final String caminho;

    public ErrorResponse(int status, String erro, String mensagem, String caminho) {
        this.status = status;
        this.erro = erro;
        this.mensagem = mensagem;
        this.caminho = caminho;
    }

    public LocalDateTime getTimestamp() { return timestamp; }
    public int getStatus() { return status; }
    public String getErro() { return erro; }
    public String getMensagem() { return mensagem; }
    public String getCaminho() { return caminho; }
}
