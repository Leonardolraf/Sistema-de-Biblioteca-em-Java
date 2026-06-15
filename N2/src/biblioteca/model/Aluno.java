package biblioteca.model;

/***********************************************************************************
 * Universidade Católica de Brasília - UCB                                         *
 * Disciplina: Programação Orientada a Objetos                                     *
 * Professor: Alexandre S. D. Santos                                               *
 * Atividade: N2 - Evolução do Sistema de Biblioteca                               *
 * Data: 14/06/2026                                                                *
 *                                                                                 *
 * Descrição:                                                                      *
 * Classe que representa um Aluno, herdando da classe abstrata Usuario.            *
 * Limite máximo de 3 livros emprestados simultaneamente.                          *
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

    @Override
    public String getDetalhe() {
        return matricula;
    }

    @Override
    public String getRotuloDetalhe() {
        return "Matrícula";
    }
}
