# Sistema de Gerenciamento de Biblioteca — N2 (Evolução)

**Universidade Católica de Brasília - UCB**
**Disciplina:** Programação Orientada a Objetos
**Professor:** Alexandre S. D. Santos
**Atividade:** N2 — Atividade Final (Evolução do Sistema de Biblioteca)
**Data:** 14/06/2026

**Alunos:**
- Leonardo Rodrigues Amorim Filho
- Caio Eduardo Moura dos Santos
- Caio Monte Lopes
- Caio Gabriel Timotio Rodrigues de Lima

---

## Descrição do Projeto

Esta é a **segunda etapa** do Sistema de Biblioteca. A primeira versão guardava tudo
em memória (`ArrayList`) e registrava operações em um arquivo de texto. Nesta evolução
o sistema passou a:

- **Persistir os dados em um banco SQLite** (livros, usuários e empréstimos), de modo
  que as informações permanecem disponíveis mesmo após fechar e reabrir o programa;
- Adotar **três padrões de projeto** — **Singleton**, **Factory** e o uso de
  **Interfaces** (padrão *Repository*);
- Reorganizar o código em uma **arquitetura em camadas** (modelo, persistência, serviço
  e apresentação), com separação clara de responsabilidades.

Todas as **regras de negócio** da etapa anterior continuam funcionando: limite de
empréstimos por tipo de usuário, bloqueio de empréstimo de livro indisponível,
atualização automática da disponibilidade na devolução e a diferenciação entre
Aluno e Professor.

---

## Arquitetura em Camadas

O código está organizado no pacote raiz `biblioteca`, dividido em sub-pacotes por
responsabilidade:

```
src/biblioteca/
├── main/          → Main (ponto de entrada e "montagem" das dependências)
├── model/         → Entidades: Usuario (abstrata), Aluno, Professor, Livro, Emprestimo
├── database/      → DatabaseConnection (SINGLETON da conexão SQLite)
├── factory/       → UsuarioFactory (FACTORY de Aluno/Professor)
├── repository/    → INTERFACES (...Repository) + implementações SQLite (...RepositorySQLite)
├── service/       → Biblioteca (regras de negócio; depende só das interfaces)
└── ui/            → AcoesMenu (interface de terminal via Scanner)
```

**Fluxo das dependências:** `Main` monta tudo → injeta os repositórios no `service`
→ o `service` aplica as regras usando apenas as **interfaces** → as implementações
SQLite usam a conexão única do **Singleton** → a **Factory** reconstrói os usuários.

A camada de serviço **não conhece SQLite**: ela enxerga somente as interfaces
`LivroRepository`, `UsuarioRepository` e `EmprestimoRepository`. Trocar o banco por
outra tecnologia seria criar novas implementações dessas interfaces, sem alterar a
regra de negócio.

---

## Padrões de Projeto Aplicados

| Padrão | Onde está | Para quê |
|---|---|---|
| **Singleton** | `database/DatabaseConnection.java` | Uma única instância da conexão com o SQLite, compartilhada por todos os repositórios. |
| **Interface (Repository)** | `repository/*Repository.java` | Contrato de persistência; desacopla as regras de negócio da tecnologia de banco. |
| **Factory** | `factory/UsuarioFactory.java` | Centraliza a criação de `Aluno`/`Professor` num só lugar (no cadastro e ao ler do banco). |

> A explicação detalhada de cada padrão — com o problema que resolve, a alternativa
> sem ele e as vantagens — está em [`docs/PADROES.md`](docs/PADROES.md).

---

## Princípios de POO

| Princípio | Implementação |
|---|---|
| **Encapsulamento** | Todos os atributos são `private`, com acesso via getters/setters. |
| **Herança** | `Aluno` e `Professor` herdam de `Usuario` (abstrata). |
| **Polimorfismo** | `getLimiteEmprestimos()`, `getTipoUsuario()` e `getDetalhe()` são sobrescritos em cada subclasse; `podeEmprestar()` usa o resultado polimórfico. |
| **Abstração** | `Usuario` é `abstract`; o serviço programa voltado às **interfaces** dos repositórios, não às implementações. |

---

## Estrutura do Banco de Dados

Três tabelas (script completo em [`sql/schema.sql`](sql/schema.sql)). O programa também
**cria as tabelas automaticamente** na primeira execução.

- **`livros`** (`id`, `titulo`, `autor`, `disponivel`)
- **`usuarios`** (`id`, `nome`, `tipo`, `detalhe`) — `tipo` ∈ {Aluno, Professor};
  `detalhe` guarda a matrícula (Aluno) ou o departamento (Professor).
- **`emprestimos`** (`id`, `id_livro` → FK, `id_usuario` → FK, `data_emprestimo`,
  `data_devolucao`, `status`) — `status` ∈ {ATIVO, DEVOLVIDO}.

