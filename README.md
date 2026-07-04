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

### Leo Morikio ([leo-morikio](https://github.com/leo-morikio))

- **V.01 — Visitante visualiza lista de ofertas**
  - `VisitanteController.java`, `OfertaService.java`, template `visitante/lista-ofertas.html`

- **U.01 — Login e autenticação**
  - `SecurityConfig.java`, `LoginController.java`, `UsuarioLogadoUtil.java`, template `login.html`

- **AD.01 — Administrador gerencia usuários (CRUD)**
  - `AdminUsuarioController.java`, `UsuarioService.java`, templates `admin/usuarios/`

- **S.01 — Secretário cria oferta**
  - `SecretarioController.java` (criação), template `secretario/ofertas/form.html`

- **S.02 — Secretário adiciona alunos (manual e CSV)**
  - `SecretarioAlunoController.java`, `InscricaoOfertaService.java`, template `secretario/alunos/form.html`

- **S.03 — Secretário acompanha ofertas**
  - Templates `secretario/ofertas/lista.html`, `secretario/ofertas/detalhes.html`, `secretario/alunos/detalhes.html`

- **S.04 — Secretário encerra oferta**
  - `SecretarioController.java` (encerramento)

- Adicionou dependência H2 e `DataLoader`

---

### Matheus Strito ([matheusstrito](https://github.com/matheusstrito))

- **AL.01 — Aluno visualiza suas ofertas**
  - `OfertaAlunoController.java`, template `aluno/lista-ofertas.html`

- **AL.02 — Aluno envia plano de trabalho**
  - `PlanoTrabalhoController.java`, `PlanoTrabalhoService.java`, template `aluno/form-plano.html`

- **AL.03 — Aluno envia relatório final**
  - `RelatorioFinalController.java`, `RelatorioFinalService.java`, template `aluno/form-relatorio.html`

- **AL.04 — Aluno envia documentação de ensino**
  - `DocumentacaoEnsinoController.java`, `DocumentacaoEnsinoService.java`, template `aluno/form-documentacao.html`

- Adicionou dependência H2 e `DataLoader`

---

### JLGPelegrino ([JLGPelegrino](https://github.com/JLGPelegrino))

- **PS.01 — Professor supervisor acompanha inscrições**
  - `ProfessorSupervisorController.java`, template `professor/supervisor/ofertas.html`

- **PS.02 — Professor supervisor aprova plano de trabalho**
  - `ProfessorSupervisorController.java`, template `professor/supervisor/aprovar-plano.html`

- **PS.03 — Professor supervisor aprova relatório final**
  - `ProfessorSupervisorController.java`, template `professor/supervisor/aprovar-relatorio.html`

- **PR.01 — Professor responsável conclui relatório**
  - `ProfessorResponsavelController.java`, template `professor/responsavel/concluir-relatorio.html`

- **PR.02 — Professor responsável analisa documentação**
  - `ProfessorResponsavelController.java`, template `professor/responsavel/analisar-documentacao.html`

- **PR.03 — Professor responsável encerra oferta**
  - `ProfessorResponsavelController.java`, template `professor/responsavel/ofertas.html`

- `ProfessorController.java`, template `professor/index.html`

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
