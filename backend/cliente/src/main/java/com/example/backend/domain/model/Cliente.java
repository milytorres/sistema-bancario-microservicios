package com.example.backend.domain.model;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder(toBuilder = true)
public class Cliente extends Persona {

    private String clienteId;
    private String contrasena;
    private Boolean estado;

    public boolean estaActivo() {
        return Boolean.TRUE.equals(estado);
    }
}
