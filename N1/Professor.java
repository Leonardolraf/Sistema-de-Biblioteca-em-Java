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
 * Classe que representa um Professor, herdando da classe Usuario.                *
 * Limite máximo de 5 livros emprestados simultaneamente.                         *
 ***********************************************************************************/

public class Professor extends Usuario {
    private static final int LIMITE_EMPRESTIMOS = 5;
    private String departamento;

    public Professor(int id, String nome, String departamento) {
        super(id, nome);
        this.departamento = departamento;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    @Override
    public int getLimiteEmprestimos() {
        return LIMITE_EMPRESTIMOS;
    }

    @Override
    public String getTipoUsuario() {
        return "Professor";
    }

    public String toString() {
        return super.toString() + " | Departamento: " + departamento;
    }
}
