package com.example.cuenta.infrastructure.config;

import com.example.cuenta.domain.exception.CuentaNoEncontradaException;
import com.example.cuenta.domain.exception.CuentaYaExisteException;
import com.example.cuenta.domain.exception.MovimientoNoEncontradoException;
import com.example.cuenta.domain.exception.ParametroReporteInvalidoException;
import com.example.cuenta.domain.exception.SaldoNoDisponibleException;
import com.example.cuenta.infrastructure.adapter.in.rest.dto.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(CuentaNoEncontradaException.class)
    public ResponseEntity<ErrorResponseDTO> manejarNoEncontrada(CuentaNoEncontradaException ex, HttpServletRequest request) {
        return construirRespuesta(HttpStatus.NOT_FOUND, ex.getMessage(), request, null);
    }

    @ExceptionHandler(CuentaYaExisteException.class)
    public ResponseEntity<ErrorResponseDTO> manejarYaExiste(CuentaYaExisteException ex, HttpServletRequest request) {
        return construirRespuesta(HttpStatus.CONFLICT, ex.getMessage(), request, null);
    }

    @ExceptionHandler(SaldoNoDisponibleException.class)
    public ResponseEntity<ErrorResponseDTO> manejarSaldoNoDisponible(SaldoNoDisponibleException ex, HttpServletRequest request) {
        return construirRespuesta(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), request, null);
    }

    @ExceptionHandler(MovimientoNoEncontradoException.class)
    public ResponseEntity<ErrorResponseDTO> manejarMovimientoNoEncontrado(MovimientoNoEncontradoException ex, HttpServletRequest request) {
        return construirRespuesta(HttpStatus.NOT_FOUND, ex.getMessage(), request, null);
    }

    @ExceptionHandler(ParametroReporteInvalidoException.class)
    public ResponseEntity<ErrorResponseDTO> manejarParametroReporteInvalido(ParametroReporteInvalidoException ex, HttpServletRequest request) {
        return construirRespuesta(HttpStatus.BAD_REQUEST, ex.getMessage(), request, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> manejarValidacion(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errores = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errores.put(error.getField(), error.getDefaultMessage()));
        return construirRespuesta(HttpStatus.BAD_REQUEST, "Error de validación en los datos enviados", request, errores);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDTO> manejarBodyMalformado(HttpMessageNotReadableException ex, HttpServletRequest request) {
        return construirRespuesta(HttpStatus.BAD_REQUEST, "El cuerpo de la petición es inválido o tiene un formato incorrecto", request, null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> manejarGenerico(Exception ex, HttpServletRequest request) {
        log.error("Error inesperado procesando {}", request.getRequestURI(), ex);
        return construirRespuesta(HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrió un error inesperado", request, null);
    }

    private ResponseEntity<ErrorResponseDTO> construirRespuesta(HttpStatus status, String mensaje,
                                                                  HttpServletRequest request, Map<String, String> errores) {
        ErrorResponseDTO body = ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(mensaje)
                .path(request.getRequestURI())
                .errores(errores)
                .build();
        return ResponseEntity.status(status).body(body);
    }
}
