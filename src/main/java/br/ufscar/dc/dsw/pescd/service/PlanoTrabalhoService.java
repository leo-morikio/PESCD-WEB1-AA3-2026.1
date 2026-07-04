package br.ufscar.dc.dsw.pescd.service;

import br.ufscar.dc.dsw.pescd.model.InscricaoOferta;
import br.ufscar.dc.dsw.pescd.model.PlanoTrabalho;
import br.ufscar.dc.dsw.pescd.model.Usuario;
import br.ufscar.dc.dsw.pescd.model.enums.StatusAluno;
import br.ufscar.dc.dsw.pescd.repository.InscricaoOfertaRepository;
import br.ufscar.dc.dsw.pescd.repository.PlanoTrabalhoRepository;
import br.ufscar.dc.dsw.pescd.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public void enviarPlano(Long inscricaoId, PlanoTrabalho plano, Long supervisorId) {
        InscricaoOferta inscricao = inscricaoOfertaRepository.findById(inscricaoId)
                .orElseThrow(() -> new RuntimeException("Inscrição não encontrada"));

        Usuario supervisor = usuarioRepository.findById(supervisorId)
                .orElseThrow(() -> new RuntimeException("Professor supervisor não encontrado"));
        inscricao.setProfessorSupervisor(supervisor);

        plano.setInscricao(inscricao);
        planoTrabalhoRepository.save(plano);

        StatusAluno anterior = inscricao.getStatus();
        inscricao.setStatus(StatusAluno.PLANO_ENVIADO);
        inscricaoOfertaRepository.save(inscricao);

        logStatusService.registrar(inscricao, anterior, StatusAluno.PLANO_ENVIADO);
    }
}