package com.portfolio.etl.batch;

import com.portfolio.etl.domain.model.Entrega;
import com.portfolio.etl.repository.EntregaRepository;
import com.portfolio.etl.repository.EscolaRepository;
import com.portfolio.etl.repository.ProdutoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.SyncTaskExecutor;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Executa o pipeline completo contra um banco H2 em memoria (perfil de teste)
 * e valida o carregamento relacional: das 40 linhas do CSV de exemplo, 5 sao
 * invalidas e descartadas, restando 35 entregas carregadas, cada uma com as
 * chaves estrangeiras de escola e produto populadas.
 */
@SpringBootTest
@Import(DeliveryEtlJobIntegrationTest.TestBatchConfig.class)
class DeliveryEtlJobIntegrationTest {

    private static final int LINHAS_VALIDAS = 35;
    private static final int LINHAS_INVALIDAS = 5;

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private EntregaRepository entregaRepository;

    @Autowired
    private EscolaRepository escolaRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Test
    void executaPipelineCarregaValidosEDescartaInvalidos() throws Exception {
        JobParameters params = new JobParametersBuilder()
                .addLong("run.timestamp", System.currentTimeMillis())
                .toJobParameters();

        JobExecution execution = jobLauncherTestUtils.launchJob(params);

        assertThat(execution.getExitStatus().getExitCode()).isEqualTo("COMPLETED");

        StepExecution step = execution.getStepExecutions().iterator().next();
        assertThat(step.getWriteCount()).isEqualTo(LINHAS_VALIDAS);
        assertThat(step.getProcessSkipCount()).isEqualTo(LINHAS_INVALIDAS);

        // Tabela-fato carregada
        assertThat(entregaRepository.count()).isEqualTo(LINHAS_VALIDAS);

        // Dimensoes populadas e sem duplicatas (produtos sao reaproveitados)
        assertThat(produtoRepository.count()).isGreaterThan(0).isLessThan(LINHAS_VALIDAS);
        assertThat(escolaRepository.count()).isGreaterThan(0);

        // Chaves estrangeiras efetivamente preenchidas
        Entrega amostra = entregaRepository.findAllWithDimensions().get(0);
        assertThat(amostra.getEscola()).isNotNull();
        assertThat(amostra.getEscola().getId()).isNotNull();
        assertThat(amostra.getProduto()).isNotNull();
        assertThat(amostra.getProduto().getId()).isNotNull();
        assertThat(amostra.getProduto().getCategoria()).isNotBlank();
    }

    /**
     * Fornece um JobLauncher sincrono (marcado como primary) para que o teste
     * possa validar o resultado logo apos a execucao, alem do JobLauncherTestUtils.
     */
    @TestConfiguration
    static class TestBatchConfig {

        @Bean
        @Primary
        JobLauncher syncJobLauncher(JobRepository jobRepository) throws Exception {
            TaskExecutorJobLauncher launcher = new TaskExecutorJobLauncher();
            launcher.setJobRepository(jobRepository);
            launcher.setTaskExecutor(new SyncTaskExecutor());
            launcher.afterPropertiesSet();
            return launcher;
        }

        @Bean
        JobLauncherTestUtils jobLauncherTestUtils(JobLauncher syncJobLauncher, Job deliveryEtlJob) {
            JobLauncherTestUtils utils = new JobLauncherTestUtils();
            utils.setJobLauncher(syncJobLauncher);
            utils.setJob(deliveryEtlJob);
            return utils;
        }
    }
}
