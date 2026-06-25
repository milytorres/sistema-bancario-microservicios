import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { environment } from '../../environments/environment';
import { MovimientoService } from './movimiento.service';
import { Movimiento, MovimientoRequest, TipoMovimiento } from './movimiento.model';

describe('MovimientoService', () => {
  let service: MovimientoService;
  let httpMock: HttpTestingController;
  const baseUrl = `${environment.cuentaApiUrl}/movimientos`;

  const movimientoMock: Movimiento = {
    id: 1,
    fecha: '2026-06-24',
    tipoMovimiento: TipoMovimiento.DEPOSITO,
    valor: 50,
    saldo: 150,
    cuentaId: 1
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule]
    });
    service = TestBed.inject(MovimientoService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('debe ser creado', () => {
    expect(service).toBeTruthy();
  });

  it('debe listar movimientos por cuenta con GET a /movimientos?cuentaId={cuentaId}', () => {
    service.listarPorCuenta(1).subscribe((movimientos) => {
      expect(movimientos).toEqual([movimientoMock]);
    });

    const req = httpMock.expectOne((r) => r.url === baseUrl);
    expect(req.request.method).toBe('GET');
    expect(req.request.params.get('cuentaId')).toBe('1');
    req.flush([movimientoMock]);
  });

  it('debe buscar un movimiento por id con GET a /movimientos/{id}', () => {
    service.buscarPorId(1).subscribe((movimiento) => {
      expect(movimiento).toEqual(movimientoMock);
    });

    const req = httpMock.expectOne(`${baseUrl}/1`);
    expect(req.request.method).toBe('GET');
    req.flush(movimientoMock);
  });

  it('debe registrar un movimiento con POST a /movimientos enviando el body completo', () => {
    const request: MovimientoRequest = {
      cuentaId: 1,
      tipoMovimiento: TipoMovimiento.DEPOSITO,
      valor: 50
    };

    service.registrar(request).subscribe((movimiento) => {
      expect(movimiento).toEqual(movimientoMock);
    });

    const req = httpMock.expectOne(baseUrl);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(request);
    req.flush(movimientoMock);
  });
});
