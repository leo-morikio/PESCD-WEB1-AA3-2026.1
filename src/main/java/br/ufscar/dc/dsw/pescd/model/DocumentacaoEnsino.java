package br.ufscar.dc.dsw.pescd.model;

import jakarta.persistence.*;

@Entity
@Table(name = "documentacoes_ensino")
public class DocumentacaoEnsino {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "inscricao_id", nullable = false)
    private InscricaoOferta inscricao;

    private String nomeInstituicao;
    private String nomeDisciplina;
    private String curso;
    private Integer cargaHoraria;
    private String caminhoArquivo;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public InscricaoOferta getInscricao() { return inscricao; }
    public void setInscricao(InscricaoOferta inscricao) { this.inscricao = inscricao; }

    public String getNomeInstituicao() { return nomeInstituicao; }
    public void setNomeInstituicao(String nomeInstituicao) { this.nomeInstituicao = nomeInstituicao; }

    public String getNomeDisciplina() { return nomeDisciplina; }
    public void setNomeDisciplina(String nomeDisciplina) { this.nomeDisciplina = nomeDisciplina; }

    public String getCurso() { return curso; }
    public void setCurso(String curso) { this.curso = curso; }

    public Integer getCargaHoraria() { return cargaHoraria; }
    public void setCargaHoraria(Integer cargaHoraria) { this.cargaHoraria = cargaHoraria; }

    public String getCaminhoArquivo() { return caminhoArquivo; }
    public void setCaminhoArquivo(String caminhoArquivo) { this.caminhoArquivo = caminhoArquivo; }
}