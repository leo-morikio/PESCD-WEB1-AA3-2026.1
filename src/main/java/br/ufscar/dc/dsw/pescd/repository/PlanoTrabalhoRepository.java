package br.ufscar.dc.dsw.pescd.repository;

import br.ufscar.dc.dsw.pescd.model.InscricaoOferta;
import br.ufscar.dc.dsw.pescd.model.PlanoTrabalho;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlanoTrabalhoRepository extends JpaRepository<PlanoTrabalho, Long> {
    Optional<PlanoTrabalho> findByInscricao(InscricaoOferta inscricao);
}