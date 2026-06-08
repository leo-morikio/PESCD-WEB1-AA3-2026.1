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

import java.util.Optional;

@Service
public class PlanoTrabalhoService {

    @Autowired
    private PlanoTrabalhoRepository planoRepository;

    @Autowired
    private InscricaoOfertaRepository inscricaoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private LogStatusService logStatusService;

    public void enviarPlano(Long inscricaoId, PlanoTrabalho plano, Long supervisorId) {
        Optional<InscricaoOferta> optInscricao = inscricaoRepository.findById(inscricaoId);
        if (!optInscricao.isPresent()) {
            throw new RuntimeException("Inscrição não encontrada");
        }
        InscricaoOferta inscricao = optInscricao.get();

        Optional<Usuario> optSupervisor = usuarioRepository.findById(supervisorId);
        if (!optSupervisor.isPresent()) {
            throw new RuntimeException("Professor supervisor não encontrado");
        }
        Usuario supervisor = optSupervisor.get();
        inscricao.setProfessorSupervisor(supervisor);

        plano.setInscricao(inscricao);
        planoRepository.save(plano);

        StatusAluno anterior = inscricao.getStatus();
        inscricao.setStatus(StatusAluno.PLANO_ENVIADO);
        inscricaoRepository.save(inscricao);

        logStatusService.registrar(inscricao, anterior, StatusAluno.PLANO_ENVIADO);
    }
}
