# PESCD - Programa de Estágios e Supervisão de Cursos de Disciplinas

Sistema web para gerenciamento de ofertas PESCD da UFSCar. Na etapa AA-3, o sistema foi convertido em uma **REST API** com autenticação **JWT** e documentação **OpenAPI/Swagger**.

## Tecnologias

- **Backend:** Java 25, Spring Boot 4, Spring MVC (REST Controllers), Spring Data JPA, Spring Security
- **Autenticação:** JWT (JSON Web Token)
- **Documentação:** OpenAPI 3 / Swagger UI (Springdoc)
- **Banco de dados:** MySQL (produção) / H2 (desenvolvimento)
- **Frontend legado (AA-2):** Thymeleaf, Bootstrap 5
- **Build:** Maven

## Como executar

    mvn spring-boot:run

- API base: `http://localhost:8080/api`
- Documentação Swagger: `http://localhost:8080/swagger-ui.html`

### Autenticação

Faça login em `POST /api/auth/login` com o corpo:

    {
      "nomeUsuario": "aluno",
      "senha": "123"
    }

A resposta traz um token JWT. Use-o nas próximas requisições no header:

    Authorization: Bearer <token>

### Usuários de teste (senha: `123`)

| Usuário     | Perfil      |
|-------------|-------------|
| admin       | ADMIN       |
| secretario  | SECRETARIO  |
| professor   | PROFESSOR   |
| aluno       | ALUNO       |

---

## Integrantes e Contribuições

> **Observação:** Após a saída do integrante João Lucas Pelegrino (JLGPelegrino) durante a etapa AA-3, suas user stories (PS.01, PS.02, PS.03, PR.01, PR.02, PR.03) foram redivididas entre Leo Morikio e Matheus Strito.

### Leo Morikio ([leo-morikio](https://github.com/leo-morikio))

**AA-2 (MVC com Thymeleaf):**

- **V.01** — Visitante visualiza lista de ofertas
- **U.01** — Login e autenticação (Spring Security base)
- **AD.01** — Administrador gerencia usuários (CRUD)
- **S.01** — Secretário cria oferta
- **S.02** — Secretário adiciona alunos (manual e CSV)
- **S.03** — Secretário acompanha ofertas
- **S.04** — Secretário encerra oferta

**AA-3 (REST API) — user stories ponta-a-ponta (Controller REST → Service → Repository/JPA):**

- **AD.01** — Administrador gerencia usuários via REST (`AdminUsuarioRestController`, `UsuarioService`)
- **V.01** — Visitante visualiza ofertas via REST, endpoint público (`VisitanteRestController`, `OfertaService`)
- **S.01, S.02, S.03, S.04** — Secretário cria/acompanha/encerra ofertas e adiciona alunos via REST (`SecretarioOfertaRestController`, `SecretarioAlunoRestController`, `OfertaService`, `InscricaoOfertaService`)
- **PS.01, PR.02, PR.03** — Histórias do professor herdadas do João (professor supervisor acompanha inscrições; professor responsável analisa documentação e encerra oferta), refatoradas para remover lambdas/method references, corrigir vazamento de senha e adicionar os campos obrigatórios de parecer/frequência/nota e as estatísticas de encerramento

**Outras contribuições técnicas (AA-3):**

- Infraestrutura REST comum: DTOs de request/response e tratamento global de erros (`GlobalExceptionHandler`, `RecursoNaoEncontradoException`, `NegocioException`)
- Migração do banco de dados para **MySQL** (`mysql-connector-j`)
- Adição da biblioteca **Apache Commons CSV** e validação real de tipo de arquivo (content-type + magic bytes, não só extensão)
- `SecurityConfig`: libera/protege rotas `/api/**` por perfil, desabilita CSRF para a API
- Correção de vazamento de senha em múltiplos endpoints GET (entidades JPA expostas sem DTO)
- Exclusão em cascata de usuário (aluno remove inscrições, secretário desvincula ofertas)
- Externalização do segredo JWT, remoção de method references do `JwtService`
- Remoção dos Controllers MVC e templates Thymeleaf órfãos (sem uso após a migração para REST + JWT stateless), limpeza do `pom.xml` e do `SecurityConfig`
- Refatoração para **injeção de dependência via construtor** (remoção de `@Autowired`) em todo o projeto

---

### Matheus Strito ([matheusstrito](https://github.com/matheusstrito))

**AA-2 (MVC com Thymeleaf):**

- **AL.01** — Aluno visualiza suas ofertas
- **AL.02** — Aluno envia plano de trabalho
- **AL.03** — Aluno envia documentação de ensino
- **AL.04** — Aluno envia relatório final (com validação da pré-condição PC-4: plano aprovado)

**AA-3 (REST API + JWT + OpenAPI):**

- Conversão das histórias do aluno (AL.01 a AL.04) para REST controllers
- **Autenticação JWT completa:**
  - `JwtService` — geração e validação de tokens
  - `JwtFilter` — interceptação e autenticação por token
  - `AuthController` — endpoint `POST /api/auth/login` que retorna token
  - Integração no `SecurityConfig` (política STATELESS, cadeia de filtros)
- **Documentação OpenAPI/Swagger** — dependência `springdoc-openapi-starter-webmvc-ui`, rotas do swagger liberadas no SecurityConfig
- **Histórias do professor (originalmente do João):**
  - **PS.02** — Professor supervisor aprova plano de trabalho
  - **PS.03** — Professor supervisor aprova relatório final
  - **PR.01** — Professor responsável conclui relatório
- Refatoração para **injeção de dependência via construtor** nos controllers e services do aluno e do professor

**Evidências:** commits desenvolvidos na branch [`matheus-aa3`](https://github.com/leo-morikio/PESCD-WEB-1-2026.1/commits/matheus-aa3?author=matheusstrito) do repositório inicial e na branch [`matheus-integracao`](../../commits/matheus-integracao?author=matheusstrito) deste repositório.

---

### João Lucas Pelegrino ([JLGPelegrino](https://github.com/JLGPelegrino)) *(saiu do grupo durante AA-3)*

**AA-2 (MVC com Thymeleaf) — versão inicial das histórias do professor:**

- **PS.01** — Professor supervisor acompanha inscrições
- **PS.02** — Professor supervisor aprova plano de trabalho
- **PS.03** — Professor supervisor aprova relatório final
- **PR.01** — Professor responsável conclui relatório
- **PR.02** — Professor responsável analisa documentação
- **PR.03** — Professor responsável encerra oferta
- `ProfessorController.java`, template `professor/index.html`

> As user stories acima foram assumidas por Leo e Matheus na etapa AA-3 (ver acima).

---

## Estrutura do projeto (AA-3)

    src/main/java/.../pescd/
    ├── config/          # SecurityConfig (JWT), JwtService, JwtFilter, DataLoader, UsuarioLogadoUtil
    ├── controller/      # REST controllers por perfil
    │   ├── AuthController.java          # Login JWT
    │   ├── rest/                        # Controllers REST do Leo
    │   ├── OfertaAlunoController.java, PlanoTrabalhoController.java, etc.
    │   └── ProfessorSupervisorController.java, ProfessorResponsavelController.java
    ├── model/           # Entidades JPA + enums
    ├── repository/      # Spring Data JPA repositories
    └── service/         # Lógica de negócio

---

## Correções e melhorias aplicadas do feedback da AA-2

- Remoção do `@Autowired` (injeção via construtor)
- Uso da biblioteca **Apache Commons CSV** no parsing (S.02)
- Migração para **MySQL** (produção)
- Refatoração para reduzir métodos longos e classes com muitas dependências