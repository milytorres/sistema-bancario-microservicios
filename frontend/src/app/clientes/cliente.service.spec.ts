import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { environment } from '../../environments/environment';
import { ClienteService } from './cliente.service';
import { Cliente, ClientePatch, ClienteRequest } from './cliente.model';

describe('ClienteService', () => {
  let service: ClienteService;
  let httpMock: HttpTestingController;
  const baseUrl = `${environment.clienteApiUrl}/clientes`;

  const clienteMock: Cliente = {
    id: 1,
    nombre: 'Juan Perez',
    genero: 'Masculino',
    edad: 30,
    identificacion: '0102030405',
    direccion: 'Calle Falsa 123',
    telefono: '0999999999',
    clienteId: 'CLI-001',
    estado: true
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule]
    });
    service = TestBed.inject(ClienteService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('debe ser creado', () => {
    expect(service).toBeTruthy();
  });

  it('debe listar clientes con GET a /clientes', () => {
    service.listar().subscribe((clientes) => {
      expect(clientes).toEqual([clienteMock]);
    });

    const req = httpMock.expectOne(baseUrl);
    expect(req.request.method).toBe('GET');
    req.flush([clienteMock]);
  });

  it('debe buscar un cliente por id con GET a /clientes/{id}', () => {
    service.buscarPorId(1).subscribe((cliente) => {
      expect(cliente).toEqual(clienteMock);
    });

    const req = httpMock.expectOne(`${baseUrl}/1`);
    expect(req.request.method).toBe('GET');
    req.flush(clienteMock);
  });

  it('debe buscar un cliente por clienteId de negocio con GET a /clientes/cliente-id/{clienteId}', () => {
    service.buscarPorClienteId('CLI-001').subscribe((cliente) => {
      expect(cliente).toEqual(clienteMock);
    });

    const req = httpMock.expectOne(`${baseUrl}/cliente-id/CLI-001`);
    expect(req.request.method).toBe('GET');
    req.flush(clienteMock);
  });

  it('debe crear un cliente con POST a /clientes enviando el body completo', () => {
    const request: ClienteRequest = {
      nombre: 'Juan Perez',
      genero: 'Masculino',
      edad: 30,
      identificacion: '0102030405',
      direccion: 'Calle Falsa 123',
      telefono: '0999999999',
      clienteId: 'CLI-001',
      contrasena: 'secreta123',
      estado: true
    };

    service.crear(request).subscribe((cliente) => {
      expect(cliente).toEqual(clienteMock);
    });

    const req = httpMock.expectOne(baseUrl);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(request);
    req.flush(clienteMock);
  });

  it('debe actualizar un cliente con PUT a /clientes/{id} enviando el body completo', () => {
    const request: ClienteRequest = {
      nombre: 'Juan Perez',
      genero: 'Masculino',
      edad: 31,
      identificacion: '0102030405',
      direccion: 'Calle Falsa 123',
      telefono: '0999999999',
      clienteId: 'CLI-001',
      contrasena: 'secreta123',
      estado: true
    };

    service.actualizar(1, request).subscribe((cliente) => {
      expect(cliente).toEqual(clienteMock);
    });

    const req = httpMock.expectOne(`${baseUrl}/1`);
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(request);
    req.flush(clienteMock);
  });

  it('debe actualizar parcialmente un cliente con PATCH a /clientes/{id}', () => {
    const patch: ClientePatch = { telefono: '0988888888' };

    service.actualizarParcial(1, patch).subscribe((cliente) => {
      expect(cliente).toEqual(clienteMock);
    });

    const req = httpMock.expectOne(`${baseUrl}/1`);
    expect(req.request.method).toBe('PATCH');
    expect(req.request.body).toEqual(patch);
    req.flush(clienteMock);
  });

  it('debe eliminar un cliente con DELETE a /clientes/{id}', () => {
    service.eliminar(1).subscribe((respuesta) => {
      expect(respuesta).toBeNull();
    });

    const req = httpMock.expectOne(`${baseUrl}/1`);
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });
});
