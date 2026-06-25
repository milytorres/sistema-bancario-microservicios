import { TestBed } from '@angular/core/testing';
import { HttpClient, HTTP_INTERCEPTORS } from '@angular/common/http';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { MatSnackBar } from '@angular/material/snack-bar';

import { ErrorInterceptor } from './error.interceptor';
import { ErrorResponseDTO } from '../models/error-response.model';

describe('ErrorInterceptor', () => {
  let httpClient: HttpClient;
  let httpMock: HttpTestingController;
  let snackBarSpy: jasmine.SpyObj<MatSnackBar>;

  beforeEach(() => {
    snackBarSpy = jasmine.createSpyObj('MatSnackBar', ['open']);

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        { provide: MatSnackBar, useValue: snackBarSpy },
        { provide: HTTP_INTERCEPTORS, useClass: ErrorInterceptor, multi: true }
      ]
    });

    httpClient = TestBed.inject(HttpClient);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('debe mostrar el mensaje del backend cuando el error trae un ErrorResponseDTO sin detalles de validacion', () => {
    const errorBody: ErrorResponseDTO = {
      timestamp: '2026-06-24T10:00:00Z',
      status: 404,
      error: 'Not Found',
      message: 'Cliente no encontrado',
      path: '/clientes/99',
      errores: null
    };

    httpClient.get('/clientes/99').subscribe({
      next: () => fail('se esperaba un error'),
      error: () => {
        expect(snackBarSpy.open).toHaveBeenCalledWith(
          'Cliente no encontrado',
          'Cerrar',
          { duration: 6000, panelClass: ['error-snackbar'] }
        );
      }
    });

    const req = httpMock.expectOne('/clientes/99');
    req.flush(errorBody, { status: 404, statusText: 'Not Found' });
  });

  it('debe mostrar el mensaje del backend combinado con los detalles de validacion cuando existen errores de campo', () => {
    const errorBody: ErrorResponseDTO = {
      timestamp: '2026-06-24T10:00:00Z',
      status: 400,
      error: 'Bad Request',
      message: 'Error de validacion',
      path: '/clientes',
      errores: { nombre: 'no debe estar vacio', identificacion: 'es obligatoria' }
    };

    httpClient.post('/clientes', {}).subscribe({
      next: () => fail('se esperaba un error'),
      error: () => {
        expect(snackBarSpy.open).toHaveBeenCalledWith(
          'Error de validacion: no debe estar vacio | es obligatoria',
          'Cerrar',
          { duration: 6000, panelClass: ['error-snackbar'] }
        );
      }
    });

    const req = httpMock.expectOne('/clientes');
    req.flush(errorBody, { status: 400, statusText: 'Bad Request' });
  });

  it('debe mostrar un mensaje de conexion cuando el status es 0 (servidor no disponible)', () => {
    httpClient.get('/clientes').subscribe({
      next: () => fail('se esperaba un error'),
      error: () => {
        expect(snackBarSpy.open).toHaveBeenCalledWith(
          'No se pudo conectar con el servidor. Verifique su conexion.',
          'Cerrar',
          { duration: 6000, panelClass: ['error-snackbar'] }
        );
      }
    });

    const req = httpMock.expectOne('/clientes');
    req.error(new ProgressEvent('error'), { status: 0, statusText: 'Unknown Error' });
  });

  it('debe mostrar un mensaje generico cuando el error no trae un body con shape de ErrorResponseDTO', () => {
    httpClient.get('/clientes').subscribe({
      next: () => fail('se esperaba un error'),
      error: () => {
        expect(snackBarSpy.open).toHaveBeenCalledWith(
          'Error inesperado (500). Intente nuevamente.',
          'Cerrar',
          { duration: 6000, panelClass: ['error-snackbar'] }
        );
      }
    });

    const req = httpMock.expectOne('/clientes');
    req.flush('texto plano sin shape de error', { status: 500, statusText: 'Internal Server Error' });
  });
});
