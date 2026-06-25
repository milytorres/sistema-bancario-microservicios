package com.example.backend.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public abstract class Persona {

    protected Long id;
    protected String nombre;
    protected String genero;
    protected Integer edad;
    protected String identificacion;
    protected String direccion;
    protected String telefono;
}
