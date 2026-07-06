package br.ufscar.dc.dsw.pescd.dto;

import br.ufscar.dc.dsw.pescd.model.LogStatus;

import java.time.LocalDateTime;

/** Representação de LogStatus exposta pela API, sem expor a InscricaoOferta/Usuario completos. */
public class LogStatusResponseDTO {

    private Long id;
    private String statusAnterior;
    private String statusNovo;
    private LocalDateTime dataHora;
    private Long usuarioResponsavelId;
    private String usuarioResponsavelNome;

    public LogStatusResponseDTO(LogStatus log) {
        this.id = log.getId();
        this.statusAnterior = log.getStatusAnterior();
        this.statusNovo = log.getStatusNovo();
        this.dataHora = log.getDataHora();
        if (log.getUsuarioResponsavel() != null) {
            this.usuarioResponsavelId = log.getUsuarioResponsavel().getId();
            this.usuarioResponsavelNome = log.getUsuarioResponsavel().getNomeCompleto();
        }
    }

    public Long getId() { return id; }
    public String getStatusAnterior() { return statusAnterior; }
    public String getStatusNovo() { return statusNovo; }
    public LocalDateTime getDataHora() { return dataHora; }
    public Long getUsuarioResponsavelId() { return usuarioResponsavelId; }
    public String getUsuarioResponsavelNome() { return usuarioResponsavelNome; }
}
