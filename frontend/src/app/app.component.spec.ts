import { TestBed } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { RouterTestingModule } from '@angular/router/testing';
import { AppComponent } from './app.component';

describe('AppComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule
      ],
      declarations: [
        AppComponent
      ],
      // NO_ERRORS_SCHEMA: el template solo contiene <app-main-layout>, declarado en
      // LayoutModule (no importado aqui). Se ignora el elemento desconocido para
      // poder probar el componente raiz de forma aislada.
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();
  });

  it('debe crear el componente raiz de la aplicacion', () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });

  it(`debe tener como titulo 'frontend'`, () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    expect(app.title).toEqual('frontend');
  });
});
