# PESCD - Programa de Estágios e Supervisão de Cursos de Disciplinas

Sistema web para gerenciamento de ofertas PESCD da UFSCar, desenvolvido com Spring Boot, Spring MVC, Spring Data JPA, Spring Security e Thymeleaf.

## Tecnologias

- **Backend:** Java 25, Spring Boot 4, Spring MVC, Spring Data JPA, Spring Security
- **Frontend:** Thymeleaf, Bootstrap 5
- **Banco de dados:** H2 (desenvolvimento) / MySQL (produção)
- **Build:** Maven

## Como executar

```bash
mvn spring:boot:run
```

Acesse `http://localhost:8080`. Usuários de teste (senha: `123`):

| Usuário      | Perfil      |
|--------------|-------------|
| admin        | ADMIN       |
| secretario   | SECRETARIO  |
| professor    | PROFESSOR   |
| aluno        | ALUNO       |

---

## Integrantes e Contribuições

### Leo Morikio (@leo_morikio)

**User Stories implementadas:**

- **U.01 — Login e autenticação**
  - `SecurityConfig.java` — configuração do Spring Security com autorização por perfil (ADMIN, SECRETARIO, PROFESSOR, ALUNO)
  - `LoginController.java` — página de login e redirecionamento por perfil após autenticação
  - `UsuarioLogadoUtil.java` — utilitário para obter o usuário JPA do contexto de segurança
  - Atualização do `DataLoader.java` com senhas BCrypt
  - Template `login.html` com Bootstrap

- **V.01 — Visitante visualiza lista de ofertas**
  - `VisitanteController.java` — GET `/` lista todas as ofertas ordenadas por semestre
  - `OfertaService.java` — serviço com lógica de negócio para ofertas
  - Template `visitante/lista-ofertas.html`

- *(Adicionar links para commits após push)*

---

### Membro 2 — [Nome] (@[github])

**User Stories implementadas:**

- **AD.01 — Administrador gerencia usuários**
  - `UsuarioService.java`, `AdminUsuarioController.java`
  - Templates `admin/usuarios/lista.html`, `admin/usuarios/form.html`

- **S.01 — Secretário cria oferta**
  - `SecretarioController.java` (criação de oferta)
  - Template `secretario/ofertas/form.html`

- **S.02 — Secretário adiciona alunos (manual e CSV)**
  - `InscricaoOfertaService.java`, `SecretarioAlunoController.java`
  - Template `secretario/alunos/form.html`

- **S.03 — Secretário acompanha ofertas**
  - Template `secretario/ofertas/lista.html`, `secretario/ofertas/detalhes.html`

- **S.04 — Secretário encerra oferta**
  - `SecretarioController.java` (endpoints de encerramento)

- *(Adicionar links para commits após push)*

---

### Membro 3 — [Nome] (@[github])

**User Stories implementadas:**

- *(Preencher)*

- *(Adicionar links para commits após push)*

---

## Estrutura do projeto

```
src/main/java/.../pescd/
├── config/          # SecurityConfig, DataLoader, UsuarioLogadoUtil
├── controller/      # Controllers por perfil
├── model/           # Entidades JPA + enums
├── repository/      # Spring Data JPA repositories
└── service/         # Lógica de negócio

src/main/resources/
├── templates/       # Thymeleaf (por perfil: visitante, admin, secretario, professor, aluno)
└── application.properties
```
