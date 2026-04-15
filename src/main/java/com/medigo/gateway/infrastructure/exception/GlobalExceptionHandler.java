package com.medigo.gateway.infrastructure.exception;

import com.medigo.gateway.application.dto.response.GatewayResponse;
import com.medigo.gateway.infrastructure.common.TraceIdHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * Manejador global de excepciones: transforma errores en respuestas estándar.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<GatewayResponse<Void>> handleMissingParam(
            MissingServletRequestParameterException ex) {
        return ResponseEntity.badRequest()
                .body(GatewayResponse.error(
                        "Parámetro requerido ausente: " + ex.getParameterName(),
                        TraceIdHolder.get()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<GatewayResponse<Void>> handleValidation(
            MethodArgumentNotValidException ex) {

        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        return ResponseEntity.badRequest()
                .body(GatewayResponse.error("Validación fallida: " + errors, TraceIdHolder.get()));
    }

    @ExceptionHandler(GatewayValidationException.class)
    public ResponseEntity<GatewayResponse<Void>> handleGatewayValidation(
            GatewayValidationException ex) {
        return ResponseEntity.status(ex.getStatus())
                .body(GatewayResponse.error(ex.getMessage(), TraceIdHolder.get()));
    }

    @ExceptionHandler(BackendUnavailableException.class)
    public ResponseEntity<GatewayResponse<Void>> handleBackendDown(
            BackendUnavailableException ex) {
        log.error("Backend no disponible: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(GatewayResponse.error(ex.getMessage(), TraceIdHolder.get()));
    }

    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<GatewayResponse<Void>> handleRateLimit(
            RateLimitExceededException ex) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .header("Retry-After", "60")
                .body(GatewayResponse.error(ex.getMessage(), TraceIdHolder.get()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<GatewayResponse<Void>> handleGeneric(Exception ex) {
        log.error("Error no manejado: {}", ex.getMessage(), ex);
        return ResponseEntity.internalServerError()
                .body(GatewayResponse.error("Error interno del gateway", TraceIdHolder.get()));
    }
}
