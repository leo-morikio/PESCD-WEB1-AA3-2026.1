package br.ufscar.dc.dsw.pescd.dto;

/** Payload para confirmação de encerramento de oferta pelo secretário (S.04). */
public class EncerrarOfertaRequestDTO {

    private String licoesAprendidas;

    public String getLicoesAprendidas() { return licoesAprendidas; }
    public void setLicoesAprendidas(String licoesAprendidas) { this.licoesAprendidas = licoesAprendidas; }
}
