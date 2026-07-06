package br.ufscar.dc.dsw.pescd.dto;

import br.ufscar.dc.dsw.pescd.model.enums.NotaAvaliacao;

/** Payload para o professor supervisor aprovar o relatório final (PS.03). */
public class AprovarRelatorioRequestDTO {

    private String parecer;
    private Integer indicadorFrequencia;
    private NotaAvaliacao notaFinal;

    public String getParecer() { return parecer; }
    public void setParecer(String parecer) { this.parecer = parecer; }

    public Integer getIndicadorFrequencia() { return indicadorFrequencia; }
    public void setIndicadorFrequencia(Integer indicadorFrequencia) { this.indicadorFrequencia = indicadorFrequencia; }

    public NotaAvaliacao getNotaFinal() { return notaFinal; }
    public void setNotaFinal(NotaAvaliacao notaFinal) { this.notaFinal = notaFinal; }
}
