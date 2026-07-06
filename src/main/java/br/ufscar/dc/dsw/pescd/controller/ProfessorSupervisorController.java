package br.ufscar.dc.dsw.pescd.controller;

import br.ufscar.dc.dsw.pescd.config.UsuarioLogadoUtil;
import br.ufscar.dc.dsw.pescd.dto.AprovarPlanoRequestDTO;
import br.ufscar.dc.dsw.pescd.dto.AprovarRelatorioRequestDTO;
import br.ufscar.dc.dsw.pescd.dto.InscricaoOfertaResponseDTO;
import br.ufscar.dc.dsw.pescd.dto.PlanoTrabalhoResponseDTO;
import br.ufscar.dc.dsw.pescd.dto.RelatorioFinalResponseDTO;
import br.ufscar.dc.dsw.pescd.model.InscricaoOferta;
import br.ufscar.dc.dsw.pescd.model.PlanoTrabalho;
import br.ufscar.dc.dsw.pescd.model.RelatorioFinal;
import br.ufscar.dc.dsw.pescd.model.Usuario;
import br.ufscar.dc.dsw.pescd.repository.UsuarioRepository;
import br.ufscar.dc.dsw.pescd.service.ProfessorSupervisorService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * PS.01, PS.02, PS.03 - Professor supervisor acompanha e aprova planos/relatórios (versão REST).
 */
@RestController
@RequestMapping("/api/professor/supervisor")
public class ProfessorSupervisorController {

    private final ProfessorSupervisorService supervisorService;
    private final UsuarioRepository usuarioRepository;

    public ProfessorSupervisorController(ProfessorSupervisorService supervisorService,
                                         UsuarioRepository usuarioRepository) {
        this.supervisorService = supervisorService;
        this.usuarioRepository = usuarioRepository;
    }

    // PS.01 - lista inscrições supervisionadas
    @GetMapping("/inscricoes")
    public List<InscricaoOfertaResponseDTO> listarInscricoes() {
        Usuario professor = UsuarioLogadoUtil.getUsuarioLogado(usuarioRepository);
        List<InscricaoOferta> inscricoes = supervisorService.listarInscricoes(professor);
        List<InscricaoOfertaResponseDTO> resposta = new ArrayList<>();
        for (InscricaoOferta inscricao : inscricoes) {
            resposta.add(new InscricaoOfertaResponseDTO(inscricao));
        }
        return resposta;
    }

    // PS.02/PS.03 RN-2 - dados do plano e do relatório, somente leitura, antes de aprovar
    @GetMapping("/inscricoes/{inscricaoId}")
    public Map<String, Object> detalhes(@PathVariable Long inscricaoId) {
        Usuario professor = UsuarioLogadoUtil.getUsuarioLogado(usuarioRepository);
        InscricaoOferta inscricao = supervisorService.detalhes(inscricaoId, professor);

        Optional<PlanoTrabalho> optPlano = supervisorService.buscarPlano(inscricao);
        PlanoTrabalhoResponseDTO planoDto = null;
        if (optPlano.isPresent()) {
            planoDto = new PlanoTrabalhoResponseDTO(optPlano.get());
        }

        Optional<RelatorioFinal> optRelatorio = supervisorService.buscarRelatorio(inscricao);
        RelatorioFinalResponseDTO relatorioDto = null;
        if (optRelatorio.isPresent()) {
            relatorioDto = new RelatorioFinalResponseDTO(optRelatorio.get());
        }

        Map<String, Object> resposta = new HashMap<>();
        resposta.put("inscricao", new InscricaoOfertaResponseDTO(inscricao));
        resposta.put("plano", planoDto);
        resposta.put("relatorio", relatorioDto);
        return resposta;
    }

    // Download do PDF do plano de trabalho (apoia a leitura exigida em PS.02 RN-2)
    @GetMapping("/inscricoes/{inscricaoId}/plano/arquivo")
    public ResponseEntity<byte[]> baixarArquivoPlano(@PathVariable Long inscricaoId) {
        Usuario professor = UsuarioLogadoUtil.getUsuarioLogado(usuarioRepository);
        byte[] arquivo = supervisorService.baixarArquivoPlano(inscricaoId, professor);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"plano-" + inscricaoId + ".pdf\"")
                .body(arquivo);
    }

    // Download do PDF do relatório final (apoia a leitura exigida em PS.03 RN-2)
    @GetMapping("/inscricoes/{inscricaoId}/relatorio/arquivo")
    public ResponseEntity<byte[]> baixarArquivoRelatorio(@PathVariable Long inscricaoId) {
        Usuario professor = UsuarioLogadoUtil.getUsuarioLogado(usuarioRepository);
        byte[] arquivo = supervisorService.baixarArquivoRelatorio(inscricaoId, professor);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"relatorio-" + inscricaoId + ".pdf\"")
                .body(arquivo);
    }

    // PS.02 - aprova plano (RN-3: exige parecer)
    @PutMapping("/aprovar-plano/{inscricaoId}")
    public InscricaoOfertaResponseDTO aprovarPlano(@PathVariable Long inscricaoId,
                                                   @RequestBody AprovarPlanoRequestDTO dto) {
        Usuario professor = UsuarioLogadoUtil.getUsuarioLogado(usuarioRepository);
        InscricaoOferta inscricao = supervisorService.aprovarPlano(inscricaoId, dto.getParecer(), professor);
        return new InscricaoOfertaResponseDTO(inscricao);
    }

    // PS.03 - aprova relatório (RN-3: exige parecer, indicador de frequência e sugestão de nota)
    @PutMapping("/aprovar-relatorio/{inscricaoId}")
    public InscricaoOfertaResponseDTO aprovarRelatorio(@PathVariable Long inscricaoId,
                                                       @RequestBody AprovarRelatorioRequestDTO dto) {
        Usuario professor = UsuarioLogadoUtil.getUsuarioLogado(usuarioRepository);
        InscricaoOferta inscricao = supervisorService.aprovarRelatorio(
                inscricaoId, dto.getParecer(), dto.getIndicadorFrequencia(), dto.getNotaFinal(), professor);
        return new InscricaoOfertaResponseDTO(inscricao);
    }
}
