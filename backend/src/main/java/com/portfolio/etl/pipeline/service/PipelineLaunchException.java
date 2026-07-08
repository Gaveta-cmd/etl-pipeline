package com.portfolio.etl.pipeline.service;

/**
 * Falha ao iniciar uma execucao do pipeline (problema de infraestrutura ou de
 * configuracao do job, nao de dados).
 */
public class PipelineLaunchException extends RuntimeException {

    public PipelineLaunchException(String message, Throwable cause) {
        super(message, cause);
    }
}
