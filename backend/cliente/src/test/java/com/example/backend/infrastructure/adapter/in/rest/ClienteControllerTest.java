package com.example.backend.infrastructure.adapter.in.rest;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.backend.domain.exception.ClienteNoEncontradoException;
import com.example.backend.domain.model.Cliente;
import com.example.backend.domain.port.in.GestionarClienteUseCase;
import com.example.backend.infrastructure.adapter.in.rest.dto.ClientePatchDTO;
import com.example.backend.infrastructure.adapter.in.rest.dto.ClienteRequestDTO;
import com.example.backend.infrastructure.adapter.in.rest.dto.ClienteResponseDTO;
import com.example.backend.infrastructure.adapter.in.rest.mapper.ClienteRestMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ClienteController.class)
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GestionarClienteUseCase clienteUseCase;

    @MockBean
    private ClienteRestMapper mapper;

    private Cliente clienteDominio() {
        return Cliente.builder()
                .id(1L)
                .nombre("Juan Perez")
                .genero("M")
                .edad(30)
                .identificacion("0102030405")
                .direccion("Av. Siempre Viva")
                .telefono("0999999999")
                .clienteId("CLI001")
                .contrasena("secreta")
                .estado(true)
                .build();
    }

    private ClienteResponseDTO responseDTO() {
        return ClienteResponseDTO.builder()
                .id(1L)
                .nombre("Juan Perez")
                .genero("M")
                .edad(30)
                .identificacion("0102030405")
                .direccion("Av. Siempre Viva")
                .telefono("0999999999")
                .clienteId("CLI001")
                .estado(true)
                .build();
    }

    @Test
    void debeListarTodosLosClientes() throws Exception {
        when(clienteUseCase.listarTodos()).thenReturn(List.of(clienteDominio()));
        when(mapper.aResponseDTO(any(Cliente.class))).thenReturn(responseDTO());

        mockMvc.perform(get("/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].clienteId").value("CLI001"));
    }

    @Test
    void debeBuscarClientePorId() throws Exception {
        when(clienteUseCase.buscarPorId(1L)).thenReturn(clienteDominio());
        when(mapper.aResponseDTO(any(Cliente.class))).thenReturn(responseDTO());

        mockMvc.perform(get("/clientes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Juan Perez"));
    }

    @Test
    void debeBuscarClientePorClienteId() throws Exception {
        when(clienteUseCase.buscarPorClienteId("CLI001")).thenReturn(clienteDominio());
        when(mapper.aResponseDTO(any(Cliente.class))).thenReturn(responseDTO());

        mockMvc.perform(get("/clientes/cliente-id/CLI001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clienteId").value("CLI001"));
    }

    @Test
    void debeCrearClienteYRetornar201() throws Exception {
        ClienteRequestDTO request = ClienteRequestDTO.builder()
                .nombre("Juan Perez")
                .genero("M")
                .edad(30)
                .identificacion("0102030405")
                .direccion("Av. Siempre Viva")
                .telefono("0999999999")
                .clienteId("CLI001")
                .contrasena("secreta")
                .estado(true)
                .build();

        when(mapper.aDominio(any(ClienteRequestDTO.class))).thenReturn(clienteDominio());
        when(clienteUseCase.crear(any(Cliente.class))).thenReturn(clienteDominio());
        when(mapper.aResponseDTO(any(Cliente.class))).thenReturn(responseDTO());

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.clienteId").value("CLI001"));
    }

    @Test
    void debeActualizarClienteCompleto() throws Exception {
        ClienteRequestDTO request = ClienteRequestDTO.builder()
                .nombre("Juan Perez Actualizado")
                .genero("M")
                .edad(31)
                .identificacion("0102030405")
                .direccion("Av. Siempre Viva")
                .telefono("0999999999")
                .clienteId("CLI001")
                .contrasena("secreta")
                .estado(true)
                .build();

        when(mapper.aDominio(any(ClienteRequestDTO.class))).thenReturn(clienteDominio());
        when(clienteUseCase.actualizar(eq(1L), any(Cliente.class))).thenReturn(clienteDominio());
        when(mapper.aResponseDTO(any(Cliente.class))).thenReturn(responseDTO());

        mockMvc.perform(put("/clientes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void debeActualizarClienteParcialmente() throws Exception {
        ClientePatchDTO request = ClientePatchDTO.builder()
                .telefono("0988888888")
                .build();

        when(mapper.aDominio(any(ClientePatchDTO.class))).thenReturn(clienteDominio());
        when(clienteUseCase.actualizarParcial(eq(1L), any(Cliente.class))).thenReturn(clienteDominio());
        when(mapper.aResponseDTO(any(Cliente.class))).thenReturn(responseDTO());

        mockMvc.perform(patch("/clientes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void debeEliminarClienteYRetornar204() throws Exception {
        mockMvc.perform(delete("/clientes/1"))
                .andExpect(status().isNoContent());

        verify(clienteUseCase).eliminar(1L);
    }

    @Test
    void debeRetornar404CuandoClienteNoExiste() throws Exception {
        when(clienteUseCase.buscarPorId(anyLong())).thenThrow(new ClienteNoEncontradoException(99L));

        mockMvc.perform(get("/clientes/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }
}
