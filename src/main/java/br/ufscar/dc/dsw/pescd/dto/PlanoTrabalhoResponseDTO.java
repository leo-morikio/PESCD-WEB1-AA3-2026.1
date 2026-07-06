package br.ufscar.dc.dsw.pescd.dto;

import br.ufscar.dc.dsw.pescd.model.PlanoTrabalho;

/** Representação de PlanoTrabalho exposta pela API, sem expor a InscricaoOferta/Usuario completos. */
public class PlanoTrabalhoResponseDTO {

    private Long id;
    private String codigoDisciplina;
    private String nomeDisciplina;
    private String curso;
    private String caminhoArquivo;
    private String parecerSupervisor;

    public PlanoTrabalhoResponseDTO(PlanoTrabalho plano) {
        this.id = plano.getId();
        this.codigoDisciplina = plano.getCodigoDisciplina();
        this.nomeDisciplina = plano.getNomeDisciplina();
        this.curso = plano.getCurso();
        this.caminhoArquivo = plano.getCaminhoArquivo();
        this.parecerSupervisor = plano.getParecerSupervisor();
    }

    public Long getId() { return id; }
    public String getCodigoDisciplina() { return codigoDisciplina; }
    public String getNomeDisciplina() { return nomeDisciplina; }
    public String getCurso() { return curso; }
    public String getCaminhoArquivo() { return caminhoArquivo; }
    public String getParecerSupervisor() { return parecerSupervisor; }
}
