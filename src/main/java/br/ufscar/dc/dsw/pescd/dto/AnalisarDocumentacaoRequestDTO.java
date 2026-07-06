package br.ufscar.dc.dsw.pescd.dto;

import br.ufscar.dc.dsw.pescd.model.enums.NotaAvaliacao;

/** Payload para o professor responsável analisar a documentação de ensino (PR.02). */
public class AnalisarDocumentacaoRequestDTO {

    private String parecer;
    private Integer indicadorFrequencia;
    private NotaAvaliacao nota;

    public String getParecer() { return parecer; }
    public void setParecer(String parecer) { this.parecer = parecer; }

    public Integer getIndicadorFrequencia() { return indicadorFrequencia; }
    public void setIndicadorFrequencia(Integer indicadorFrequencia) { this.indicadorFrequencia = indicadorFrequencia; }

    public NotaAvaliacao getNota() { return nota; }
    public void setNota(NotaAvaliacao nota) { this.nota = nota; }
}
