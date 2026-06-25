package com.example.backend.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ClienteTest {

    @Test
    void debeEstarActivoCuandoEstadoEsTrue() {
        Cliente cliente = Cliente.builder()
                .estado(true)
                .build();

        assertThat(cliente.estaActivo()).isTrue();
    }

    @Test
    void debeEstarInactivoCuandoEstadoEsFalse() {
        Cliente cliente = Cliente.builder()
                .estado(false)
                .build();

        assertThat(cliente.estaActivo()).isFalse();
    }

    @Test
    void debeEstarInactivoCuandoEstadoEsNulo() {
        Cliente cliente = Cliente.builder()
                .estado(null)
                .build();

        assertThat(cliente.estaActivo()).isFalse();
    }

    @Test
    void debeConstruirClienteConTodosSusCamposHeredadosYPropios() {
        Cliente cliente = Cliente.builder()
                .id(1L)
                .nombre("Juan Perez")
                .genero("M")
                .edad(30)
                .identificacion("0102030405")
                .direccion("Av. Siempre Viva 123")
                .telefono("0991234567")
                .clienteId("CLI001")
                .contrasena("clave123")
                .estado(true)
                .build();

        assertThat(cliente.getId()).isEqualTo(1L);
        assertThat(cliente.getNombre()).isEqualTo("Juan Perez");
        assertThat(cliente.getGenero()).isEqualTo("M");
        assertThat(cliente.getEdad()).isEqualTo(30);
        assertThat(cliente.getIdentificacion()).isEqualTo("0102030405");
        assertThat(cliente.getDireccion()).isEqualTo("Av. Siempre Viva 123");
        assertThat(cliente.getTelefono()).isEqualTo("0991234567");
        assertThat(cliente.getClienteId()).isEqualTo("CLI001");
        assertThat(cliente.getContrasena()).isEqualTo("clave123");
        assertThat(cliente.estaActivo()).isTrue();
    }
}
