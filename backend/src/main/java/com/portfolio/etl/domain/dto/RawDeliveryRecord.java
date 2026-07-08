package com.portfolio.etl.domain.dto;

import lombok.Data;

/**
 * Registro cru lido do CSV, ainda como texto e sem validacao.
 *
 * Representa a fronteira de entrada do pipeline (etapa Extract): os campos sao
 * mantidos como String para que a validacao e a conversao de tipos aconteçam de
 * forma controlada no processor (etapa Transform), permitindo descartar linhas
 * malformadas sem quebrar a leitura.
 */
@Data
public class RawDeliveryRecord {

    private String escola;
    private String municipio;
    private String uf;
    private String produto;
    private String categoria;
    private String quantidadeKg;
    private String custoUnitario;
    private String dataEntrega;
}
