package com.example.cuenta.domain.exception;

public class CuentaYaExisteException extends RuntimeException {

    public CuentaYaExisteException(String numeroCuenta) {
        super("Ya existe una cuenta con numeroCuenta: " + numeroCuenta);
    }
}
