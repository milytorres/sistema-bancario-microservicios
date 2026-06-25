package com.example.cuenta.infrastructure.adapter.out.client;

import com.example.cuenta.domain.model.ClienteInfo;
import com.example.cuenta.domain.port.out.ClienteClientPort;
import com.example.cuenta.infrastructure.adapter.out.client.dto.ClienteHttpResponse;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
public class ClienteHttpAdapter implements ClienteClientPort {

    private final RestTemplate restTemplate;
    private final String clienteServiceBaseUrl;

    public ClienteHttpAdapter(RestTemplate restTemplate,
                               @Value("${cliente-service.base-url}") String clienteServiceBaseUrl) {
        this.restTemplate = restTemplate;
        this.clienteServiceBaseUrl = clienteServiceBaseUrl;
    }

    @Override
    public Optional<ClienteInfo> buscarPorClienteId(String clienteId) {
        try {
            ClienteHttpResponse respuesta = restTemplate.getForObject(
                    clienteServiceBaseUrl + "/clientes/cliente-id/{clienteId}",
                    ClienteHttpResponse.class,
                    clienteId);
            if (respuesta == null) {
                return Optional.empty();
            }
            return Optional.of(new ClienteInfo(respuesta.clienteId(), respuesta.nombre()));
        } catch (HttpClientErrorException.NotFound ex) {
            return Optional.empty();
        }
    }
}
