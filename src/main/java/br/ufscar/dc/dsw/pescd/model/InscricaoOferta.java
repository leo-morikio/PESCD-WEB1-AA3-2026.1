package br.ufscar.dc.dsw.pescd.model;

import br.ufscar.dc.dsw.pescd.model.enums.StatusAluno;
import jakarta.persistence.*;

@Entity
@Table(name = "inscricoes_oferta")
public class InscricaoOferta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "aluno_id", nullable = false)
    private Usuario aluno;

    @ManyToOne
    @JoinColumn(name = "oferta_id", nullable = false)
    private Oferta oferta;

    @ManyToOne
    @JoinColumn(name = "professor_supervisor_id")
    private Usuario professorSupervisor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusAluno status = StatusAluno.NAO_ENVIADO;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Usuario getAluno() { return aluno; }
    public void setAluno(Usuario aluno) { this.aluno = aluno; }

    public Oferta getOferta() { return oferta; }
    public void setOferta(Oferta oferta) { this.oferta = oferta; }

    public Usuario getProfessorSupervisor() { return professorSupervisor; }
    public void setProfessorSupervisor(Usuario professorSupervisor) { this.professorSupervisor = professorSupervisor; }

    public StatusAluno getStatus() { return status; }
    public void setStatus(StatusAluno status) { this.status = status; }
}