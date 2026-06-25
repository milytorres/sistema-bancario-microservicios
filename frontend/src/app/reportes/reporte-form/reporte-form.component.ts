import { Component, OnDestroy, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

import { ReporteMovimiento } from '../reporte.model';
import { ReporteService } from '../reporte.service';

/**
 * Formulario de generacion de reporte de movimientos (GET /reportes).
 * Filtros: rango de fechas (desde/hasta) y clienteId de negocio (ej. CLI-001).
 * Columnas de la tabla de resultados alineadas a ReporteMovimientoResponseDTO:
 * Fecha, Cliente, Numero Cuenta, Tipo, Saldo Inicial, Estado, Movimiento, Saldo Disponible.
 */
@Component({
  selector: 'app-reporte-form',
  templateUrl: './reporte-form.component.html',
  styleUrls: ['./reporte-form.component.scss']
})
export class ReporteFormComponent implements OnDestroy {

  readonly columnas: string[] = [
    'fecha',
    'cliente',
    'numeroCuenta',
    'tipo',
    'saldoInicial',
    'estado',
    'movimiento',
    'saldoDisponible'
  ];

  readonly dataSource = new MatTableDataSource<ReporteMovimiento>([]);

  cargando = false;
  errorCarga = false;
  seBusco = false;

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  private readonly destroy$ = new Subject<void>();

  readonly form: FormGroup = this.fb.group({
    clienteId: ['', [Validators.required]],
    fechaDesde: [null, [Validators.required]],
    fechaHasta: [null, [Validators.required]]
  });

  constructor(
    private readonly fb: FormBuilder,
    private readonly reporteService: ReporteService
  ) { }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  generar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const { clienteId, fechaDesde, fechaHasta } = this.form.getRawValue();

    this.cargando = true;
    this.errorCarga = false;
    this.seBusco = true;
    this.reporteService.generar({
      clienteId,
      fechaDesde: this.formatearFecha(fechaDesde),
      fechaHasta: this.formatearFecha(fechaHasta)
    }).pipe(takeUntil(this.destroy$)).subscribe({
      next: (reporte) => {
        this.dataSource.data = reporte;
        this.dataSource.paginator = this.paginator;
        this.cargando = false;
      },
      error: () => {
        this.cargando = false;
        this.errorCarga = true;
      }
    });
  }

  /** Convierte el Date del mat-datepicker al formato "yyyy-MM-dd" que espera el backend. */
  private formatearFecha(fecha: Date): string {
    const anio = fecha.getFullYear();
    const mes = String(fecha.getMonth() + 1).padStart(2, '0');
    const dia = String(fecha.getDate()).padStart(2, '0');
    return `${anio}-${mes}-${dia}`;
  }
}
