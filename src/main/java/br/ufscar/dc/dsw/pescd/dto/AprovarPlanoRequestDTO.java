package br.ufscar.dc.dsw.pescd.dto;

/** Payload para o professor supervisor aprovar o plano de trabalho (PS.02). */
public class AprovarPlanoRequestDTO {

    private String parecer;

    public String getParecer() { return parecer; }
    public void setParecer(String parecer) { this.parecer = parecer; }
}
