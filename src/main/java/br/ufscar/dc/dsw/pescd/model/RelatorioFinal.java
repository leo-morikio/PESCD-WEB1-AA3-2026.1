package br.ufscar.dc.dsw.pescd.model;

import jakarta.persistence.*;

@Entity
@Table(name = "relatorios_finais")
public class RelatorioFinal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "inscricao_id", nullable = false)
    private InscricaoOferta inscricao;

    private Integer indicadorFrequencia;
    private String caminhoArquivo;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public InscricaoOferta getInscricao() { return inscricao; }
    public void setInscricao(InscricaoOferta inscricao) { this.inscricao = inscricao; }

    public Integer getIndicadorFrequencia() { return indicadorFrequencia; }
    public void setIndicadorFrequencia(Integer indicadorFrequencia) { this.indicadorFrequencia = indicadorFrequencia; }

    public String getCaminhoArquivo() { return caminhoArquivo; }
    public void setCaminhoArquivo(String caminhoArquivo) { this.caminhoArquivo = caminhoArquivo; }
}