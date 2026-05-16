# TCC Backend - API de Gerenciamento de TCC

Este é o backend do sistema de gerenciamento de Trabalhos de Conclusão de Curso (TCC), uma plataforma robusta desenvolvida para facilitar a colaboração entre alunos e orientadores.

## 🚀 Tecnologias

- **Java 21**
- **Spring Boot 4.0.3**
- **Spring Security (JWT)**
- **Spring Data JPA**
- **PostgreSQL / H2**
- **Lombok**
- **Swagger / OpenAPI 3.0**

## 🏗️ Arquitetura

O projeto utiliza uma arquitetura em camadas bem definida:
- `Controller`: Exposição de endpoints REST e documentação.
- `Service`: Lógica de negócio e orquestração de processos.
- `Repository`: Interface de persistência com Spring Data.
- `Model`: Entidades JPA e mapeamento objeto-relacional.
- `DTO`: Objetos de transferência para requests e responses, garantindo desacoplamento da camada de dados.
- `Security`: Filtros de autenticação stateless via JWT.

## 🛡️ Segurança

- **Autenticação:** Stateless via JWT.
- **Autorização:** Baseada em perfis de usuário (ALUNO, ORIENTADOR).
- **Criptografia:** Senhas armazenadas com BCrypt.
- **Auditoria:** Controle de permissões granular nos serviços.

## 📝 Documentação da API

A documentação interativa via Swagger pode ser acessada em:
`http://localhost:8080/swagger-ui.html`

## 🛠️ Configuração e Execução

### Pré-requisitos
- JDK 21
- PostgreSQL (ou usar H2 em memória)

### Passos
1. Clone o repositório.
2. Copie o arquivo `.env.example` para `.env` e ajuste as variáveis.
3. Execute o comando:
   ```bash
   ./mvnw spring-boot:run
   ```

## 🧪 Testes

Para rodar a suíte de testes unitários e de integração:
```bash
./mvnw test
```

## 📈 Funcionalidades Principais
- Gestão de Projetos e Recrutamento.
- Sistema de Mensageria Privada e de Grupo.
- Acompanhamento de Progresso e Feedbacks.
- Gestão de Documentos e Notificações em Tempo Real (via polling/app).
