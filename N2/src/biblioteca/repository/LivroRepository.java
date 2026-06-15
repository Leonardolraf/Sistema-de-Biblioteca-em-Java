package biblioteca.repository;

import java.util.List;

import biblioteca.model.Livro;

/***********************************************************************************
 * Universidade Católica de Brasília - UCB                                         *
 * Disciplina: Programação Orientada a Objetos                                     *
 * Atividade: N2 - Evolução do Sistema de Biblioteca                               *
 *                                                                                 *
 * PADRÃO: INTERFACE (Repository)                                                   *
 *                                                                                 *
 * Define o CONTRATO de persistência de livros, sem dizer COMO é feito.            *
 * A camada de serviço (Biblioteca) depende desta interface, e não da classe       *
 * concreta LivroRepositorySQLite. Isso desacopla a regra de negócio da            *
 * tecnologia de banco: trocar SQLite por outro mecanismo é criar outra            *
 * implementação desta mesma interface, sem tocar no serviço.                      *
 ***********************************************************************************/

public interface LivroRepository {

    // Persiste um novo livro.
    void salvar(Livro livro);

    // Busca um livro pelo ID; retorna null se não existir.
    Livro buscarPorId(int id);

    // Lista todos os livros do acervo.
    List<Livro> listarTodos();

    // Atualiza apenas o status de disponibilidade (usado em empréstimo/devolução).
    void atualizarDisponibilidade(int id, boolean disponivel);
}
