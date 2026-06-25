package com.example.backend.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.backend.domain.exception.ClienteNoEncontradoException;
import com.example.backend.domain.exception.ClienteYaExisteException;
import com.example.backend.domain.model.Cliente;
import com.example.backend.domain.port.out.ClienteRepositoryPort;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepositoryPort clienteRepositoryPort;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ClienteService clienteService;

    @Test
    void debeLanzarExcepcionSiClienteIdYaExiste() {
        Cliente cliente = Cliente.builder()
                .clienteId("CLI001")
                .nombre("Juan Perez")
                .build();

        when(clienteRepositoryPort.existePorClienteId("CLI001")).thenReturn(true);

        assertThatThrownBy(() -> clienteService.crear(cliente))
                .isInstanceOf(ClienteYaExisteException.class)
                .hasMessageContaining("CLI001");

        verify(clienteRepositoryPort, never()).guardar(any());
    }

    @Test
    void debeCrearClienteCuandoClienteIdNoExiste() {
        Cliente cliente = Cliente.builder()
                .clienteId("CLI002")
                .nombre("Maria Lopez")
                .contrasena("1234")
                .estado(true)
                .build();

        when(clienteRepositoryPort.existePorClienteId("CLI002")).thenReturn(false);
        when(passwordEncoder.encode("1234")).thenReturn("HASH_SIMULADO");
        when(clienteRepositoryPort.guardar(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Cliente resultado = clienteService.crear(cliente);

        assertThat(resultado.getClienteId()).isEqualTo("CLI002");
        assertThat(resultado.getContrasena()).isEqualTo("HASH_SIMULADO");
        verify(clienteRepositoryPort).guardar(any());
    }

    @Test
    void debeLanzarExcepcionSiClienteNoExistePorId() {
        when(clienteRepositoryPort.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clienteService.buscarPorId(99L))
                .isInstanceOf(ClienteNoEncontradoException.class);
    }
}
