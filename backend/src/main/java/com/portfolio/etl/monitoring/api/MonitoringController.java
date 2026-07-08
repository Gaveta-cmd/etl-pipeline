package com.portfolio.etl.monitoring.api;

import com.portfolio.etl.monitoring.dto.ExecutionSummary;
import com.portfolio.etl.monitoring.dto.PipelineStats;
import com.portfolio.etl.monitoring.service.MonitoringService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Monitoramento", description = "Consulta de execucoes e metricas do pipeline")
@RestController
@RequestMapping("/api/pipeline")
public class MonitoringController {

    private final MonitoringService monitoringService;

    public MonitoringController(MonitoringService monitoringService) {
        this.monitoringService = monitoringService;
    }

    @Operation(summary = "Lista as execucoes mais recentes do pipeline")
    @GetMapping("/runs")
    public List<ExecutionSummary> listRuns(@RequestParam(defaultValue = "20") int limit) {
        int safeLimit = Math.max(1, Math.min(limit, 200));
        return monitoringService.listExecutions(safeLimit);
    }

    @Operation(summary = "Detalha uma execucao pelo seu runId")
    @GetMapping("/runs/{runId}")
    public ResponseEntity<ExecutionSummary> getRun(@PathVariable long runId) {
        return monitoringService.getExecution(runId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Metricas agregadas das execucoes e dos dados carregados")
    @GetMapping("/stats")
    public PipelineStats stats() {
        return monitoringService.getStats();
    }
}
