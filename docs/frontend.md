# Frontend (Angular 18 + Material)

Este documento descreve a arquitetura, funcionalidades e o passo a passo para subir o frontend deste desafio técnico pela primeira vez.

## Visão Geral

- Stack: Angular 18 (standalone components) + Angular Material (tema prebuilt azure-blue).
- Objetivo: CRUD de Benefícios e Transferência entre benefícios, consumindo o backend Spring Boot.
- Tratamento de erros: interceptor global exibe mensagens do backend via `MatSnackBar`.

## APIs Consumidas

- Listar benefícios: `GET /api/v1/beneficios`
- Obter benefício: `GET /api/v1/beneficios/{id}`
- Criar benefício: `POST /api/v1/beneficios`
- Atualizar benefício: `PUT /api/v1/beneficios/{id}`
- Remover benefício: `DELETE /api/v1/beneficios/{id}`
- Transferência: `POST /api/v1/beneficios/transfer` com payload `{ fromId, toId, amount }`
- Erros: backend retorna `{ "error": "mensagem" }` com status 400/422/404

## Estrutura (arquivos relevantes)

- `frontend/src/app/app.component.*` – Toolbar e navegação principal.
- `frontend/src/app/app.routes.ts` – Rotas standalone (lazy loading).
- `frontend/src/app/core/models/beneficio.model.ts` – Tipos `Beneficio`, `BeneficioRequest`, `TransferRequest`.
- `frontend/src/app/core/services/beneficios.service.ts` – Cliente HTTP (CRUD + transfer).
- `frontend/src/app/core/interceptors/error.interceptor.ts` – Interceptor de erros (snackbar).
- `frontend/src/app/features/beneficios/list/beneficios-list.component.*` – Lista com filtro, paginação e ações.
- `frontend/src/app/features/beneficios/form/beneficios-form.component.*` – Formulário criar/editar.
- `frontend/src/app/features/transfer/form/transfer-form.component.*` – Tela de transferência com selects e validação.
- `frontend/src/environments/*` – `environment.ts` e `environment.development.ts` com `apiUrl: '/api/v1'`.
- `frontend/proxy.conf.json` – Proxy do dev-server para `http://localhost:8080`.
- `frontend/angular.json` – Config do build (tema Material, fileReplacements de environment, budgets).

## Pré‑requisitos

- Node.js 18+ (ou 20 LTS).
- Backend rodando em `http://localhost:8080` (padrão do projeto Spring Boot).

## Primeira execução (após clonar)

1) Instalar dependências do frontend
- `cd frontend`
- `npm install`

2) Subir em modo desenvolvimento (com proxy)
- `npm start`
- Acesse: `http://localhost:4200`

Observações
- O proxy (`frontend/proxy.conf.json`) redireciona chamadas `"/api"` para `http://localhost:8080`. O código usa `environment.apiUrl = '/api/v1'`, então nenhuma alteração é necessária se o backend estiver na porta 8080 local.
- Caso o backend rode em outra porta/host, ajuste `frontend/proxy.conf.json` (propriedade `target`).

## Build de produção

- `cd frontend`
- `npm run build`
- Saída em `frontend/dist/tmp`.

## Funcionalidades

- Lista de Benefícios
  - Tabela (`MatTable`) com filtro por texto, ordenação e paginação (client‑side).
  - Ações: Criar, Editar, Excluir (com diálogo de confirmação) e Transferir.

- Formulário de Benefício
  - Campos: `nome` (obrigatório), `descricao`, `valor` (obrigatório, ≥ 0), `ativo`.
  - Criação e edição usando `Reactive Forms` e `BeneficiosService`.

- Transferência de Benefícios
  - Combos para selecionar benefício de origem e destino (dados carregados do backend).
  - Campo de valor (obrigatório, > 0).
  - Validação impede selecionar a mesma origem e destino.
  - Suporta pré‑selecionar origem via query param `?fromId=` ao clicar em “Transferir” pela lista.

## Rotas

- `/beneficios` – Lista
- `/beneficios/new` – Criar
- `/beneficios/:id` – Editar
- `/beneficios/transfer` – Transferir

## Notas Técnicas

- `HttpClient` e o interceptor são fornecidos no bootstrap (`app.config.ts`).
- O tema Material usado é o prebuilt `azure-blue`. Ajustável em `frontend/angular.json` (campo `styles`).
- A rota `beneficios/transfer` é declarada antes de `beneficios/:id` para evitar colisão.

## Troubleshooting

- Erro de CORS durante desenvolvimento: garanta que está rodando com `npm start` (usa o proxy). Se necessário, confira/encontre `frontend/proxy.conf.json`.
- Backend fora da porta 8080: altere `target` no proxy ou rode o backend na porta padrão.
- Aviso de budget no build: ajuste budgets em `frontend/angular.json` se quiser suprimir o warning.

