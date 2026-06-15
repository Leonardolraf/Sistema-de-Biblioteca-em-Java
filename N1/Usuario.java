/***********************************************************************************
 * Universidade Católica de Brasília - UCB                                         *
 * Disciplina: Programação Orientada a Objetos                                     *
 * Professor: Alexandre S. D. Santos                                               *
 * Data: 12/04/2026                                                                *
 *                                                                                 *
 * Alunos:                                                                         *
 *   - Leonardo Rodrigues Amorim Filho                                             *
 *   - Caio Eduardo Moura dos Santos                                               *
 *   - Caio Monte Lopes                                                            *
 *                                                                                 *
 * Descrição:                                                                      *
 * Classe abstrata que representa um usuário da biblioteca.                        *
 * Serve como base para as subclasses Aluno e Professor.                          *
 ***********************************************************************************/

public abstract class Usuario {
    private int id;
    private String nome;
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

    // Métodos abstratos que cada subclasse deve implementar
    public abstract int getLimiteEmprestimos();
    public abstract String getTipoUsuario();

    // Verifica se o usuário ainda pode pegar livros emprestados
    public boolean podeEmprestar() {
        return livrosEmprestados < getLimiteEmprestimos();
    }

    public String toString() {
        return "ID: " + id + " | Nome: " + nome + " | Tipo: " + getTipoUsuario()
                + " | Empréstimos: " + livrosEmprestados + "/" + getLimiteEmprestimos();
    }
}
