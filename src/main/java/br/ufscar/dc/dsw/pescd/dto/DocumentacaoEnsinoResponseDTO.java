package br.ufscar.dc.dsw.pescd.dto;

import br.ufscar.dc.dsw.pescd.model.DocumentacaoEnsino;
import br.ufscar.dc.dsw.pescd.model.enums.NotaAvaliacao;

/** Representação de DocumentacaoEnsino exposta pela API, sem expor a InscricaoOferta/Usuario completos. */
public class DocumentacaoEnsinoResponseDTO {

    private Long id;
    private String nomeInstituicao;
    private String nomeDisciplina;
    private String curso;
    private Integer cargaHoraria;
    private String caminhoArquivo;
    private String parecerResponsavel;
    private Integer indicadorFrequencia;
    private NotaAvaliacao notaFinal;

    public DocumentacaoEnsinoResponseDTO(DocumentacaoEnsino documentacao) {
        this.id = documentacao.getId();
        this.nomeInstituicao = documentacao.getNomeInstituicao();
        this.nomeDisciplina = documentacao.getNomeDisciplina();
        this.curso = documentacao.getCurso();
        this.cargaHoraria = documentacao.getCargaHoraria();
        this.caminhoArquivo = documentacao.getCaminhoArquivo();
        this.parecerResponsavel = documentacao.getParecerResponsavel();
        this.indicadorFrequencia = documentacao.getIndicadorFrequencia();
        this.notaFinal = documentacao.getNotaFinal();
    }

    public Long getId() { return id; }
    public String getNomeInstituicao() { return nomeInstituicao; }
    public String getNomeDisciplina() { return nomeDisciplina; }
    public String getCurso() { return curso; }
    public Integer getCargaHoraria() { return cargaHoraria; }
    public String getCaminhoArquivo() { return caminhoArquivo; }
    public String getParecerResponsavel() { return parecerResponsavel; }
    public Integer getIndicadorFrequencia() { return indicadorFrequencia; }
    public NotaAvaliacao getNotaFinal() { return notaFinal; }
}
