import { Component, Input, OnChanges, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

import { Movimiento } from '../movimiento.model';
import { MovimientoService } from '../movimiento.service';
import { Cuenta } from '../../cuentas/cuenta.model';
import { CuentaService } from '../../cuentas/cuenta.service';

/**
 * Listado de movimientos de una cuenta especifica (alineado a GET /movimientos?cuentaId={id}).
 * Acepta cuentaId por @Input (para uso embebido futuro), pero como ruta standalone
 * (/movimientos) no recibe ningun @Input del router, por eso incluye su propio
 * selector de cuenta para ser autosuficiente.
 */
@Component({
  selector: 'app-movimiento-list',
  templateUrl: './movimiento-list.component.html',
  styleUrls: ['./movimiento-list.component.scss']
})
export class MovimientoListComponent implements OnChanges, OnInit, OnDestroy {

  @Input() cuentaId: number | null = null;

  readonly columnas: string[] = ['fecha', 'tipoMovimiento', 'valor', 'saldo'];

  readonly dataSource = new MatTableDataSource<Movimiento>([]);

  cuentas: Cuenta[] = [];
  cargando = false;
  errorCarga = false;
  cargandoCuentas = false;

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  private readonly destroy$ = new Subject<void>();

  constructor(
    private readonly movimientoService: MovimientoService,
    private readonly cuentaService: CuentaService
  ) { }

  ngOnInit(): void {
    this.cargandoCuentas = true;
    this.cuentaService.listar().pipe(takeUntil(this.destroy$)).subscribe({
      next: (cuentas) => {
        this.cuentas = cuentas;
        this.cargandoCuentas = false;
      },
      error: () => {
        this.cargandoCuentas = false;
      }
    });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  ngOnChanges(): void {
    if (this.cuentaId) {
      this.cargarMovimientos(this.cuentaId);
    }
  }

  seleccionarCuenta(cuentaId: number): void {
    this.cuentaId = cuentaId;
    this.cargarMovimientos(cuentaId);
  }

  cargarMovimientos(cuentaId: number): void {
    this.cargando = true;
    this.errorCarga = false;
    this.movimientoService.listarPorCuenta(cuentaId).pipe(takeUntil(this.destroy$)).subscribe({
      next: (movimientos) => {
        this.dataSource.data = movimientos;
        this.dataSource.paginator = this.paginator;
        this.cargando = false;
      },
      error: () => {
        this.cargando = false;
        this.errorCarga = true;
      }
    });
  }
}
