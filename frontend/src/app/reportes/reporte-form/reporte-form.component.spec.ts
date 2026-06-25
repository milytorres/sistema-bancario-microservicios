import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { ReactiveFormsModule } from '@angular/forms';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatButtonModule } from '@angular/material/button';
import { of } from 'rxjs';

import { ReporteFormComponent } from './reporte-form.component';
import { ReporteService } from '../reporte.service';
import { ReporteFiltro } from '../reporte.model';

describe('ReporteFormComponent', () => {
  let component: ReporteFormComponent;
  let fixture: ComponentFixture<ReporteFormComponent>;
  let reporteServiceSpy: jasmine.SpyObj<ReporteService>;

  beforeEach(async () => {
    reporteServiceSpy = jasmine.createSpyObj('ReporteService', ['generar']);
    reporteServiceSpy.generar.and.returnValue(of([]));

    await TestBed.configureTestingModule({
      declarations: [ReporteFormComponent],
      imports: [
        ReactiveFormsModule,
        HttpClientTestingModule,
        NoopAnimationsModule,
        MatFormFieldModule,
        MatInputModule,
        MatDatepickerModule,
        MatNativeDateModule,
        MatTableModule,
        MatPaginatorModule,
        MatButtonModule
      ],
      providers: [{ provide: ReporteService, useValue: reporteServiceSpy }]
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ReporteFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('debe crear el componente', () => {
    expect(component).toBeTruthy();
  });

  it('debe marcar el formulario como invalido si falta clienteId, fechaDesde o fechaHasta', () => {
    component.form.patchValue({ clienteId: '', fechaDesde: null, fechaHasta: null });
    expect(component.form.invalid).toBeTrue();
  });

  it('debe marcar el formulario como valido cuando todos los campos requeridos estan completos', () => {
    component.form.patchValue({
      clienteId: 'CLI-001',
      fechaDesde: new Date(2026, 5, 1),
      fechaHasta: new Date(2026, 5, 24)
    });
    expect(component.form.valid).toBeTrue();
  });

  it('debe llamar al servicio con las fechas formateadas como yyyy-MM-dd, no como objeto Date crudo', () => {
    // Regresion: bug real corregido manualmente donde se enviaba el objeto Date sin formatear.
    component.form.patchValue({
      clienteId: 'CLI-001',
      fechaDesde: new Date(2026, 5, 1), // 1 de junio de 2026
      fechaHasta: new Date(2026, 5, 24) // 24 de junio de 2026
    });

    component.generar();

    expect(reporteServiceSpy.generar).toHaveBeenCalledTimes(1);
    const filtroEnviado: ReporteFiltro = reporteServiceSpy.generar.calls.mostRecent().args[0];

    expect(filtroEnviado.fechaDesde).toBe('2026-06-01');
    expect(filtroEnviado.fechaHasta).toBe('2026-06-24');
    expect(typeof filtroEnviado.fechaDesde).toBe('string');
    expect(typeof filtroEnviado.fechaHasta).toBe('string');
    expect(filtroEnviado.clienteId).toBe('CLI-001');
  });

  it('debe formatear con cero a la izquierda meses y dias de un solo digito', () => {
    component.form.patchValue({
      clienteId: 'CLI-001',
      fechaDesde: new Date(2026, 0, 5), // 5 de enero de 2026
      fechaHasta: new Date(2026, 0, 9) // 9 de enero de 2026
    });

    component.generar();

    const filtroEnviado: ReporteFiltro = reporteServiceSpy.generar.calls.mostRecent().args[0];
    expect(filtroEnviado.fechaDesde).toBe('2026-01-05');
    expect(filtroEnviado.fechaHasta).toBe('2026-01-09');
  });

  it('no debe llamar al servicio si el formulario es invalido', () => {
    component.form.patchValue({ clienteId: '', fechaDesde: null, fechaHasta: null });
    component.generar();
    expect(reporteServiceSpy.generar).not.toHaveBeenCalled();
  });
});
