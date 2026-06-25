/**
 * Alineado a ErrorResponseDTO de ambos backends (cliente y cuenta), generado por sus
 * respectivos GlobalExceptionHandler. Mismo shape en los dos microservicios.
 */
export interface ErrorResponseDTO {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
  errores: Record<string, string> | null;
}
