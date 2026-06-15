# Guia de Arguição — perguntas da apresentação respondidas

O enunciado avisa que **todos os integrantes serão questionados individualmente** e que
a nota considera a **capacidade de explicar a arquitetura**. Abaixo, as perguntas
sugeridas no enunciado já respondidas e ligadas ao código. Estude para explicar com
suas palavras, não decorar.

---

### 1. Por que foi utilizado o padrão Singleton?
Para ter **uma única conexão** com o banco SQLite compartilhada por todo o sistema. O
SQLite é um arquivo único; várias conexões abertas ao mesmo tempo desperdiçam recursos e
podem travar o arquivo (*file lock*). O Singleton (`DatabaseConnection`) concentra a
criação da conexão e os `PRAGMA` num só lugar. → `database/DatabaseConnection.java`

### 2. Qual a vantagem de utilizar interfaces neste projeto?
Desacoplam a **regra de negócio** da **tecnologia de banco**. A classe `Biblioteca` só
conhece as interfaces `LivroRepository`, `UsuarioRepository`, `EmprestimoRepository`;
não sabe que por trás é SQLite. Trocar o banco = criar outra implementação da interface,
sem mexer no serviço. Também facilita testes (dá para usar uma implementação falsa).

### 3. Como o Factory foi implementado?
`UsuarioFactory.criar(tipo, ...)` recebe o tipo ("Aluno"/"Professor") e retorna a
subclasse certa com um `switch`. É usado em dois pontos: no **cadastro** (menu) e na
**leitura do banco** (`UsuarioRepositorySQLite` reconstrói o objeto a partir da coluna
`tipo`). → `factory/UsuarioFactory.java`

### 4. Como ocorre a persistência dos dados?
Via **JDBC + SQLite**. Cada repositório usa `PreparedStatement` (consultas
parametrizadas) sobre a conexão do Singleton para `INSERT`/`SELECT`/`UPDATE`. Os dados
ficam no arquivo `biblioteca.db`, que persiste entre execuções. As tabelas são criadas
automaticamente na primeira execução (`DatabaseConnection.criarTabelas()`).

### 5. O que aconteceria se a classe Singleton fosse removida?
Cada repositório teria que abrir e gerenciar a própria conexão, repetindo URL e `PRAGMA`
em vários lugares. Isso gera código duplicado, mais chance de inconsistência e risco de
travar o arquivo do SQLite por múltiplas conexões simultâneas.

### 6. Como é realizada a conexão com o banco?
`DriverManager.getConnection("jdbc:sqlite:biblioteca.db")` dentro do construtor privado
do Singleton. Logo após, liga `PRAGMA foreign_keys = ON` e cria as tabelas. Os
repositórios pegam essa conexão por `DatabaseConnection.getInstance().getConexao()`.
O driver é o `sqlite-jdbc` (xerial), em `lib/`.

### 7. Como estão representados os relacionamentos entre as tabelas?
A tabela **`emprestimos`** tem `id_livro` e `id_usuario` como **chaves estrangeiras**
para `livros(id)` e `usuarios(id)`. Isso modela o relacionamento N:N entre usuários e
livros ao longo do tempo: um usuário tem vários empréstimos, um livro aparece em vários
empréstimos. As FKs garantem integridade (não dá para emprestar a um usuário/livro
inexistente).

### 8. Onde está implementada a regra de limite de empréstimos?
Em `Biblioteca.realizarEmprestimo()`. Ela busca o usuário, preenche o contador com
`emprestimoRepository.contarAtivosPorUsuario(id)` (um `COUNT` no banco) e chama
`usuario.podeEmprestar()`, que compara com `getLimiteEmprestimos()` — **3** para Aluno,
**5** para Professor (polimorfismo). O limite vem do banco, então vale mesmo após
reiniciar o programa.

### 9. Como ocorre a devolução de um livro?
`Biblioteca.realizarDevolucao(idLivro)` chama `devolverPorLivro()`, que faz um `UPDATE`
marcando o empréstimo ATIVO daquele livro como `DEVOLVIDO` e gravando a `data_devolucao`
(o registro **não é apagado** — vira histórico). Em seguida, o livro volta a
"Disponível" via `atualizarDisponibilidade(idLivro, true)`.

### 10. Como o sistema garante que um livro não seja emprestado duas vezes?
Duas verificações em `realizarEmprestimo()`: o flag `livro.isDisponivel()` **e**
`emprestimoRepository.livroTemEmprestimoAtivo(idLivro)` (checa se há linha com
status 'ATIVO' para aquele livro). Se qualquer uma indicar que está emprestado, a
operação é negada antes de gravar.

---

## Quem explica o quê (sugestão de divisão)

Como cada um será arguido individualmente, vale cada integrante dominar **um pilar** a
fundo, mas todos entenderem o conjunto:

| Integrante | Foco principal |
|---|---|
| Leonardo | Singleton (`DatabaseConnection`) + conexão JDBC |
| Caio Eduardo | Interfaces / Repository + injeção de dependência no serviço |
| Caio Monte | Factory (`UsuarioFactory`) + herança/polimorfismo de `Usuario` |
| Caio Gabriel | Modelagem do banco + regras de negócio (limite, disponibilidade, devolução) |

> Importante: o professor pode perguntar **qualquer** tópico a **qualquer** integrante.
> A divisão é só para garantir um "dono" por assunto — todos devem conseguir explicar o
> fluxo geral (Main monta → serviço usa interfaces → repositórios SQLite → Singleton).
