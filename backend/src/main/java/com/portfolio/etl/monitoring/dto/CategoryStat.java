package com.portfolio.etl.monitoring.dto;

import java.math.BigDecimal;

/**
 * Total de registros e custo carregados por categoria de produto.
 */
public record CategoryStat(String categoria, long quantidade, BigDecimal custoTotal) {
}
