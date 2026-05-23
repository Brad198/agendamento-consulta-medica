# ClinicaCare - Appointment API

API REST para gerenciamento de agendamentos de consultas médicas. Este projeto faz parte de um sistema integrado composto por um backend (Spring Boot) e um frontend (Next.js).

## Contexto do Projeto

Esta API foi desenvolvida como parte de um desafio técnico que envolve:

- **Desenvolvimento ponta a ponta**: criação de recursos no front-end, API no back-end, persistência em banco de dados e logs mínimos para diagnóstico.
- **Análise de incidente**: cenários de erro recorrente com base em logs, sugerindo correções e medidas de prevenção.

## Tecnologias Utilizadas

- **Java 21**
- **Spring Boot 4.0.6**
- **Spring Data JPA**
- **Spring Validation**
- **SpringDoc OpenAPI** (documentação da API)
- **PostgreSQL** (banco de dados relacional)
- **Docker & Docker Compose** (infraestrutura local)
- **Testcontainers** (testes de integração com PostgreSQL real)
- **JUnit 5 & Mockito** (testes unitários)
- **Maven** (gerenciamento de dependências e build)

## Requisitos

- Java 21
- Git
- Docker e Docker Compose
- Maven (ou use o wrapper `./mvnw` incluso no projeto)

## Como Executar

### 1. Configurar o Banco de Dados

Crie um arquivo `.env` na raiz do projeto `appointment-api`:

```bash
DATABASE_NAME=clinicare
DATABASE_USER=postgres
DATABASE_PASSWORD=postgres
DATABASE_HOST=localhost
DATABASE_PORT=5432
DATABASE_DIALECT=postgresql
```

Inicie o PostgreSQL via Docker Compose:

```bash
docker compose up -d
```

> O arquivo `compose.yaml` já está configurado com as credenciais padrão.

### 2. Executar a Aplicação

```bash
./mvnw spring-boot:run
```

A API estará disponível em:
- **Base URL**: `http://localhost:8080`
- **Documentação OpenAPI**: `http://localhost:8080/api-docs`
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **Health Check**: `http://localhost:8080/actuator/health`

### 3. Dados Iniciais

Ao iniciar, o banco é inicializado automaticamente (`ddl-auto: create-drop` + `data.sql`) com:
- **8 médicos** de diversas especialidades
- **4 pacientes**
- **6 consultas** pré-agendadas

> Isso facilita os testes manuais e a demonstração do fluxo completo.

## Endpoints da API

### Médicos

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `GET` | `/doctors` | Listar todos os médicos |
| `GET` | `/doctors/{id}` | Buscar médico por UUID |

### Pacientes

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `GET` | `/patients` | Listar todos os pacientes |
| `GET` | `/patients/{id}` | Buscar paciente por UUID |
| `GET` | `/patients/{id}/appointments` | Listar consultas de um paciente |

### Consultas

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `GET` | `/appointments` | Listar todas as consultas |
| `GET` | `/appointments/{id}` | Buscar consulta por ID |
| `POST` | `/appointments` | Criar nova consulta |
| `PATCH` | `/appointments/{id}` | Atualizar parcialmente uma consulta |
| `DELETE` | `/appointments/{appointmentId}/patients/{patientId}` | Cancelar consulta de um paciente |

### Payloads

#### Criar Consulta (`POST /appointments`)

```json
{
  "doctorId": "018f9e61-a111-7a6f-b123-456789abcdef",
  "patientId": "018f9e61-b111-7a6f-b123-456789abcdef",
  "initialDatetime": "2026-12-25T14:00:00",
  "endDatetime": "2026-12-25T15:00:00",
  "notes": "Consulta de rotina"
}
```

**Regras de validação:**
- `doctorId` e `patientId` são obrigatórios
- `initialDatetime` e `endDatetime` são obrigatórios
- `initialDatetime` deve ser uma data futura (`@Future`)
- `endDatetime` deve ser maior que `initialDatetime`
- `notes` tem limite de 255 caracteres
- Não é permitido agendar em horários conflitantes (médico ou paciente já ocupado)

#### Atualizar Consulta (`PATCH /appointments/{id}`)

```json
{
  "doctorId": "018f9e61-a111-7a6f-b123-456789abcdef",
  "patientId": "018f9e61-b111-7a6f-b123-456789abcdef",
  "initialDatetime": "2026-12-25T16:00:00",
  "endDatetime": "2026-12-25T17:00:00",
  "notes": "Horário atualizado"
}
```

**Regras de segurança:**
- Apenas o paciente dono da consulta pode atualizá-la
- Não é permitido mover para horários conflitantes

### Respostas de Erro

A API retorna erros padronizados com mensagens claras:

- `404 Not Found` — Recurso não encontrado (médico, paciente ou consulta)
- `409 Conflict` — Conflito de horário ou recurso já existente
- `403 Forbidden` — Ação não permitida (ex: paciente tentando alterar consulta de outro)
- `400 Bad Request` — Dados de entrada inválidos (validação)

## Testes

O projeto possui testes unitários e de integração cobrindo os cenários principais.

### Executar todos os testes

```bash
./mvnw clean test
```

### Testes Unitários

Localizados em `src/test/java/com/clinicare/appointment_api/service/`:

