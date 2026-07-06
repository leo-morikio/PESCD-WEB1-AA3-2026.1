package br.ufscar.dc.dsw.pescd.dto;

import br.ufscar.dc.dsw.pescd.model.RelatorioFinal;
import br.ufscar.dc.dsw.pescd.model.enums.NotaAvaliacao;

/** Representação de RelatorioFinal exposta pela API, sem expor a InscricaoOferta/Usuario completos. */
public class RelatorioFinalResponseDTO {

    private Long id;
    private Integer indicadorFrequencia;
    private String caminhoArquivo;
    private String parecerSupervisor;
    private NotaAvaliacao notaFinal;
    private String parecerResponsavel;

    public RelatorioFinalResponseDTO(RelatorioFinal relatorio) {
        this.id = relatorio.getId();
        this.indicadorFrequencia = relatorio.getIndicadorFrequencia();
        this.caminhoArquivo = relatorio.getCaminhoArquivo();
        this.parecerSupervisor = relatorio.getParecerSupervisor();
        this.notaFinal = relatorio.getNotaFinal();
        this.parecerResponsavel = relatorio.getParecerResponsavel();
    }

    public Long getId() { return id; }
    public Integer getIndicadorFrequencia() { return indicadorFrequencia; }
    public String getCaminhoArquivo() { return caminhoArquivo; }
    public String getParecerSupervisor() { return parecerSupervisor; }
    public NotaAvaliacao getNotaFinal() { return notaFinal; }
    public String getParecerResponsavel() { return parecerResponsavel; }
}
