package com.example.backend.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI().info(new Info()
                .title("Microservicio Cliente/Persona")
                .description("Gestión de clientes y personas del sistema bancario. CRUD de /clientes (F1) y búsqueda por clienteId para comunicación entre microservicios.")
                .version("1.0.0"));
    }
}
