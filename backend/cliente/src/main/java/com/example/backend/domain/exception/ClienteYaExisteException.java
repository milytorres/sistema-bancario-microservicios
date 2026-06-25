package com.example.backend.domain.exception;

public class ClienteYaExisteException extends RuntimeException {

    public ClienteYaExisteException(String clienteId) {
        super("Ya existe un cliente con clienteId: " + clienteId);
    }
}
