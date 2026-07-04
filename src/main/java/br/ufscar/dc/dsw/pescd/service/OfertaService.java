package br.ufscar.dc.dsw.pescd.service;

import br.ufscar.dc.dsw.pescd.model.Oferta;
import br.ufscar.dc.dsw.pescd.model.Usuario;
import br.ufscar.dc.dsw.pescd.repository.InscricaoOfertaRepository;
import br.ufscar.dc.dsw.pescd.repository.OfertaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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
        return ofertaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Oferta não encontrada: " + id));
    }

    public void salvar(Oferta oferta, Usuario criador) {
        // S.01 RN-1: se nome não preenchido, gera a partir do semestre
        if (oferta.getNome() == null || oferta.getNome().isBlank()) {
            oferta.setNome("PESCD " + oferta.getSemestre());
        }
        // S.01 RN-4: registra quem e quando criou
        oferta.setCriadoPor(criador);
        oferta.setCriadoEm(LocalDateTime.now());
        ofertaRepository.save(oferta);
    }

    public long contarAlunos(Oferta oferta) {
        return inscricaoRepository.countByOferta(oferta);
    }

    public List<Oferta> listarPorProfessorResponsavel(Usuario professor) {
        return ofertaRepository.findByProfessorResponsavel(professor);
    }
}
