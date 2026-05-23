# ClinicaCare - Appointment Frontend

Aplicação web para gerenciamento de agendamentos de consultas médicas. Este projeto faz parte de um sistema integrado composto por um backend (Spring Boot) e um frontend (Next.js).

## Contexto do Projeto

Este frontend foi desenvolvido como parte de um desafio técnico que envolve:

- **Desenvolvimento ponta a ponta**: interface web completa integrada à API REST, com fluxo de seleção de paciente, agendamento de consultas e cancelamento.
- **Análise de incidente**: tratamento de erros HTTP e feedback visual ao usuário para cenários de falha (conflitos de horário, permissões, indisponibilidade de serviço).

## Tecnologias Utilizadas

- **Next.js 16** (App Router)
- **React 19**
- **TypeScript 5**
- **Tailwind CSS 4**
- **Material UI (MUI) v9** — componentes de interface (inputs, tabelas, modais, breadcrumbs)
- **MUI X Date Pickers** — seleção de data e hora
- **TanStack React Query** — gerenciamento de estado server-side, cache e sincronização de dados
- **React Hook Form** — formulários reativos com performance otimizada
- **Zod** — validação de esquemas e tipos
- **Day.js** — manipulação de datas
- **Sonner** — notificações toast
- **Biome** — linting e formatação

## Requisitos

- Node.js 20+
- pnpm (recomendado) ou npm
- Git
- Backend (`appointment-api`) em execução em `http://localhost:8080`

## Como Executar

### 1. Configurar a URL da API

Crie ou edite o arquivo `.env` na raiz do projeto:

```bash
NEXT_PUBLIC_API_URL=http://localhost:8080
```

> Certifique-se de que o backend está rodando antes de iniciar o frontend.

### 2. Instalar Dependências

```bash
pnpm install
```

### 3. Iniciar o Servidor de Desenvolvimento

```bash
pnpm dev
```

A aplicação estará disponível em `http://localhost:3000`.

### 4. Build para Produção

```bash
pnpm build
pnpm start
```

### Scripts Disponíveis

| Comando | Descrição |
|---------|-----------|
| `pnpm dev` | Inicia o servidor de desenvolvimento com hot reload |
| `pnpm build` | Gera o build otimizado para produção |
| `pnpm start` | Inicia o servidor de produção |
| `pnpm lint` | Executa o linter (Biome) |
| `pnpm format` | Formata o código automaticamente |

## Fluxo de Telas e Funcionalidades

O aplicativo é composto por duas telas principais que formam um fluxo completo de agendamento:

### 1. Seleção de Paciente (`/patients`)

- Lista todos os pacientes cadastrados em cards interativos
- Exibe informações resumidas: nome, data de nascimento, telefone e documento
- Ao clicar em um card, o usuário é redirecionado à tela de agendamentos daquele paciente
- Estado vazio amigável quando não há pacientes cadastrados

### 2. Gerenciamento de Consultas (`/patients/{id}/appointments`)

Esta tela é dividida em duas seções:

#### A. Novo Agendamento (Formulário)

- **Seleção de médico**: dropdown com nome e especialidade
- **Data/hora de início e término**: date-time pickers com validação de datas passadas e lógica de preenchimento automático (ao selecionar início, o término preenche +1h)
- **Observações**: campo de texto livre multiline
- **Validações em tempo real**:
  - Todos os campos obrigatórios
  - Data de término deve ser posterior à data de início
  - Formatos de UUID e ISO datetime validados via Zod
- **Feedback**: toast de sucesso ou erro após submissão
- **Sincronização automática**: a tabela de histórico é atualizada instantaneamente após criação via invalidação de cache do React Query

#### B. Histórico de Consultas (Tabela)

- Lista todas as consultas do paciente em tabela responsiva
- Colunas: Médico, Especialidade, Data de Início, Data de Término, Observações, Ação
- **Visualizar observações**: ícone que abre modal com o conteúdo completo
- **Cancelar consulta**: botão que abre modal de confirmação antes de enviar a requisição DELETE
- Estado vazio com mensagem contextual quando não há consultas
- Contador de agendamentos no cabeçalho da página

## Integração com a API

O frontend consome os seguintes endpoints do backend:

| Serviço | Método | Endpoint | Descrição |
|---------|--------|----------|-----------|
| `getPatients` | `GET` | `/patients` | Lista todos os pacientes |
| `getPatientById` | `GET` | `/patients/{id}` | Detalhes de um paciente |
| `getDoctors` | `GET` | `/doctors` | Lista todos os médicos |
| `getAppointmentsByPatient` | `GET` | `/patients/{id}/appointments` | Consultas de um paciente |
| `createAppointment` | `POST` | `/appointments` | Cria um novo agendamento |
| `cancelAppointmentByPatient` | `DELETE` | `/appointments/{id}/patients/{patientId}` | Cancela um agendamento |

### Tratamento de Erros

Todas as requisições passam por uma classe customizada `HttpResponseError` que padroniza o tratamento de falhas:

- **Mensagens da API**: exibidas diretamente ao usuário via toast (ex: "Conflito de horário")
- **Erros de validação (400)**: lista de campos inválidos formatados em string legível
- **Erros de servidor (500)**: mensagem genérica amigável com orientação para contatar o administrador
- **Páginas de erro**: estados de erro visualmente diferenciados com ícones e texto explicativo quando o carregamento inicial falha