| Classe | Cenários Cobertos |
|--------|-------------------|
| `AppointmentServiceTest` | 11 cenários: buscar, listar, criar, atualizar, cancelar consultas; validações de conflito de horário, permissões e recursos inexistentes |
| `DoctorServiceTest` | 3 cenários: listar médicos, buscar por ID existente e inexistente |
| `PatientServiceTest` | 3 cenários: listar pacientes, buscar por ID existente e inexistente |

**Destaques dos testes de agendamento:**
- Validação de conflito de horário no momento da criação
- Verificação de permissão (apenas o dono pode alterar/cancelar)
- Tratamento de médicos e pacientes inexistentes
- Uso de mocks (Mockito) para isolamento de testes

### Testes de Integração

`AppointmentServiceIntegrationTest` — utiliza **Testcontainers** com PostgreSQL real:

- **Concorrência na criação**: garante que apenas um agendamento seja criado quando duas threads tentam reservar o mesmo horário simultaneamente (testa o lock pessimista)
- **Concorrência na atualização**: garante que apenas uma atualização simultânea para o mesmo slot seja bem-sucedida

> Esses testes validam a integridade do banco e o controle de concorrência em cenários reais de race condition.

## Logs e Diagnóstico

A aplicação utiliza o log padrão do Spring Boot (SLF4J/Logback). Logs são emitidos automaticamente para:

- Requisições HTTP recebidas
- Erros de validação e exceções de negócio
- Operações de persistência (`show-sql: true` no `application.yml` para desenvolvimento)

**Configuração de logs:**
- Stack traces não são expostos em respostas de erro (`include-stacktrace: never`)
- Health check disponível em `/actuator/health` para monitoramento

## Estrutura de Pastas

```
appointment-api/
├── src/
│   ├── main/
│   │   ├── java/com/clinicare/appointment_api/
│   │   │   ├── controller/          # Controllers REST
│   │   │   ├── service/             # Regras de negócio
│   │   │   ├── repository/          # Interfaces Spring Data JPA
│   │   │   ├── entity/              # Entidades JPA
│   │   │   ├── dto/                 # Data Transfer Objects (records)
│   │   │   ├── shared/              # Exceções, enums, utilitários e validações customizadas
│   │   │   └── configuration/       # Configurações (CORS, OpenAPI, Exception Handler)
│   │   └── resources/
│   │       ├── application.yml      # Configurações da aplicação
│   │       └── data.sql             # Dados iniciais (seed)
│   └── test/
│       └── java/com/clinicare/appointment_api/
│           └── service/             # Testes unitários e de integração
├── compose.yaml                     # Docker Compose (PostgreSQL)
├── Dockerfile                       # Imagem da aplicação
├── pom.xml                          # Dependências Maven
└── README.md                        # Este arquivo
```

## Decisões Técnicas, Trade-offs e Melhorias Futuras

### Decisões Tomadas

1. **Lock pessimista (`PESSIMISTIC_WRITE`) no repositório de médicos**: escolhido para evitar race conditions na criação e atualização de consultas, garantindo integridade dos horários em ambientes concorrentes.
2. **Validação customizada `@StartBeforeEnd`**: reutilizável e declarativa, garantindo que `endDatetime` seja sempre posterior a `initialDatetime`.
3. **Uso de records para DTOs**: imutabilidade, menos boilerplate e clareza na API.
4. **Dados seed via `data.sql`**: acelera o desenvolvimento e demonstração sem necessidade de scripts externos.
5. **Exceções de negócio específicas**: `ResourceNotFoundException`, `ResourceAlreadyExistsException`, `ForbiddenActionException` e `BussinessException` permitem mapeamento preciso de status HTTP e mensagens amigáveis.

### Trade-offs

- **`ddl-auto: create-drop`**: facilita o desenvolvimento local, mas deve ser alterado para `validate` ou `none` em produção. Em produção, migrations com Flyway ou Liquibase são recomendadas.
- **Documentação via SpringDoc**: gera a documentação automaticamente, mas depende de anotações nos controllers. Em APIs muito grandes, manter documentação separada (ex: Markdown) pode ser mais legível.
- **Testes focados na camada de serviço**: optei por testar a lógica de negócio principal via unitários e usar integração apenas para concorrência. Isso acelera o feedback loop, mas adicionar testes de controller (@WebMvcTest) aumentaria a cobertura de contrato da API.

### Possíveis Melhorias Futuras

- [ ] Adicionar **autenticação e autorização** (OAuth2/JWT) para identificar o paciente logado, eliminando a necessidade de passar `patientId` nas URLs de cancelamento/alteração.
- [ ] Implementar **paginação** nos endpoints de listagem (`/appointments`, `/doctors`, `/patients`) para suportar grandes volumes de dados.
- [ ] Adicionar **soft delete** em consultas (campo `deletedAt`) ao invés de remoção física, preservando histórico.
- [ ] Migrar para **Flyway** ou **Liquibase** para versionamento de schema em produção.
- [ ] Expandir testes de integração para cobrir os controllers (`@SpringBootTest` com `TestRestTemplate` ou `WebTestClient`).
- [ ] Adicionar **métricas e tracing** (Micrometer + Prometheus/Jaeger) para observabilidade em produção.
- [ ] Cachear listas de médicos e pacientes com **Redis** ou **Caffeine** para reduzir carga no banco.

---

> **Dica**: Acesse `http://localhost:8080/swagger-ui.html` após iniciar a aplicação para explorar e testar os endpoints interativamente.
