# Plano de Implementação do Frontend (Angular 18 + Material)

Este documento descreve um plano objetivo para implementar um aplicativo Angular simples, limpo e bem estruturado para consumir o backend de manutenção e transferência de benefícios.

## Objetivo

- Implementar um CRUD de Benefícios e a funcionalidade de Transferência, consumindo as APIs existentes do backend.
- Entregar UI/UX limpa usando Angular Material, formulários reativos e boas práticas.

## APIs do Backend (referência)

- Listar benefícios: `GET /api/v1/beneficios`
- Obter benefício: `GET /api/v1/beneficios/{id}`
- Criar benefício: `POST /api/v1/beneficios`
- Atualizar benefício: `PUT /api/v1/beneficios/{id}`
- Remover benefício: `DELETE /api/v1/beneficios/{id}`
- Transferência: `POST /api/v1/beneficios/transfer` payload `{ fromId, toId, amount }`
- Erros: respostas em `{ "error": "mensagem" }` com status 400/422/404
- Porta: `8080`

## Stack e decisões

- Angular 18 com componentes standalone.
- Angular Material (tema prebuilt) e Reactive Forms.
- `HttpClient` com interceptor de erros (MatSnackBar).
- Proxy do Angular DevServer para evitar CORS em desenvolvimento.

## Estrutura de Pastas

```
src/app/
  core/
    models/
      beneficio.model.ts
    services/
      beneficios.service.ts
    interceptors/
      error.interceptor.ts
  shared/
    components/
      confirm-dialog/
        confirm-dialog.component.ts
  features/
    beneficios/
      list/
        beneficios-list.component.ts
      form/
        beneficios-form.component.ts
    transfer/
      form/
        transfer-form.component.ts
  app.routes.ts
  app.config.ts
styles.scss
```

## Configuração (environments e proxy)

1) Environments

Criar arquivos:

`src/environments/environment.ts`
```ts
export const environment = {
  production: false,
  apiUrl: '/api/v1',
};
```

`src/environments/environment.development.ts`
```ts
export const environment = {
  production: false,
  apiUrl: '/api/v1',
};
```

2) Proxy (evita CORS no dev)

`proxy.conf.json`
```json
{
  "/api": {
    "target": "http://localhost:8080",
    "secure": false,
    "changeOrigin": true
  }
}
```

Atualizar script de start no `package.json` para usar o proxy:

```json
{
  "scripts": {
    "start": "ng serve --proxy-config proxy.conf.json"
  }
}
```

## Modelos (TypeScript)

`src/app/core/models/beneficio.model.ts`
```ts
export interface Beneficio {
  id: number;
  nome: string;
  descricao?: string;
  valor: number;
  ativo: boolean;
}

export interface BeneficioRequest {
  nome: string;
  descricao?: string;
  valor: number;
  ativo: boolean;
}

export interface TransferRequest {
  fromId: number;
  toId: number;
  amount: number;
}
```

## Service HTTP

`src/app/core/services/beneficios.service.ts`
```ts
import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Beneficio, BeneficioRequest, TransferRequest } from '../models/beneficio.model';

@Injectable({ providedIn: 'root' })
export class BeneficiosService {
  private readonly http = inject(HttpClient);
  private readonly base = `${environment.apiUrl}/beneficios`;

  list(): Observable<Beneficio[]> { return this.http.get<Beneficio[]>(this.base); }
  get(id: number): Observable<Beneficio> { return this.http.get<Beneficio>(`${this.base}/${id}`); }
  create(dto: BeneficioRequest): Observable<Beneficio> { return this.http.post<Beneficio>(this.base, dto); }
  update(id: number, dto: BeneficioRequest): Observable<Beneficio> { return this.http.put<Beneficio>(`${this.base}/${id}`, dto); }
  delete(id: number): Observable<void> { return this.http.delete<void>(`${this.base}/${id}`); }
  transfer(payload: TransferRequest): Observable<void> { return this.http.post<void>(`${this.base}/transfer`, payload); }
}
```

