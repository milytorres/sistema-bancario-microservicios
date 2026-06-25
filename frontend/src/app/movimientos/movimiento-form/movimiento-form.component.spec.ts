import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatButtonModule } from '@angular/material/button';
import { of } from 'rxjs';

import { MovimientoFormComponent } from './movimiento-form.component';
import { MovimientoService } from '../movimiento.service';
import { CuentaService } from '../../cuentas/cuenta.service';
import { Cuenta, TipoCuenta } from '../../cuentas/cuenta.model';
import { TipoMovimiento } from '../movimiento.model';

describe('MovimientoFormComponent', () => {
  let component: MovimientoFormComponent;
  let fixture: ComponentFixture<MovimientoFormComponent>;
  let cuentaServiceSpy: jasmine.SpyObj<CuentaService>;
  let movimientoServiceSpy: jasmine.SpyObj<MovimientoService>;
  let routerSpy: jasmine.SpyObj<Router>;

  const cuentasMock: Cuenta[] = [
    {
      id: 1,
      numeroCuenta: '001-001',
      tipoCuenta: TipoCuenta.AHORRO,
      saldoInicial: 100,
      saldoDisponible: 100,
      estado: true,
      clienteId: 'CLI-001'
    },
    {
      id: 2,
      numeroCuenta: '002-002',
      tipoCuenta: TipoCuenta.CORRIENTE,
      saldoInicial: 200,
      saldoDisponible: 200,
      estado: true,
      clienteId: 'CLI-002'
    }
  ];

  beforeEach(async () => {
    cuentaServiceSpy = jasmine.createSpyObj('CuentaService', ['listar']);
    cuentaServiceSpy.listar.and.returnValue(of(cuentasMock));
    movimientoServiceSpy = jasmine.createSpyObj('MovimientoService', ['registrar']);
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      declarations: [MovimientoFormComponent],
      imports: [
        ReactiveFormsModule,
        RouterTestingModule,
        NoopAnimationsModule,
        MatFormFieldModule,
        MatInputModule,
        MatSelectModule,
        MatAutocompleteModule,
        MatButtonModule
      ],
      providers: [
        { provide: MovimientoService, useValue: movimientoServiceSpy },
        { provide: CuentaService, useValue: cuentaServiceSpy },
        { provide: Router, useValue: routerSpy }
      ]
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MovimientoFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('debe crear el componente', () => {
    expect(component).toBeTruthy();
  });

  it('debe cargar la lista de cuentas al inicializar', () => {
    expect(cuentaServiceSpy.listar).toHaveBeenCalled();
    expect(component.cuentas).toEqual(cuentasMock);
  });

  it('al seleccionar una cuenta del autocomplete debe guardar el id numerico real en el control oculto, no el objeto completo', () => {
    component.seleccionarCuenta(cuentasMock[1]);

    const valorControl = component.form.get('cuentaId')?.value;
    expect(valorControl).toBe(2);
    expect(typeof valorControl).toBe('number');
  });

  it('mostrarCuenta debe formatear el texto visible combinando numeroCuenta y tipoCuenta', () => {
    const texto = component.mostrarCuenta(cuentasMock[0]);
    expect(texto).toBe('001-001 (AHORRO)');
  });

  it('el filtro del buscador debe incluir cuentas que coincidan por numeroCuenta o tipoCuenta, ignorando mayusculas', () => {
    let resultado: Cuenta[] = [];
    component.cuentasFiltradas$.subscribe((cuentas) => (resultado = cuentas));

    component.buscadorCuenta.setValue('corriente');

    expect(resultado.length).toBe(1);
    expect(resultado[0].id).toBe(2);
  });

  it('debe marcar el formulario como invalido si falta cuentaId, tipoMovimiento o valor', () => {
    component.form.patchValue({ cuentaId: null, tipoMovimiento: null, valor: null });
    expect(component.form.invalid).toBeTrue();
  });

  it('debe marcar el formulario como invalido si el valor es cero o negativo', () => {
    component.seleccionarCuenta(cuentasMock[0]);
    component.form.patchValue({ tipoMovimiento: TipoMovimiento.RETIRO, valor: 0 });
    expect(component.form.invalid).toBeTrue();
  });

  it('al registrar con el formulario valido debe llamar al servicio con el cuentaId real', () => {
    movimientoServiceSpy.registrar.and.returnValue(of({
      id: 1,
      fecha: '2026-06-24',
      tipoMovimiento: TipoMovimiento.DEPOSITO,
      valor: 50,
      saldo: 150,
      cuentaId: 1
    }));

    component.seleccionarCuenta(cuentasMock[0]);
    component.form.patchValue({ tipoMovimiento: TipoMovimiento.DEPOSITO, valor: 50 });

    component.registrar();

    expect(movimientoServiceSpy.registrar).toHaveBeenCalledWith({
      cuentaId: 1,
      tipoMovimiento: TipoMovimiento.DEPOSITO,
      valor: 50
    });
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/movimientos']);
  });
});
