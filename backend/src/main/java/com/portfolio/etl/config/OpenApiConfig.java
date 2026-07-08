package com.portfolio.etl.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Metadados da documentacao OpenAPI, expostos em /swagger-ui.html.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI etlOpenApi() {
        return new OpenAPI().info(new Info()
                .title("ETL Pipeline API")
                .version("1.0.0")
                .description("""
                        API do pipeline ETL de entregas de merenda escolar.
                        Permite disparar execucoes do pipeline (Spring Batch) e
                        consultar o monitoramento de cada run: status, contagens
                        e metricas dos dados carregados.""")
                .contact(new Contact().name("Davi Augusto").url("https://github.com/Gaveta-cmd")));
    }
}
