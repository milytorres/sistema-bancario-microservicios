import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { ReporteFormComponent } from './reporte-form/reporte-form.component';

const routes: Routes = [
  { path: '', component: ReporteFormComponent }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ReportesRoutingModule { }
