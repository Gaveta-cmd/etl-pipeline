package com.portfolio.etl.repository;

import com.portfolio.etl.domain.model.Escola;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EscolaRepository extends JpaRepository<Escola, Long> {

    Optional<Escola> findByNomeAndMunicipioAndUf(String nome, String municipio, String uf);
}
