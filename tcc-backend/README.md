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
- `CORS_ALLOWED_ORIGIN_PATTERNS`: origens permitidas, separadas por virgula. Em producao no Render, mantenha somente as aplicacoes web publicas permitidas; o desktop em desenvolvimento usa uma proxy local.
- `JPA_DDL_AUTO`: padrao `update`.
- `JPA_SHOW_SQL`: padrao `false`.
- `JWT_EXPIRATION_MS`: padrao `2592000000`.
- `ADMIN_BOOTSTRAP_NAME`, `ADMIN_BOOTSTRAP_EMAIL`, `ADMIN_BOOTSTRAP_PASSWORD`: criam a primeira conta administrativa somente se o email ainda nao existir. A senha deve ter ao menos 8 caracteres.

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

1. No Render, crie um Blueprint a partir deste repositorio e selecione o arquivo `tcc-backend/render.yaml`. Ele ja define a subpasta `tcc-backend` como raiz do servico Docker.
2. Configure as variaveis secretas `DB_URL`, `DB_USER`, `DB_PASSWORD` e `JWT_SECRET` solicitadas pelo Blueprint.
3. Para Supabase, mantenha SSL com `DB_SSL_MODE=require` ou inclua `?sslmode=require` no `DB_URL`.
4. Se preferir criar um Web Service manual, selecione Docker, informe `tcc-backend` como **Root Directory** e copie tambem as variaveis nao secretas do `render.yaml`, especialmente `CORS_ALLOWED_ORIGIN_PATTERNS`.

O Render define `PORT` automaticamente, e o Spring Boot le essa porta com `server.port=${PORT:8080}`.

Depois do primeiro deploy, copie a URL publica gerada pelo Render (por exemplo, `https://seu-servico.onrender.com`) para a configuracao `DESKTOP_API_PROXY_TARGET` do desktop.

## Ping de disponibilidade

O endpoint publico `GET /api/health` responde com `200 OK` sem corpo e sem consultar o banco.
Para um agendador externo de monitoramento, configure o ping para:

```text
https://seu-servico.onrender.com/api/health
```

## Migracoes manuais do banco

Ao atualizar um banco existente que foi criado antes do perfil administrativo, execute
`docs/migrations/2026-05-26-allow-admin-user-type.sql` uma vez antes de configurar
`ADMIN_BOOTSTRAP_*`. O script atualiza a restricao da tabela `usuario` para permitir
os perfis `ALUNO`, `ORIENTADOR` e `ADMIN`.

## API administrativa

Todas as rotas abaixo exigem JWT de um usuario com tipo `ADMIN`:

- `GET /api/admin/dashboard`: indicadores e atividade recente.
- `GET|POST|PUT /api/admin/usuarios` e `PATCH /api/admin/usuarios/{id}/ativo`: gestao de alunos, orientadores e administradores.
- `GET|POST|PUT|DELETE /api/admin/projetos` e `PATCH /api/admin/projetos/{id}/status`: projetos e oportunidades.
- `GET|DELETE /api/admin/inscricoes` e `PATCH /api/admin/inscricoes/{id}/status`: moderacao de inscricoes.
- `GET|DELETE /api/admin/documentos` e `PATCH /api/admin/documentos/{id}/status`: revisao documental; preview e download continuam em `/api/documentos/{id}/...`.
- `GET|POST|PUT|DELETE /api/admin/areas`: areas de pesquisa.
- `GET /api/admin/relatorios/resumo`, `GET /api/admin/auditoria` e `GET|PUT /api/admin/configuracoes`: governanca.

Alteracoes administrativas de dados sao registradas em auditoria. Configuracoes aceitam apenas chaves operacionais predefinidas e nao armazenam segredos.
