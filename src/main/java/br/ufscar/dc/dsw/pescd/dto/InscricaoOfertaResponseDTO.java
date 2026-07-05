package br.ufscar.dc.dsw.pescd.dto;

import br.ufscar.dc.dsw.pescd.model.InscricaoOferta;
import br.ufscar.dc.dsw.pescd.model.enums.StatusAluno;

/** Representação de InscricaoOferta exposta pela API (S.02/S.03). */
public class InscricaoOfertaResponseDTO {

    private Long id;
    private Long ofertaId;
    private Long alunoId;
    private String alunoNome;
    private String alunoEmail;
    private Long professorSupervisorId;
    private String professorSupervisorNome;
    private StatusAluno status;
    private String statusExibicao;

    public InscricaoOfertaResponseDTO(InscricaoOferta inscricao) {
        this.id = inscricao.getId();
        this.ofertaId = inscricao.getOferta().getId();
        this.alunoId = inscricao.getAluno().getId();
        this.alunoNome = inscricao.getAluno().getNomeCompleto();
        this.alunoEmail = inscricao.getAluno().getEmail();
        if (inscricao.getProfessorSupervisor() != null) {
            this.professorSupervisorId = inscricao.getProfessorSupervisor().getId();
            this.professorSupervisorNome = inscricao.getProfessorSupervisor().getNomeCompleto();
        }
        this.status = inscricao.getStatus();
        this.statusExibicao = inscricao.getStatusExibicao();
    }

    public Long getId() { return id; }
    public Long getOfertaId() { return ofertaId; }
    public Long getAlunoId() { return alunoId; }
    public String getAlunoNome() { return alunoNome; }
    public String getAlunoEmail() { return alunoEmail; }
    public Long getProfessorSupervisorId() { return professorSupervisorId; }
    public String getProfessorSupervisorNome() { return professorSupervisorNome; }
    public StatusAluno getStatus() { return status; }
    public String getStatusExibicao() { return statusExibicao; }
}
