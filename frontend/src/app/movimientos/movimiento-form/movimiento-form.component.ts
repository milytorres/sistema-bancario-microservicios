import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Observable, Subject } from 'rxjs';
import { map, startWith, takeUntil } from 'rxjs/operators';

import { MovimientoService } from '../movimiento.service';
import { TipoMovimiento } from '../movimiento.model';
import { Cuenta } from '../../cuentas/cuenta.model';
import { CuentaService } from '../../cuentas/cuenta.service';

/**
 * Formulario de registro de movimiento (POST /movimientos).
 * Validadores alineados a MovimientoRequestDTO (backend/cuenta):
 * - cuentaId: requerido
 * - tipoMovimiento: requerido
 * - valor: requerido, mayor a cero (min 0.01)
 *
 * La validacion de saldo disponible (si aplica para retiros) vive en el backend;
 * el frontend solo muestra el error que el backend devuelva (ErrorResponseDTO).
 */
@Component({
  selector: 'app-movimiento-form',
  templateUrl: './movimiento-form.component.html',
  styleUrls: ['./movimiento-form.component.scss']
})
export class MovimientoFormComponent implements OnInit, OnDestroy {

  readonly tiposMovimiento = Object.values(TipoMovimiento);

  cuentas: Cuenta[] = [];
  cuentasFiltradas$!: Observable<Cuenta[]>;
  cargandoCuentas = false;
  guardando = false;

  /** Control de texto del buscador (separado de cuentaId, que guarda el id real seleccionado). */
  readonly buscadorCuenta = new FormControl('');

  private readonly destroy$ = new Subject<void>();

  readonly form: FormGroup = this.fb.group({
    cuentaId: [null, [Validators.required]],
    tipoMovimiento: [null, [Validators.required]],
    valor: [null, [Validators.required, Validators.min(0.01)]]
  });

  constructor(
    private readonly fb: FormBuilder,
    private readonly movimientoService: MovimientoService,
    private readonly cuentaService: CuentaService,
    private readonly router: Router
  ) { }

  ngOnInit(): void {
    this.cargandoCuentas = true;
    this.cuentaService.listar().pipe(takeUntil(this.destroy$)).subscribe({
      next: (cuentas) => {
        this.cuentas = cuentas;
        this.cargandoCuentas = false;
        this.cuentasFiltradas$ = this.buscadorCuenta.valueChanges.pipe(
          takeUntil(this.destroy$),
          startWith(''),
          map((texto) => this.filtrarCuentas(texto ?? ''))
        );
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

  private filtrarCuentas(texto: string): Cuenta[] {
    const valorBusqueda = texto.toLowerCase();
    return this.cuentas.filter((cuenta) =>
      cuenta.numeroCuenta.toLowerCase().includes(valorBusqueda) ||
      cuenta.tipoCuenta.toLowerCase().includes(valorBusqueda)
    );
  }

  /** Se llama al seleccionar una opción del buscador (mat-autocomplete). */
  seleccionarCuenta(cuenta: Cuenta): void {
    this.form.get('cuentaId')?.setValue(cuenta.id);
  }

  mostrarCuenta(cuenta: Cuenta): string {
    return cuenta ? `${cuenta.numeroCuenta} (${cuenta.tipoCuenta})` : '';
  }

  registrar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.guardando = true;
    this.movimientoService.registrar(this.form.getRawValue()).pipe(takeUntil(this.destroy$)).subscribe({
      next: () => this.router.navigate(['/movimientos']),
      error: () => {
        this.guardando = false;
      }
    });
  }

  cancelar(): void {
    this.router.navigate(['/movimientos']);
  }
}
