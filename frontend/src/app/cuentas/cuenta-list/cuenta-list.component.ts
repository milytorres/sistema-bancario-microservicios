import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

import { Cuenta } from '../cuenta.model';
import { CuentaService } from '../cuenta.service';
import { ConfirmDialogComponent } from '../../shared/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-cuenta-list',
  templateUrl: './cuenta-list.component.html',
  styleUrls: ['./cuenta-list.component.scss']
})
export class CuentaListComponent implements OnInit, OnDestroy {

  readonly columnas: string[] = ['numeroCuenta', 'tipoCuenta', 'saldoDisponible', 'estado', 'clienteId', 'acciones'];

  readonly dataSource = new MatTableDataSource<Cuenta>([]);

  cargando = false;
  errorCarga = false;
  eliminandoId: number | null = null;

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  private readonly destroy$ = new Subject<void>();

  constructor(
    private readonly cuentaService: CuentaService,
    private readonly router: Router,
    private readonly dialog: MatDialog
  ) { }

  ngOnInit(): void {
    this.cargarCuentas();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  cargarCuentas(): void {
    this.cargando = true;
    this.errorCarga = false;
    this.cuentaService.listar().pipe(takeUntil(this.destroy$)).subscribe({
      next: (cuentas) => {
        this.dataSource.data = cuentas;
        this.dataSource.paginator = this.paginator;
        this.cargando = false;
      },
      error: () => {
        this.cargando = false;
        this.errorCarga = true;
      }
    });
  }

  verCuenta(cuenta: Cuenta): void {
    this.router.navigate(['/cuentas', cuenta.id, 'editar']);
  }

  editarCuenta(cuenta: Cuenta): void {
    this.router.navigate(['/cuentas', cuenta.id, 'editar']);
  }

  eliminarCuenta(cuenta: Cuenta): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      data: {
        titulo: 'Eliminar cuenta',
        mensaje: `¿Eliminar la cuenta "${cuenta.numeroCuenta}"? Esta acción no se puede deshacer.`
      }
    });

    dialogRef.afterClosed().pipe(takeUntil(this.destroy$)).subscribe((confirmado) => {
      if (!confirmado) {
        return;
      }
      this.eliminandoId = cuenta.id;
      this.cuentaService.eliminar(cuenta.id).pipe(takeUntil(this.destroy$)).subscribe({
        next: () => {
          this.eliminandoId = null;
          this.cargarCuentas();
        },
        error: () => {
          this.eliminandoId = null;
        }
      });
    });
  }
}
