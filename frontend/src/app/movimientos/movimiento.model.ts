/** Alineado al enum TipoMovimiento del backend (cuenta). */
export enum TipoMovimiento {
  DEPOSITO = 'DEPOSITO',
  RETIRO = 'RETIRO'
}

/** Alineado a MovimientoResponseDTO (backend/cuenta). */
export interface Movimiento {
  id: number;
  fecha: string;
  tipoMovimiento: TipoMovimiento;
  valor: number;
  saldo: number;
  cuentaId: number;
}

/**
 * Alineado a MovimientoRequestDTO (POST /movimientos). Reglas de validacion del backend:
 * - cuentaId: @NotNull
 * - tipoMovimiento: @NotNull
 * - valor: @NotNull, @DecimalMin(0.01) (debe ser mayor a cero), @Digits(integer=13, fraction=2)
 */
export interface MovimientoRequest {
  cuentaId: number;
  tipoMovimiento: TipoMovimiento;
  valor: number;
}
