import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { environment } from '../../environments/environment';
import { CuentaService } from './cuenta.service';
import { Cuenta, CuentaPatch, CuentaRequest, TipoCuenta } from './cuenta.model';

describe('CuentaService', () => {
  let service: CuentaService;
  let httpMock: HttpTestingController;
  const baseUrl = `${environment.cuentaApiUrl}/cuentas`;

  const cuentaMock: Cuenta = {
    id: 1,
    numeroCuenta: '001-001',
    tipoCuenta: TipoCuenta.AHORRO,
    saldoInicial: 100,
    saldoDisponible: 100,
    estado: true,
    clienteId: 'CLI-001'
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule]
    });
    service = TestBed.inject(CuentaService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('debe ser creado', () => {
    expect(service).toBeTruthy();
  });

  it('debe listar cuentas con GET a /cuentas', () => {
    service.listar().subscribe((cuentas) => {
      expect(cuentas).toEqual([cuentaMock]);
    });

    const req = httpMock.expectOne(baseUrl);
    expect(req.request.method).toBe('GET');
    req.flush([cuentaMock]);
  });

  it('debe buscar una cuenta por id con GET a /cuentas/{id}', () => {
    service.buscarPorId(1).subscribe((cuenta) => {
      expect(cuenta).toEqual(cuentaMock);
    });

    const req = httpMock.expectOne(`${baseUrl}/1`);
    expect(req.request.method).toBe('GET');
    req.flush(cuentaMock);
  });

  it('debe crear una cuenta con POST a /cuentas enviando el body completo', () => {
    const request: CuentaRequest = {
      numeroCuenta: '001-001',
      tipoCuenta: TipoCuenta.AHORRO,
      saldoInicial: 100,
      estado: true,
      clienteId: 'CLI-001'
    };

    service.crear(request).subscribe((cuenta) => {
      expect(cuenta).toEqual(cuentaMock);
    });

    const req = httpMock.expectOne(baseUrl);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(request);
    req.flush(cuentaMock);
  });

  it('debe actualizar una cuenta con PUT a /cuentas/{id} enviando el body completo', () => {
    const request: CuentaRequest = {
      numeroCuenta: '001-001',
      tipoCuenta: TipoCuenta.CORRIENTE,
      saldoInicial: 100,
      estado: true,
      clienteId: 'CLI-001'
    };

    service.actualizar(1, request).subscribe((cuenta) => {
      expect(cuenta).toEqual(cuentaMock);
    });

    const req = httpMock.expectOne(`${baseUrl}/1`);
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(request);
    req.flush(cuentaMock);
  });

  it('debe actualizar parcialmente una cuenta con PATCH a /cuentas/{id}', () => {
    const patch: CuentaPatch = { estado: false };

    service.actualizarParcial(1, patch).subscribe((cuenta) => {
      expect(cuenta).toEqual(cuentaMock);
    });

    const req = httpMock.expectOne(`${baseUrl}/1`);
    expect(req.request.method).toBe('PATCH');
    expect(req.request.body).toEqual(patch);
    req.flush(cuentaMock);
  });

  it('debe eliminar una cuenta con DELETE a /cuentas/{id}', () => {
    service.eliminar(1).subscribe((respuesta) => {
      expect(respuesta).toBeNull();
    });

    const req = httpMock.expectOne(`${baseUrl}/1`);
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });
});
