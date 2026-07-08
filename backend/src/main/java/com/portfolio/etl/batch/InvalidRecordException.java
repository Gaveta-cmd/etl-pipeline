package com.portfolio.etl.batch;

/**
 * Sinaliza que um registro de entrada nao passou nas regras de validacao da
 * etapa Transform. E tratada como skip pelo passo tolerante a falhas: a linha
 * e descartada e contabilizada, sem abortar o pipeline.
 */
public class InvalidRecordException extends RuntimeException {

    public InvalidRecordException(String message) {
        super(message);
    }
}
