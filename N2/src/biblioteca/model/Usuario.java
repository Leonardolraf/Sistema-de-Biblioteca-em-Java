package biblioteca.model;

/***********************************************************************************
 * Universidade Católica de Brasília - UCB                                         *
 * Disciplina: Programação Orientada a Objetos                                     *
 * Professor: Alexandre S. D. Santos                                               *
 * Atividade: N2 - Evolução do Sistema de Biblioteca                               *
 * Data: 14/06/2026                                                                *
 *                                                                                 *
 * Alunos:                                                                         *
 *   - Leonardo Rodrigues Amorim Filho                                             *
 *   - Caio Eduardo Moura dos Santos                                               *
 *   - Caio Monte Lopes                                                            *
 *   - Caio Gabriel Timotio Rodrigues de Lima                                      *
 *                                                                                 *
 * Descrição:                                                                      *
 * Classe abstrata que representa um usuário da biblioteca.                        *
 * Serve como base para as subclasses Aluno e Professor (herança/polimorfismo).    *
 ***********************************************************************************/

public abstract class Usuario {
    private int id;
    private String nome;

    // Quantidade de empréstimos ATIVOS. Não é persistida diretamente: é
    // preenchida pelo repositório a partir de uma contagem na tabela
    // "emprestimos" (a tabela é a única fonte de verdade). Assim o número
    // nunca dessincroniza do banco.
    private int livrosEmprestados;

    public Usuario(int id, String nome) {
        this.id = id;
        this.nome = nome;
        this.livrosEmprestados = 0;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getLivrosEmprestados() {
        return livrosEmprestados;
    }

    public void setLivrosEmprestados(int livrosEmprestados) {
        this.livrosEmprestados = livrosEmprestados;
    }

    // Métodos abstratos: cada subclasse define seu próprio comportamento.
    public abstract int getLimiteEmprestimos();
    public abstract String getTipoUsuario();

    // Detalhe específico do tipo (matrícula do aluno / departamento do professor).
    // Usado tanto na exibição quanto na persistência genérica (coluna "detalhe").
    public abstract String getDetalhe();

    // Rótulo do detalhe, para impressão amigável ("Matrícula" ou "Departamento").
    public abstract String getRotuloDetalhe();

    // Regra de negócio reaproveitada da etapa anterior: usa o resultado
    // polimórfico de getLimiteEmprestimos() definido em cada subclasse.
    public boolean podeEmprestar() {
        return livrosEmprestados < getLimiteEmprestimos();
    }

    @Override
    public String toString() {
        return "ID: " + id + " | Nome: " + nome + " | Tipo: " + getTipoUsuario()
                + " | Empréstimos: " + livrosEmprestados + "/" + getLimiteEmprestimos()
                + " | " + getRotuloDetalhe() + ": " + getDetalhe();
    }
}
