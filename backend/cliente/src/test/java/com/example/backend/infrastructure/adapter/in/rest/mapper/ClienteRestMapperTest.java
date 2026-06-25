package com.example.backend.infrastructure.adapter.in.rest.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.backend.domain.model.Cliente;
import com.example.backend.infrastructure.adapter.in.rest.dto.ClientePatchDTO;
import com.example.backend.infrastructure.adapter.in.rest.dto.ClienteRequestDTO;
import com.example.backend.infrastructure.adapter.in.rest.dto.ClienteResponseDTO;
import org.junit.jupiter.api.Test;

class ClienteRestMapperTest {

    private final ClienteRestMapper mapper = new ClienteRestMapper();

    @Test
    void debeMapearClienteRequestDTOADominioConTodosLosCampos() {
        ClienteRequestDTO request = ClienteRequestDTO.builder()
                .nombre("Ana Torres")
                .genero("F")
                .edad(25)
                .identificacion("0911223344")
                .direccion("Calle Falsa 123")
                .telefono("0987654321")
                .clienteId("CLI100")
                .contrasena("clave123")
                .estado(true)
                .build();

        Cliente cliente = mapper.aDominio(request);

        assertThat(cliente.getNombre()).isEqualTo("Ana Torres");
        assertThat(cliente.getGenero()).isEqualTo("F");
        assertThat(cliente.getEdad()).isEqualTo(25);
        assertThat(cliente.getIdentificacion()).isEqualTo("0911223344");
        assertThat(cliente.getDireccion()).isEqualTo("Calle Falsa 123");
        assertThat(cliente.getTelefono()).isEqualTo("0987654321");
        assertThat(cliente.getClienteId()).isEqualTo("CLI100");
        assertThat(cliente.getContrasena()).isEqualTo("clave123");
        assertThat(cliente.getEstado()).isTrue();
    }

    @Test
    void debeMapearClientePatchDTOADominioSinClienteId() {
        ClientePatchDTO patch = ClientePatchDTO.builder()
                .nombre("Nuevo Nombre")
                .telefono("0911111111")
                .contrasena("nuevaClave")
                .estado(false)
                .build();

        Cliente cliente = mapper.aDominio(patch);

        assertThat(cliente.getNombre()).isEqualTo("Nuevo Nombre");
        assertThat(cliente.getTelefono()).isEqualTo("0911111111");
        assertThat(cliente.getContrasena()).isEqualTo("nuevaClave");
        assertThat(cliente.getEstado()).isFalse();
        assertThat(cliente.getClienteId()).isNull();
    }

    @Test
    void debeMapearDominioAResponseDTOSinExponerContrasena() {
        Cliente cliente = Cliente.builder()
                .id(5L)
                .nombre("Carlos Ruiz")
                .genero("M")
                .edad(40)
                .identificacion("0922334455")
                .direccion("Av. Central")
                .telefono("0933333333")
                .clienteId("CLI200")
                .contrasena("noDebeSalir")
                .estado(true)
                .build();

        ClienteResponseDTO response = mapper.aResponseDTO(cliente);

        assertThat(response.id()).isEqualTo(5L);
        assertThat(response.nombre()).isEqualTo("Carlos Ruiz");
        assertThat(response.clienteId()).isEqualTo("CLI200");
        assertThat(response.estado()).isTrue();

        // Regla de seguridad del dominio: el DTO de respuesta NUNCA debe exponer la contraseña.
        // Se verifica reflexivamente que ClienteResponseDTO no declara ningún campo "contrasena".
        boolean tieneCampoContrasena = java.util.Arrays.stream(ClienteResponseDTO.class.getDeclaredFields())
                .anyMatch(field -> field.getName().toLowerCase().contains("contrasena"));
        assertThat(tieneCampoContrasena).isFalse();
    }
}
