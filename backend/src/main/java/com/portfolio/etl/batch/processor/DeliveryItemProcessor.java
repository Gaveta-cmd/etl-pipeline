package com.portfolio.etl.batch.processor;

import com.portfolio.etl.batch.InvalidRecordException;
import com.portfolio.etl.domain.dto.RawDeliveryRecord;
import com.portfolio.etl.domain.dto.ValidatedDelivery;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Etapa Transform: valida, normaliza e enriquece cada registro.
 *
 * Regras aplicadas:
 * - campos obrigatorios (escola, municipio, produto, categoria) nao podem ser vazios;
 * - quantidade e custo unitario devem ser numericos e nao negativos;
 * - a data deve estar no formato ISO (yyyy-MM-dd);
 * - a UF e normalizada para maiusculas e os textos sao aparados.
 *
 * Registros que violam qualquer regra lancam InvalidRecordException e sao
 * descartados pelo skip policy do passo, sem interromper o pipeline. O custo
 * total (quantidade x custo unitario) e calculado aqui. A saida e um
 * ValidatedDelivery escalar; a resolucao das dimensoes fica a cargo do writer.
 */
@Component
public class DeliveryItemProcessor implements ItemProcessor<RawDeliveryRecord, ValidatedDelivery> {

    private static final DateTimeFormatter ISO_DATE = DateTimeFormatter.ISO_LOCAL_DATE;

    @Override
    public ValidatedDelivery process(RawDeliveryRecord raw) {
        String escola = requireText(raw.getEscola(), "escola");
        String municipio = requireText(raw.getMunicipio(), "municipio");
        String produto = requireText(raw.getProduto(), "produto");
        String categoria = requireText(raw.getCategoria(), "categoria");
        String uf = normalizeUf(raw.getUf());

        BigDecimal quantidade = parsePositiveAmount(raw.getQuantidadeKg(), "quantidade_kg");
        BigDecimal custoUnitario = parseNonNegativeAmount(raw.getCustoUnitario(), "custo_unitario");
        LocalDate dataEntrega = parseDate(raw.getDataEntrega());

        BigDecimal custoTotal = quantidade.multiply(custoUnitario).setScale(2, RoundingMode.HALF_UP);

        return new ValidatedDelivery(
                escola, municipio, uf, produto, categoria,
                quantidade, custoUnitario, custoTotal, dataEntrega);
    }

    private String requireText(String value, String field) {
        if (!StringUtils.hasText(value)) {
            throw new InvalidRecordException("campo obrigatorio ausente: " + field);
        }
        return value.trim();
    }

    private String normalizeUf(String uf) {
        String normalized = requireText(uf, "uf").toUpperCase();
        if (normalized.length() != 2) {
            throw new InvalidRecordException("uf invalida: " + uf);
        }
        return normalized;
    }

    private BigDecimal parsePositiveAmount(String value, String field) {
        BigDecimal amount = parseNonNegativeAmount(value, field);
        if (amount.signum() <= 0) {
            throw new InvalidRecordException(field + " deve ser maior que zero: " + value);
        }
        return amount;
    }

    private BigDecimal parseNonNegativeAmount(String value, String field) {
        if (!StringUtils.hasText(value)) {
            throw new InvalidRecordException("campo obrigatorio ausente: " + field);
        }
        try {
            BigDecimal amount = new BigDecimal(value.trim());
            if (amount.signum() < 0) {
                throw new InvalidRecordException(field + " nao pode ser negativo: " + value);
            }
            return amount.setScale(2, RoundingMode.HALF_UP);
        } catch (NumberFormatException ex) {
            throw new InvalidRecordException(field + " nao e numerico: " + value);
        }
    }

    private LocalDate parseDate(String value) {
        if (!StringUtils.hasText(value)) {
            throw new InvalidRecordException("campo obrigatorio ausente: data_entrega");
        }
        try {
            return LocalDate.parse(value.trim(), ISO_DATE);
        } catch (DateTimeParseException ex) {
            throw new InvalidRecordException("data_entrega em formato invalido (esperado yyyy-MM-dd): " + value);
        }
    }
}
