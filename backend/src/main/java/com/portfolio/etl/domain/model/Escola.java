package com.portfolio.etl.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Dimensao Escola: identifica de forma unica uma escola pela combinacao
 * nome + municipio + uf. Populada de forma incremental (get-or-create)
 * durante a carga do pipeline.
 */
@Entity
@Table(name = "escola", uniqueConstraints = @UniqueConstraint(
        name = "uk_escola_nome_municipio_uf", columnNames = {"nome", "municipio", "uf"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Escola {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String municipio;

    @Column(nullable = false, length = 2)
    private String uf;
}
