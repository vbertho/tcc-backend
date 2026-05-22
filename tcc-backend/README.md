# TCC Backend

Backend Spring Boot preparado para rodar localmente, em Docker e no Render.

## Variaveis de ambiente

Crie um arquivo `.env` na raiz do backend usando `.env.example` como base.

Obrigatorias:

- `DB_URL`: URL JDBC do PostgreSQL. Para Supabase em producao, use `jdbc:postgresql://HOST:PORT/postgres?sslmode=require`.
- `DB_USER`: usuario do banco.
- `DB_PASSWORD`: senha do banco.
- `JWT_SECRET`: chave forte para assinar JWT. Use base64 com pelo menos 32 bytes.

Opcionais:

- `PORT`: porta usada pela aplicacao. No Render ela e definida automaticamente.
- `DB_SSL_MODE`: use `require` no Supabase/Render e `disable` em PostgreSQL local sem SSL.
- `CORS_ALLOWED_ORIGIN_PATTERNS`: origens permitidas, separadas por virgula. Em producao no Render, use `https://front-end-tcc-ten.vercel.app,https://*.vercel.app` para permitir a URL principal e previews da Vercel.
- `JPA_DDL_AUTO`: padrao `update`.
- `JPA_SHOW_SQL`: padrao `false`.
- `JWT_EXPIRATION_MS`: padrao `2592000000`.

## Rodar local

```bash
./mvnw spring-boot:run
```

No Windows PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

## Build Maven

```bash
./mvnw clean package -DskipTests
```

O jar gerado fica em `target/tcc-backend-0.0.1-SNAPSHOT.jar`.

## Docker

```bash
docker build -t tcc-backend .
docker run --env-file .env -p 8080:8080 tcc-backend
```

O `Dockerfile` usa Java 21, compila o projeto com Maven e executa o jar gerado.

## Deploy no Render

1. Crie um novo Web Service no Render.
2. Selecione deploy via Docker.
3. Aponte para a raiz deste backend, onde estao `Dockerfile` e `render.yaml`.
4. Configure as variaveis `DB_URL`, `DB_USER`, `DB_PASSWORD` e `JWT_SECRET`. O `render.yaml` ja define `CORS_ALLOWED_ORIGIN_PATTERNS` como `https://front-end-tcc-ten.vercel.app,https://*.vercel.app`.
5. Para Supabase, mantenha SSL com `DB_SSL_MODE=require` ou inclua `?sslmode=require` no `DB_URL`.

O Render define `PORT` automaticamente, e o Spring Boot le essa porta com `server.port=${PORT:8080}`.
