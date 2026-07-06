package br.ufscar.dc.dsw.pescd.controller;

import br.ufscar.dc.dsw.pescd.config.UsuarioLogadoUtil;
import br.ufscar.dc.dsw.pescd.dto.AnalisarDocumentacaoRequestDTO;
import br.ufscar.dc.dsw.pescd.dto.ConcluirRelatorioRequestDTO;
import br.ufscar.dc.dsw.pescd.dto.DocumentacaoEnsinoResponseDTO;
import br.ufscar.dc.dsw.pescd.dto.EncerramentoOfertaResponseDTO;
import br.ufscar.dc.dsw.pescd.dto.EstatisticasOfertaDTO;
import br.ufscar.dc.dsw.pescd.dto.InscricaoOfertaResponseDTO;
import br.ufscar.dc.dsw.pescd.dto.OfertaResponseDTO;
import br.ufscar.dc.dsw.pescd.dto.PlanoTrabalhoResponseDTO;
import br.ufscar.dc.dsw.pescd.dto.RelatorioFinalResponseDTO;
import br.ufscar.dc.dsw.pescd.model.DocumentacaoEnsino;
import br.ufscar.dc.dsw.pescd.model.InscricaoOferta;
import br.ufscar.dc.dsw.pescd.model.Oferta;
import br.ufscar.dc.dsw.pescd.model.PlanoTrabalho;
import br.ufscar.dc.dsw.pescd.model.RelatorioFinal;
import br.ufscar.dc.dsw.pescd.model.Usuario;
import br.ufscar.dc.dsw.pescd.repository.UsuarioRepository;
import br.ufscar.dc.dsw.pescd.service.OfertaService;
import br.ufscar.dc.dsw.pescd.service.ProfessorResponsavelService;
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
 * PR.01, PR.02, PR.03 - Professor responsável acompanha e encerra ofertas (versão REST).
 */
@RestController
@RequestMapping("/api/professor/responsavel")
public class ProfessorResponsavelController {

    private final ProfessorResponsavelService responsavelService;
    private final OfertaService ofertaService;
    private final UsuarioRepository usuarioRepository;

    public ProfessorResponsavelController(ProfessorResponsavelService responsavelService,
                                          OfertaService ofertaService,
                                          UsuarioRepository usuarioRepository) {
        this.responsavelService = responsavelService;
        this.ofertaService = ofertaService;
        this.usuarioRepository = usuarioRepository;
    }

    // lista ofertas do professor responsável
    @GetMapping("/ofertas")
    public List<OfertaResponseDTO> listarOfertas() {
        Usuario professor = UsuarioLogadoUtil.getUsuarioLogado(usuarioRepository);
        List<Oferta> ofertas = responsavelService.listarOfertas(professor);
        List<OfertaResponseDTO> resposta = new ArrayList<>();
        for (Oferta oferta : ofertas) {
            resposta.add(new OfertaResponseDTO(oferta, ofertaService.contarAlunos(oferta)));
        }
        return resposta;
    }

    // PR.01/PR.02 RN-2 - dados do plano, relatório e documentação, somente leitura
    @GetMapping("/inscricoes/{inscricaoId}")
    public Map<String, Object> detalhes(@PathVariable Long inscricaoId) {
        Usuario professor = UsuarioLogadoUtil.getUsuarioLogado(usuarioRepository);
        InscricaoOferta inscricao = responsavelService.detalhes(inscricaoId, professor);

        Optional<PlanoTrabalho> optPlano = responsavelService.buscarPlano(inscricao);
        PlanoTrabalhoResponseDTO planoDto = null;
        if (optPlano.isPresent()) {
            planoDto = new PlanoTrabalhoResponseDTO(optPlano.get());
        }

        Optional<RelatorioFinal> optRelatorio = responsavelService.buscarRelatorio(inscricao);
        RelatorioFinalResponseDTO relatorioDto = null;
        if (optRelatorio.isPresent()) {
            relatorioDto = new RelatorioFinalResponseDTO(optRelatorio.get());
        }

        Optional<DocumentacaoEnsino> optDocumentacao = responsavelService.buscarDocumentacao(inscricao);
        DocumentacaoEnsinoResponseDTO documentacaoDto = null;
        if (optDocumentacao.isPresent()) {
            documentacaoDto = new DocumentacaoEnsinoResponseDTO(optDocumentacao.get());
        }

        Map<String, Object> resposta = new HashMap<>();
        resposta.put("inscricao", new InscricaoOfertaResponseDTO(inscricao));
        resposta.put("plano", planoDto);
        resposta.put("relatorio", relatorioDto);
        resposta.put("documentacao", documentacaoDto);
        return resposta;
    }

