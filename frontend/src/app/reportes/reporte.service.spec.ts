import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { environment } from '../../environments/environment';
import { ReporteService } from './reporte.service';
import { ReporteFiltro, ReporteMovimientoApi } from './reporte.model';

describe('ReporteService', () => {
  let service: ReporteService;
  let httpMock: HttpTestingController;
  const baseUrl = `${environment.cuentaApiUrl}/reportes`;

  const itemApiMock: ReporteMovimientoApi = {
    'Fecha': '2026-06-24',
    'Cliente': 'Juan Perez',
    'Numero Cuenta': '001-001',
    'Tipo': 'AHORRO',
    'Saldo Inicial': 100,
    'Estado': true,
    'Movimiento': 50,
    'Saldo Disponible': 150
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule]
    });
    service = TestBed.inject(ReporteService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('debe ser creado', () => {
    expect(service).toBeTruthy();
  });

  it('debe generar el reporte con GET a /reportes enviando fecha combinada y clienteId como params', () => {
    const filtro: ReporteFiltro = {
      clienteId: 'CLI-001',
      fechaDesde: '2026-06-01',
      fechaHasta: '2026-06-24'
    };

    service.generar(filtro).subscribe((reporte) => {
      expect(reporte).toEqual([
        {
          fecha: '2026-06-24',
          cliente: 'Juan Perez',
          numeroCuenta: '001-001',
          tipo: 'AHORRO',
          saldoInicial: 100,
          estado: true,
          movimiento: 50,
          saldoDisponible: 150
        }
      ]);
    });

    const req = httpMock.expectOne((r) => r.url === baseUrl);
    expect(req.request.method).toBe('GET');
    expect(req.request.params.get('fecha')).toBe('2026-06-01,2026-06-24');
    expect(req.request.params.get('cliente')).toBe('CLI-001');
    req.flush([itemApiMock]);
  });

  it('debe mapear las claves en espanol del backend al modelo camelCase del frontend', () => {
    const filtro: ReporteFiltro = {
      clienteId: 'CLI-001',
      fechaDesde: '2026-06-01',
      fechaHasta: '2026-06-24'
    };

    let resultado: any;
    service.generar(filtro).subscribe((reporte) => (resultado = reporte[0]));

    const req = httpMock.expectOne((r) => r.url === baseUrl);
    req.flush([itemApiMock]);

    expect(resultado.fecha).toBe('2026-06-24');
    expect(resultado.numeroCuenta).toBe('001-001');
    expect(resultado.saldoInicial).toBe(100);
    expect(resultado.saldoDisponible).toBe(150);
  });
});
