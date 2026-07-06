package br.ufscar.dc.dsw.pescd.model;

import br.ufscar.dc.dsw.pescd.model.enums.NotaAvaliacao;
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

    // PS.03 RN-3 - parecer e sugestão de nota do professor supervisor
    private String parecerSupervisor;
    @Enumerated(EnumType.STRING)
    private NotaAvaliacao notaFinal;

    // PR.01 RN-3 - parecer final do professor responsável
    private String parecerResponsavel;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public InscricaoOferta getInscricao() { return inscricao; }
    public void setInscricao(InscricaoOferta inscricao) { this.inscricao = inscricao; }

    public Integer getIndicadorFrequencia() { return indicadorFrequencia; }
    public void setIndicadorFrequencia(Integer indicadorFrequencia) { this.indicadorFrequencia = indicadorFrequencia; }

    public String getCaminhoArquivo() { return caminhoArquivo; }
    public void setCaminhoArquivo(String caminhoArquivo) { this.caminhoArquivo = caminhoArquivo; }

    public String getParecerSupervisor() { return parecerSupervisor; }
    public void setParecerSupervisor(String parecerSupervisor) { this.parecerSupervisor = parecerSupervisor; }

    public NotaAvaliacao getNotaFinal() { return notaFinal; }
    public void setNotaFinal(NotaAvaliacao notaFinal) { this.notaFinal = notaFinal; }

    public String getParecerResponsavel() { return parecerResponsavel; }
    public void setParecerResponsavel(String parecerResponsavel) { this.parecerResponsavel = parecerResponsavel; }
}