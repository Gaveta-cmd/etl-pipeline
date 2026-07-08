package com.portfolio.etl.domain.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Registro ja validado e normalizado pela etapa Transform, com o custo total
 * calculado. Carrega os atributos de dimensao (escola/produto) ainda como
 * valores escalares; o writer resolve as dimensoes e monta a tabela-fato.
 */
public record ValidatedDelivery(
        String escolaNome,
        String municipio,
        String uf,
        String produtoNome,
        String categoria,
        BigDecimal quantidadeKg,
        BigDecimal custoUnitario,
        BigDecimal custoTotal,
        LocalDate dataEntrega
) {
}
