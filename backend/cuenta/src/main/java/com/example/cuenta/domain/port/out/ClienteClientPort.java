package com.example.cuenta.domain.port.out;

import com.example.cuenta.domain.model.ClienteInfo;
import java.util.Optional;

/**
 * Puerto de salida hacia el microservicio Cliente/Persona (comunicación entre microservicios vía HTTP).
 */
public interface ClienteClientPort {

    Optional<ClienteInfo> buscarPorClienteId(String clienteId);
}
