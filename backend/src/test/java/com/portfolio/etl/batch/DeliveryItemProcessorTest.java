package com.portfolio.etl.batch;

import com.portfolio.etl.batch.processor.DeliveryItemProcessor;
import com.portfolio.etl.domain.dto.RawDeliveryRecord;
import com.portfolio.etl.domain.dto.ValidatedDelivery;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DeliveryItemProcessorTest {

    private final DeliveryItemProcessor processor = new DeliveryItemProcessor();

    private RawDeliveryRecord valid() {
        RawDeliveryRecord raw = new RawDeliveryRecord();
        raw.setEscola("  EMEF Teste ");
        raw.setMunicipio("Sao Paulo");
        raw.setUf("sp");
        raw.setProduto("Arroz");
        raw.setCategoria("Graos");
        raw.setQuantidadeKg("100");
        raw.setCustoUnitario("4.90");
        raw.setDataEntrega("2026-06-01");
        return raw;
    }

    @Test
    void transformaNormalizaECalculaCustoTotal() {
        ValidatedDelivery result = processor.process(valid());

        assertThat(result.escolaNome()).isEqualTo("EMEF Teste");
        assertThat(result.uf()).isEqualTo("SP");
        assertThat(result.custoTotal()).isEqualByComparingTo(new BigDecimal("490.00"));
        assertThat(result.dataEntrega()).isEqualTo(LocalDate.of(2026, 6, 1));
    }

    @Test
    void descartaCampoObrigatorioAusente() {
        RawDeliveryRecord raw = valid();
        raw.setEscola("");
        assertThatThrownBy(() -> processor.process(raw))
                .isInstanceOf(InvalidRecordException.class);
    }

    @Test
    void descartaQuantidadeNaoPositiva() {
        RawDeliveryRecord raw = valid();
        raw.setQuantidadeKg("-10");
        assertThatThrownBy(() -> processor.process(raw))
                .isInstanceOf(InvalidRecordException.class);
    }

    @Test
    void descartaCustoNaoNumerico() {
        RawDeliveryRecord raw = valid();
        raw.setCustoUnitario("abc");
        assertThatThrownBy(() -> processor.process(raw))
                .isInstanceOf(InvalidRecordException.class);
    }

    @Test
    void descartaDataEmFormatoInvalido() {
        RawDeliveryRecord raw = valid();
        raw.setDataEntrega("01/06/2026");
        assertThatThrownBy(() -> processor.process(raw))
                .isInstanceOf(InvalidRecordException.class);
    }
}
