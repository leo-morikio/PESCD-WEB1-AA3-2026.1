package br.ufscar.dc.dsw.pescd.model;

import jakarta.persistence.*;

@Entity
@Table(name = "planos_trabalho")
public class PlanoTrabalho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "inscricao_id", nullable = false)
    private InscricaoOferta inscricao;

    private String codigoDisciplina;
    private String nomeDisciplina;
    private String curso;
    private String caminhoArquivo;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public InscricaoOferta getInscricao() { return inscricao; }
    public void setInscricao(InscricaoOferta inscricao) { this.inscricao = inscricao; }

    public String getCodigoDisciplina() { return codigoDisciplina; }
    public void setCodigoDisciplina(String codigoDisciplina) { this.codigoDisciplina = codigoDisciplina; }

    public String getNomeDisciplina() { return nomeDisciplina; }
    public void setNomeDisciplina(String nomeDisciplina) { this.nomeDisciplina = nomeDisciplina; }

    public String getCurso() { return curso; }
    public void setCurso(String curso) { this.curso = curso; }

    public String getCaminhoArquivo() { return caminhoArquivo; }
    public void setCaminhoArquivo(String caminhoArquivo) { this.caminhoArquivo = caminhoArquivo; }
}