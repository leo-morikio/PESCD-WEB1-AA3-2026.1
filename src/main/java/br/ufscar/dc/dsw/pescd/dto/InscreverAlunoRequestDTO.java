package br.ufscar.dc.dsw.pescd.dto;

/** Payload para inscrição manual de um aluno em uma oferta (S.02). */
public class InscreverAlunoRequestDTO {

    private Long alunoId;
    private Long supervisorId;

    public Long getAlunoId() { return alunoId; }
    public void setAlunoId(Long alunoId) { this.alunoId = alunoId; }

    public Long getSupervisorId() { return supervisorId; }
    public void setSupervisorId(Long supervisorId) { this.supervisorId = supervisorId; }
}
