package br.ufscar.dc.dsw.pescd.controller;

import br.ufscar.dc.dsw.pescd.model.ConfiguracaoSistema;
import br.ufscar.dc.dsw.pescd.repository.ConfiguracaoSistemaRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

// S.04 RN-3 - Admin configura instruções de encerramento
@Controller
@RequestMapping("/admin/configuracao")
public class AdminConfiguracaoController {

    private final ConfiguracaoSistemaRepository configuracaoRepository;

    public AdminConfiguracaoController(ConfiguracaoSistemaRepository configuracaoRepository) {
        this.configuracaoRepository = configuracaoRepository;
    }

    private ConfiguracaoSistema getOuCriar() {
        Optional<ConfiguracaoSistema> opt = configuracaoRepository.findById(1L);
        if (opt.isPresent()) {
            return opt.get();
        }
        ConfiguracaoSistema c = new ConfiguracaoSistema();
        c.setInstrucaoEncerramento("");
        return c;
    }

    @GetMapping
    public String form(Model model) {
        model.addAttribute("config", getOuCriar());
        return "admin/configuracao";
    }

    @PostMapping
    public String salvar(@RequestParam String instrucaoEncerramento, RedirectAttributes ra) {
        ConfiguracaoSistema config = getOuCriar();
        config.setInstrucaoEncerramento(instrucaoEncerramento);
        configuracaoRepository.save(config);
        ra.addFlashAttribute("sucesso", "Configuração salva com sucesso.");
        return "redirect:/admin/configuracao";
    }
}
