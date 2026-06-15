package biblioteca.model;

/***********************************************************************************
 * Universidade Católica de Brasília - UCB                                         *
 * Disciplina: Programação Orientada a Objetos                                     *
 * Professor: Alexandre S. D. Santos                                               *
 * Atividade: N2 - Evolução do Sistema de Biblioteca                               *
 * Data: 14/06/2026                                                                *
 *                                                                                 *
 * Descrição:                                                                      *
 * Classe que representa um Livro do acervo.                                       *
 * Controla o status de disponibilidade (Disponível/Emprestado).                   *
 ***********************************************************************************/

public class Livro {
    private int id;
    private String titulo;
    private String autor;
    private boolean disponivel;

    public Livro(int id, String titulo, String autor) {
        this(id, titulo, autor, true);
    }

    // Construtor usado pelo repositório ao reconstruir o objeto a partir do banco,
    // preservando o status de disponibilidade gravado.
    public Livro(int id, String titulo, String autor, boolean disponivel) {
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.disponivel = disponivel;
    }

    public int getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public boolean isDisponivel() {
        return disponivel;
    }

    public void setDisponivel(boolean disponivel) {
        this.disponivel = disponivel;
    }

    public String getStatusTexto() {
        return disponivel ? "Disponível" : "Emprestado";
    }

    @Override
    public String toString() {
        return "ID: " + id + " | Título: " + titulo + " | Autor: " + autor
                + " | Status: " + getStatusTexto();
    }
}
