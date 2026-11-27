# Backend — Gerenciamento de Obrigações

API REST em **Spring Boot** para um escritório de contabilidade gerenciar **empresas**, **usuários responsáveis**, **obrigações** e **documentos anexos**, com MySQL 8 e armazenamento de arquivos em disco.

## Finalidade deste README

Documentar **somente o backend**: stack, como configurar e executar a API (Docker ou local), estrutura de pacotes, contratos HTTP, regras de negócio, decisões de arquitetura (incluindo UTF-8/utf8mb4 e seed demo) e melhorias futuras.

**Links relativos** (`../README.md`, frontend) funcionam quando este repo está clonado **dentro** do monorepo `player-contabilidade` (layout do avaliador). Se você clonou só o backend, use as URLs GitHub: [raiz](https://github.com/marcosvrca/player-contabilidade) · [frontend](https://github.com/marcosvrca/gerenciamento-obrigacoes-frontend).

## Quadro Trello

https://trello.com/b/WFkCNXHG/gerenciamento-de-obrigacoes

## Tecnologias utilizadas

| Área | Tecnologia |
|------|------------|
| Linguagem / runtime | Java 21 |
| Framework | Spring Boot 3.4 |
| Persistência | Spring Data JPA, Hibernate, Bean Validation |
| Banco | MySQL 8 (`utf8mb4` / `utf8mb4_unicode_ci`) |
| Migrações | Flyway (`ddl-auto=validate`) |
| API docs | SpringDoc OpenAPI (Swagger UI) |
| Testes | JUnit 5, Testcontainers (MySQL) |
| Empacotamento | Maven Wrapper, Docker, Docker Compose |
| Upload | Multipart (limite configurável), filesystem |

## Pré-requisitos

- **Docker Desktop** instalado e **em execução** (recomendado: API + MySQL via Compose)  
- JDK 21 e Maven 3.9+ (ou `./mvnw` / `mvnw.cmd`) — só se for rodar a API **localmente** fora do container

## Configuração do ambiente

Variáveis principais (`src/main/resources/application.yml` e/ou Compose):

| Variável | Padrão | Descrição |
|----------|--------|-----------|
| `SPRING_DATASOURCE_URL` | JDBC local com `createDatabaseIfNotExist`, `useUnicode=true`, `characterEncoding=UTF-8`, `connectionCollation=utf8mb4_unicode_ci`, `characterSetResults=UTF-8` | Conexão MySQL |
| `SPRING_DATASOURCE_USERNAME` | `obrigacoes` | Usuário DB |
| `SPRING_DATASOURCE_PASSWORD` | `obrigacoes` | Senha DB |
| `STORAGE_PATH` | `./uploads` | Pasta de anexos |
| `SERVER_PORT` | `8080` | Porta HTTP |
| `JAVA_TOOL_OPTIONS` | (Compose) `-Dfile.encoding=UTF-8 -Dsun.jnu.encoding=UTF-8` | Encoding da JVM no container |

Encoding HTTP (servlet):

- `server.servlet.encoding.charset=UTF-8`  
- `force` / `force-request` / `force-response` habilitados  
- `EncodingConfig` reforça `CharacterEncodingFilter` e JSON em UTF-8  

MySQL no Compose sobe com `--character-set-server=utf8mb4` e `--collation-server=utf8mb4_unicode_ci`. A migration **V5** garante database/tabelas em utf8mb4.

## Instruções de execução

### Docker Compose (API + MySQL)

Na raiz deste repositório:

```bash
docker compose up --build
```

Se o daemon não estiver ativo, abra o Docker Desktop e tente de novo.  
Se o nome do container já existir (`Conflict ... obrigacoes-mysql` / `obrigacoes-api`):

```bash
docker rm -f obrigacoes-mysql obrigacoes-api
docker compose up --build
```

Serviços:

| Serviço | URL / porta |
|---------|-------------|
| API | http://localhost:8080 |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| OpenAPI JSON | http://localhost:8080/api-docs |
| MySQL | `localhost:3306` |

Flyway aplica as migrations na subida da API.

### API local (MySQL no Docker)

```bash
docker compose up mysql
# JAVA_HOME = JDK 21
./mvnw spring-boot:run
```

No Windows: `mvnw.cmd spring-boot:run`.

### Seed de dados demo

Scripts em `scripts/` (após MySQL/API saudáveis; container nomeado `obrigacoes-mysql`):

- `seed-demo-data.sql` — ~10 usuários, ~30 empresas, ~60 obrigações (idempotente para CNPJs/e-mails de demo)  
- `seed-demo-data.ps1` — Windows (PowerShell)  
- `seed-demo-data.sh` — Linux / macOS

**Importante:** os scripts usam `docker cp` + `mysql --default-character-set=utf8mb4` e `SOURCE`. Pipar o SQL via shell/PowerShell pode corromper acentos (`Fábio` → `F?bio`).

```powershell
# Windows
.\scripts\seed-demo-data.ps1
```

```bash
# Linux / macOS
chmod +x ./scripts/seed-demo-data.sh
./scripts/seed-demo-data.sh
```

## Estrutura do projeto

```
gerenciamento-obrigacoes-backend/
  docker-compose.yml
  Dockerfile
  pom.xml
  scripts/
    seed-demo-data.sql
    seed-demo-data.ps1
    seed-demo-data.sh
  src/main/java/com/escritorio/obrigacoes/
    domain/          # entidades e enums (status, situação)
    repository/      # Spring Data JPA
    service/         # regras de negócio
    web/             # controllers + DTOs
    exception/       # handler global + erros de domínio
    config/          # CORS, storage, OpenAPI, EncodingConfig
  src/main/resources/
    application.yml
    db/migration/    # Flyway V1…V5 (inclui utf8mb4)
  src/test/          # unitários + integração Testcontainers
  .github/workflows/ci.yml
```

## Principais endpoints

| Recurso | Métodos |
|---------|---------|
| Empresas | `GET/POST /api/empresas`, `GET/PUT /api/empresas/{id}` |
| Usuários | `GET/POST /api/usuarios`, `GET/PUT /api/usuarios/{id}` |
| Obrigações | `GET/POST /api/obrigacoes`, `GET/PUT /api/obrigacoes/{id}` |
| Status | `PATCH /api/obrigacoes/{id}/status` |
| Anexos | `POST/GET /api/obrigacoes/{id}/anexos`, `GET /api/anexos/{id}/download` |

Contratos detalhados: Swagger UI após subir a API.

## Regras de negócio

- **Situação Ativo/Inativo:** inativos permanecem consultáveis; não entram em novas atribuições de obrigação.  
- **Status da obrigação:** apenas `PENDENTE → EM_ANDAMENTO → CONCLUIDA`.  
- **Concluída:** não altera status nem aceita edição de dados.  
- **Anexos:** associados a uma obrigação **já persistida** (upload após criação).

## Testes

```bash
./mvnw -B verify
```

Inclui testes de domínio/serviço e integração com Testcontainers (MySQL). O CI usa Testcontainers compatível com Docker Engine recente.

## CI/CD

Workflow GitHub Actions (`.github/workflows/ci.yml`) executa `mvn -B verify` em push/PR.

## Decisões arquiteturais

### Camadas e API

- Separação **Controller → Service → Repository**.  
- DTOs com Bean Validation; erros de domínio via `@RestControllerAdvice`.  
- OpenAPI para descoberta e validação manual dos contratos.  
- CORS liberado para consumo pelo app Flutter em desenvolvimento.

### Persistência e schema

- Flyway como fonte de verdade do schema; Hibernate em `validate`.  
- Anexos no **filesystem** (`STORAGE_PATH`), metadados no banco — simples de operar com volume Docker.  
- Multipart com limites explícitos (20MB arquivo / 25MB request).

### UTF-8 / utf8mb4 (decisão recente)

- JDBC com `characterEncoding=UTF-8` e `connectionCollation=utf8mb4_unicode_ci`.  
- Servlet encoding forçado em request/response.  
- MySQL server + migration **V5** em utf8mb4.  
- Objetivo: gravar e ler corretamente cedilha, til e demais acentos.

### Seed demo (decisão recente)

- Dados realistas para demos e UX (listas, paginação, filtros).  
- Carga via `docker cp` (`seed-demo-data.ps1` / `seed-demo-data.sh`) para preservar bytes UTF-8 do arquivo SQL.

## Melhorias futuras

### Identidade, acesso e dados

- **Auth + RBAC** — login, cadastro de usuários da aplicação e papéis (ex.: admin vs. responsável), protegendo endpoints.  
- **Escopo por usuário** — listagens e alertas filtrados pelo vínculo do responsável (admin vê tudo).  
- **Paginação e filtros na API** — page/size, status, empresa, prazo; hoje a UI pagina em memória.  
- **Auditoria** — histórico de mudança de status e, se útil, soft delete.

### Alertas e canais

- **Central de alertas** — eventos por obrigação (criação, status, prazo) persistidos e consultáveis por usuário.  
- **E-mail** — worker/fila para disparos, respeitando preferências do usuário (quais tipos receber: novas obrigações criadas, a vencer, do dia, da semana).  
- **Integrações** — webhooks ou conectores configuráveis (sistemas externos; WhatsApp Business API quando houver conta/provedor).

### Domínio contábil

- **Regras de prazo** — classificação automática a vencer / vencida.  
- **Obrigações recorrentes** — geração por competência a partir de modelos.  
- **Exportação** — endpoints ou jobs para CSV/PDF gerencial.  
- **Anexos em object storage** (S3) e retenção configurável.

### Plataforma

- Secrets e perfis staging/prod; métricas, health rico e tracing.

## Relação com o frontend

O app Flutter consome esta API em `http://localhost:8080` por padrão. Ver [README do frontend](../gerenciamento-obrigacoes-frontend/README.md) e o [README raiz](../README.md).
