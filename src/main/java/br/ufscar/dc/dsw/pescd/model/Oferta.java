package br.ufscar.dc.dsw.pescd.model;

import br.ufscar.dc.dsw.pescd.model.enums.StatusOferta;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "ofertas")
public class Oferta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @Column(nullable = false)
    private String semestre;

    @Column(nullable = false)
    private LocalDate dataInicio;

    @Column(nullable = false)
    private LocalDate dataFim;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusOferta status = StatusOferta.ATIVA;

    @ManyToOne
    @JoinColumn(name = "professor_responsavel_id", nullable = false)
    private Usuario professorResponsavel;

    @ManyToOne
    @JoinColumn(name = "criado_por_id")
    private Usuario criadoPor;

    @Column
    private LocalDateTime criadoEm;

    @ManyToOne
    @JoinColumn(name = "encerrado_por_id")
    private Usuario encerradoPor;

    @Column
    private LocalDateTime encerradoEm;

    @Column(columnDefinition = "TEXT")
    private String licoesAprendidas;

    // Status calculado — não persistido
    public String getStatusExibicao() {
        return switch (status) {
            case CONCLUIDA -> "Concluída";
            case AGUARDANDO_ENCERRAMENTO_SECRETARIO -> "Aguardando encerramento do secretário";
            case ATIVA -> LocalDate.now().isAfter(dataFim) ? "Em atraso" : "Em andamento";
        };
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getSemestre() { return semestre; }
    public void setSemestre(String semestre) { this.semestre = semestre; }

    public LocalDate getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDate dataInicio) { this.dataInicio = dataInicio; }

    public LocalDate getDataFim() { return dataFim; }
    public void setDataFim(LocalDate dataFim) { this.dataFim = dataFim; }

    public StatusOferta getStatus() { return status; }
    public void setStatus(StatusOferta status) { this.status = status; }

    public Usuario getProfessorResponsavel() { return professorResponsavel; }
    public void setProfessorResponsavel(Usuario professorResponsavel) { this.professorResponsavel = professorResponsavel; }

    public Usuario getCriadoPor() { return criadoPor; }
    public void setCriadoPor(Usuario criadoPor) { this.criadoPor = criadoPor; }

    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }

    public Usuario getEncerradoPor() { return encerradoPor; }
    public void setEncerradoPor(Usuario encerradoPor) { this.encerradoPor = encerradoPor; }

    public LocalDateTime getEncerradoEm() { return encerradoEm; }
    public void setEncerradoEm(LocalDateTime encerradoEm) { this.encerradoEm = encerradoEm; }

    public String getLicoesAprendidas() { return licoesAprendidas; }
    public void setLicoesAprendidas(String licoesAprendidas) { this.licoesAprendidas = licoesAprendidas; }
}