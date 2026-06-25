import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

import { ClienteService } from '../cliente.service';

/**
 * Formulario de creacion/edicion de Cliente.
 * Validadores alineados a ClienteRequestDTO (backend/cliente):
 * - nombre: requerido, maximo 120 caracteres
 * - genero: maximo 20 caracteres
 * - edad: numero positivo
 * - identificacion: requerido, maximo 30 caracteres
 * - direccion: maximo 200 caracteres
 * - telefono: maximo 20 caracteres
 * - clienteId: requerido, maximo 50 caracteres (solo en creacion, no editable)
 * - contrasena: requerida solo al crear (ClientePatchDTO la vuelve opcional)
 * - estado: requerido
 */
@Component({
  selector: 'app-cliente-form',
  templateUrl: './cliente-form.component.html',
  styleUrls: ['./cliente-form.component.scss']
})
export class ClienteFormComponent implements OnInit, OnDestroy {

  clienteId: number | null = null;
  modoEdicion = false;
  cargando = false;
  guardando = false;
  readonly hoy = new Date();

  private readonly destroy$ = new Subject<void>();

  readonly form: FormGroup = this.fb.group({
    nombre: ['', [Validators.required, Validators.maxLength(120)]],
    genero: ['', [Validators.maxLength(20)]],
    /** Fecha de nacimiento: solo se usa en el frontend para calcular "edad" (campo real del backend). */
    fechaNacimiento: [null],
    // Sin validadores: Angular nunca los ejecuta en un control deshabilitado.
    // El valor lo calcula calcularEdad() a partir de fechaNacimiento, siempre positivo.
    edad: [{ value: null, disabled: true }],
    identificacion: ['', [Validators.required, Validators.maxLength(30)]],
    direccion: ['', [Validators.maxLength(200)]],
    telefono: ['', [Validators.maxLength(20)]],
    clienteId: ['', [Validators.required, Validators.maxLength(50)]],
    contrasena: ['', [Validators.required]],
    estado: [true, [Validators.required]]
  });

  constructor(
    private readonly fb: FormBuilder,
    private readonly clienteService: ClienteService,
    private readonly route: ActivatedRoute,
    private readonly router: Router
  ) { }

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.clienteId = Number(idParam);
      this.modoEdicion = true;
      // En edicion, el clienteId de negocio no se modifica.
      this.form.get('clienteId')?.disable();
      // En edicion, la contrasena es opcional (PATCH).
      this.form.get('contrasena')?.clearValidators();
      this.form.get('contrasena')?.updateValueAndValidity();
      this.cargarCliente(this.clienteId);
    }

    this.form.get('fechaNacimiento')?.valueChanges.pipe(takeUntil(this.destroy$)).subscribe((fecha: Date | null) => {
      if (fecha) {
        this.form.get('edad')?.setValue(this.calcularEdad(fecha));
      }
    });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  /** Calcula la edad en años completos a partir de una fecha de nacimiento. */
  private calcularEdad(fechaNacimiento: Date): number {
    const hoy = new Date();
    let edad = hoy.getFullYear() - fechaNacimiento.getFullYear();
    const aunNoCumpleEsteAnio =
      hoy.getMonth() < fechaNacimiento.getMonth() ||
      (hoy.getMonth() === fechaNacimiento.getMonth() && hoy.getDate() < fechaNacimiento.getDate());
    if (aunNoCumpleEsteAnio) {
      edad--;
    }
    return edad;
  }

  private cargarCliente(id: number): void {
    this.cargando = true;
    this.clienteService.buscarPorId(id).pipe(takeUntil(this.destroy$)).subscribe({
      next: (cliente) => {
        this.form.patchValue(cliente);
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

    if (this.modoEdicion && this.clienteId) {
      // PATCH: solo se envia "contrasena" si el usuario realmente la modifico
      // (el control queda vacio por defecto en edicion porque no se precarga el password).
      // "fechaNacimiento" nunca se envia: es solo un dato auxiliar del frontend para calcular "edad".
      const { contrasena, clienteId, fechaNacimiento, ...resto } = this.form.getRawValue();
      const patch = contrasena ? { ...resto, contrasena } : resto;

      this.clienteService.actualizarParcial(this.clienteId, patch).pipe(takeUntil(this.destroy$)).subscribe({
        next: () => this.router.navigate(['/clientes']),
        error: () => {
          this.guardando = false;
        }
      });
    } else {
      const { fechaNacimiento, ...payload } = this.form.getRawValue();
      this.clienteService.crear(payload).pipe(takeUntil(this.destroy$)).subscribe({
        next: () => this.router.navigate(['/clientes']),
        error: () => {
          this.guardando = false;
        }
      });
    }
  }

  cancelar(): void {
    this.router.navigate(['/clientes']);
  }
}
