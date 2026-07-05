package br.ufscar.dc.dsw.pescd.repository;

import br.ufscar.dc.dsw.pescd.model.InscricaoOferta;
import br.ufscar.dc.dsw.pescd.model.Oferta;
import br.ufscar.dc.dsw.pescd.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InscricaoOfertaRepository extends JpaRepository<InscricaoOferta, Long> {
    List<InscricaoOferta> findByAluno(Usuario aluno);
    List<InscricaoOferta> findByOferta(Oferta oferta);
    List<InscricaoOferta> findByProfessorSupervisor(Usuario professor);
    Optional<InscricaoOferta> findByAlunoAndOferta(Usuario aluno, Oferta oferta);
    long countByOferta(Oferta oferta);
    void deleteByAlunoId(Long alunoId);
}