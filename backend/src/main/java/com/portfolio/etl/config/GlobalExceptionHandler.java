package com.portfolio.etl.config;

import com.portfolio.etl.pipeline.service.PipelineLaunchException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Traduz excecoes da aplicacao em respostas ProblemDetail (RFC 7807),
 * padronizando o corpo de erro consumido pelo dashboard.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PipelineLaunchException.class)
    public ProblemDetail handleLaunchFailure(PipelineLaunchException ex) {
        log.error("Falha ao disparar o pipeline", ex);
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("Falha ao iniciar o pipeline");
        return problem;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUnexpected(Exception ex) {
        log.error("Erro inesperado", ex);
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno ao processar a requisicao.");
        problem.setTitle("Erro interno");
        return problem;
    }
}
