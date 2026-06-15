# Diagrama de Classes — atualizado (N2)

Duas versões do mesmo diagrama: **Mermaid** (renderiza direto no GitHub/VS Code) e
**PlantUML** (cole em https://www.plantuml.com/plantuml para exportar um PNG, caso a
entrega exija imagem).

---

## Versão Mermaid

```mermaid
classDiagram
    class Usuario {
        <<abstract>>
        -int id
        -String nome
        -int livrosEmprestados
        +getLimiteEmprestimos() int*
        +getTipoUsuario() String*
        +getDetalhe() String*
        +getRotuloDetalhe() String*
        +podeEmprestar() boolean
    }
    class Aluno {
        -int LIMITE_EMPRESTIMOS = 3
        -String matricula
    }
    class Professor {
        -int LIMITE_EMPRESTIMOS = 5
        -String departamento
    }
    class Livro {
        -int id
        -String titulo
        -String autor
        -boolean disponivel
        +getStatusTexto() String
    }
    class Emprestimo {
        -Usuario usuario
        -Livro livro
        -String dataEmprestimo
        -String dataDevolucao
        -String status
        +isAtivo() boolean
    }

    Usuario <|-- Aluno
    Usuario <|-- Professor
    Emprestimo --> Usuario
    Emprestimo --> Livro

    class UsuarioFactory {
        <<Factory>>
        +criar(tipo, id, nome, detalhe)$ Usuario
        +criarPorOpcao(opcao, id, nome, detalhe)$ Usuario
    }
    UsuarioFactory ..> Aluno : cria
    UsuarioFactory ..> Professor : cria

    class DatabaseConnection {
        <<Singleton>>
        -DatabaseConnection instancia$
        -Connection conexao
        -DatabaseConnection()
        +getInstance()$ DatabaseConnection
        +getConexao() Connection
    }

    class LivroRepository {
        <<interface>>
        +salvar(Livro)
        +buscarPorId(int) Livro
        +listarTodos() List
        +atualizarDisponibilidade(int, boolean)
    }
    class UsuarioRepository {
        <<interface>>
        +salvar(Usuario)
        +buscarPorId(int) Usuario
        +listarTodos() List
    }
    class EmprestimoRepository {
        <<interface>>
        +registrar(Emprestimo)
        +devolverPorLivro(int) boolean
        +contarAtivosPorUsuario(int) int
        +livroTemEmprestimoAtivo(int) boolean
        +listarAtivos() List
        +listarTodos() List
    }

    class LivroRepositorySQLite
    class UsuarioRepositorySQLite
    class EmprestimoRepositorySQLite

    LivroRepository <|.. LivroRepositorySQLite
    UsuarioRepository <|.. UsuarioRepositorySQLite
    EmprestimoRepository <|.. EmprestimoRepositorySQLite

    LivroRepositorySQLite ..> DatabaseConnection : usa conexão
    UsuarioRepositorySQLite ..> DatabaseConnection : usa conexão
    EmprestimoRepositorySQLite ..> DatabaseConnection : usa conexão
    UsuarioRepositorySQLite ..> UsuarioFactory : reconstrói

    class Biblioteca {
        <<service>>
        +adicionarLivro(Livro) boolean
        +adicionarUsuario(Usuario) boolean
        +realizarEmprestimo(int, int)
        +realizarDevolucao(int)
        +listarLivros()
        +listarUsuarios()
        +listarEmprestimosAtivos()
        +listarHistoricoEmprestimos()
    }
    Biblioteca --> LivroRepository
    Biblioteca --> UsuarioRepository
    Biblioteca --> EmprestimoRepository

    class AcoesMenu {
        +imprimirMenuPrincipal()
        +cadastrarLivro(Scanner, Biblioteca)
        +cadastrarUsuario(Scanner, Biblioteca)
        +realizarEmprestimo(Scanner, Biblioteca)
        +realizarDevolucao(Scanner, Biblioteca)
    }
    class Main {
        +main(String[])$
    }
    AcoesMenu ..> Biblioteca
    AcoesMenu ..> UsuarioFactory
    Main ..> Biblioteca
    Main ..> AcoesMenu
    Main ..> DatabaseConnection
    Main ..> LivroRepositorySQLite
    Main ..> UsuarioRepositorySQLite
    Main ..> EmprestimoRepositorySQLite
```

---

## Versão PlantUML

```plantuml
@startuml
skinparam classAttributeIconSize 0

abstract class Usuario {
  -id : int
  -nome : String
  -livrosEmprestados : int
  +{abstract} getLimiteEmprestimos() : int
  +{abstract} getTipoUsuario() : String
  +{abstract} getDetalhe() : String
  +podeEmprestar() : boolean
}
class Aluno {
  -LIMITE_EMPRESTIMOS : int = 3
  -matricula : String
}
class Professor {
  -LIMITE_EMPRESTIMOS : int = 5
  -departamento : String
}
class Livro {
  -id : int
  -titulo : String
  -autor : String
  -disponivel : boolean
}
class Emprestimo {
  -dataEmprestimo : String
  -dataDevolucao : String
  -status : String
}

Usuario <|-- Aluno
Usuario <|-- Professor
Emprestimo --> Usuario
Emprestimo --> Livro

class UsuarioFactory <<Factory>> {
  +{static} criar(tipo, id, nome, detalhe) : Usuario
  +{static} criarPorOpcao(opcao, id, nome, detalhe) : Usuario
}
UsuarioFactory ..> Aluno
UsuarioFactory ..> Professor

class DatabaseConnection <<Singleton>> {
  -{static} instancia : DatabaseConnection
  -conexao : Connection
  -DatabaseConnection()
  +{static} getInstance() : DatabaseConnection
  +getConexao() : Connection
}

interface LivroRepository
interface UsuarioRepository
interface EmprestimoRepository
class LivroRepositorySQLite
class UsuarioRepositorySQLite
class EmprestimoRepositorySQLite

LivroRepository <|.. LivroRepositorySQLite
UsuarioRepository <|.. UsuarioRepositorySQLite
EmprestimoRepository <|.. EmprestimoRepositorySQLite
LivroRepositorySQLite ..> DatabaseConnection
UsuarioRepositorySQLite ..> DatabaseConnection
EmprestimoRepositorySQLite ..> DatabaseConnection
UsuarioRepositorySQLite ..> UsuarioFactory

class Biblioteca <<service>>
Biblioteca --> LivroRepository
Biblioteca --> UsuarioRepository
Biblioteca --> EmprestimoRepository

class AcoesMenu
class Main
AcoesMenu ..> Biblioteca
AcoesMenu ..> UsuarioFactory
Main ..> Biblioteca
Main ..> AcoesMenu
Main ..> DatabaseConnection
@enduml
```

---

## Modelo do Banco (relacionamentos)

```
usuarios (1) ───< emprestimos >─── (1) livros
            id_usuario        id_livro

- 1 usuário pode ter vários empréstimos.
- 1 livro pode aparecer em vários empréstimos ao longo do tempo.
- A tabela "emprestimos" resolve o N:N e guarda o histórico (status ATIVO/DEVOLVIDO).
```
