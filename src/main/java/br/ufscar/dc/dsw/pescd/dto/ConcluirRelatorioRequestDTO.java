package br.ufscar.dc.dsw.pescd.dto;

import br.ufscar.dc.dsw.pescd.model.enums.NotaAvaliacao;

/** Payload para o professor responsável concluir o relatório final (PR.01). */
public class ConcluirRelatorioRequestDTO {

    private String parecer;
    private Integer frequencia;
    private NotaAvaliacao nota;

    public String getParecer() { return parecer; }
    public void setParecer(String parecer) { this.parecer = parecer; }

    public Integer getFrequencia() { return frequencia; }
    public void setFrequencia(Integer frequencia) { this.frequencia = frequencia; }

    public NotaAvaliacao getNota() { return nota; }
    public void setNota(NotaAvaliacao nota) { this.nota = nota; }
}
