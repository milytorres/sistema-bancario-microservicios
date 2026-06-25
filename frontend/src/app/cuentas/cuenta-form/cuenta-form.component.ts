import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable, Subject } from 'rxjs';
import { map, startWith, takeUntil } from 'rxjs/operators';

import { CuentaService } from '../cuenta.service';
import { TipoCuenta } from '../cuenta.model';
import { Cliente } from '../../clientes/cliente.model';
import { ClienteService } from '../../clientes/cliente.service';

/**
 * Formulario de creacion/edicion de Cuenta.
 * Validadores alineados a CuentaRequestDTO (backend/cuenta):
 * - numeroCuenta: requerido (solo en creacion, no editable)
 * - tipoCuenta: requerido
 * - saldoInicial: requerido, no negativo, maximo 13 enteros y 2 decimales (solo en creacion)
 * - estado: requerido
 * - clienteId: requerido (clienteId de negocio, solo en creacion)
 *
 * En edicion solo se permite modificar tipoCuenta y estado (alineado a CuentaPatchDTO).
 */
@Component({
  selector: 'app-cuenta-form',
  templateUrl: './cuenta-form.component.html',
  styleUrls: ['./cuenta-form.component.scss']
})
export class CuentaFormComponent implements OnInit, OnDestroy {

  readonly tiposCuenta = Object.values(TipoCuenta);

  cuentaId: number | null = null;
  modoEdicion = false;
  cargando = false;
  guardando = false;

  clientes: Cliente[] = [];
  clientesFiltrados$!: Observable<Cliente[]>;
  cargandoClientes = false;

  private readonly destroy$ = new Subject<void>();

  /** Control de texto del buscador de cliente (separado de "clienteId", que es el usuario real seleccionado). */
  readonly buscadorCliente = new FormControl('');

  readonly form: FormGroup = this.fb.group({
    numeroCuenta: ['', [Validators.required]],
    tipoCuenta: [null, [Validators.required]],
    saldoInicial: [null, [Validators.required, Validators.min(0)]],
    estado: [true, [Validators.required]],
    clienteId: ['', [Validators.required]]
  });

  constructor(
    private readonly fb: FormBuilder,
    private readonly cuentaService: CuentaService,
    private readonly clienteService: ClienteService,
    private readonly route: ActivatedRoute,
    private readonly router: Router
  ) { }

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.cuentaId = Number(idParam);
      this.modoEdicion = true;
      // En edicion (PATCH) solo se permiten modificar tipoCuenta y estado.
      this.form.get('numeroCuenta')?.disable();
      this.form.get('saldoInicial')?.disable();
      this.form.get('clienteId')?.disable();
      this.buscadorCliente.disable();
      this.cargarCuenta(this.cuentaId);
    }

    this.cargandoClientes = true;
    this.clienteService.listar().pipe(takeUntil(this.destroy$)).subscribe({
      next: (clientes) => {
        this.clientes = clientes;
        this.cargandoClientes = false;
        this.clientesFiltrados$ = this.buscadorCliente.valueChanges.pipe(
          takeUntil(this.destroy$),
          startWith(''),
          map((texto) => this.filtrarClientes(texto ?? ''))
        );
      },
      error: () => {
        this.cargandoClientes = false;
      }
    });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private filtrarClientes(texto: string): Cliente[] {
    const valorBusqueda = texto.toLowerCase();
    return this.clientes.filter((cliente) =>
      cliente.clienteId.toLowerCase().includes(valorBusqueda) ||
      cliente.nombre.toLowerCase().includes(valorBusqueda)
    );
  }

  /** Se llama al seleccionar una opción del buscador (mat-autocomplete). */
  seleccionarCliente(cliente: Cliente): void {
    this.form.get('clienteId')?.setValue(cliente.clienteId);
  }

  mostrarCliente(cliente: Cliente): string {
    return cliente ? `${cliente.nombre} (${cliente.clienteId})` : '';
  }

  private cargarCuenta(id: number): void {
    this.cargando = true;
    this.cuentaService.buscarPorId(id).pipe(takeUntil(this.destroy$)).subscribe({
      next: (cuenta) => {
        this.form.patchValue(cuenta);
        this.buscadorCliente.setValue(cuenta.clienteId);
        this.cargando = false;
      },
      error: () => {
        this.cargando = false;
      }
    });
  }

  guardar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.guardando = true;

    if (this.modoEdicion && this.cuentaId) {
      const patch = {
        tipoCuenta: this.form.get('tipoCuenta')?.value,
        estado: this.form.get('estado')?.value
      };
      this.cuentaService.actualizarParcial(this.cuentaId, patch).pipe(takeUntil(this.destroy$)).subscribe({
        next: () => this.router.navigate(['/cuentas']),
        error: () => {
          this.guardando = false;
        }
      });
    } else {
      this.cuentaService.crear(this.form.getRawValue()).pipe(takeUntil(this.destroy$)).subscribe({
        next: () => this.router.navigate(['/cuentas']),
        error: () => {
          this.guardando = false;
        }
      });
    }
  }

  cancelar(): void {
    this.router.navigate(['/cuentas']);
  }
}
