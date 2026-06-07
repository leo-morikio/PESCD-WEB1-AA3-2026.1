package br.ufscar.dc.dsw.pescd.repository;

import br.ufscar.dc.dsw.pescd.model.InscricaoOferta;
import br.ufscar.dc.dsw.pescd.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InscricaoOfertaRepository extends JpaRepository<InscricaoOferta, Long> {
    List<InscricaoOferta> findByAluno(Usuario aluno);
}