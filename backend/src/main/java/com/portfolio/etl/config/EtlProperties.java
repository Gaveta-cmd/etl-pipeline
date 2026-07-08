package com.portfolio.etl.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Parametros de negocio do pipeline, externalizados via application.yml (prefixo "etl").
 *
 * @param source    fonte de dados a ser ingerida
 * @param chunkSize numero de registros por transacao no passo de carga
 */
@ConfigurationProperties(prefix = "etl")
public record EtlProperties(Source source, int chunkSize) {

    public record Source(String location) {
    }

    public int chunkSizeOrDefault() {
        return chunkSize > 0 ? chunkSize : 100;
    }
}
