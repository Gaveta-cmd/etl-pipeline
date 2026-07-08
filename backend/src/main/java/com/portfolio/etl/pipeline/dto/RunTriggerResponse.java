package com.portfolio.etl.pipeline.dto;

import java.time.LocalDateTime;

/**
 * Resposta do disparo de uma execucao: identifica o run recem criado para que
 * o cliente possa acompanhar seu progresso.
 */
public record RunTriggerResponse(Long runId, String status, LocalDateTime startedAt) {
}
