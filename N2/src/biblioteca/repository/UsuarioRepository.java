package biblioteca.repository;

import java.util.List;

import biblioteca.model.Usuario;

/***********************************************************************************
 * Universidade Católica de Brasília - UCB                                         *
 * Disciplina: Programação Orientada a Objetos                                     *
 * Atividade: N2 - Evolução do Sistema de Biblioteca                               *
 *                                                                                 *
 * PADRÃO: INTERFACE (Repository)                                                   *
 *                                                                                 *
 * Contrato de persistência de usuários (Aluno/Professor armazenados na mesma      *
 * tabela, diferenciados pela coluna "tipo"). A reconstrução do objeto correto     *
 * a partir da linha é delegada à UsuarioFactory.                                  *
 ***********************************************************************************/

public interface UsuarioRepository {

    // Persiste um novo usuário (a coluna "tipo" guarda Aluno/Professor).
    void salvar(Usuario usuario);

    // Busca um usuário pelo ID; retorna null se não existir.
    // A implementação usa a Factory para devolver a subclasse correta.
    Usuario buscarPorId(int id);

    // Lista todos os usuários cadastrados.
    List<Usuario> listarTodos();
}
