package br.ufscar.dc.dsw.pescd.controller;

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

    @Autowired
    private InscricaoOfertaRepository inscricaoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/aluno/ofertas")
    public String listarOfertas(Model model) {

        // TEMPORÁRIO: enquanto o login não existe, busco o aluno de exemplo pelo email.
        // Quando o login do grupo ficar pronto, troca por: o aluno autenticado na sessão.
        Usuario alunoLogado = usuarioRepository.findByEmail("aluno@ufscar.br");

        List<InscricaoOferta> inscricoes = inscricaoRepository.findByAluno(alunoLogado);
        model.addAttribute("inscricoes", inscricoes);

        return "aluno/lista-ofertas";
    }
}