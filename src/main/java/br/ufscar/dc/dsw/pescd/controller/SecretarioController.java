package br.ufscar.dc.dsw.pescd.controller;

import br.ufscar.dc.dsw.pescd.config.UsuarioLogadoUtil;
import br.ufscar.dc.dsw.pescd.model.Oferta;
import br.ufscar.dc.dsw.pescd.model.Usuario;
import br.ufscar.dc.dsw.pescd.model.enums.Perfil;
import br.ufscar.dc.dsw.pescd.model.enums.StatusOferta;
import br.ufscar.dc.dsw.pescd.repository.UsuarioRepository;
import br.ufscar.dc.dsw.pescd.service.InscricaoOfertaService;
import br.ufscar.dc.dsw.pescd.service.OfertaService;
import br.ufscar.dc.dsw.pescd.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

// S.01, S.03, S.04 - Secretário gerencia ofertas
@Controller
@RequestMapping("/secretario")
public class SecretarioController {

    private final OfertaService ofertaService;

    private final UsuarioService usuarioService;

    private final InscricaoOfertaService inscricaoService;

    private final UsuarioRepository usuarioRepository;


    //Construtor para instanciar as services - sem autowired
    public SecretarioController(OfertaService ofertaService, UsuarioService usuarioService, InscricaoOfertaService inscricaoService, UsuarioRepository usuarioRepository) {
        this.ofertaService = ofertaService;
        this.usuarioService = usuarioService;
        this.inscricaoService = inscricaoService;
        this.usuarioRepository = usuarioRepository;
    }

    // S.03 - Lista ofertas (painel do secretário)
    @GetMapping("/ofertas")
    public String listarOfertas(Model model) {
        List<Oferta> ofertas = ofertaService.listarTodasOrdenadas();
        model.addAttribute("ofertas", ofertas);
        model.addAttribute("ofertaService", ofertaService);
        return "secretario/ofertas/lista";
    }

    // S.01 - Formulário nova oferta
    @GetMapping("/ofertas/nova")
    public String novaOferta(Model model) {
        model.addAttribute("oferta", new Oferta());
        model.addAttribute("professores", usuarioService.listarTodos().stream()
                .filter(u -> u.getPerfil() == Perfil.PROFESSOR)
                .toList());
        return "secretario/ofertas/form";
    }

    // S.01 - Salva nova oferta
    @PostMapping("/ofertas/salvar")
    public String salvarOferta(@ModelAttribute Oferta oferta,
                               @RequestParam(required = false) Long professorResponsavelId,
                               RedirectAttributes ra) {
        try {
            // S.01 RN-2: validar datas
            if (oferta.getDataFim() != null && oferta.getDataInicio() != null
                    && oferta.getDataFim().isBefore(oferta.getDataInicio())) {
                ra.addFlashAttribute("erro", "A data de fim não pode ser anterior à data de início.");
                return "redirect:/secretario/ofertas/nova";
            }

            if (professorResponsavelId != null) {
                oferta.setProfessorResponsavel(usuarioService.buscarPorId(professorResponsavelId));
            }
            oferta.setStatus(StatusOferta.ATIVA);

            Usuario secretario = UsuarioLogadoUtil.getUsuarioLogado(usuarioRepository);
            ofertaService.salvar(oferta, secretario);
            ra.addFlashAttribute("sucesso", "Oferta criada com sucesso.");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("erro", e.getMessage());
            return "redirect:/secretario/ofertas/nova";
        }
        return "redirect:/secretario/ofertas";
    }

    // S.03 - Detalhes de uma oferta
    @GetMapping("/ofertas/{id}")
    public String detalhesOferta(@PathVariable Long id, Model model) {
        Oferta oferta = ofertaService.buscarPorId(id);
        model.addAttribute("oferta", oferta);
        model.addAttribute("ofertaService", ofertaService);
        model.addAttribute("inscritos", inscricaoService.listarPorOferta(oferta));
        return "secretario/ofertas/detalhes";
    }

    // S.04 - Confirmar encerramento
    @PostMapping("/ofertas/{id}/encerrar")
    public String encerrarOferta(@PathVariable Long id,
                                 @RequestParam(required = false) String licoesAprendidas,
                                 RedirectAttributes ra) {
        try {
            Oferta oferta = ofertaService.buscarPorId(id);
            if (oferta.getStatus() != StatusOferta.AGUARDANDO_ENCERRAMENTO_SECRETARIO) {
                ra.addFlashAttribute("erro", "A oferta não está aguardando encerramento.");
                return "redirect:/secretario/ofertas/" + id;
            }
            Usuario secretario = UsuarioLogadoUtil.getUsuarioLogado(usuarioRepository);
            oferta.setStatus(StatusOferta.CONCLUIDA);
            oferta.setEncerradoPor(secretario);
            oferta.setEncerradoEm(LocalDateTime.now());
            oferta.setLicoesAprendidas(licoesAprendidas);
            ofertaService.salvar(oferta, secretario);
            ra.addFlashAttribute("sucesso", "Oferta encerrada com sucesso.");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/secretario/ofertas";
    }
}
