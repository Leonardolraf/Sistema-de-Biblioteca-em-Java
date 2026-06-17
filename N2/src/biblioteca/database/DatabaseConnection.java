package biblioteca.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/***********************************************************************************
 * Universidade Católica de Brasília - UCB                                         *
 * Disciplina: Programação Orientada a Objetos                                     *
 * Professor: Alexandre S. D. Santos                                               *
 * Atividade: N2 - Evolução do Sistema de Biblioteca                               *
 * Data: 14/06/2026                                                                *
 *                                                                                 *
 * PADRÃO DE PROJETO: SINGLETON                                                     *
 *                                                                                 *
 * Garante que exista UMA ÚNICA instância da conexão com o banco SQLite durante    *
 * toda a execução do programa. Os três repositórios compartilham essa mesma       *
 * conexão, evitando abrir/fechar conexões repetidamente e prevenindo problemas    *
 * de concorrência e travamento de arquivo (file lock) típicos do SQLite.          *
 *                                                                                 *
 * Como o Singleton é implementado aqui:                                           *
 *   1. O construtor é PRIVADO -> ninguém de fora consegue dar "new".              *
 *   2. Um atributo estático guarda a única instância.                             *
 *   3. O método estático getInstance() cria a instância na 1ª chamada e devolve   *
 *      sempre a mesma nas seguintes (inicialização preguiçosa / lazy).            *
 ***********************************************************************************/

public class DatabaseConnection {

    // Caminho do arquivo do banco. SQLite cria o arquivo automaticamente se não existir.
    private static final String URL = "jdbc:sqlite:biblioteca.db";

    // Única instância da classe (coração do Singleton).
    private static DatabaseConnection instancia;

    // A conexão JDBC compartilhada por todo o sistema.
    private Connection conexao;

    // Construtor PRIVADO: impede a criação de instâncias fora desta classe.
    private DatabaseConnection() {
        try {
            this.conexao = DriverManager.getConnection(URL);
            // Habilita a verificação de chaves estrangeiras (vem desligada por padrão no SQLite).
            try (Statement st = conexao.createStatement()) {
                st.execute("PRAGMA foreign_keys = ON;");
            }
            criarTabelas();
        } catch (SQLException excecao) {
            // Sem banco o sistema não funciona: falha rápido com mensagem clara.
            throw new RuntimeException("Falha ao conectar ao banco de dados: "
                    + excecao.getMessage(), excecao);
        }
    }

    // Ponto de acesso global à instância única.
    // "synchronized" evita que duas threads criem instâncias diferentes ao mesmo tempo.
    public static synchronized DatabaseConnection getInstance() {
        if (instancia == null) {
            instancia = new DatabaseConnection();
        }
        return instancia;
    }

    // Devolve a conexão compartilhada para os repositórios usarem.
    public Connection getConexao() {
        return conexao;
    }

    // Fecha a conexão compartilhada. Deve ser chamado UMA vez, ao encerrar o
    // programa (ver Main), garantindo um encerramento limpo do recurso.
    public void fechar() {
        try {
            if (conexao != null && !conexao.isClosed()) {
                conexao.close();
            }
        } catch (SQLException excecao) {
            System.out.println("Aviso: erro ao fechar a conexão: " + excecao.getMessage());
        }
    }

    // Cria o esquema (tabelas) caso ainda não exista. Mantém o mesmo conteúdo
    // do script sql/schema.sql, garantindo que o programa rode mesmo sem rodar
    // o script manualmente.
    private void criarTabelas() throws SQLException {
        String criarLivros =
                "CREATE TABLE IF NOT EXISTS livros (" +
                "    id INTEGER PRIMARY KEY," +
                "    titulo TEXT NOT NULL," +
                "    autor TEXT NOT NULL," +
                "    disponivel INTEGER NOT NULL DEFAULT 1" +
                ");";

        String criarUsuarios =
                "CREATE TABLE IF NOT EXISTS usuarios (" +
                "    id INTEGER PRIMARY KEY," +
                "    nome TEXT NOT NULL," +
                "    tipo TEXT NOT NULL CHECK (tipo IN ('Aluno','Professor'))," +
                "    detalhe TEXT" +
                ");";

        // Um empréstimo devolvido NÃO é apagado: vira status 'DEVOLVIDO'.
        // Assim a tabela guarda o histórico completo e continua sendo a
        // única fonte de verdade dos empréstimos ativos.
        String criarEmprestimos =
                "CREATE TABLE IF NOT EXISTS emprestimos (" +
                "    id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "    id_livro INTEGER NOT NULL," +
                "    id_usuario INTEGER NOT NULL," +
                "    data_emprestimo TEXT NOT NULL," +
                "    data_devolucao TEXT," +
                "    status TEXT NOT NULL DEFAULT 'ATIVO' CHECK (status IN ('ATIVO','DEVOLVIDO'))," +
                "    FOREIGN KEY (id_livro) REFERENCES livros(id)," +
                "    FOREIGN KEY (id_usuario) REFERENCES usuarios(id)" +
                ");";

        try (Statement st = conexao.createStatement()) {
            st.execute(criarLivros);
            st.execute(criarUsuarios);
            st.execute(criarEmprestimos);
        }
    }
}
