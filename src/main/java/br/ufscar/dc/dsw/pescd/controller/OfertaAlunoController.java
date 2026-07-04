package br.ufscar.dc.dsw.pescd.controller;

import br.ufscar.dc.dsw.pescd.config.UsuarioLogadoUtil;
import br.ufscar.dc.dsw.pescd.model.InscricaoOferta;
import br.ufscar.dc.dsw.pescd.model.Usuario;
import br.ufscar.dc.dsw.pescd.repository.InscricaoOfertaRepository;
import br.ufscar.dc.dsw.pescd.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class OfertaAlunoController {

    private final InscricaoOfertaRepository inscricaoOfertaRepository;

    private final UsuarioRepository usuarioRepository;

    public OfertaAlunoController(InscricaoOfertaRepository inscricaoOfertaRepository, UsuarioRepository usuarioRepository) {
        this.inscricaoOfertaRepository = inscricaoOfertaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/aluno/ofertas")
    public String listarOfertas(Model model) {

        Usuario alunoLogado = UsuarioLogadoUtil.getUsuarioLogado(usuarioRepository);

        List<InscricaoOferta> inscricoes = inscricaoOfertaRepository.findByAluno(alunoLogado);
        model.addAttribute("inscricoes", inscricoes);

        return "aluno/lista-ofertas";
    }
}