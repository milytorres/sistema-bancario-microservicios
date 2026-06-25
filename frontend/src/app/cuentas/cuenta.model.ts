/** Alineado al enum TipoCuenta del backend (cuenta). */
export enum TipoCuenta {
  AHORRO = 'AHORRO',
  CORRIENTE = 'CORRIENTE'
}

/** Alineado a CuentaResponseDTO (backend/cuenta). */
export interface Cuenta {
  id: number;
  numeroCuenta: string;
  tipoCuenta: TipoCuenta;
  saldoInicial: number;
  saldoDisponible: number;
  estado: boolean;
  clienteId: string;
}

/**
 * Alineado a CuentaRequestDTO (POST/PUT /cuentas). Reglas de validacion del backend:
 * - numeroCuenta: @NotBlank
 * - tipoCuenta: @NotNull
 * - saldoInicial: @NotNull, @DecimalMin(0.0), @Digits(integer=13, fraction=2)
 * - estado: @NotNull
 * - clienteId: @NotBlank (clienteId de negocio, ej. CLI-001)
 */
export interface CuentaRequest {
  numeroCuenta: string;
  tipoCuenta: TipoCuenta;
  saldoInicial: number;
  estado: boolean;
  clienteId: string;
}

/** Alineado a CuentaPatchDTO (PATCH /cuentas/{id}): solo tipoCuenta y estado son editables parcialmente. */
export interface CuentaPatch {
  tipoCuenta?: TipoCuenta;
  estado?: boolean;
}
