package br.ufscar.dc.dsw.pescd.service;

import br.ufscar.dc.dsw.pescd.exception.NegocioException;
import br.ufscar.dc.dsw.pescd.exception.RecursoNaoEncontradoException;
import br.ufscar.dc.dsw.pescd.model.InscricaoOferta;
import br.ufscar.dc.dsw.pescd.model.LogStatus;
import br.ufscar.dc.dsw.pescd.model.PlanoTrabalho;
import br.ufscar.dc.dsw.pescd.model.RelatorioFinal;
import br.ufscar.dc.dsw.pescd.model.Usuario;
import br.ufscar.dc.dsw.pescd.model.enums.NotaAvaliacao;
import br.ufscar.dc.dsw.pescd.model.enums.StatusAluno;
import br.ufscar.dc.dsw.pescd.repository.InscricaoOfertaRepository;
import br.ufscar.dc.dsw.pescd.repository.LogStatusRepository;
import br.ufscar.dc.dsw.pescd.repository.PlanoTrabalhoRepository;
import br.ufscar.dc.dsw.pescd.repository.RelatorioFinalRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProfessorSupervisorService {

    private final InscricaoOfertaRepository inscricaoRepository;
    private final LogStatusRepository logStatusRepository;
    private final PlanoTrabalhoRepository planoTrabalhoRepository;
    private final RelatorioFinalRepository relatorioFinalRepository;
    private final ArquivoStorageService arquivoStorageService;

    public ProfessorSupervisorService(InscricaoOfertaRepository inscricaoRepository,
                                      LogStatusRepository logStatusRepository,
                                      PlanoTrabalhoRepository planoTrabalhoRepository,
                                      RelatorioFinalRepository relatorioFinalRepository,
                                      ArquivoStorageService arquivoStorageService) {
        this.inscricaoRepository = inscricaoRepository;
        this.logStatusRepository = logStatusRepository;
        this.planoTrabalhoRepository = planoTrabalhoRepository;
        this.relatorioFinalRepository = relatorioFinalRepository;
        this.arquivoStorageService = arquivoStorageService;
    }

    // PS.01 - lista inscrições que este professor supervisiona
    public List<InscricaoOferta> listarInscricoes(Usuario professor) {
        return inscricaoRepository.findByProfessorSupervisor(professor);
    }

    // PS.02/PS.03 RN-2 - dados do plano e do relatório para leitura antes de aprovar
    public InscricaoOferta detalhes(Long inscricaoId, Usuario professor) {
        return buscarInscricaoSupervisionada(inscricaoId, professor);
    }

    public Optional<PlanoTrabalho> buscarPlano(InscricaoOferta inscricao) {
        return planoTrabalhoRepository.findByInscricao(inscricao);
    }

    public Optional<RelatorioFinal> buscarRelatorio(InscricaoOferta inscricao) {
        return relatorioFinalRepository.findByInscricao(inscricao);
    }

    // Download do PDF do plano de trabalho (PS.02)
    public byte[] baixarArquivoPlano(Long inscricaoId, Usuario professor) {
        InscricaoOferta inscricao = buscarInscricaoSupervisionada(inscricaoId, professor);
        Optional<PlanoTrabalho> optPlano = planoTrabalhoRepository.findByInscricao(inscricao);
        if (!optPlano.isPresent()) {
            throw new RecursoNaoEncontradoException("Plano de trabalho não encontrado para a inscrição: " + inscricaoId);
        }
        return arquivoStorageService.lerArquivo(optPlano.get().getCaminhoArquivo());
    }

    // Download do PDF do relatório final (PS.03)
    public byte[] baixarArquivoRelatorio(Long inscricaoId, Usuario professor) {
        InscricaoOferta inscricao = buscarInscricaoSupervisionada(inscricaoId, professor);
        Optional<RelatorioFinal> optRelatorio = relatorioFinalRepository.findByInscricao(inscricao);
        if (!optRelatorio.isPresent()) {
            throw new RecursoNaoEncontradoException("Relatório final não encontrado para a inscrição: " + inscricaoId);
        }
        return arquivoStorageService.lerArquivo(optRelatorio.get().getCaminhoArquivo());
    }

    // PS.02 - aprova plano de trabalho, registrando o parecer do supervisor
    public InscricaoOferta aprovarPlano(Long inscricaoId, String parecer, Usuario professor) {
        InscricaoOferta inscricao = buscarInscricaoSupervisionada(inscricaoId, professor);

        if (inscricao.getStatus() != StatusAluno.PLANO_ENVIADO) {
            throw new NegocioException("Só é possível aprovar um plano que foi enviado.");
        }
        if (parecer == null || parecer.isBlank()) {
            throw new NegocioException("O parecer é obrigatório para aprovar o plano.");
        }

        Optional<PlanoTrabalho> optPlano = planoTrabalhoRepository.findByInscricao(inscricao);
        if (!optPlano.isPresent()) {
            throw new RecursoNaoEncontradoException("Plano de trabalho não encontrado para a inscrição: " + inscricaoId);
        }
        PlanoTrabalho plano = optPlano.get();
        plano.setParecerSupervisor(parecer);
        planoTrabalhoRepository.save(plano);

        String statusAnterior = inscricao.getStatus().name();
        inscricao.setStatus(StatusAluno.PLANO_APROVADO);
        inscricaoRepository.save(inscricao);
        registrarLog(inscricao, statusAnterior, StatusAluno.PLANO_APROVADO.name(), professor);
        return inscricao;
    }

    // PS.03 - aprova relatório final, registrando parecer, frequência e sugestão de nota
    public InscricaoOferta aprovarRelatorio(Long inscricaoId, String parecer, Integer indicadorFrequencia,
                                            NotaAvaliacao notaFinal, Usuario professor) {
        InscricaoOferta inscricao = buscarInscricaoSupervisionada(inscricaoId, professor);

        if (inscricao.getStatus() != StatusAluno.RELATORIO_ENVIADO) {
            throw new NegocioException("Só é possível aprovar um relatório que foi enviado.");
        }
        if (parecer == null || parecer.isBlank()) {
            throw new NegocioException("O parecer é obrigatório para aprovar o relatório.");
        }
        if (indicadorFrequencia == null) {
            throw new NegocioException("O indicador de frequência é obrigatório.");
        }
        if (notaFinal == null) {
            throw new NegocioException("A sugestão de nota é obrigatória.");
        }

        Optional<RelatorioFinal> optRelatorio = relatorioFinalRepository.findByInscricao(inscricao);
        if (!optRelatorio.isPresent()) {
            throw new RecursoNaoEncontradoException("Relatório final não encontrado para a inscrição: " + inscricaoId);
        }
        RelatorioFinal relatorio = optRelatorio.get();
        relatorio.setParecerSupervisor(parecer);
        relatorio.setIndicadorFrequencia(indicadorFrequencia);
        relatorio.setNotaFinal(notaFinal);
        relatorioFinalRepository.save(relatorio);

        String statusAnterior = inscricao.getStatus().name();
        inscricao.setStatus(StatusAluno.RELATORIO_APROVADO_SUPERVISOR);
        inscricaoRepository.save(inscricao);
        registrarLog(inscricao, statusAnterior, StatusAluno.RELATORIO_APROVADO_SUPERVISOR.name(), professor);
        return inscricao;
    }

    private InscricaoOferta buscarInscricao(Long inscricaoId) {
        Optional<InscricaoOferta> opt = inscricaoRepository.findById(inscricaoId);
        if (!opt.isPresent()) {
            throw new RecursoNaoEncontradoException("Inscrição não encontrada: " + inscricaoId);
        }
        return opt.get();
    }

    // Garante que a inscrição pertence a este professor supervisor (evita ver dados de outro supervisor)
    private InscricaoOferta buscarInscricaoSupervisionada(Long inscricaoId, Usuario professor) {
        InscricaoOferta inscricao = buscarInscricao(inscricaoId);
        Usuario supervisor = inscricao.getProfessorSupervisor();
        if (supervisor == null || !supervisor.getId().equals(professor.getId())) {
            throw new RecursoNaoEncontradoException("Inscrição não encontrada: " + inscricaoId);
        }
        return inscricao;
    }

    private void registrarLog(InscricaoOferta inscricao, String anterior, String novo, Usuario professor) {
        logStatusRepository.save(new LogStatus(inscricao, anterior, novo, professor));
    }
}
