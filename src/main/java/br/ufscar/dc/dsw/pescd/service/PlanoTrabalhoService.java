package br.ufscar.dc.dsw.pescd.service;

import br.ufscar.dc.dsw.pescd.exception.NegocioException;
import br.ufscar.dc.dsw.pescd.exception.RecursoNaoEncontradoException;
import br.ufscar.dc.dsw.pescd.model.InscricaoOferta;
import br.ufscar.dc.dsw.pescd.model.PlanoTrabalho;
import br.ufscar.dc.dsw.pescd.model.Usuario;
import br.ufscar.dc.dsw.pescd.model.enums.StatusAluno;
import br.ufscar.dc.dsw.pescd.repository.InscricaoOfertaRepository;
import br.ufscar.dc.dsw.pescd.repository.PlanoTrabalhoRepository;
import br.ufscar.dc.dsw.pescd.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PlanoTrabalhoService {

    private final PlanoTrabalhoRepository planoTrabalhoRepository;

    private final InscricaoOfertaRepository inscricaoOfertaRepository;

    private final UsuarioRepository usuarioRepository;

    private final LogStatusService logStatusService;

    public PlanoTrabalhoService(PlanoTrabalhoRepository planoTrabalhoRepository, InscricaoOfertaRepository inscricaoOfertaRepository,
                                UsuarioRepository usuarioRepository, LogStatusService logStatusService) {
        this.planoTrabalhoRepository = planoTrabalhoRepository;
        this.inscricaoOfertaRepository = inscricaoOfertaRepository;
        this.usuarioRepository = usuarioRepository;
        this.logStatusService = logStatusService;
    }

    // AL.02 - aluno envia plano de trabalho e escolhe o professor supervisor
    public void enviarPlano(Long inscricaoId, PlanoTrabalho plano, Long supervisorId, Usuario aluno) {
        Optional<InscricaoOferta> optInscricao = inscricaoOfertaRepository.findById(inscricaoId);
        if (!optInscricao.isPresent()) {
            throw new RecursoNaoEncontradoException("Inscrição não encontrada: " + inscricaoId);
        }
        InscricaoOferta inscricao = optInscricao.get();

        if (!inscricao.getAluno().getId().equals(aluno.getId())) {
            throw new RecursoNaoEncontradoException("Inscrição não encontrada: " + inscricaoId);
        }

        if (inscricao.getStatus() != StatusAluno.NAO_ENVIADO) {
            throw new NegocioException("Já existe um plano de trabalho enviado para esta inscrição.");
        }

        Optional<Usuario> optSupervisor = usuarioRepository.findById(supervisorId);
        if (!optSupervisor.isPresent()) {
            throw new RecursoNaoEncontradoException("Professor supervisor não encontrado: " + supervisorId);
        }
        inscricao.setProfessorSupervisor(optSupervisor.get());

        plano.setInscricao(inscricao);
        planoTrabalhoRepository.save(plano);

        StatusAluno anterior = inscricao.getStatus();
        inscricao.setStatus(StatusAluno.PLANO_ENVIADO);
        inscricaoOfertaRepository.save(inscricao);

        logStatusService.registrar(inscricao, anterior, StatusAluno.PLANO_ENVIADO);
    }
}
