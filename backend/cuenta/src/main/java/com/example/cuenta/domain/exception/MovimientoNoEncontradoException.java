package com.example.cuenta.domain.exception;

public class MovimientoNoEncontradoException extends RuntimeException {

    public MovimientoNoEncontradoException(Long id) {
        super("Movimiento no encontrado con id: " + id);
    }
}
