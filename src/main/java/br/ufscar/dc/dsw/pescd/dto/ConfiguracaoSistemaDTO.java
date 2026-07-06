package br.ufscar.dc.dsw.pescd.dto;

import br.ufscar.dc.dsw.pescd.model.ConfiguracaoSistema;

/** DTO de request/response para a configuração global do sistema (S.04 RN-3). */
public class ConfiguracaoSistemaDTO {

    private String instrucaoEncerramento;

    public ConfiguracaoSistemaDTO() { }

    public ConfiguracaoSistemaDTO(ConfiguracaoSistema config) {
        this.instrucaoEncerramento = config.getInstrucaoEncerramento();
    }

    public String getInstrucaoEncerramento() { return instrucaoEncerramento; }
    public void setInstrucaoEncerramento(String instrucaoEncerramento) { this.instrucaoEncerramento = instrucaoEncerramento; }
}
