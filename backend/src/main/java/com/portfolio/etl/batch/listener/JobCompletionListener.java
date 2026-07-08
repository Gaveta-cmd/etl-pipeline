package com.portfolio.etl.batch.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Loga um resumo ao inicio e ao fim de cada execucao do job, consolidando as
 * metricas de leitura, escrita e descarte de todos os passos.
 */
@Slf4j
@Component
public class JobCompletionListener implements JobExecutionListener {

    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("Pipeline iniciado (runId={})", jobExecution.getId());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        long read = 0;
        long written = 0;
        long skipped = 0;
        for (StepExecution step : jobExecution.getStepExecutions()) {
            read += step.getReadCount();
            written += step.getWriteCount();
            skipped += step.getProcessSkipCount() + step.getReadSkipCount() + step.getWriteSkipCount();
        }

        LocalDateTime start = jobExecution.getStartTime();
        LocalDateTime end = jobExecution.getEndTime();
        long millis = (start != null && end != null) ? Duration.between(start, end).toMillis() : 0;

        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("Pipeline concluido (runId={}): lidos={}, carregados={}, descartados={}, duracao={}ms",
                    jobExecution.getId(), read, written, skipped, millis);
        } else {
            log.error("Pipeline finalizado com status {} (runId={}): {}",
                    jobExecution.getStatus(), jobExecution.getId(), jobExecution.getAllFailureExceptions());
        }
    }
}
