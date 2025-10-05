import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'beneficios'
  },
  {
    path: 'beneficios',
    loadComponent: () =>
      import('./features/beneficios/list/beneficios-list.component').then((m) => m.BeneficiosListComponent),
    title: 'Benefícios'
  },
  {
    path: 'beneficios/new',
    loadComponent: () =>
      import('./features/beneficios/form/beneficios-form.component').then((m) => m.BeneficiosFormComponent),
    title: 'Novo Benefício'
  },
  {
    path: 'beneficios/transfer',
    loadComponent: () =>
      import('./features/transfer/form/transfer-form.component').then((m) => m.TransferFormComponent),
    title: 'Transferência de Benefícios'
  },
  {
    path: 'beneficios/:id',
    loadComponent: () =>
      import('./features/beneficios/form/beneficios-form.component').then((m) => m.BeneficiosFormComponent),
    title: 'Editar Benefício'
  },
  {
    path: '**',
    redirectTo: 'beneficios'
  }
];
