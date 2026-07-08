package com.portfolio.etl.config;

import com.portfolio.etl.batch.InvalidRecordException;
import com.portfolio.etl.batch.listener.JobCompletionListener;
import com.portfolio.etl.batch.listener.SkippedRecordListener;
import com.portfolio.etl.batch.mapper.RawDeliveryFieldSetMapper;
import com.portfolio.etl.batch.processor.DeliveryItemProcessor;
import com.portfolio.etl.batch.writer.EntregaItemWriter;
import com.portfolio.etl.domain.dto.RawDeliveryRecord;
import com.portfolio.etl.domain.dto.ValidatedDelivery;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Define o pipeline ETL como um Job do Spring Batch com um passo orientado a
 * chunk:
 *
 *   Extract  -> FlatFileItemReader le o CSV configurado em etl.source.location
 *   Transform-> DeliveryItemProcessor valida, normaliza e calcula o custo total
 *   Load     -> EntregaItemWriter resolve as dimensoes (Escola/Produto) e grava
 *               a tabela-fato Entrega com as chaves estrangeiras
 *
 * O passo e tolerante a falhas: registros invalidos (InvalidRecordException)
 * sao descartados ate um limite de seguranca, sem abortar a execucao.
 */
@Configuration
public class BatchConfig {

    public static final String JOB_NAME = "deliveryEtlJob";
    private static final int SKIP_LIMIT = 1000;

    private final EtlProperties properties;

    public BatchConfig(EtlProperties properties) {
        this.properties = properties;
    }

    @Bean
    public FlatFileItemReader<RawDeliveryRecord> deliveryReader(ResourceLoader resourceLoader) {
        Resource resource = resourceLoader.getResource(properties.source().location());
        return new FlatFileItemReaderBuilder<RawDeliveryRecord>()
                .name("deliveryReader")
                .resource(resource)
                .linesToSkip(1)
                .delimited()
                .names("escola", "municipio", "uf", "produto", "categoria",
                        "quantidade_kg", "custo_unitario", "data_entrega")
                .fieldSetMapper(new RawDeliveryFieldSetMapper())
                .build();
    }

    @Bean
    public Step deliveryLoadStep(JobRepository jobRepository,
                                 PlatformTransactionManager transactionManager,
                                 FlatFileItemReader<RawDeliveryRecord> deliveryReader,
                                 DeliveryItemProcessor deliveryProcessor,
                                 EntregaItemWriter entregaWriter,
                                 SkippedRecordListener skippedRecordListener) {
        return new StepBuilder("deliveryLoadStep", jobRepository)
                .<RawDeliveryRecord, ValidatedDelivery>chunk(properties.chunkSizeOrDefault(), transactionManager)
                .reader(deliveryReader)
                .processor(deliveryProcessor)
                .writer(entregaWriter)
                .faultTolerant()
                .skip(InvalidRecordException.class)
                .skipLimit(SKIP_LIMIT)
                .listener(entregaWriter)
                .listener(skippedRecordListener)
                .build();
    }

    @Bean
    public Job deliveryEtlJob(JobRepository jobRepository,
                              Step deliveryLoadStep,
                              JobCompletionListener jobCompletionListener) {
        return new JobBuilder(JOB_NAME, jobRepository)
                .listener(jobCompletionListener)
                .start(deliveryLoadStep)
                .build();
    }
}
