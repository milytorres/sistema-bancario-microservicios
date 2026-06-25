package com.example.cuenta.domain.exception;

/**
 * Excepción de negocio para F3 del PDF: un movimiento que excede el saldo disponible.
 * El mensaje debe ser exactamente "Saldo no disponible" (literal del enunciado).
 */
public class SaldoNoDisponibleException extends RuntimeException {

    public SaldoNoDisponibleException() {
        super("Saldo no disponible");
    }
}
