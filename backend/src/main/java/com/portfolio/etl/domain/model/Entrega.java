package com.portfolio.etl.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Tabela-fato Entrega: cada registro carregado do CSV, ja validado e com o
 * custo total calculado. Relaciona-se com as dimensoes Escola e Produto por
 * chaves estrangeiras (nao nulas). A coluna run_id rastreia qual execucao do
 * pipeline inseriu a linha.
 */
@Entity
@Table(name = "entrega", indexes = {
        @Index(name = "idx_entrega_run_id", columnList = "run_id"),
        @Index(name = "idx_entrega_escola", columnList = "escola_id"),
        @Index(name = "idx_entrega_produto", columnList = "produto_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Entrega {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "escola_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_entrega_escola"))
    private Escola escola;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "produto_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_entrega_produto"))
    private Produto produto;

    @Column(name = "quantidade_kg", nullable = false, precision = 12, scale = 2)
    private BigDecimal quantidadeKg;

    @Column(name = "custo_unitario", nullable = false, precision = 12, scale = 2)
    private BigDecimal custoUnitario;

    @Column(name = "custo_total", nullable = false, precision = 14, scale = 2)
    private BigDecimal custoTotal;

    @Column(name = "data_entrega", nullable = false)
    private LocalDate dataEntrega;

    @Column(name = "run_id")
    private Long runId;
}
