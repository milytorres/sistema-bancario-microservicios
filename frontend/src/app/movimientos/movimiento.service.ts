import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '../../environments/environment';
import { Movimiento, MovimientoRequest } from './movimiento.model';

/**
 * Servicio de acceso a los endpoints /movimientos del microservicio "cuenta" (puerto 8082).
 * Firmas alineadas a MovimientoController.
 */
@Injectable({
  providedIn: 'root'
})
export class MovimientoService {

  private readonly baseUrl = `${environment.cuentaApiUrl}/movimientos`;

  constructor(private readonly http: HttpClient) { }

  /** GET /movimientos?cuentaId={cuentaId} */
  listarPorCuenta(cuentaId: number): Observable<Movimiento[]> {
    const params = new HttpParams().set('cuentaId', cuentaId);
    return this.http.get<Movimiento[]>(this.baseUrl, { params });
  }

  /** GET /movimientos/{id} */
  buscarPorId(id: number): Observable<Movimiento> {
    return this.http.get<Movimiento>(`${this.baseUrl}/${id}`);
  }

  /** POST /movimientos */
  registrar(request: MovimientoRequest): Observable<Movimiento> {
    return this.http.post<Movimiento>(this.baseUrl, request);
  }
}
