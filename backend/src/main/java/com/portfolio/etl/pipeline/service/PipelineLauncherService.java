package com.portfolio.etl.pipeline.service;

import com.portfolio.etl.pipeline.dto.RunTriggerResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Orquestra o disparo de uma nova execucao do pipeline.
 *
 * Cada disparo recebe um parametro de timestamp unico, o que garante uma nova
 * JobInstance a cada chamada (o Spring Batch recusaria parametros identicos ja
 * concluidos). O launcher e assincrono, entao o metodo retorna assim que o run
 * e criado, ainda em andamento.
 */
@Slf4j
@Service
public class PipelineLauncherService {

    private final JobLauncher asyncJobLauncher;
    private final Job deliveryEtlJob;

    public PipelineLauncherService(@Qualifier("asyncJobLauncher") JobLauncher asyncJobLauncher,
                                   Job deliveryEtlJob) {
        this.asyncJobLauncher = asyncJobLauncher;
        this.deliveryEtlJob = deliveryEtlJob;
    }

    public RunTriggerResponse launch() {
        JobParameters parameters = new JobParametersBuilder()
                .addLong("run.timestamp", System.currentTimeMillis())
                .toJobParameters();
        try {
            JobExecution execution = asyncJobLauncher.run(deliveryEtlJob, parameters);
            log.info("Disparo aceito (runId={}, status={})", execution.getId(), execution.getStatus());
            return new RunTriggerResponse(
                    execution.getId(),
                    execution.getStatus().name(),
                    execution.getStartTime());
        } catch (Exception ex) {
            throw new PipelineLaunchException("Nao foi possivel iniciar o pipeline: " + ex.getMessage(), ex);
        }
    }
}