**Decisão de modelagem importante:** a devolução **não apaga** o empréstimo — ela muda
o `status` para `DEVOLVIDO` e grava a `data_devolucao`. Com isso:
- a tabela `emprestimos` guarda o **histórico completo**;
- ela é a **única fonte de verdade** dos empréstimos ativos (um livro está emprestado
  se houver linha `ATIVO`; o nº de empréstimos do usuário é o `COUNT` das linhas `ATIVO`).
  O contador de empréstimos do usuário é sempre **derivado** do banco, então nunca
  dessincroniza.

**Relacionamentos:** `usuarios 1—N emprestimos N—1 livros` (a tabela `emprestimos`
resolve o N:N entre usuários e livros ao longo do tempo). As FKs garantem integridade
(`PRAGMA foreign_keys = ON`).

---

## Funcionalidades (menu)

```
1 - Cadastrar livro
2 - Cadastrar usuário
3 - Realizar empréstimo
4 - Realizar devolução
5 - Listar livros
6 - Listar usuários cadastrados
7 - Listar empréstimos ativos
8 - Ver histórico de empréstimos   (ativos + devolvidos, vindos do banco)
0 - Sair
```

---

## Regras de Negócio (mantidas da etapa anterior)

1. **Limite por tipo de usuário:** Aluno até **3** empréstimos; Professor até **5**.
   Verificado em `Biblioteca.realizarEmprestimo()` via `usuario.podeEmprestar()`, com
   o contador vindo de `EmprestimoRepository.contarAtivosPorUsuario()`.
2. **Disponibilidade:** um livro só é emprestado se estiver disponível e sem empréstimo
   ATIVO. Empréstimo duplicado é bloqueado.
3. **Atualização automática:** o empréstimo marca o livro como indisponível; a devolução
   marca o empréstimo como `DEVOLVIDO` e devolve o livro a "Disponível".
4. **Validação de existência:** livro e usuário são checados antes de qualquer operação.
5. **Unicidade de IDs:** cadastro com ID já existente é cancelado (validação antecipada).

---

## Como Executar

### Pré-requisitos
- **JDK 8 ou superior** (testado em JDK 25).
- O driver **`lib/sqlite-jdbc-3.53.2.0.jar`** já acompanha o projeto (não precisa baixar).

### Windows (mais fácil)
```bat
compilar.bat
executar.bat
```

### Manual (qualquer SO)
Compilar (gera os `.class` em `out/`):
```bash
javac -encoding UTF-8 -cp "lib/sqlite-jdbc-3.53.2.0.jar" -d out src/biblioteca/**/*.java
```
Executar (Windows usa `;` no classpath; Linux/Mac usam `:`):
```bash
# Windows
java -cp "out;lib/sqlite-jdbc-3.53.2.0.jar" biblioteca.main.Main
# Linux / Mac
java -cp "out:lib/sqlite-jdbc-3.53.2.0.jar" biblioteca.main.Main
```

> **Observação (JDK 9+):** o driver SQLite usa código nativo e o Java pode exibir um
> aviso `WARNING: ... restricted method ... System::load`. É inofensivo. Para silenciá-lo,
> rode com `--enable-native-access=ALL-UNNAMED` (já incluso no `executar.bat`).

O banco `biblioteca.db` é criado automaticamente na primeira execução. O arquivo que
acompanha a entrega já vem com **dados de exemplo** (4 livros, 3 usuários, 1 empréstimo
ativo e 1 no histórico).

---

## Estrutura de Arquivos

```
Sistema-de-Biblioteca-N2/
├── lib/
│   └── sqlite-jdbc-3.53.2.0.jar      # driver JDBC do SQLite
├── src/biblioteca/
│   ├── main/Main.java
│   ├── model/{Usuario,Aluno,Professor,Livro,Emprestimo}.java
│   ├── database/DatabaseConnection.java        # SINGLETON
│   ├── factory/UsuarioFactory.java             # FACTORY
│   ├── repository/
│   │   ├── LivroRepository.java                # INTERFACE
│   │   ├── UsuarioRepository.java              # INTERFACE
│   │   ├── EmprestimoRepository.java           # INTERFACE
│   │   ├── LivroRepositorySQLite.java
│   │   ├── UsuarioRepositorySQLite.java
│   │   └── EmprestimoRepositorySQLite.java
│   ├── service/Biblioteca.java                 # regras de negócio
│   └── ui/AcoesMenu.java                        # menu CLI
├── sql/schema.sql                    # script de criação das tabelas
├── docs/
│   ├── PADROES.md                    # onde/por quê de Singleton, Interface, Factory
│   ├── diagrama-classes.md           # diagrama de classes atualizado (Mermaid/PlantUML)
│   └── ARGUICAO.md                   # perguntas da apresentação respondidas
├── biblioteca.db                     # banco SQLite com dados de exemplo
├── compilar.bat / executar.bat
└── README.md
```

---

## Tecnologias

- **Java SE** (testado no JDK 25)
- **SQLite** via **JDBC** (`org.xerial:sqlite-jdbc` 3.53.2.0)
- `java.sql` (`Connection`, `PreparedStatement`, `ResultSet`)
- `java.util.Scanner` — interface de terminal
