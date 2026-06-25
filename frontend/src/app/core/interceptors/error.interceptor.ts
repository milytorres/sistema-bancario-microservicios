import { Injectable } from '@angular/core';
import {
  HttpErrorResponse,
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest
} from '@angular/common/http';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';

import { ErrorResponseDTO } from '../models/error-response.model';

/**
 * Interceptor centralizado de errores HTTP.
 * Ambos microservicios (cliente y cuenta) devuelven el mismo shape de error
 * (ErrorResponseDTO: timestamp, status, error, message, path, errores) desde su
 * GlobalExceptionHandler. Este interceptor lo parsea y muestra una notificacion
 * unica via MatSnackBar, evitando manejo disperso de errores en cada componente.
 */
@Injectable()
export class ErrorInterceptor implements HttpInterceptor {

  constructor(private readonly snackBar: MatSnackBar) { }

  intercept(req: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    return next.handle(req).pipe(
      catchError((error: HttpErrorResponse) => {
        this.mostrarError(error);
        throw error;
      })
    );
  }

  private mostrarError(error: HttpErrorResponse): void {
    const mensaje = this.extraerMensaje(error);
    this.snackBar.open(mensaje, 'Cerrar', {
      duration: 6000,
      panelClass: ['error-snackbar']
    });
  }

  private extraerMensaje(error: HttpErrorResponse): string {
    const body = error.error as ErrorResponseDTO | null;

    if (error.status === 0) {
      return 'No se pudo conectar con el servidor. Verifique su conexion.';
    }

    if (body && typeof body === 'object' && body.message) {
      if (body.errores && Object.keys(body.errores).length > 0) {
        const detalles = Object.values(body.errores).join(' | ');
        return `${body.message}: ${detalles}`;
      }
      return body.message;
    }

    return `Error inesperado (${error.status}). Intente nuevamente.`;
  }
}
