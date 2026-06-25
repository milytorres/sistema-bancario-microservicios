import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

import { Cliente } from '../cliente.model';
import { ClienteService } from '../cliente.service';
import { ConfirmDialogComponent } from '../../shared/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-cliente-list',
  templateUrl: './cliente-list.component.html',
  styleUrls: ['./cliente-list.component.scss']
})
export class ClienteListComponent implements OnInit, OnDestroy {

  // El campo "contrasena" nunca se incluye aqui: no se muestra en ningun listado.
  readonly columnas: string[] = ['nombre', 'identificacion', 'clienteId', 'estado', 'acciones'];

  readonly dataSource = new MatTableDataSource<Cliente>([]);

  cargando = false;
  errorCarga = false;
  eliminandoId: number | null = null;

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  private readonly destroy$ = new Subject<void>();

  constructor(
    private readonly clienteService: ClienteService,
    private readonly router: Router,
    private readonly dialog: MatDialog
  ) { }

  ngOnInit(): void {
    this.cargarClientes();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  cargarClientes(): void {
    this.cargando = true;
    this.errorCarga = false;
    this.clienteService.listar().pipe(takeUntil(this.destroy$)).subscribe({
      next: (clientes) => {
        this.dataSource.data = clientes;
        this.dataSource.paginator = this.paginator;
        this.cargando = false;
      },
      error: () => {
        this.cargando = false;
        this.errorCarga = true;
      }
    });
  }

  verCliente(cliente: Cliente): void {
    this.router.navigate(['/clientes', cliente.id, 'editar']);
  }

  editarCliente(cliente: Cliente): void {
    this.router.navigate(['/clientes', cliente.id, 'editar']);
  }

  eliminarCliente(cliente: Cliente): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      data: {
        titulo: 'Eliminar cliente',
        mensaje: `¿Eliminar al cliente "${cliente.nombre}"? Esta acción no se puede deshacer.`
      }
    });

    dialogRef.afterClosed().pipe(takeUntil(this.destroy$)).subscribe((confirmado) => {
      if (!confirmado) {
        return;
      }
      this.eliminandoId = cliente.id;
      this.clienteService.eliminar(cliente.id).pipe(takeUntil(this.destroy$)).subscribe({
        next: () => {
          this.eliminandoId = null;
          this.cargarClientes();
        },
        error: () => {
          this.eliminandoId = null;
        }
      });
    });
  }
}
