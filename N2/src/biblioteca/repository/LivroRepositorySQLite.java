package biblioteca.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import biblioteca.database.DatabaseConnection;
import biblioteca.model.Livro;

/***********************************************************************************
 * Universidade Católica de Brasília - UCB                                         *
 * Disciplina: Programação Orientada a Objetos                                     *
 * Atividade: N2 - Evolução do Sistema de Biblioteca                               *
 *                                                                                 *
 * Implementação SQLite de LivroRepository.                                         *
 * Usa a conexão única fornecida pelo Singleton DatabaseConnection e               *
 * PreparedStatement (consultas parametrizadas) para evitar SQL Injection.         *
 ***********************************************************************************/

public class LivroRepositorySQLite implements LivroRepository {

    // Pega a conexão compartilhada do Singleton.
    private Connection getConexao() {
        return DatabaseConnection.getInstance().getConexao();
    }

    @Override
    public void salvar(Livro livro) {
        String sql = "INSERT INTO livros (id, titulo, autor, disponivel) VALUES (?, ?, ?, ?);";
        try (PreparedStatement ps = getConexao().prepareStatement(sql)) {
            ps.setInt(1, livro.getId());
            ps.setString(2, livro.getTitulo());
            ps.setString(3, livro.getAutor());
            ps.setInt(4, livro.isDisponivel() ? 1 : 0);
            ps.executeUpdate();
        } catch (SQLException excecao) {
            throw new RuntimeException("Erro ao salvar livro: " + excecao.getMessage(), excecao);
        }
    }

    @Override
    public Livro buscarPorId(int id) {
        String sql = "SELECT id, titulo, autor, disponivel FROM livros WHERE id = ?;";
        try (PreparedStatement ps = getConexao().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return montarLivro(rs);
                }
            }
        } catch (SQLException excecao) {
            throw new RuntimeException("Erro ao buscar livro: " + excecao.getMessage(), excecao);
        }
        return null;
    }

    @Override
    public List<Livro> listarTodos() {
        List<Livro> livros = new ArrayList<>();
        String sql = "SELECT id, titulo, autor, disponivel FROM livros ORDER BY id;";
        try (PreparedStatement ps = getConexao().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                livros.add(montarLivro(rs));
            }
        } catch (SQLException excecao) {
            throw new RuntimeException("Erro ao listar livros: " + excecao.getMessage(), excecao);
        }
        return livros;
    }

    @Override
    public void atualizarDisponibilidade(int id, boolean disponivel) {
        String sql = "UPDATE livros SET disponivel = ? WHERE id = ?;";
        try (PreparedStatement ps = getConexao().prepareStatement(sql)) {
            ps.setInt(1, disponivel ? 1 : 0);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException excecao) {
            throw new RuntimeException("Erro ao atualizar disponibilidade: "
                    + excecao.getMessage(), excecao);
        }
    }

    // Reconstrói o objeto Livro a partir de uma linha do ResultSet.
    private Livro montarLivro(ResultSet rs) throws SQLException {
        return new Livro(
                rs.getInt("id"),
                rs.getString("titulo"),
                rs.getString("autor"),
                rs.getInt("disponivel") == 1
        );
    }
}
