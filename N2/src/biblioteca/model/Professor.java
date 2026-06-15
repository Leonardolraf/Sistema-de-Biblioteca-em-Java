package biblioteca.model;

/***********************************************************************************
 * Universidade Católica de Brasília - UCB                                         *
 * Disciplina: Programação Orientada a Objetos                                     *
 * Professor: Alexandre S. D. Santos                                               *
 * Atividade: N2 - Evolução do Sistema de Biblioteca                               *
 * Data: 14/06/2026                                                                *
 *                                                                                 *
 * Descrição:                                                                      *
 * Classe que representa um Professor, herdando da classe abstrata Usuario.        *
 * Limite máximo de 5 livros emprestados simultaneamente.                          *
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

    @Override
    public String getDetalhe() {
        return departamento;
    }

    @Override
    public String getRotuloDetalhe() {
        return "Departamento";
    }
}
