package br.ufscar.dc.dsw.pescd.controller;

import br.ufscar.dc.dsw.pescd.model.PlanoTrabalho;
import br.ufscar.dc.dsw.pescd.model.enums.Perfil;
import br.ufscar.dc.dsw.pescd.service.PlanoTrabalhoService;
import br.ufscar.dc.dsw.pescd.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/aluno/plano")
public class PlanoTrabalhoController {

    private final PlanoTrabalhoService planoTrabalhoService;

    private final UsuarioService usuarioService;

    public PlanoTrabalhoController(PlanoTrabalhoService planoTrabalhoService, UsuarioService usuarioService) {
        this.planoTrabalhoService = planoTrabalhoService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/novo/{inscricaoId}")
    public String mostrarFormulario(@PathVariable Long inscricaoId, Model model) {
        model.addAttribute("plano", new PlanoTrabalho());
        model.addAttribute("inscricaoId", inscricaoId);
        model.addAttribute("professores", usuarioService.listarTodos().stream()
                .filter(u -> u.getPerfil() == Perfil.PROFESSOR)
                .toList());
        return "aluno/form-plano";
    }

    @PostMapping("/enviar/{inscricaoId}")
    public String enviarPlano(@PathVariable Long inscricaoId,
                              @ModelAttribute PlanoTrabalho plano,
                              @RequestParam Long supervisorId,
                              RedirectAttributes ra) {
        try {
            planoTrabalhoService.enviarPlano(inscricaoId, plano, supervisorId);
            ra.addFlashAttribute("sucesso", "Plano enviado com sucesso.");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/aluno/ofertas";
    }
}