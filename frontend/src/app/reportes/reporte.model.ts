/**
 * Shape EXACTO de la respuesta JSON real del backend (ReporteMovimientoResponseDTO),
 * cuyas claves son literales en español con mayusculas y espacios via @JsonProperty.
 * Nunca asumir camelCase aqui: estas claves deben coincidir 1:1 con el JSON recibido.
 */
export interface ReporteMovimientoApi {
  'Fecha': string;
  'Cliente': string;
  'Numero Cuenta': string;
  'Tipo': string;
  'Saldo Inicial': number;
  'Estado': boolean;
  'Movimiento': number;
  'Saldo Disponible': number;
}

/**
 * Modelo TypeScript en camelCase para el reporte de movimientos, usado por los
 * componentes del frontend. ReporteService mapea explicitamente ReporteMovimientoApi
 * (claves en español) a esta interfaz.
 */
export interface ReporteMovimiento {
  fecha: string;
  cliente: string;
  numeroCuenta: string;
  tipo: string;
  saldoInicial: number;
  estado: boolean;
  movimiento: number;
  saldoDisponible: number;
}

/** Filtro de busqueda alineado a los query params de GET /reportes. */
export interface ReporteFiltro {
  /** clienteId de negocio (ej. CLI-001), no el id interno. */
  clienteId: string;
  fechaDesde: string;
  fechaHasta: string;
}
