package com.portfolio.etl.batch.writer;

import com.portfolio.etl.domain.dto.ValidatedDelivery;
import com.portfolio.etl.domain.model.Entrega;
import com.portfolio.etl.domain.model.Escola;
import com.portfolio.etl.domain.model.Produto;
import com.portfolio.etl.repository.EntregaRepository;
import com.portfolio.etl.repository.EscolaRepository;
import com.portfolio.etl.repository.ProdutoRepository;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Etapa Load dimensional: para cada registro validado, resolve (get-or-create)
 * as dimensoes Escola e Produto e persiste a tabela-fato Entrega apontando para
 * elas via chave estrangeira.
 *
 * As dimensoes ja resolvidas sao mantidas em cache durante a execucao para
 * evitar buscas repetidas e insercoes duplicadas dentro do mesmo run. O cache
 * e limpo no inicio de cada passo.
 */
@Component
public class EntregaItemWriter implements ItemWriter<ValidatedDelivery> {

    private final EscolaRepository escolaRepository;
    private final ProdutoRepository produtoRepository;
    private final EntregaRepository entregaRepository;

    private final Map<String, Escola> escolaCache = new HashMap<>();
    private final Map<String, Produto> produtoCache = new HashMap<>();

    private Long runId;

    public EntregaItemWriter(EscolaRepository escolaRepository,
                             ProdutoRepository produtoRepository,
                             EntregaRepository entregaRepository) {
        this.escolaRepository = escolaRepository;
        this.produtoRepository = produtoRepository;
        this.entregaRepository = entregaRepository;
    }

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        this.runId = stepExecution.getJobExecutionId();
        escolaCache.clear();
        produtoCache.clear();
    }

    @Override
    public void write(Chunk<? extends ValidatedDelivery> chunk) {
        List<Entrega> entregas = new ArrayList<>(chunk.size());
        for (ValidatedDelivery item : chunk) {
            Escola escola = resolveEscola(item);
            Produto produto = resolveProduto(item);
            entregas.add(Entrega.builder()
                    .escola(escola)
                    .produto(produto)
                    .quantidadeKg(item.quantidadeKg())
                    .custoUnitario(item.custoUnitario())
                    .custoTotal(item.custoTotal())
                    .dataEntrega(item.dataEntrega())
                    .runId(runId)
                    .build());
        }
        entregaRepository.saveAll(entregas);
    }

    private Escola resolveEscola(ValidatedDelivery item) {
        String key = item.escolaNome() + "|" + item.municipio() + "|" + item.uf();
        return escolaCache.computeIfAbsent(key, k ->
                escolaRepository.findByNomeAndMunicipioAndUf(item.escolaNome(), item.municipio(), item.uf())
                        .orElseGet(() -> escolaRepository.save(Escola.builder()
                                .nome(item.escolaNome())
                                .municipio(item.municipio())
                                .uf(item.uf())
                                .build())));
    }

    private Produto resolveProduto(ValidatedDelivery item) {
        return produtoCache.computeIfAbsent(item.produtoNome(), nome ->
                produtoRepository.findByNome(nome)
                        .orElseGet(() -> produtoRepository.save(Produto.builder()
                                .nome(nome)
                                .categoria(item.categoria())
                                .build())));
    }
}
