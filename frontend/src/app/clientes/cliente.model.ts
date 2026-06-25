/**
 * Modelo alineado a ClienteResponseDTO del backend (cliente, /clientes).
 * El campo "contrasena" NUNCA llega en el response, solo se usa en el request de creacion/edicion.
 */
export interface Cliente {
  id: number;
  nombre: string;
  genero: string | null;
  edad: number | null;
  identificacion: string;
  direccion: string | null;
  telefono: string | null;
  clienteId: string;
  estado: boolean;
}

/**
 * Alineado a ClienteRequestDTO (POST/PUT /clientes). Reglas de validacion del backend:
 * - nombre: @NotBlank, @Size(max = 120)
 * - genero: @Size(max = 20)
 * - edad: @Positive
 * - identificacion: @NotBlank, @Size(max = 30)
 * - direccion: @Size(max = 200)
 * - telefono: @Size(max = 20)
 * - clienteId: @NotBlank, @Size(max = 50)
 * - contrasena: @NotBlank
 * - estado: @NotNull
 */
export interface ClienteRequest {
  nombre: string;
  genero: string | null;
  edad: number | null;
  identificacion: string;
  direccion: string | null;
  telefono: string | null;
  clienteId: string;
  contrasena: string;
  estado: boolean;
}

/**
 * Alineado a ClientePatchDTO (PATCH /clientes/{id}). Todos los campos son opcionales en el backend.
 */
export type ClientePatch = Partial<Omit<ClienteRequest, 'clienteId'>>;
