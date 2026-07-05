package br.ufscar.dc.dsw.pescd.dto;

import br.ufscar.dc.dsw.pescd.model.Oferta;
import br.ufscar.dc.dsw.pescd.model.enums.StatusOferta;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** Representação de Oferta exposta pela API (S.01/S.03/S.04, V.01). */
public class OfertaResponseDTO {

    private Long id;
    private String nome;
    private String semestre;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private StatusOferta status;
    private String statusExibicao;
    private Long professorResponsavelId;
    private String professorResponsavelNome;
    private String licoesAprendidas;
    private LocalDateTime encerradoEm;
    private long totalAlunos;

    public OfertaResponseDTO(Oferta oferta, long totalAlunos) {
        this.id = oferta.getId();
        this.nome = oferta.getNome();
        this.semestre = oferta.getSemestre();
        this.dataInicio = oferta.getDataInicio();
        this.dataFim = oferta.getDataFim();
        this.status = oferta.getStatus();
        this.statusExibicao = oferta.getStatusExibicao();
        if (oferta.getProfessorResponsavel() != null) {
            this.professorResponsavelId = oferta.getProfessorResponsavel().getId();
            this.professorResponsavelNome = oferta.getProfessorResponsavel().getNomeCompleto();
        }
        this.licoesAprendidas = oferta.getLicoesAprendidas();
        this.encerradoEm = oferta.getEncerradoEm();
        this.totalAlunos = totalAlunos;
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getSemestre() { return semestre; }
    public LocalDate getDataInicio() { return dataInicio; }
    public LocalDate getDataFim() { return dataFim; }
    public StatusOferta getStatus() { return status; }
    public String getStatusExibicao() { return statusExibicao; }
    public Long getProfessorResponsavelId() { return professorResponsavelId; }
    public String getProfessorResponsavelNome() { return professorResponsavelNome; }
    public String getLicoesAprendidas() { return licoesAprendidas; }
    public LocalDateTime getEncerradoEm() { return encerradoEm; }
    public long getTotalAlunos() { return totalAlunos; }
}
