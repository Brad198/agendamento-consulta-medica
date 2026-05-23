# Agendamento de Consultas Médicas

Este repositório reúne dois projetos integrados para suportar o agendamento de consultas médicas:

- `appointment-api`: backend em Spring Boot que expõe uma API REST para gerenciar pacientes, médicos e consultas.
- `appointment-frontend`: frontend em Next.js que oferece uma interface web para visualizar, criar e cancelar agendamentos.

## Visão geral dos projetos

### appointment-api

O projeto `appointment-api` é um serviço REST desenvolvido com Spring Boot, Spring Data JPA e PostgreSQL.
Ele foi pensado para:

- Realizar consultas medicas (Os clientes e médicos vão ser inseridos no db automaticamente para praticidade)
- Validar requisições com Spring Validation
- Documentar a API com SpringDoc OpenAPI
- Inicializar um banco de dados PostgreSQL via Docker Compose

### appointment-frontend

O projeto `appointment-frontend` é uma aplicação web moderna construída com Next.js e React.
Ele fornece:

- Telas de listagem e cadastro de consultas
- Seleção de pacientes e médicos
- Integração com a API via requisições HTTP
- Formulário reativo com `react-hook-form` e validação de esquema
- Interface visual com Material UI e notificação de ações

## Ferramentas necessárias

Para executar os dois projetos, você precisa de:

- Java 21
- Git
- Node.js 20+ (recomendado)
- `pnpm` (recomendado) ou `npm`
- Docker e Docker Compose (recomendado para a camada de banco de dados)

## Como executar

### 1. Configurar o backend (`appointment-api`)

1. Navegue até o diretório:
```bash
cd appointment-api
```

2. Configure as variáveis de ambiente para o banco PostgreSQL. Um exemplo de `.env`:
```bash
DATABASE_NAME=clinicare
DATABASE_USER=postgres
DATABASE_PASSWORD=postgres
DATABASE_HOST=localhost
DATABASE_PORT=5432
DATABASE_DIALECT=postgresql
```

3. Inicie o banco de dados com Docker Compose:
```bash
docker compose up -d
```

4. Execute a API com o wrapper Maven:
```bash
./mvnw spring-boot:run
```

> A API estará disponível em `http://localhost:8080`.

### 2. Configurar o frontend (`appointment-frontend`)

1. Navegue até o diretório:
```bash
cd ../appointment-frontend
```

2. Instale as dependências:
```bash
pnpm install
```

3. Inicie o frontend:
```bash
pnpm dev
```

> A interface web será aberta em `http://localhost:3000`.

## Observações importantes

- O backend usa `./mvnw`, então não é necessário ter Maven instalado globalmente.
- O frontend usa Next.js 16, React 19 e bibliotecas de UI do Material UI.
- A configuração do banco é centralizada em `appointment-api/src/main/resources/application.yml`.

## Endpoints úteis

- Documentação OpenAPI: `http://localhost:8080/api-docs`
- Interface Swagger UI (se habilitada): `http://localhost:8080/swagger-ui.html`

## Estrutura de pastas

- `appointment-api/` - Serviço backend com Spring Boot
- `appointment-frontend/` - Aplicação web Next.js

## Bom uso

Este conjunto permite desenvolver e testar um fluxo completo de agendamento: Selecionar paciente, agendar uma consulta e cancelar agendamentos pela interface web.
