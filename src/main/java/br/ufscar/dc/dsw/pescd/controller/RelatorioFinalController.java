package br.ufscar.dc.dsw.pescd.controller;

import br.ufscar.dc.dsw.pescd.config.UsuarioLogadoUtil;
import br.ufscar.dc.dsw.pescd.model.RelatorioFinal;
import br.ufscar.dc.dsw.pescd.model.Usuario;
import br.ufscar.dc.dsw.pescd.repository.UsuarioRepository;
import br.ufscar.dc.dsw.pescd.service.ArquivoStorageService;
import br.ufscar.dc.dsw.pescd.service.RelatorioFinalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/aluno/relatorio")
public class RelatorioFinalController {

    private final RelatorioFinalService relatorioService;
    private final ArquivoStorageService arquivoStorageService;
    private final UsuarioRepository usuarioRepository;

    public RelatorioFinalController(RelatorioFinalService relatorioService,
                                    ArquivoStorageService arquivoStorageService,
                                    UsuarioRepository usuarioRepository) {
        this.relatorioService = relatorioService;
        this.arquivoStorageService = arquivoStorageService;
        this.usuarioRepository = usuarioRepository;
    }

    // AL.04 - aluno envia relatório final (PC-4: plano precisa estar aprovado)
    @PostMapping(value = "/{inscricaoId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> enviarRelatorio(
            @PathVariable Long inscricaoId,
            @RequestParam Integer indicadorFrequencia,
            @RequestParam("arquivo") MultipartFile arquivo) {

        Usuario aluno = UsuarioLogadoUtil.getUsuarioLogado(usuarioRepository);
        String caminho = arquivoStorageService.salvarPdf(arquivo);

        RelatorioFinal relatorio = new RelatorioFinal();
        relatorio.setIndicadorFrequencia(indicadorFrequencia);
        relatorio.setCaminhoArquivo(caminho);

        relatorioService.enviarRelatorio(inscricaoId, relatorio, aluno);

        Map<String, String> resposta = new HashMap<>();
        resposta.put("mensagem", "Relatório enviado com sucesso");
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }
}
