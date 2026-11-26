# Backend — Gerenciamento de Obrigações

API REST em Spring Boot para escritório de contabilidade gerenciar empresas, usuários, obrigações e documentos anexos.

## Quadro Trello

https://trello.com/b/WFkCNXHG/gerenciamento-de-obrigacoes

## Tecnologias

- Java 21
- Spring Boot 3.4
- Spring Data JPA + Validation
- MySQL 8
- Flyway
- SpringDoc OpenAPI
- Testcontainers / JUnit 5
- Docker / Docker Compose

## Pré-requisitos

- JDK 21
- Maven 3.9+
- Docker (recomendado para MySQL e execução completa)

## Configuração do ambiente

Variáveis principais (`application.yml`):

| Variável | Padrão | Descrição |
|----------|--------|-----------|
| `SPRING_DATASOURCE_URL` | `jdbc:mysql://localhost:3306/obrigacoes...` | JDBC |
| `SPRING_DATASOURCE_USERNAME` | `obrigacoes` | Usuário DB |
| `SPRING_DATASOURCE_PASSWORD` | `obrigacoes` | Senha DB |
| `STORAGE_PATH` | `./uploads` | Pasta de anexos |
| `SERVER_PORT` | `8080` | Porta HTTP |

## Execução com Docker Compose

Na raiz deste repositório:

```bash
docker compose up --build
```

Serviços:

- API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- MySQL: localhost:3306

As migrations Flyway rodam automaticamente na inicialização da API.

## Execução local (sem Docker da API)

1. Suba apenas o MySQL (`docker compose up mysql`)
2. Configure `JAVA_HOME` para JDK 21
3. Execute:

```bash
./mvnw spring-boot:run
```

## Estrutura do projeto

```
src/main/java/com/escritorio/obrigacoes
  domain/       # entidades e enums
  repository/   # JPA
  service/      # regras de negócio
  web/          # controllers + DTOs
  exception/    # handler global
  config/       # CORS, storage, OpenAPI
src/main/resources/db/migration  # Flyway
```

## Principais endpoints

- `GET/POST /api/empresas`, `GET/PUT /api/empresas/{id}`
- `GET/POST /api/usuarios`, `GET/PUT /api/usuarios/{id}`
- `GET/POST /api/obrigacoes`, `GET/PUT /api/obrigacoes/{id}`
- `PATCH /api/obrigacoes/{id}/status`
- `POST/GET /api/obrigacoes/{id}/anexos`
- `GET /api/anexos/{id}/download`

## Regras de negócio

- Situação Ativo/Inativo: registros inativos permanecem consultáveis, mas não podem ser usados em novas atribuições
- Status da obrigação: apenas `PENDENTE → EM_ANDAMENTO → CONCLUIDA`
- Obrigação concluída não pode ter status alterado nem ser editada

## Testes

```bash
mvn -B verify
```

Inclui testes unitários de domínio/serviço e integração com Testcontainers (MySQL).

## CI/CD

Workflow GitHub Actions em `.github/workflows/ci.yml` executa `mvn -B verify`.

## Decisões arquiteturais

- Camadas Controller → Service → Repository
- Validação via Bean Validation nos DTOs
- Exceções de domínio mapeadas por `@RestControllerAdvice`
- Persistência de anexos em filesystem (volume Docker)
- Flyway com `ddl-auto=validate` para evolução controlada do schema

## Melhorias futuras

- Autenticação/autorização (JWT)
- Storage em object storage (S3)
- Soft delete explícito e auditoria
- Paginação e filtros avançados
- Notificações de prazo
