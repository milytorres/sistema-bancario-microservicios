package com.example.cuenta.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI().info(new Info()
                .title("Microservicio Cuenta/Movimiento")
                .description("Gestión de cuentas y movimientos del sistema bancario. CRUD de /cuentas y /movimientos (F1, F2), validación de saldo (F3) y reporte de estado de cuenta (F4).")
                .version("1.0.0"));
    }
}
