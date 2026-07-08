package com.portfolio.etl.repository;

import com.portfolio.etl.domain.model.Entrega;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface EntregaRepository extends JpaRepository<Entrega, Long> {

    @Query("SELECT COALESCE(SUM(e.custoTotal), 0) FROM Entrega e")
    BigDecimal sumCustoTotal();

    @Query("SELECT e FROM Entrega e JOIN FETCH e.escola JOIN FETCH e.produto")
    List<Entrega> findAllWithDimensions();

    @Query("""
            SELECT e.produto.categoria AS categoria,
                   COUNT(e)            AS quantidade,
                   COALESCE(SUM(e.custoTotal), 0) AS custoTotal
            FROM Entrega e
            GROUP BY e.produto.categoria
            ORDER BY custoTotal DESC
            """)
    List<CategoryAggregate> aggregateByCategoria();

    /**
     * Projecao da agregacao por categoria (via join com a dimensao Produto).
     */
    interface CategoryAggregate {
        String getCategoria();

        long getQuantidade();

        BigDecimal getCustoTotal();
    }
}
