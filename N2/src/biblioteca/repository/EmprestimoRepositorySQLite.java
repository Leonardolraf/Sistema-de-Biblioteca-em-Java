package biblioteca.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import biblioteca.database.DatabaseConnection;
import biblioteca.model.Emprestimo;
import biblioteca.model.Livro;
import biblioteca.model.Usuario;

/***********************************************************************************
 * Universidade Católica de Brasília - UCB                                         *
 * Disciplina: Programação Orientada a Objetos                                     *
 * Atividade: N2 - Evolução do Sistema de Biblioteca                               *
 *                                                                                 *
 * Implementação SQLite de EmprestimoRepository.                                    *
 * Depende das INTERFACES LivroRepository e UsuarioRepository (e não das classes   *
 * concretas) para reconstruir o livro e o usuário de cada empréstimo — exemplo    *
 * de baixo acoplamento por programar voltado à interface.                          *
 ***********************************************************************************/

public class EmprestimoRepositorySQLite implements EmprestimoRepository {

    private final LivroRepository livroRepository;
    private final UsuarioRepository usuarioRepository;

    // Injeção de dependência via construtor: recebe as interfaces, não as implementações.
    public EmprestimoRepositorySQLite(LivroRepository livroRepository,
                                      UsuarioRepository usuarioRepository) {
        this.livroRepository = livroRepository;
        this.usuarioRepository = usuarioRepository;
    }

    private Connection getConexao() {
        return DatabaseConnection.getInstance().getConexao();
    }

    @Override
    public void registrar(Emprestimo emprestimo) {
        String sql = "INSERT INTO emprestimos (id_livro, id_usuario, data_emprestimo, status) "
                   + "VALUES (?, ?, ?, 'ATIVO');";
        try (PreparedStatement ps = getConexao().prepareStatement(sql)) {
            ps.setInt(1, emprestimo.getLivro().getId());
            ps.setInt(2, emprestimo.getUsuario().getId());
            ps.setString(3, emprestimo.getDataEmprestimo());
            ps.executeUpdate();
        } catch (SQLException excecao) {
            throw new RuntimeException("Erro ao registrar empréstimo: "
                    + excecao.getMessage(), excecao);
        }
    }

    @Override
    public boolean devolverPorLivro(int idLivro) {
        // Marca como devolvido apenas o empréstimo ATIVO daquele livro.
        String sql = "UPDATE emprestimos SET status = 'DEVOLVIDO', data_devolucao = ? "
                   + "WHERE id_livro = ? AND status = 'ATIVO';";
        try (PreparedStatement ps = getConexao().prepareStatement(sql)) {
            ps.setString(1, Emprestimo.agora());
            ps.setInt(2, idLivro);
            int linhasAfetadas = ps.executeUpdate();
            return linhasAfetadas > 0;
        } catch (SQLException excecao) {
            throw new RuntimeException("Erro ao devolver empréstimo: "
                    + excecao.getMessage(), excecao);
        }
    }

    @Override
    public int contarAtivosPorUsuario(int idUsuario) {
        String sql = "SELECT COUNT(*) AS total FROM emprestimos "
                   + "WHERE id_usuario = ? AND status = 'ATIVO';";
        try (PreparedStatement ps = getConexao().prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        } catch (SQLException excecao) {
            throw new RuntimeException("Erro ao contar empréstimos: "
                    + excecao.getMessage(), excecao);
        }
        return 0;
    }

    @Override
    public boolean livroTemEmprestimoAtivo(int idLivro) {
        String sql = "SELECT 1 FROM emprestimos WHERE id_livro = ? AND status = 'ATIVO' LIMIT 1;";
        try (PreparedStatement ps = getConexao().prepareStatement(sql)) {
            ps.setInt(1, idLivro);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException excecao) {
            throw new RuntimeException("Erro ao verificar empréstimo ativo: "
                    + excecao.getMessage(), excecao);
        }
    }

    @Override
    public List<Emprestimo> listarAtivos() {
        return listar("SELECT * FROM emprestimos WHERE status = 'ATIVO' ORDER BY id;");
    }

    @Override
    public List<Emprestimo> listarDevolvidos() {
        return listar("SELECT * FROM emprestimos WHERE status = 'DEVOLVIDO' ORDER BY id;");
    }

    @Override
    public List<Emprestimo> listarTodos() {
        return listar("SELECT * FROM emprestimos ORDER BY id;");
    }

    // Executa a consulta e reconstrói cada empréstimo com seu livro e usuário.
    private List<Emprestimo> listar(String sql) {
        List<Emprestimo> emprestimos = new ArrayList<>();
        try (PreparedStatement ps = getConexao().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Livro livro = livroRepository.buscarPorId(rs.getInt("id_livro"));
                Usuario usuario = usuarioRepository.buscarPorId(rs.getInt("id_usuario"));
                // Defesa: ignora linhas órfãs (livro/usuário inexistente) para não quebrar
                // a listagem caso o banco seja editado por fora.
                if (livro == null || usuario == null) {
                    continue;
                }
                emprestimos.add(new Emprestimo(
                        usuario,
                        livro,
                        rs.getString("data_emprestimo"),
                        rs.getString("data_devolucao"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException excecao) {
            throw new RuntimeException("Erro ao listar empréstimos: "
                    + excecao.getMessage(), excecao);
        }
        return emprestimos;
    }
}
