package br.ufscar.dc.dsw.pescd.repository;

import br.ufscar.dc.dsw.pescd.model.InscricaoOferta;
import br.ufscar.dc.dsw.pescd.model.RelatorioFinal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RelatorioFinalRepository extends JpaRepository<RelatorioFinal, Long> {
    Optional<RelatorioFinal> findByInscricao(InscricaoOferta inscricao);
}