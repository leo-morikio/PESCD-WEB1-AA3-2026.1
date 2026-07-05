package br.ufscar.dc.dsw.pescd.dto;

import br.ufscar.dc.dsw.pescd.model.RelatorioFinal;

/** Representação de RelatorioFinal exposta pela API, sem expor a InscricaoOferta/Usuario completos. */
public class RelatorioFinalResponseDTO {

    private Long id;
    private Integer indicadorFrequencia;
    private String caminhoArquivo;

    public RelatorioFinalResponseDTO(RelatorioFinal relatorio) {
        this.id = relatorio.getId();
        this.indicadorFrequencia = relatorio.getIndicadorFrequencia();
        this.caminhoArquivo = relatorio.getCaminhoArquivo();
    }

    public Long getId() { return id; }
    public Integer getIndicadorFrequencia() { return indicadorFrequencia; }
    public String getCaminhoArquivo() { return caminhoArquivo; }
}
