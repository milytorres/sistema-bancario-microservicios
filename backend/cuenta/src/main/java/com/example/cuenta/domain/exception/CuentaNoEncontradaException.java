package com.example.cuenta.domain.exception;

public class CuentaNoEncontradaException extends RuntimeException {

    public CuentaNoEncontradaException(Long id) {
        super("Cuenta no encontrada con id: " + id);
    }

    public CuentaNoEncontradaException(String mensaje) {
        super(mensaje);
    }
}
