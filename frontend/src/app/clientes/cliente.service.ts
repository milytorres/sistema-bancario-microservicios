import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '../../environments/environment';
import { Cliente, ClientePatch, ClienteRequest } from './cliente.model';

/**
 * Servicio de acceso a los endpoints /clientes del microservicio "cliente" (puerto 8081).
 * Firmas alineadas a ClienteController.
 */
@Injectable({
  providedIn: 'root'
})
export class ClienteService {

  private readonly baseUrl = `${environment.clienteApiUrl}/clientes`;

  constructor(private readonly http: HttpClient) { }

  /** GET /clientes */
  listar(): Observable<Cliente[]> {
    return this.http.get<Cliente[]>(this.baseUrl);
  }

  /** GET /clientes/{id} */
  buscarPorId(id: number): Observable<Cliente> {
    return this.http.get<Cliente>(`${this.baseUrl}/${id}`);
  }

  /** GET /clientes/cliente-id/{clienteId} */
  buscarPorClienteId(clienteId: string): Observable<Cliente> {
    return this.http.get<Cliente>(`${this.baseUrl}/cliente-id/${clienteId}`);
  }

  /** POST /clientes */
  crear(request: ClienteRequest): Observable<Cliente> {
    return this.http.post<Cliente>(this.baseUrl, request);
  }

  /** PUT /clientes/{id} */
  actualizar(id: number, request: ClienteRequest): Observable<Cliente> {
    return this.http.put<Cliente>(`${this.baseUrl}/${id}`, request);
  }

  /** PATCH /clientes/{id} */
  actualizarParcial(id: number, request: ClientePatch): Observable<Cliente> {
    return this.http.patch<Cliente>(`${this.baseUrl}/${id}`, request);
  }

  /** DELETE /clientes/{id} */
  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
