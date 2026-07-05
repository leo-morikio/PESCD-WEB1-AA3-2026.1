package br.ufscar.dc.dsw.pescd.controller.rest;

import br.ufscar.dc.dsw.pescd.dto.ConfiguracaoSistemaDTO;
import br.ufscar.dc.dsw.pescd.model.ConfiguracaoSistema;
import br.ufscar.dc.dsw.pescd.repository.ConfiguracaoSistemaRepository;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * S.04 RN-3 - Administrador configura instruções de encerramento (versão REST).
 * Equivalente à AdminConfiguracaoController (MVC), mas retornando JSON.
 */
@RestController
@RequestMapping("/api/admin/configuracao")
public class AdminConfiguracaoRestController {

    private final ConfiguracaoSistemaRepository configuracaoRepository;

    public AdminConfiguracaoRestController(ConfiguracaoSistemaRepository configuracaoRepository) {
        this.configuracaoRepository = configuracaoRepository;
    }

    private ConfiguracaoSistema getOuCriar() {
        Optional<ConfiguracaoSistema> optConfig = configuracaoRepository.findById(1L);
        if (optConfig.isPresent()) {
            return optConfig.get();
        }
        ConfiguracaoSistema config = new ConfiguracaoSistema();
        config.setInstrucaoEncerramento("");
        return config;
    }

    @GetMapping
    public ConfiguracaoSistemaDTO buscar() {
        return new ConfiguracaoSistemaDTO(getOuCriar());
    }

    @PutMapping
    public ConfiguracaoSistemaDTO salvar(@RequestBody ConfiguracaoSistemaDTO dto) {
        ConfiguracaoSistema config = getOuCriar();
        config.setInstrucaoEncerramento(dto.getInstrucaoEncerramento());
        configuracaoRepository.save(config);
        return new ConfiguracaoSistemaDTO(config);
    }
}
