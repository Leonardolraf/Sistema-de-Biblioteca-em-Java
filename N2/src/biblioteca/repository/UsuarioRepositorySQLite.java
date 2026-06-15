package biblioteca.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import biblioteca.database.DatabaseConnection;
import biblioteca.factory.UsuarioFactory;
import biblioteca.model.Usuario;

/***********************************************************************************
 * Universidade Católica de Brasília - UCB                                         *
 * Disciplina: Programação Orientada a Objetos                                     *
 * Atividade: N2 - Evolução do Sistema de Biblioteca                               *
 *                                                                                 *
 * Implementação SQLite de UsuarioRepository.                                       *
 * Ao reconstruir cada linha, usa a UsuarioFactory para devolver a subclasse       *
 * correta (Aluno ou Professor) a partir da coluna "tipo" — integrando o padrão    *
 * Factory à persistência.                                                          *
 ***********************************************************************************/

public class UsuarioRepositorySQLite implements UsuarioRepository {

    private Connection getConexao() {
        return DatabaseConnection.getInstance().getConexao();
    }

    @Override
    public void salvar(Usuario usuario) {
        String sql = "INSERT INTO usuarios (id, nome, tipo, detalhe) VALUES (?, ?, ?, ?);";
        try (PreparedStatement ps = getConexao().prepareStatement(sql)) {
            ps.setInt(1, usuario.getId());
            ps.setString(2, usuario.getNome());
            ps.setString(3, usuario.getTipoUsuario());   // "Aluno" ou "Professor"
            ps.setString(4, usuario.getDetalhe());        // matrícula ou departamento
            ps.executeUpdate();
        } catch (SQLException excecao) {
            throw new RuntimeException("Erro ao salvar usuário: " + excecao.getMessage(), excecao);
        }
    }

    @Override
    public Usuario buscarPorId(int id) {
        String sql = "SELECT id, nome, tipo, detalhe FROM usuarios WHERE id = ?;";
        try (PreparedStatement ps = getConexao().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return montarUsuario(rs);
                }
            }
        } catch (SQLException excecao) {
            throw new RuntimeException("Erro ao buscar usuário: " + excecao.getMessage(), excecao);
        }
        return null;
    }

    @Override
    public List<Usuario> listarTodos() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT id, nome, tipo, detalhe FROM usuarios ORDER BY id;";
        try (PreparedStatement ps = getConexao().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                usuarios.add(montarUsuario(rs));
            }
        } catch (SQLException excecao) {
            throw new RuntimeException("Erro ao listar usuários: " + excecao.getMessage(), excecao);
        }
        return usuarios;
    }

    // Reconstrói o Usuario delegando a criação da subclasse correta à Factory.
    private Usuario montarUsuario(ResultSet rs) throws SQLException {
        return UsuarioFactory.criar(
                rs.getString("tipo"),
                rs.getInt("id"),
                rs.getString("nome"),
                rs.getString("detalhe")
        );
    }
}
