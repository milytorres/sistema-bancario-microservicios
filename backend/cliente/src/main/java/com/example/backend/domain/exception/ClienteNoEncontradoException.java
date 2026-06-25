package com.example.backend.domain.exception;

public class ClienteNoEncontradoException extends RuntimeException {

    public ClienteNoEncontradoException(Long id) {
        super("Cliente no encontrado con id: " + id);
    }

    public ClienteNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
