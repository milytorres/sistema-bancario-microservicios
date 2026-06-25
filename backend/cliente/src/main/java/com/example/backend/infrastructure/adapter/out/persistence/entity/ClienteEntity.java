package com.example.backend.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@DiscriminatorValue("CLIENTE")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteEntity extends PersonaEntity {

    @Column(name = "cliente_id", nullable = false, unique = true, length = 50)
    private String clienteId;

    @Column(name = "contrasena", nullable = false)
    private String contrasena;

    @Column(nullable = false)
    private Boolean estado;
}
