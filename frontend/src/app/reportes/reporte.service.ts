import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { environment } from '../../environments/environment';
import { ReporteFiltro, ReporteMovimiento, ReporteMovimientoApi } from './reporte.model';

/**
 * Servicio de acceso al endpoint /reportes del microservicio "cuenta" (puerto 8082).
 * Contrato real: GET /reportes?fecha={desde},{hasta}&cliente={clienteId}.
 * "fecha" es UN solo query param con ambas fechas separadas por coma (no dos params).
 * "cliente" es el clienteId de negocio (ej. CLI-001), no el id interno.
 */
@Injectable({
  providedIn: 'root'
})
export class ReporteService {

  private readonly baseUrl = `${environment.cuentaApiUrl}/reportes`;

  constructor(private readonly http: HttpClient) { }

  /** GET /reportes?fecha={fechaDesde},{fechaHasta}&cliente={clienteId} */
  generar(filtro: ReporteFiltro): Observable<ReporteMovimiento[]> {
    const fecha = `${filtro.fechaDesde},${filtro.fechaHasta}`;
    const params = new HttpParams()
      .set('fecha', fecha)
      .set('cliente', filtro.clienteId);

    return this.http.get<ReporteMovimientoApi[]>(this.baseUrl, { params }).pipe(
      map((respuesta) => respuesta.map(this.mapearAModeloFrontend))
    );
  }

  /**
   * Mapea las claves literales en español del backend (ReporteMovimientoResponseDTO,
   * via @JsonProperty: "Fecha", "Cliente", "Numero Cuenta", "Tipo", "Saldo Inicial",
   * "Estado", "Movimiento", "Saldo Disponible") al modelo camelCase usado en el frontend.
   */
  private mapearAModeloFrontend(item: ReporteMovimientoApi): ReporteMovimiento {
    return {
      fecha: item['Fecha'],
      cliente: item['Cliente'],
      numeroCuenta: item['Numero Cuenta'],
      tipo: item['Tipo'],
      saldoInicial: item['Saldo Inicial'],
      estado: item['Estado'],
      movimiento: item['Movimiento'],
      saldoDisponible: item['Saldo Disponible']
    };
  }
}
