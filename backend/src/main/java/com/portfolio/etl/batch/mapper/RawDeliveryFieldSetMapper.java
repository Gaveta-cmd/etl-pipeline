package com.portfolio.etl.batch.mapper;

import com.portfolio.etl.domain.dto.RawDeliveryRecord;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;

/**
 * Converte cada linha do CSV (FieldSet) em um RawDeliveryRecord, mantendo os
 * valores como texto. Nenhuma validacao acontece aqui: a leitura deve ser
 * resiliente, e a critica dos dados fica concentrada no processor.
 */
public class RawDeliveryFieldSetMapper implements FieldSetMapper<RawDeliveryRecord> {

    @Override
    public RawDeliveryRecord mapFieldSet(FieldSet fieldSet) {
        RawDeliveryRecord record = new RawDeliveryRecord();
        record.setEscola(fieldSet.readString("escola"));
        record.setMunicipio(fieldSet.readString("municipio"));
        record.setUf(fieldSet.readString("uf"));
        record.setProduto(fieldSet.readString("produto"));
        record.setCategoria(fieldSet.readString("categoria"));
        record.setQuantidadeKg(fieldSet.readString("quantidade_kg"));
        record.setCustoUnitario(fieldSet.readString("custo_unitario"));
        record.setDataEntrega(fieldSet.readString("data_entrega"));
        return record;
    }
}
