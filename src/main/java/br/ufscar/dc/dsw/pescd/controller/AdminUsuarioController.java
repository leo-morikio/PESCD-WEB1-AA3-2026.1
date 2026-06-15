package br.ufscar.dc.dsw.pescd.controller;

import br.ufscar.dc.dsw.pescd.config.UsuarioLogadoUtil;
import br.ufscar.dc.dsw.pescd.model.Usuario;
import br.ufscar.dc.dsw.pescd.model.enums.Perfil;
import br.ufscar.dc.dsw.pescd.repository.UsuarioRepository;
import br.ufscar.dc.dsw.pescd.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// AD.01 - Administrador gerencia usuários do sistema
@Controller
@RequestMapping("/admin/usuarios")
public class AdminUsuarioController {

    private final UsuarioService usuarioService;

    private final UsuarioRepository usuarioRepository;

    public AdminUsuarioController(UsuarioService usuarioService, UsuarioRepository usuarioRepository) {
        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;
    }

    // AD.01 - Lista todos os usuários
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioService.listarTodos());
        return "admin/usuarios/lista";
    }

    // AD.01 - Formulário novo usuário
    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("perfis", Perfil.values());
        return "admin/usuarios/form";
    }

    // AD.01 - Salva novo usuário
    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Usuario usuario, RedirectAttributes ra) {
        try {
            usuarioService.salvar(usuario);
            ra.addFlashAttribute("sucesso", "Usuário cadastrado com sucesso.");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("erro", e.getMessage());
            return "redirect:/admin/usuarios/novo";
        }
        return "redirect:/admin/usuarios";
    }

    // AD.01 - Formulário editar usuário
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("usuario", usuarioService.buscarPorId(id));
        model.addAttribute("perfis", Perfil.values());
        return "admin/usuarios/form";
    }

    // AD.01 - Atualiza usuário existente
    @PostMapping("/atualizar")
    public String atualizar(@ModelAttribute Usuario usuario, RedirectAttributes ra) {
        try {
            usuarioService.salvar(usuario);
            ra.addFlashAttribute("sucesso", "Usuário atualizado com sucesso.");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/admin/usuarios";
    }

    // AD.01 RN-3 - Exclui usuário (não pode ser o próprio admin logado)
    @PostMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id, RedirectAttributes ra) {
        try {
            Long idLogado = UsuarioLogadoUtil.getUsuarioLogado(usuarioRepository).getId();
            usuarioService.excluir(id, idLogado);
            ra.addFlashAttribute("sucesso", "Usuário removido com sucesso.");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/admin/usuarios";
    }
}
