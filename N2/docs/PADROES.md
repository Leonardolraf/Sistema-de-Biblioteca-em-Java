# Padrões de Projeto — onde e por quê

Documento exigido na entrega: explica **onde** cada padrão foi aplicado e **por quê**.
Os três padrões obrigatórios são Singleton, Interface (Repository) e Factory.

---

## 1. Singleton — `database/DatabaseConnection.java`

**O que é:** garante que exista **uma única instância** de uma classe durante toda a
execução, com um ponto de acesso global a ela.

**Onde está:** a classe `DatabaseConnection` guarda a conexão JDBC com o SQLite.

**Como foi implementado:**
1. O **construtor é privado** → ninguém consegue dar `new DatabaseConnection()` de fora.
2. Um atributo **estático** (`private static DatabaseConnection instancia`) guarda a
   única instância.
3. O método estático **`getInstance()`** cria a instância na primeira chamada e devolve
   sempre a mesma nas seguintes (inicialização preguiçosa). É `synchronized` para evitar
   que duas threads criem instâncias diferentes ao mesmo tempo.

**Qual problema resolve:** os três repositórios precisam de uma conexão com o banco. Sem
o Singleton, cada um abriria a sua, ou abriríamos/fecharíamos conexões a cada operação.
No SQLite (que é um arquivo único) isso causa desperdício e risco de *file lock*
(travamento do arquivo). Com o Singleton, todos compartilham **a mesma** conexão.

**O que aconteceria se a classe Singleton fosse removida:** cada repositório teria que
gerenciar sua própria conexão (abrir, fechar, repetir a URL e os `PRAGMA` em vários
lugares). O código ficaria duplicado e sujeito a inconsistências e travamentos de
arquivo. O Singleton concentra essa responsabilidade num só ponto.

---

## 2. Interface (padrão Repository) — `repository/*Repository.java`

**O que é:** um **contrato** (conjunto de métodos) que diz *o que* pode ser feito, sem
dizer *como*. Várias classes podem implementar a mesma interface de formas diferentes.

**Onde está:** três interfaces de persistência —
- `LivroRepository`
- `UsuarioRepository`
- `EmprestimoRepository`

Cada uma tem uma implementação concreta para SQLite:
`LivroRepositorySQLite`, `UsuarioRepositorySQLite`, `EmprestimoRepositorySQLite`.

**Como foi usado:** a camada de serviço (`Biblioteca`) recebe as interfaces **no
construtor** (injeção de dependência) e trabalha **somente** com elas — nunca menciona
SQLite. Quem decide a implementação concreta é o `Main`:

```java
LivroRepository livroRepository = new LivroRepositorySQLite();   // Main escolhe
Biblioteca biblioteca = new Biblioteca(livroRepository, ...);    // serviço só vê a interface
```

**Qual a vantagem de usar interfaces neste projeto:**
- **Baixo acoplamento:** a regra de negócio não depende da tecnologia de banco. Para
  trocar SQLite por MySQL, arquivo, ou um *mock* de teste, basta criar outra
  implementação da mesma interface — sem tocar em `Biblioteca`.
- **Testabilidade:** dá para testar o serviço com uma implementação falsa em memória.
- **Organização:** o contrato deixa explícito o que a persistência precisa oferecer.

---

## 3. Factory — `factory/UsuarioFactory.java`

**O que é:** um objeto/método responsável por **criar** instâncias, escondendo de quem
chama qual subclasse concreta está sendo instanciada.

**Onde está:** `UsuarioFactory.criar(tipo, id, nome, detalhe)` decide se cria um `Aluno`
ou um `Professor` a partir do `tipo` ("Aluno"/"Professor"). Há também
`criarPorOpcao(opcao, ...)` para o menu, que trabalha com 1/2.

**Como foi implementado:** um `switch` sobre o tipo retorna `new Aluno(...)` ou
`new Professor(...)`. A fábrica é usada em **dois lugares**:
1. No **cadastro** (`AcoesMenu`), quando o usuário escolhe 1 (Aluno) ou 2 (Professor);
2. No **repositório** (`UsuarioRepositorySQLite`), ao **reconstruir** o objeto correto a
   partir da coluna `tipo` lida do banco.

**Qual problema o Factory resolveu:** antes, a decisão "`if tipo == 1 new Aluno() else
new Professor()`" apareceria **espalhada** — no menu e na leitura do banco. Duplicar essa
lógica é frágil.

**Qual seria a alternativa caso ele não existisse:** repetir o `if/else` de criação em
cada ponto que precisa de um `Usuario`, acoplando o menu **e** o repositório às classes
concretas `Aluno` e `Professor`.

**Quais vantagens trouxe:**
- **Ponto único de mudança:** para adicionar um novo tipo de usuário (ex.: `Visitante`),
  altera-se **só** a fábrica.
- **Reuso:** o mesmo método cria usuários no cadastro e na reconstrução vinda do banco.
- **Menos acoplamento:** menu e repositório não precisam conhecer as subclasses.

---

## Resumo visual

```
Main  ──cria──►  RepositórioSQLite  ──implementa──►  Interface Repository
  │                                                        ▲
  └──injeta as interfaces──►  Biblioteca (serviço) ────────┘ (só vê a interface)

DatabaseConnection (Singleton) ──conexão única──►  todos os RepositórioSQLite
UsuarioFactory  ──cria Aluno/Professor──►  usado no cadastro e na leitura do banco
```
