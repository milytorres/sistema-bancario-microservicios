import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatButtonModule } from '@angular/material/button';

import { ClienteFormComponent } from './cliente-form.component';

describe('ClienteFormComponent', () => {
  let component: ClienteFormComponent;
  let fixture: ComponentFixture<ClienteFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ClienteFormComponent],
      imports: [
        ReactiveFormsModule,
        RouterTestingModule,
        HttpClientTestingModule,
        NoopAnimationsModule,
        MatFormFieldModule,
        MatInputModule,
        MatSelectModule,
        MatCheckboxModule,
        MatDatepickerModule,
        MatNativeDateModule,
        MatButtonModule
      ],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { paramMap: convertToParamMap({}) } }
        }
      ]
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ClienteFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('debe crear el componente', () => {
    expect(component).toBeTruthy();
  });

  it('debe calcular la edad correctamente cuando el cumpleanos de este anio ya paso', () => {
    const hoy = new Date();
    // Cumpleanos 5 anios atras y un dia antes de hoy (ya paso este anio).
    const fechaNacimiento = new Date(hoy.getFullYear() - 5, hoy.getMonth(), hoy.getDate());
    fechaNacimiento.setDate(fechaNacimiento.getDate() - 1);

    component.form.get('fechaNacimiento')?.setValue(fechaNacimiento);

    expect(component.form.get('edad')?.value).toBe(5);
  });

  it('debe calcular la edad correctamente cuando el cumpleanos de este anio aun no se cumple', () => {
    const hoy = new Date();
    // Cumpleanos 5 anios atras pero un dia despues de hoy (aun no se cumple este anio).
    const fechaNacimiento = new Date(hoy.getFullYear() - 5, hoy.getMonth(), hoy.getDate());
    fechaNacimiento.setDate(fechaNacimiento.getDate() + 1);

    component.form.get('fechaNacimiento')?.setValue(fechaNacimiento);

    expect(component.form.get('edad')?.value).toBe(4);
  });

  it('debe marcar el formulario como invalido si nombre, identificacion o clienteId estan vacios', () => {
    component.form.get('nombre')?.setValue('');
    component.form.get('identificacion')?.setValue('');
    component.form.get('clienteId')?.setValue('');
    component.form.get('contrasena')?.setValue('');

    expect(component.form.invalid).toBeTrue();
  });

  it('debe marcar el formulario como valido cuando todos los campos requeridos estan completos', () => {
    component.form.patchValue({
      nombre: 'Juan Perez',
      identificacion: '0102030405',
      clienteId: 'CLI-001',
      contrasena: 'secreta123',
      estado: true
    });

    expect(component.form.valid).toBeTrue();
  });
});
