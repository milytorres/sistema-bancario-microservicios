import { Component } from '@angular/core';

interface NavLink {
  label: string;
  route: string;
  icon: string;
}

@Component({
  selector: 'app-main-layout',
  templateUrl: './main-layout.component.html',
  styleUrls: ['./main-layout.component.scss']
})
export class MainLayoutComponent {

  readonly navLinks: NavLink[] = [
    { label: 'Clientes', route: '/clientes', icon: 'people' },
    { label: 'Cuentas', route: '/cuentas', icon: 'account_balance_wallet' },
    { label: 'Movimientos', route: '/movimientos', icon: 'swap_horiz' },
    { label: 'Reportes', route: '/reportes', icon: 'assessment' }
  ];
}
