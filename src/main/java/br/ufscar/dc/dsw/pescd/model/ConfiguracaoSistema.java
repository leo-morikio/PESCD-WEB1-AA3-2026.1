package br.ufscar.dc.dsw.pescd.model;

import jakarta.persistence.*;

/**
 * S.04 RN-3 - Configurações do sistema editáveis pelo Administrador.
 * Usa padrão singleton: sempre existe exatamente um registro com id=1.
 */
@Entity
@Table(name = "configuracao_sistema")
public class ConfiguracaoSistema {

    @Id
    private Long id = 1L;

    @Column(columnDefinition = "TEXT")
    private String instrucaoEncerramento;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getInstrucaoEncerramento() { return instrucaoEncerramento; }
    public void setInstrucaoEncerramento(String instrucaoEncerramento) {
        this.instrucaoEncerramento = instrucaoEncerramento;
    }
}
