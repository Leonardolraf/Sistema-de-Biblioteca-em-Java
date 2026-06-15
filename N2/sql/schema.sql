-- ============================================================================
-- Universidade Católica de Brasília - UCB
-- Disciplina: Programação Orientada a Objetos
-- Atividade: N2 - Evolução do Sistema de Biblioteca
--
-- Script de criação das tabelas (SQLite).
-- OBS.: o programa Java já cria estas tabelas automaticamente na primeira
-- execução (ver biblioteca.database.DatabaseConnection). Este script é um
-- registro do esquema e permite recriar o banco manualmente, se desejado:
--     sqlite3 biblioteca.db < sql/schema.sql
-- ============================================================================

-- Ativa a verificação de chaves estrangeiras (desligada por padrão no SQLite).
PRAGMA foreign_keys = ON;

-- ----------------------------------------------------------------------------
-- Tabela de livros do acervo.
-- "disponivel": 1 = Disponível, 0 = Emprestado.
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS livros (
    id         INTEGER PRIMARY KEY,
    titulo     TEXT    NOT NULL,
    autor      TEXT    NOT NULL,
    disponivel INTEGER NOT NULL DEFAULT 1
);

-- ----------------------------------------------------------------------------
-- Tabela de usuários (Aluno e Professor na mesma tabela).
-- "tipo": discrimina a subclasse; "detalhe": matrícula (Aluno) ou
-- departamento (Professor). A reconstrução do objeto correto é feita pela
-- UsuarioFactory no código Java.
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS usuarios (
    id      INTEGER PRIMARY KEY,
    nome    TEXT    NOT NULL,
    tipo    TEXT    NOT NULL CHECK (tipo IN ('Aluno','Professor')),
    detalhe TEXT
);

-- ----------------------------------------------------------------------------
-- Tabela de empréstimos (relacionamento usuário x livro).
-- A devolução NÃO apaga a linha: muda o "status" para 'DEVOLVIDO' e grava a
-- "data_devolucao". Assim a tabela guarda o histórico completo e continua
-- sendo a única fonte de verdade dos empréstimos ATIVOS.
--   - livro emprestado  = existe linha com status 'ATIVO' para aquele id_livro
--   - nº de empréstimos = COUNT das linhas 'ATIVO' daquele id_usuario
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS emprestimos (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    id_livro        INTEGER NOT NULL,
    id_usuario      INTEGER NOT NULL,
    data_emprestimo TEXT    NOT NULL,
    data_devolucao  TEXT,
    status          TEXT    NOT NULL DEFAULT 'ATIVO' CHECK (status IN ('ATIVO','DEVOLVIDO')),
    FOREIGN KEY (id_livro)   REFERENCES livros(id),
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id)
);
