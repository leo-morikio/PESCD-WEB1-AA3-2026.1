package br.ufscar.dc.dsw.pescd.dto;

import br.ufscar.dc.dsw.pescd.model.Oferta;

import java.time.LocalDate;

/** Payload de entrada para criação/atualização de Oferta via API (S.01). */
public class OfertaRequestDTO {

    private String nome;
    private String semestre;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private Long professorResponsavelId;

    public Oferta paraEntidade() {
        Oferta oferta = new Oferta();
        oferta.setNome(nome);
        oferta.setSemestre(semestre);
        oferta.setDataInicio(dataInicio);
        oferta.setDataFim(dataFim);
        return oferta;
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getSemestre() { return semestre; }
    public void setSemestre(String semestre) { this.semestre = semestre; }

    public LocalDate getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDate dataInicio) { this.dataInicio = dataInicio; }

    public LocalDate getDataFim() { return dataFim; }
    public void setDataFim(LocalDate dataFim) { this.dataFim = dataFim; }

    public Long getProfessorResponsavelId() { return professorResponsavelId; }
    public void setProfessorResponsavelId(Long professorResponsavelId) { this.professorResponsavelId = professorResponsavelId; }
}
