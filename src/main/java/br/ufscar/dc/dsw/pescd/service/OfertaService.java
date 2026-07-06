package br.ufscar.dc.dsw.pescd.service;

import br.ufscar.dc.dsw.pescd.exception.NegocioException;
import br.ufscar.dc.dsw.pescd.exception.RecursoNaoEncontradoException;
import br.ufscar.dc.dsw.pescd.model.Oferta;
import br.ufscar.dc.dsw.pescd.model.Usuario;
import br.ufscar.dc.dsw.pescd.repository.InscricaoOfertaRepository;
import br.ufscar.dc.dsw.pescd.repository.OfertaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OfertaService {

    private final OfertaRepository ofertaRepository;

    private final InscricaoOfertaRepository inscricaoRepository;

    public OfertaService(OfertaRepository ofertaRepository, InscricaoOfertaRepository inscricaoRepository) {
        this.ofertaRepository = ofertaRepository;
        this.inscricaoRepository = inscricaoRepository;
    }

    public List<Oferta> listarTodasOrdenadas() {
        return ofertaRepository.findAllByOrderBySemestreDesc();
    }

    public Oferta buscarPorId(Long id) {
        Optional<Oferta> opt = ofertaRepository.findById(id);
        if (!opt.isPresent()) {
            throw new RecursoNaoEncontradoException("Oferta não encontrada: " + id);
        }
        return opt.get();
    }

    public void salvar(Oferta oferta, Usuario criador) {
        // S.01 RN-2: data de fim não pode ser anterior à data de início
        if (oferta.getDataFim() != null && oferta.getDataInicio() != null
                && oferta.getDataFim().isBefore(oferta.getDataInicio())) {
            throw new NegocioException("A data de fim não pode ser anterior à data de início.");
        }
        // S.01 RN-1: se nome não preenchido, gera a partir do semestre
        if (oferta.getNome() == null || oferta.getNome().isBlank()) {
            oferta.setNome("PESCD " + oferta.getSemestre());
        }
        // S.01 RN-4: registra quem e quando criou (apenas em criação nova)
        if (oferta.getId() == null) {
            // S.01 RN-5: toda oferta precisa ter um professor responsável ao ser criada
            if (oferta.getProfessorResponsavel() == null) {
                throw new NegocioException("A oferta precisa ter um professor responsável.");
            }
            oferta.setCriadoPor(criador);
            oferta.setCriadoEm(LocalDateTime.now());
        }
        ofertaRepository.save(oferta);
    }

    public long contarAlunos(Oferta oferta) {
        return inscricaoRepository.countByOferta(oferta);
    }

    public List<Oferta> listarPorProfessorResponsavel(Usuario professor) {
        return ofertaRepository.findByProfessorResponsavel(professor);
    }
}
