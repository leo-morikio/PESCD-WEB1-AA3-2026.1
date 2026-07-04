package br.ufscar.dc.dsw.pescd.controller;

import br.ufscar.dc.dsw.pescd.config.UsuarioLogadoUtil;
import br.ufscar.dc.dsw.pescd.model.InscricaoOferta;
import br.ufscar.dc.dsw.pescd.model.Usuario;
import br.ufscar.dc.dsw.pescd.repository.InscricaoOfertaRepository;
import br.ufscar.dc.dsw.pescd.repository.UsuarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/aluno")
public class OfertaAlunoController {

    private final InscricaoOfertaRepository inscricaoRepository;
    private final UsuarioRepository usuarioRepository;

    public OfertaAlunoController(InscricaoOfertaRepository inscricaoRepository,
                                 UsuarioRepository usuarioRepository) {
        this.inscricaoRepository = inscricaoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/ofertas")
    public ResponseEntity<List<InscricaoOferta>> listarOfertas() {
        Usuario alunoLogado = UsuarioLogadoUtil.getUsuarioLogado(usuarioRepository);
        List<InscricaoOferta> inscricoes = inscricaoRepository.findByAluno(alunoLogado);
        return ResponseEntity.ok(inscricoes);
    }
}