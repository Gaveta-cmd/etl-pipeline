package com.portfolio.etl.monitoring.service;

import com.portfolio.etl.config.BatchConfig;
import com.portfolio.etl.monitoring.dto.CategoryStat;
import com.portfolio.etl.monitoring.dto.ExecutionSummary;
import com.portfolio.etl.monitoring.dto.PipelineStats;
import com.portfolio.etl.repository.EntregaRepository;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Consolida o monitoramento do pipeline lendo os metadados de execucao do
 * Spring Batch (via JobExplorer) e as metricas dos dados carregados (via
 * repositorio). Nao altera estado: e a camada de leitura do dashboard.
 */
@Service
public class MonitoringService {

    /** Teto de execucoes inspecionadas ao montar as estatisticas agregadas. */
    private static final int MAX_INSTANCES = 500;

    private final JobExplorer jobExplorer;
    private final EntregaRepository repository;

    public MonitoringService(JobExplorer jobExplorer, EntregaRepository repository) {
        this.jobExplorer = jobExplorer;
        this.repository = repository;
    }

    /**
     * Lista as execucoes mais recentes, da mais nova para a mais antiga.
     */
    public List<ExecutionSummary> listExecutions(int limit) {
        return loadExecutions(MAX_INSTANCES).stream()
                .map(this::toSummary)
                .limit(limit)
                .toList();
    }

    /**
     * Detalha uma execucao especifica.
     */
    public Optional<ExecutionSummary> getExecution(long runId) {
        return Optional.ofNullable(jobExplorer.getJobExecution(runId)).map(this::toSummary);
    }

    /**
     * Metricas agregadas para os cards do dashboard.
     */
    public PipelineStats getStats() {
        List<JobExecution> executions = loadExecutions(MAX_INSTANCES);

        long completed = executions.stream().filter(e -> e.getStatus() == BatchStatus.COMPLETED).count();
        long failed = executions.stream().filter(e -> e.getStatus() == BatchStatus.FAILED).count();
        long running = executions.stream().filter(e -> e.getStatus().isRunning()).count();

        Optional<JobExecution> last = executions.stream().findFirst();
        Long lastDuration = last.map(this::durationMs).orElse(null);

        List<CategoryStat> categorias = repository.aggregateByCategoria().stream()
                .map(a -> new CategoryStat(a.getCategoria(), a.getQuantidade(), a.getCustoTotal()))
                .toList();

        return new PipelineStats(
                executions.size(),
                completed,
                failed,
                running,
                repository.count(),
                repository.sumCustoTotal(),
                lastDuration,
                last.map(JobExecution::getStartTime).orElse(null),
                categorias
        );
    }

    /**
     * Carrega as execucoes do job, ordenadas da mais recente para a mais antiga.
     */
    private List<JobExecution> loadExecutions(int max) {
        try {
            List<JobInstance> instances = jobExplorer.getJobInstances(BatchConfig.JOB_NAME, 0, max);
            return instances.stream()
                    .flatMap(instance -> jobExplorer.getJobExecutions(instance).stream())
                    .sorted(Comparator.comparingLong(JobExecution::getId).reversed())
                    .toList();
        } catch (Exception ex) {
            // Job ainda nao executou: sem instancias registradas.
            if (ex instanceof NoSuchJobException) {
                return List.of();
            }
            throw ex;
        }
    }

    private ExecutionSummary toSummary(JobExecution execution) {
        long read = 0;
        long written = 0;
        long skipped = 0;
        for (StepExecution step : execution.getStepExecutions()) {
            read += step.getReadCount();
            written += step.getWriteCount();
            skipped += step.getProcessSkipCount() + step.getReadSkipCount() + step.getWriteSkipCount();
        }
        return new ExecutionSummary(
                execution.getId(),
                execution.getStatus().name(),
                execution.getExitStatus().getExitCode(),
                execution.getStartTime(),
                execution.getEndTime(),
                durationMs(execution),
                read,
                written,
                skipped
        );
    }

    private Long durationMs(JobExecution execution) {
        if (execution.getStartTime() == null || execution.getEndTime() == null) {
            return null;
        }
        return Duration.between(execution.getStartTime(), execution.getEndTime()).toMillis();
    }
}