Adicionar o HttpClient no bootstrap (em `app.config.ts`):

```ts
import { provideHttpClient, withInterceptors } from '@angular/common/http';
// ... no providers:
provideHttpClient(withInterceptors([errorInterceptor]))
```

## Interceptor de Erros

`src/app/core/interceptors/error.interceptor.ts`
```ts
import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { catchError, throwError } from 'rxjs';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const snack = inject(MatSnackBar);
  return next(req).pipe(
    catchError((err: HttpErrorResponse) => {
      const msg = (err.error?.error as string) || 'Falha ao comunicar com o servidor';
      snack.open(msg, 'Fechar', { duration: 4000 });
      return throwError(() => err);
    })
  );
};
```

Lembrete: importar `MatSnackBarModule` onde o app é inicializado (ou no componente raiz) e garantir `provideAnimationsAsync()` já presente.

## Rotas e Layout

`src/app/app.routes.ts`
```ts
import { Routes } from '@angular/router';
import { BeneficiosListComponent } from './features/beneficios/list/beneficios-list.component';
import { BeneficiosFormComponent } from './features/beneficios/form/beneficios-form.component';
import { TransferFormComponent } from './features/transfer/form/transfer-form.component';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'beneficios' },
  { path: 'beneficios', component: BeneficiosListComponent, title: 'Benefícios' },
  { path: 'beneficios/new', component: BeneficiosFormComponent, title: 'Novo Benefício' },
  { path: 'beneficios/:id', component: BeneficiosFormComponent, title: 'Editar Benefício' },
  { path: 'beneficios/transfer', component: TransferFormComponent, title: 'Transferência' },
  { path: '**', redirectTo: 'beneficios' },
];
```

Toolbar simples (no AppComponent) com navegação para Benefícios e Transferência usando `MatToolbar`, `MatButton` e `routerLink`.

## Páginas

1) Lista de Benefícios (`BeneficiosListComponent`)

- `MatTable` com colunas: `id`, `nome`, `descricao`, `valor | currency:'BRL'`, `ativo`, `acoes`.
- Busca por nome com `MatFormField + input` (filtro local).
- `MatPaginator` e `MatSort` (client-side para simplicidade).
- Ações: Editar (navega para `/beneficios/:id`), Excluir (confirmação em `MatDialog`), Transferir (atalho para `/beneficios/transfer`).

2) Formulário de Benefício (`BeneficiosFormComponent`)

- Reactive Form com campos: `nome` (required), `descricao`, `valor` (required, > 0), `ativo` (checkbox).
- Modo criar: `/beneficios/new` → `create()`
- Modo editar: `/beneficios/:id` → carrega `get(id)` e usa `update(id)`
- Feedback com `MatSnackBar` e navegação de volta para a lista.

3) Transferência (`TransferFormComponent`)

- Form com `fromId`, `toId`, `amount` (required, > 0).
- Envia `POST /beneficios/transfer`. Exibe sucesso/erro via snackbar.

## Módulos/Imports de UI (Material)

- `MatToolbarModule`, `MatButtonModule`, `MatIconModule`
- `MatTableModule`, `MatPaginatorModule`, `MatSortModule`
- `MatFormFieldModule`, `MatInputModule`, `MatCheckboxModule`
- `MatSnackBarModule`, `MatDialogModule`, `MatCardModule`, `MatProgressSpinnerModule`

## Execução

1) Backend rodando em `http://localhost:8080`.
2) No frontend: `npm start` (usa o proxy para `/api`).
3) Acessar `http://localhost:4200`.

## Entregáveis (MVP)

- Models, service e interceptor implementados.
- Rotas configuradas e toolbar básica.
- Páginas de Lista, Formulário e Transferência funcionando e integradas ao backend.
- Tratamento de erros via snackbar.

## Próximos incrementos (opcional)

- Paginação/ordenação server-side.
- Máscara e formatação para valores monetários.
- Testes unitários leves para o service e componentes.

