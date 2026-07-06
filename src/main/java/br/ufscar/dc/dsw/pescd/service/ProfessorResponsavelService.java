package br.ufscar.dc.dsw.pescd.service;

import br.ufscar.dc.dsw.pescd.dto.EstatisticasOfertaDTO;
import br.ufscar.dc.dsw.pescd.exception.NegocioException;
import br.ufscar.dc.dsw.pescd.exception.RecursoNaoEncontradoException;
import br.ufscar.dc.dsw.pescd.model.DocumentacaoEnsino;
import br.ufscar.dc.dsw.pescd.model.InscricaoOferta;
import br.ufscar.dc.dsw.pescd.model.LogStatus;
import br.ufscar.dc.dsw.pescd.model.Oferta;
import br.ufscar.dc.dsw.pescd.model.PlanoTrabalho;
import br.ufscar.dc.dsw.pescd.model.RelatorioFinal;
import br.ufscar.dc.dsw.pescd.model.Usuario;
import br.ufscar.dc.dsw.pescd.model.enums.NotaAvaliacao;
import br.ufscar.dc.dsw.pescd.model.enums.StatusAluno;
import br.ufscar.dc.dsw.pescd.model.enums.StatusOferta;
import br.ufscar.dc.dsw.pescd.repository.DocumentacaoEnsinoRepository;
import br.ufscar.dc.dsw.pescd.repository.InscricaoOfertaRepository;
import br.ufscar.dc.dsw.pescd.repository.LogStatusRepository;
import br.ufscar.dc.dsw.pescd.repository.OfertaRepository;
import br.ufscar.dc.dsw.pescd.repository.PlanoTrabalhoRepository;
import br.ufscar.dc.dsw.pescd.repository.RelatorioFinalRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ProfessorResponsavelService {

    private final InscricaoOfertaRepository inscricaoRepository;
    private final OfertaRepository ofertaRepository;
    private final LogStatusRepository logStatusRepository;
    private final RelatorioFinalRepository relatorioFinalRepository;
    private final DocumentacaoEnsinoRepository documentacaoEnsinoRepository;
    private final PlanoTrabalhoRepository planoTrabalhoRepository;
    private final ArquivoStorageService arquivoStorageService;

    public ProfessorResponsavelService(InscricaoOfertaRepository inscricaoRepository,
                                       OfertaRepository ofertaRepository,
                                       LogStatusRepository logStatusRepository,
                                       RelatorioFinalRepository relatorioFinalRepository,
                                       DocumentacaoEnsinoRepository documentacaoEnsinoRepository,
                                       PlanoTrabalhoRepository planoTrabalhoRepository,
                                       ArquivoStorageService arquivoStorageService) {
        this.inscricaoRepository = inscricaoRepository;
        this.ofertaRepository = ofertaRepository;
        this.logStatusRepository = logStatusRepository;
        this.relatorioFinalRepository = relatorioFinalRepository;
        this.documentacaoEnsinoRepository = documentacaoEnsinoRepository;
        this.planoTrabalhoRepository = planoTrabalhoRepository;
        this.arquivoStorageService = arquivoStorageService;
    }

    // PR.01/PR.02 RN-2 - dados do plano, relatório e documentação para leitura antes de decidir
    public InscricaoOferta detalhes(Long inscricaoId, Usuario professor) {
        return buscarInscricaoDaOferta(inscricaoId, professor);
    }

    public Optional<PlanoTrabalho> buscarPlano(InscricaoOferta inscricao) {
        return planoTrabalhoRepository.findByInscricao(inscricao);
    }

    public Optional<RelatorioFinal> buscarRelatorio(InscricaoOferta inscricao) {
        return relatorioFinalRepository.findByInscricao(inscricao);
    }

    public Optional<DocumentacaoEnsino> buscarDocumentacao(InscricaoOferta inscricao) {
        return documentacaoEnsinoRepository.findByInscricao(inscricao);
    }

    // Download do PDF do relatório final (apoia a leitura exigida em PR.01 RN-2)
    public byte[] baixarArquivoRelatorio(Long inscricaoId, Usuario professor) {
        InscricaoOferta inscricao = buscarInscricaoDaOferta(inscricaoId, professor);
        Optional<RelatorioFinal> optRelatorio = relatorioFinalRepository.findByInscricao(inscricao);
        if (!optRelatorio.isPresent()) {
            throw new RecursoNaoEncontradoException("Relatório final não encontrado para a inscrição: " + inscricaoId);
        }
        return arquivoStorageService.lerArquivo(optRelatorio.get().getCaminhoArquivo());
    }

    // Download do PDF da documentação de ensino (apoia a leitura exigida em PR.02 RN-2)
    public byte[] baixarArquivoDocumentacao(Long inscricaoId, Usuario professor) {
        InscricaoOferta inscricao = buscarInscricaoDaOferta(inscricaoId, professor);
        Optional<DocumentacaoEnsino> optDocumentacao = documentacaoEnsinoRepository.findByInscricao(inscricao);
        if (!optDocumentacao.isPresent()) {
            throw new RecursoNaoEncontradoException("Documentação não encontrada para a inscrição: " + inscricaoId);
        }
        return arquivoStorageService.lerArquivo(optDocumentacao.get().getCaminhoArquivo());
    }

    // PR.01 - conclui o relatório do aluno, registrando parecer, frequência e nota finais
    public InscricaoOferta concluirRelatorio(Long inscricaoId, String parecer, Integer frequencia,
                                             NotaAvaliacao nota, Usuario professor) {
        InscricaoOferta inscricao = buscarInscricaoDaOferta(inscricaoId, professor);

        if (inscricao.getStatus() != StatusAluno.RELATORIO_APROVADO_SUPERVISOR) {
            throw new NegocioException("O relatório precisa estar aprovado pelo supervisor antes de concluir.");
        }
        if (parecer == null || parecer.isBlank()) {
            throw new NegocioException("O parecer é obrigatório para concluir o relatório.");
        }
        if (frequencia == null) {
            throw new NegocioException("A frequência é obrigatória.");
        }
        if (nota == null) {
            throw new NegocioException("A nota é obrigatória.");
        }

        Optional<RelatorioFinal> optRelatorio = relatorioFinalRepository.findByInscricao(inscricao);
        if (!optRelatorio.isPresent()) {
            throw new RecursoNaoEncontradoException("Relatório final não encontrado para a inscrição: " + inscricaoId);
        }
        RelatorioFinal relatorio = optRelatorio.get();
        relatorio.setParecerResponsavel(parecer);
        relatorio.setIndicadorFrequencia(frequencia);
        relatorio.setNotaFinal(nota);
        relatorioFinalRepository.save(relatorio);

        String statusAnterior = inscricao.getStatus().name();
        inscricao.setStatus(StatusAluno.CONCLUIDO_RESPONSAVEL);
        inscricaoRepository.save(inscricao);
        registrarLog(inscricao, statusAnterior, StatusAluno.CONCLUIDO_RESPONSAVEL.name(), professor);
        return inscricao;
    }

    // PR.02 - analisa documentação e conclui, registrando parecer, indicador de frequência e nota
    public InscricaoOferta analisarDocumentacao(Long inscricaoId, String parecer, Integer indicadorFrequencia,
                                                NotaAvaliacao nota, Usuario professor) {
        InscricaoOferta inscricao = buscarInscricaoDaOferta(inscricaoId, professor);

        if (inscricao.getStatus() != StatusAluno.DOCUMENTACAO_ENVIADA) {
            throw new NegocioException("Só é possível analisar uma documentação que foi enviada.");
        }
        if (parecer == null || parecer.isBlank()) {
            throw new NegocioException("O parecer é obrigatório para analisar a documentação.");
        }
        if (indicadorFrequencia == null) {
            throw new NegocioException("O indicador de frequência é obrigatório.");
        }
        if (nota == null) {
            throw new NegocioException("A nota é obrigatória.");
        }

        Optional<DocumentacaoEnsino> optDocumentacao = documentacaoEnsinoRepository.findByInscricao(inscricao);
        if (!optDocumentacao.isPresent()) {
            throw new RecursoNaoEncontradoException("Documentação não encontrada para a inscrição: " + inscricaoId);
        }
        DocumentacaoEnsino documentacao = optDocumentacao.get();
        documentacao.setParecerResponsavel(parecer);
        documentacao.setIndicadorFrequencia(indicadorFrequencia);
        documentacao.setNotaFinal(nota);
        documentacaoEnsinoRepository.save(documentacao);

        String statusAnterior = inscricao.getStatus().name();
        inscricao.setStatus(StatusAluno.CONCLUIDO_RESPONSAVEL);
        inscricaoRepository.save(inscricao);
        registrarLog(inscricao, statusAnterior, StatusAluno.CONCLUIDO_RESPONSAVEL.name(), professor);
        return inscricao;
    }

    // PR.03 - encerra oferta (só se todas inscrições concluídas)
    public Oferta encerrarOferta(Long ofertaId, String licoesAprendidas, Usuario professor) {
        Optional<Oferta> optOferta = ofertaRepository.findById(ofertaId);
        if (!optOferta.isPresent()) {
            throw new RecursoNaoEncontradoException("Oferta não encontrada: " + ofertaId);
        }
        Oferta oferta = optOferta.get();

        List<InscricaoOferta> inscricoes = inscricaoRepository.findByOferta(oferta);
        boolean todosConcluidos = true;
        for (InscricaoOferta inscricao : inscricoes) {
            if (inscricao.getStatus() != StatusAluno.CONCLUIDO_RESPONSAVEL) {
                todosConcluidos = false;
            }
        }

        if (!todosConcluidos) {
            throw new NegocioException("Nem todos os alunos concluíram. Não é possível encerrar.");
        }

        oferta.setStatus(StatusOferta.AGUARDANDO_ENCERRAMENTO_SECRETARIO);
        oferta.setEncerradoPor(professor);
        oferta.setEncerradoEm(LocalDateTime.now());
        oferta.setLicoesAprendidas(licoesAprendidas);
        ofertaRepository.save(oferta);
        return oferta;
    }

    // PR.03 RN-2 - estatísticas dos alunos concluídos (estágio x documentação, frequência média, notas)
    public EstatisticasOfertaDTO calcularEstatisticas(Oferta oferta) {
        List<InscricaoOferta> inscricoes = inscricaoRepository.findByOferta(oferta);

        long porEstagio = 0;
        long porDocumentacao = 0;
        long somaFrequencias = 0;
        long totalComFrequencia = 0;
        long quantidadeA = 0;
        long quantidadeB = 0;
        long quantidadeC = 0;
        long quantidadeD = 0;
        long quantidadeE = 0;

        for (InscricaoOferta inscricao : inscricoes) {
            if (inscricao.getStatus() != StatusAluno.CONCLUIDO_RESPONSAVEL) {
                continue;
            }

            Optional<RelatorioFinal> optRelatorio = relatorioFinalRepository.findByInscricao(inscricao);
            if (optRelatorio.isPresent()) {
                porEstagio++;
                RelatorioFinal relatorio = optRelatorio.get();
                if (relatorio.getIndicadorFrequencia() != null) {
                    somaFrequencias = somaFrequencias + relatorio.getIndicadorFrequencia();
                    totalComFrequencia++;
                }
                NotaAvaliacao nota = relatorio.getNotaFinal();
                if (nota == NotaAvaliacao.A) { quantidadeA++; }
                if (nota == NotaAvaliacao.B) { quantidadeB++; }
                if (nota == NotaAvaliacao.C) { quantidadeC++; }
                if (nota == NotaAvaliacao.D) { quantidadeD++; }
                if (nota == NotaAvaliacao.E) { quantidadeE++; }
                continue;
            }

            Optional<DocumentacaoEnsino> optDocumentacao = documentacaoEnsinoRepository.findByInscricao(inscricao);
            if (optDocumentacao.isPresent()) {
                porDocumentacao++;
                DocumentacaoEnsino documentacao = optDocumentacao.get();
                if (documentacao.getIndicadorFrequencia() != null) {
                    somaFrequencias = somaFrequencias + documentacao.getIndicadorFrequencia();
                    totalComFrequencia++;
                }
                NotaAvaliacao nota = documentacao.getNotaFinal();
                if (nota == NotaAvaliacao.A) { quantidadeA++; }
                if (nota == NotaAvaliacao.B) { quantidadeB++; }
                if (nota == NotaAvaliacao.C) { quantidadeC++; }
                if (nota == NotaAvaliacao.D) { quantidadeD++; }
                if (nota == NotaAvaliacao.E) { quantidadeE++; }
            }
        }

        EstatisticasOfertaDTO estatisticas = new EstatisticasOfertaDTO();
        estatisticas.setPorEstagio(porEstagio);
        estatisticas.setPorDocumentacao(porDocumentacao);
        estatisticas.setTotalConcluidos(porEstagio + porDocumentacao);
        if (totalComFrequencia > 0) {
            estatisticas.setFrequenciaMedia((double) somaFrequencias / totalComFrequencia);
        }
        estatisticas.setQuantidadeNotaA(quantidadeA);
        estatisticas.setQuantidadeNotaB(quantidadeB);
        estatisticas.setQuantidadeNotaC(quantidadeC);
        estatisticas.setQuantidadeNotaD(quantidadeD);
        estatisticas.setQuantidadeNotaE(quantidadeE);
        return estatisticas;
    }

    // lista ofertas do professor responsável
    public List<Oferta> listarOfertas(Usuario professor) {
        return ofertaRepository.findByProfessorResponsavel(professor);
    }

    private InscricaoOferta buscarInscricao(Long inscricaoId) {
        Optional<InscricaoOferta> opt = inscricaoRepository.findById(inscricaoId);
        if (!opt.isPresent()) {
            throw new RecursoNaoEncontradoException("Inscrição não encontrada: " + inscricaoId);
        }
        return opt.get();
    }

    // Garante que a inscrição pertence a uma oferta da qual este professor é o responsável
    private InscricaoOferta buscarInscricaoDaOferta(Long inscricaoId, Usuario professor) {
        InscricaoOferta inscricao = buscarInscricao(inscricaoId);
        Usuario responsavel = inscricao.getOferta().getProfessorResponsavel();
        if (responsavel == null || !responsavel.getId().equals(professor.getId())) {
            throw new RecursoNaoEncontradoException("Inscrição não encontrada: " + inscricaoId);
        }
        return inscricao;
    }

    private void registrarLog(InscricaoOferta inscricao, String anterior, String novo, Usuario professor) {
        logStatusRepository.save(new LogStatus(inscricao, anterior, novo, professor));
    }
}