## Validações

As validações ocorrem em duas camadas:

1. **Cliente (Zod + React Hook Form)**:
   - `doctorId` e `patientId`: UUID válido
   - `initialDatetime` e `endDatetime`: ISO datetime válido
   - `endDatetime` deve ser maior ou igual a `initialDatetime`
   - Campos obrigatórios

2. **Servidor (Spring Validation)**:
   - Regras duplicadas garantem integridade mesmo se o client-side for burlado

## Estrutura de Pastas

```
appointment-frontend/
├── app/
│   ├── patients/
│   │   ├── page.tsx                    # Tela de seleção de pacientes
│   │   └── [id]/appointments/
│   │       └── page.tsx                # Tela de agendamentos do paciente
│   ├── layout.tsx                      # Layout raiz com fontes e providers
│   ├── ClientProviders.tsx             # Provedores client-side (React Query, MUI Theme, Dayjs, Sonner)
│   ├── page.tsx                        # Redireciona / → /patients
│   └── globals.css                     # Estilos globais + Tailwind
├── components/
│   ├── patients/
│   │   └── PatientsSelector.tsx        # Grid de cards de pacientes
│   └── appointments/
│       ├── AppointmentForm.tsx         # Formulário de criação de consulta
│       ├── AppointmentTable.tsx        # Tabela de histórico de consultas
│       ├── DeleteConfirmationModal.tsx # Modal de confirmação de cancelamento
│       └── NotesModal.tsx              # Modal de visualização de observações
├── services/
│   ├── getPatients.ts
│   ├── getPatientById.ts
│   ├── getDoctors.ts
│   ├── getAppointments.ts
│   ├── getAppointmentsByPatient.ts
│   ├── createAppointment.ts
│   └── cancelAppointmentByPatient.ts
├── types/
│   └── entities/
│       ├── Patient.ts
│       ├── Doctor.ts
│       └── Appointment.ts
├── utils/
│   ├── schemas/
│   │   └── appointment.ts              # Schema Zod para validação de formulário
│   └── errors/
│       └── ErrorHttpResponse.ts        # Classe de erro padronizada para HTTP
├── .env                                # Variáveis de ambiente
├── package.json
├── tsconfig.json
├── biome.json                          # Configuração de lint/format
└── README.md                           # Este arquivo
```

## Decisões Técnicas, Trade-offs e Melhorias Futuras

### Decisões Tomadas

1. **Server Components + Client Components**: as páginas (`page.tsx`) são Server Components que buscam dados iniciais via `fetch`, enquanto formulários, tabelas e modais são Client Components com interatividade. Isso reduz JavaScript enviado ao cliente e melhora o SEO/TTI.
2. **React Query para mutações**: após criar ou cancelar uma consulta, o cache do React Query é invalidado automaticamente, evitando necessidade de refresh manual da página.
3. **Zod + React Hook Form**: validação declarativa e tipada com re-renderização mínima, proporcionando UX fluida em formulários complexos.
4. **MUI + Tailwind CSS**: MUI fornece componentes acessíveis e consistentes; Tailwind adiciona utilitários rápidos para layout e espaçamento customizado sem CSS modules.
5. **Biome ao invés de ESLint + Prettier**: ferramenta única e rápida para lint e format, com suporte nativo a React e Next.js.

### Trade-offs

- **Sem testes automatizados no frontend**: a prioridade foi entregar o fluxo completo funcional e integrado. O projeto atual não possui testes unitários ou E2E, o que aumenta o risco de regressões em refatorações de UI.
- **Fetch nativo ao invés de Axios/TanStack Query no servidor**: as Server Components usam `fetch` nativo do Next.js. Isso é simples e eficiente, mas perde-se o interceptador centralizado de erros que bibliotecas como Axios oferecem.
- **Estado local para modais**: os modais de confirmação e observações usam `useState` local. Para uma aplicação maior, um gerenciador de diálogos global (ex: Zustand ou Context API) evitaria prop drilling.

### Possíveis Melhorias Futuras

- [ ] Adicionar **testes unitários** com Vitest + React Testing Library para componentes e serviços
- [ ] Adicionar **testes E2E** com Playwright cobrindo o fluxo completo: selecionar paciente → agendar → cancelar
- [ ] Implementar **paginação e busca** na tabela de consultas e na lista de pacientes
- [ ] Adicionar **edição de consultas** (PATCH) diretamente na interface, reaproveitando o formulário de criação
- [ ] Criar um **interceptador HTTP global** para tratamento unificado de erros 401/403/500 e refresh de token
- [ ] Implementar **skeleton screens** e estados de loading mais granulares durante a navegação
- [ ] Adicionar **internacionalização (i18n)** para suportar múltiplos idiomas
- [ ] Substituir `fetch` por **Axios** ou configurar um **cliente HTTP customizado** com timeout, retries e cancelamento de requisições

---

> **Dica**: Certifique-se de que o backend (`appointment-api`) esteja rodando em `http://localhost:8080` antes de iniciar o frontend. A interface espera que a API esteja disponível para listar pacientes, médicos e consultas.
