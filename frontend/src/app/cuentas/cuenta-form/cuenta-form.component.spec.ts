import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatButtonModule } from '@angular/material/button';
import { of } from 'rxjs';

import { CuentaFormComponent } from './cuenta-form.component';
import { CuentaService } from '../cuenta.service';
import { ClienteService } from '../../clientes/cliente.service';
import { Cliente } from '../../clientes/cliente.model';

describe('CuentaFormComponent', () => {
  let component: CuentaFormComponent;
  let fixture: ComponentFixture<CuentaFormComponent>;
  let clienteServiceSpy: jasmine.SpyObj<ClienteService>;
  let cuentaServiceSpy: jasmine.SpyObj<CuentaService>;

  const clientesMock: Cliente[] = [
    {
      id: 1,
      nombre: 'Juan Perez',
      genero: 'Masculino',
      edad: 30,
      identificacion: '0102030405',
      direccion: null,
      telefono: null,
      clienteId: 'CLI-001',
      estado: true
    },
    {
      id: 2,
      nombre: 'Maria Lopez',
      genero: 'Femenino',
      edad: 25,
      identificacion: '0203040506',
      direccion: null,
      telefono: null,
      clienteId: 'CLI-002',
      estado: true
    }
  ];

  beforeEach(async () => {
    clienteServiceSpy = jasmine.createSpyObj('ClienteService', ['listar']);
    clienteServiceSpy.listar.and.returnValue(of(clientesMock));
    cuentaServiceSpy = jasmine.createSpyObj('CuentaService', ['buscarPorId', 'crear', 'actualizarParcial']);

    await TestBed.configureTestingModule({
      declarations: [CuentaFormComponent],
      imports: [
        ReactiveFormsModule,
        RouterTestingModule,
        NoopAnimationsModule,
        MatFormFieldModule,
        MatInputModule,
        MatSelectModule,
        MatCheckboxModule,
        MatAutocompleteModule,
        MatButtonModule
      ],
      providers: [
        { provide: CuentaService, useValue: cuentaServiceSpy },
        { provide: ClienteService, useValue: clienteServiceSpy },
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { paramMap: convertToParamMap({}) } }
        }
      ]
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CuentaFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('debe crear el componente', () => {
    expect(component).toBeTruthy();
  });

  it('debe cargar la lista de clientes al inicializar', () => {
    expect(clienteServiceSpy.listar).toHaveBeenCalled();
    expect(component.clientes).toEqual(clientesMock);
  });

  it('al seleccionar un cliente del autocomplete debe guardar el clienteId real en el control oculto, no el objeto completo', () => {
    component.seleccionarCliente(clientesMock[1]);

    const valorControl = component.form.get('clienteId')?.value;
    expect(valorControl).toBe('CLI-002');
    expect(typeof valorControl).toBe('string');
  });

  it('mostrarCliente debe formatear el texto visible combinando nombre y clienteId', () => {
    const texto = component.mostrarCliente(clientesMock[0]);
    expect(texto).toBe('Juan Perez (CLI-001)');
  });

  it('el filtro del buscador debe incluir clientes que coincidan por nombre o por clienteId, ignorando mayusculas', () => {
    let resultado: Cliente[] = [];
    component.clientesFiltrados$.subscribe((clientes) => (resultado = clientes));

    component.buscadorCliente.setValue('maria');

    expect(resultado.length).toBe(1);
    expect(resultado[0].clienteId).toBe('CLI-002');
  });

  it('debe marcar el formulario como invalido si faltan campos requeridos', () => {
    component.form.patchValue({ numeroCuenta: '', tipoCuenta: null, saldoInicial: null, clienteId: '' });
    expect(component.form.invalid).toBeTrue();
  });
});
