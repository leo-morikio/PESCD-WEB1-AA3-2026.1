package br.ufscar.dc.dsw.pescd.repository;

import br.ufscar.dc.dsw.pescd.model.DocumentacaoEnsino;
import br.ufscar.dc.dsw.pescd.model.InscricaoOferta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DocumentacaoEnsinoRepository extends JpaRepository<DocumentacaoEnsino, Long> {
    Optional<DocumentacaoEnsino> findByInscricao(InscricaoOferta inscricao);
}