    // Download do PDF do relatório final (apoia a leitura exigida em PR.01 RN-2)
    @GetMapping("/inscricoes/{inscricaoId}/relatorio/arquivo")
    public ResponseEntity<byte[]> baixarArquivoRelatorio(@PathVariable Long inscricaoId) {
        Usuario professor = UsuarioLogadoUtil.getUsuarioLogado(usuarioRepository);
        byte[] arquivo = responsavelService.baixarArquivoRelatorio(inscricaoId, professor);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"relatorio-" + inscricaoId + ".pdf\"")
                .body(arquivo);
    }

    // Download do PDF da documentação de ensino (apoia a leitura exigida em PR.02 RN-2)
    @GetMapping("/inscricoes/{inscricaoId}/documentacao/arquivo")
    public ResponseEntity<byte[]> baixarArquivoDocumentacao(@PathVariable Long inscricaoId) {
        Usuario professor = UsuarioLogadoUtil.getUsuarioLogado(usuarioRepository);
        byte[] arquivo = responsavelService.baixarArquivoDocumentacao(inscricaoId, professor);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"documentacao-" + inscricaoId + ".pdf\"")
                .body(arquivo);
    }

    // PR.01 - conclui relatório (RN-3: exige parecer, frequência e nota)
    @PutMapping("/concluir-relatorio/{inscricaoId}")
    public InscricaoOfertaResponseDTO concluirRelatorio(@PathVariable Long inscricaoId,
                                                        @RequestBody ConcluirRelatorioRequestDTO dto) {
        Usuario professor = UsuarioLogadoUtil.getUsuarioLogado(usuarioRepository);
        InscricaoOferta inscricao = responsavelService.concluirRelatorio(
                inscricaoId, dto.getParecer(), dto.getFrequencia(), dto.getNota(), professor);
        return new InscricaoOfertaResponseDTO(inscricao);
    }

    // PR.02 - analisa documentação (RN-3: exige parecer, indicador de frequência e nota)
    @PutMapping("/analisar-documentacao/{inscricaoId}")
    public InscricaoOfertaResponseDTO analisarDocumentacao(@PathVariable Long inscricaoId,
                                                           @RequestBody AnalisarDocumentacaoRequestDTO dto) {
        Usuario professor = UsuarioLogadoUtil.getUsuarioLogado(usuarioRepository);
        InscricaoOferta inscricao = responsavelService.analisarDocumentacao(
                inscricaoId, dto.getParecer(), dto.getIndicadorFrequencia(), dto.getNota(), professor);
        return new InscricaoOfertaResponseDTO(inscricao);
    }

    // PR.03 - encerra oferta (RN-2: retorna estatísticas dos alunos concluídos)
    @PutMapping("/encerrar-oferta/{ofertaId}")
    public EncerramentoOfertaResponseDTO encerrarOferta(@PathVariable Long ofertaId,
                                                        @RequestBody Map<String, String> body) {
        Usuario professor = UsuarioLogadoUtil.getUsuarioLogado(usuarioRepository);
        String licoesAprendidas = body.get("licoesAprendidas");

        EstatisticasOfertaDTO estatisticas = responsavelService.calcularEstatisticas(ofertaService.buscarPorId(ofertaId));
        Oferta oferta = responsavelService.encerrarOferta(ofertaId, licoesAprendidas, professor);
        OfertaResponseDTO ofertaDto = new OfertaResponseDTO(oferta, ofertaService.contarAlunos(oferta));
        return new EncerramentoOfertaResponseDTO(ofertaDto, estatisticas);
    }
}
