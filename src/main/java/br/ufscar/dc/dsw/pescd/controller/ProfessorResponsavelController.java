package br.ufscar.dc.dsw.pescd.controller;

import br.ufscar.dc.dsw.pescd.config.UsuarioLogadoUtil;
import br.ufscar.dc.dsw.pescd.model.InscricaoOferta;
import br.ufscar.dc.dsw.pescd.model.Oferta;
import br.ufscar.dc.dsw.pescd.model.Usuario;
import br.ufscar.dc.dsw.pescd.repository.UsuarioRepository;
import br.ufscar.dc.dsw.pescd.service.ProfessorResponsavelService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/professor/responsavel")
public class ProfessorResponsavelController {

    private final ProfessorResponsavelService responsavelService;
    private final UsuarioRepository usuarioRepository;

    public ProfessorResponsavelController(ProfessorResponsavelService responsavelService,
                                          UsuarioRepository usuarioRepository) {
        this.responsavelService = responsavelService;
        this.usuarioRepository = usuarioRepository;
    }

    // lista ofertas do professor responsável
    @GetMapping("/ofertas")
    public ResponseEntity<List<Oferta>> listarOfertas() {
        Usuario professor = UsuarioLogadoUtil.getUsuarioLogado(usuarioRepository);
        return ResponseEntity.ok(responsavelService.listarOfertas(professor));
    }

    // PR.01 - conclui relatório
    @PutMapping("/concluir-relatorio/{inscricaoId}")
    public ResponseEntity<InscricaoOferta> concluirRelatorio(@PathVariable Long inscricaoId) {
        Usuario professor = UsuarioLogadoUtil.getUsuarioLogado(usuarioRepository);
        return ResponseEntity.ok(responsavelService.concluirRelatorio(inscricaoId, professor));
    }

    // PR.02 - analisa documentação
    @PutMapping("/analisar-documentacao/{inscricaoId}")
    public ResponseEntity<InscricaoOferta> analisarDocumentacao(@PathVariable Long inscricaoId) {
        Usuario professor = UsuarioLogadoUtil.getUsuarioLogado(usuarioRepository);
        return ResponseEntity.ok(responsavelService.analisarDocumentacao(inscricaoId, professor));
    }

    // PR.03 - encerra oferta
    @PutMapping("/encerrar-oferta/{ofertaId}")
    public ResponseEntity<Oferta> encerrarOferta(@PathVariable Long ofertaId,
                                                 @RequestBody Map<String, String> body) {
        Usuario professor = UsuarioLogadoUtil.getUsuarioLogado(usuarioRepository);
        String licoesAprendidas = body.get("licoesAprendidas");
        return ResponseEntity.ok(responsavelService.encerrarOferta(ofertaId, licoesAprendidas, professor));
    }
}