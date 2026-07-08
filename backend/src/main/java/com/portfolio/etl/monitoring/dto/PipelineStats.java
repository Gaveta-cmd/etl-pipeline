package com.portfolio.etl.monitoring.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Metricas agregadas exibidas no topo do dashboard: visao geral das execucoes
 * e dos dados atualmente carregados.
 */
public record PipelineStats(
        long totalRuns,
        long completedRuns,
        long failedRuns,
        long runningRuns,
        long totalRecordsLoaded,
        BigDecimal totalCost,
        Long lastDurationMs,
        LocalDateTime lastRunAt,
        List<CategoryStat> categorias
) {
}
