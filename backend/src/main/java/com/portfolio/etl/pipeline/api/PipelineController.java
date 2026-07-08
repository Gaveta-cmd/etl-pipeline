package com.portfolio.etl.pipeline.api;

import com.portfolio.etl.pipeline.dto.RunTriggerResponse;
import com.portfolio.etl.pipeline.service.PipelineLauncherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Pipeline", description = "Disparo de execucoes do pipeline ETL")
@RestController
@RequestMapping("/api/pipeline")
public class PipelineController {

    private final PipelineLauncherService launcher;

    public PipelineController(PipelineLauncherService launcher) {
        this.launcher = launcher;
    }

    @Operation(summary = "Dispara uma nova execucao do pipeline (assincrona)")
    @PostMapping("/run")
    public ResponseEntity<RunTriggerResponse> run() {
        RunTriggerResponse response = launcher.launch();
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }
}
