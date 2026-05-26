package br.ufscar.dc.dsw.pescd.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "logs_status")
public class LogStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "inscricao_id", nullable = false)
    private InscricaoOferta inscricao;

    private String statusAnterior;

    @Column(nullable = false)
    private String statusNovo;

    @Column(nullable = false)
    private LocalDateTime dataHora;

    @ManyToOne
    @JoinColumn(name = "usuario_responsavel_id")
    private Usuario usuarioResponsavel;

    public LogStatus() {}

    public LogStatus(InscricaoOferta inscricao, String statusAnterior,
                     String statusNovo, Usuario usuarioResponsavel) {
        this.inscricao = inscricao;
        this.statusAnterior = statusAnterior;
        this.statusNovo = statusNovo;
        this.usuarioResponsavel = usuarioResponsavel;
        this.dataHora = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public InscricaoOferta getInscricao() { return inscricao; }
    public void setInscricao(InscricaoOferta inscricao) { this.inscricao = inscricao; }

    public String getStatusAnterior() { return statusAnterior; }
    public void setStatusAnterior(String statusAnterior) { this.statusAnterior = statusAnterior; }

    public String getStatusNovo() { return statusNovo; }
    public void setStatusNovo(String statusNovo) { this.statusNovo = statusNovo; }

    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }

    public Usuario getUsuarioResponsavel() { return usuarioResponsavel; }
    public void setUsuarioResponsavel(Usuario usuarioResponsavel) { this.usuarioResponsavel = usuarioResponsavel; }
}