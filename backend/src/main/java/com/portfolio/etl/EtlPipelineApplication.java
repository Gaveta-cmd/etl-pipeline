package com.portfolio.etl;

import com.portfolio.etl.config.EtlProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Ponto de entrada da aplicacao.
 *
 * O pipeline ETL nao roda no startup (spring.batch.job.enabled=false);
 * a execucao acontece sob demanda pela API REST, e o dashboard acompanha
 * cada run em tempo real.
 */
@EnableAsync
@EnableConfigurationProperties(EtlProperties.class)
@SpringBootApplication
public class EtlPipelineApplication {

    public static void main(String[] args) {
        SpringApplication.run(EtlPipelineApplication.class, args);
    }
}
