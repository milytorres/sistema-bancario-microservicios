import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '../../environments/environment';
import { Cuenta, CuentaPatch, CuentaRequest } from './cuenta.model';

/**
 * Servicio de acceso a los endpoints /cuentas del microservicio "cuenta" (puerto 8082).
 * Firmas alineadas a CuentaController.
 */
@Injectable({
  providedIn: 'root'
})
export class CuentaService {

  private readonly baseUrl = `${environment.cuentaApiUrl}/cuentas`;

  constructor(private readonly http: HttpClient) { }

  /** GET /cuentas */
  listar(): Observable<Cuenta[]> {
    return this.http.get<Cuenta[]>(this.baseUrl);
  }

  /** GET /cuentas/{id} */
  buscarPorId(id: number): Observable<Cuenta> {
    return this.http.get<Cuenta>(`${this.baseUrl}/${id}`);
  }

  /** POST /cuentas */
  crear(request: CuentaRequest): Observable<Cuenta> {
    return this.http.post<Cuenta>(this.baseUrl, request);
  }

  /** PUT /cuentas/{id} */
  actualizar(id: number, request: CuentaRequest): Observable<Cuenta> {
    return this.http.put<Cuenta>(`${this.baseUrl}/${id}`, request);
  }

  /** PATCH /cuentas/{id} */
  actualizarParcial(id: number, request: CuentaPatch): Observable<Cuenta> {
    return this.http.patch<Cuenta>(`${this.baseUrl}/${id}`, request);
  }

  /** DELETE /cuentas/{id} */
  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
