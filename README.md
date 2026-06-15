# Sistema de Gerenciamento de Biblioteca

**Universidade Católica de Brasília - UCB**
**Disciplina:** Programação Orientada a Objetos
**Professor:** Alexandre S. D. Santos

**Alunos:**
- Leonardo Rodrigues Amorim Filho
- Caio Eduardo Moura dos Santos
- Caio Monte Lopes
- Caio Gabriel Timotio Rodrigues de Lima

---

Este repositório reúne as **duas etapas** do projeto de Sistema de Biblioteca,
cada uma em sua própria pasta:

## 📁 [`N1/`](./N1) — Etapa 1 (versão inicial)

Primeira versão do sistema. Cadastro de livros e usuários (Aluno/Professor),
empréstimos e devoluções, com as regras de negócio básicas. Os dados ficam **em
memória** (`ArrayList`) e o histórico de operações é gravado em um **arquivo de
texto** (`historico.txt`).

➡️ Detalhes, funcionalidades e como executar: **[N1/README.md](./N1/README.md)**

## 📁 [`N2/`](./N2) — Etapa 2 (N2 — Atividade Final / evolução)

Evolução da etapa 1. Mantém todas as regras de negócio, mas agora com **persistência
em banco de dados SQLite** e a aplicação de **padrões de projeto**:

- **Singleton** — conexão única com o banco (`DatabaseConnection`);
- **Factory** — criação de `Aluno`/`Professor` (`UsuarioFactory`);
- **Interface / Repository** — contratos de persistência desacoplados da regra de negócio.

O código foi reorganizado em **camadas** (model, database, factory, repository,
service, ui) e inclui script SQL, banco de exemplo, diagrama de classes atualizado
e documentação dos padrões.

➡️ Detalhes, arquitetura e como executar: **[N2/README.md](./N2/README.md)**

---

## Comparação rápida das etapas

| Aspecto | N1 (Etapa 1) | N2 (Etapa 2) |
|---|---|---|
| Armazenamento | Memória (`ArrayList`) | Banco **SQLite** (JDBC) |
| Histórico | Arquivo de texto (`historico.txt`) | Tabela `emprestimos` (status ATIVO/DEVOLVIDO) |
| Padrões de projeto | — | Singleton, Factory, Repository/Interface |
| Organização | Classes na raiz | Pacotes em camadas (`biblioteca.*`) |
| Persistência após fechar | Não (só o TXT do histórico) | **Sim** (todos os dados) |

> As regras de negócio são as mesmas nas duas etapas: limite de empréstimos por
> tipo de usuário (Aluno: 3, Professor: 5), bloqueio de empréstimo de livro
> indisponível, atualização automática da disponibilidade na devolução e a
> diferenciação entre Aluno e Professor.
