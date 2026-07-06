package br.ufscar.dc.dsw.pescd.controller;

import br.ufscar.dc.dsw.pescd.config.UsuarioLogadoUtil;
import br.ufscar.dc.dsw.pescd.model.PlanoTrabalho;
import br.ufscar.dc.dsw.pescd.model.Usuario;
import br.ufscar.dc.dsw.pescd.repository.UsuarioRepository;
import br.ufscar.dc.dsw.pescd.service.ArquivoStorageService;
import br.ufscar.dc.dsw.pescd.service.PlanoTrabalhoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/aluno/plano")
public class PlanoTrabalhoController {

    private final PlanoTrabalhoService planoService;
    private final ArquivoStorageService arquivoStorageService;
    private final UsuarioRepository usuarioRepository;

    public PlanoTrabalhoController(PlanoTrabalhoService planoService,
                                   ArquivoStorageService arquivoStorageService,
                                   UsuarioRepository usuarioRepository) {
        this.planoService = planoService;
        this.arquivoStorageService = arquivoStorageService;
        this.usuarioRepository = usuarioRepository;
    }

    // AL.02 - aluno envia plano de trabalho
    @PostMapping(value = "/{inscricaoId}", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> enviarPlano(
            @PathVariable Long inscricaoId,
            @RequestParam String codigoDisciplina,
            @RequestParam String nomeDisciplina,
            @RequestParam String curso,
            @RequestParam Long supervisorId,
            @RequestParam("arquivo") MultipartFile arquivo) {

        Usuario aluno = UsuarioLogadoUtil.getUsuarioLogado(usuarioRepository);
        String caminho = arquivoStorageService.salvarPdf(arquivo);

        PlanoTrabalho plano = new PlanoTrabalho();
        plano.setCodigoDisciplina(codigoDisciplina);
        plano.setNomeDisciplina(nomeDisciplina);
        plano.setCurso(curso);
        plano.setCaminhoArquivo(caminho);

        planoService.enviarPlano(inscricaoId, plano, supervisorId, aluno);

        Map<String, String> resposta = new java.util.HashMap<>();
        resposta.put("mensagem", "Plano enviado com sucesso");
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }
}
