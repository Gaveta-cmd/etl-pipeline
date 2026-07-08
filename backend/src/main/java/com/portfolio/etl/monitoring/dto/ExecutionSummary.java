package com.portfolio.etl.monitoring.dto;

import java.time.LocalDateTime;

/**
 * Visao resumida de uma execucao (run) do pipeline, montada a partir dos
 * metadados do Spring Batch.
 */
public record ExecutionSummary(
        Long runId,
        String status,
        String exitCode,
        LocalDateTime startTime,
        LocalDateTime endTime,
        Long durationMs,
        long recordsRead,
        long recordsLoaded,
        long recordsSkipped
) {
}
