package biblioteca.repository;

import java.util.List;

import biblioteca.model.Emprestimo;

/***********************************************************************************
 * Universidade Católica de Brasília - UCB                                         *
 * Disciplina: Programação Orientada a Objetos                                     *
 * Atividade: N2 - Evolução do Sistema de Biblioteca                               *
 *                                                                                 *
 * PADRÃO: INTERFACE (Repository)                                                   *
 *                                                                                 *
 * Contrato de persistência dos empréstimos. A tabela "emprestimos" é a única      *
 * fonte de verdade: um livro está emprestado se tiver uma linha com status        *
 * 'ATIVO', e o número de empréstimos de um usuário é a CONTAGEM dessas linhas.     *
 ***********************************************************************************/

public interface EmprestimoRepository {

    // Registra um novo empréstimo (status 'ATIVO').
    void registrar(Emprestimo emprestimo);

    // Marca como devolvido o empréstimo ativo do livro informado.
    // Retorna true se havia um empréstimo ativo para devolver.
    boolean devolverPorLivro(int idLivro);

    // Conta quantos empréstimos ATIVOS o usuário possui (para a regra de limite).
    int contarAtivosPorUsuario(int idUsuario);

    // Indica se o livro tem algum empréstimo ATIVO (impede empréstimo duplicado).
    boolean livroTemEmprestimoAtivo(int idLivro);

    // Lista apenas os empréstimos ativos no momento.
    List<Emprestimo> listarAtivos();

    // Lista apenas os empréstimos já devolvidos (status 'DEVOLVIDO').
    List<Emprestimo> listarDevolvidos();

    // Lista todos os empréstimos (ativos + devolvidos) = histórico completo.
    List<Emprestimo> listarTodos();
}
