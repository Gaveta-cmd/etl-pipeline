package com.portfolio.etl.batch.listener;

import com.portfolio.etl.domain.dto.RawDeliveryRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.annotation.OnSkipInProcess;
import org.springframework.stereotype.Component;

/**
 * Registra em log cada linha descartada na etapa Transform, com o motivo da
 * rejeicao. Isso da rastreabilidade sobre a qualidade dos dados de origem.
 */
@Slf4j
@Component
public class SkippedRecordListener {

    @OnSkipInProcess
    public void onSkipInProcess(RawDeliveryRecord item, Throwable cause) {
        log.warn("Registro descartado (escola='{}', produto='{}'): {}",
                item.getEscola(), item.getProduto(), cause.getMessage());
    }
}
