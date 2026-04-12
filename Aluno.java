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
 * Classe que representa um Aluno, herdando da classe Usuario.                    *
 * Limite máximo de 3 livros emprestados simultaneamente.                         *
 ***********************************************************************************/

public class Aluno extends Usuario {
    private static final int LIMITE_EMPRESTIMOS = 3;
    private String matricula;

    public Aluno(int id, String nome, String matricula) {
        super(id, nome);
        this.matricula = matricula;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    @Override
    public int getLimiteEmprestimos() {
        return LIMITE_EMPRESTIMOS;
    }

    @Override
    public String getTipoUsuario() {
        return "Aluno";
    }

    public String toString() {
        return super.toString() + " | Matrícula: " + matricula;
    }
}
