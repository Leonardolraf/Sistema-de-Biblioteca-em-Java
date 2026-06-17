package biblioteca.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/***********************************************************************************
 * Universidade Católica de Brasília - UCB                                         *
 * Disciplina: Programação Orientada a Objetos                                     *
 * Professor: Alexandre S. D. Santos                                               *
 * Atividade: N2 - Evolução do Sistema de Biblioteca                               *
 * Data: 14/06/2026                                                                *
 *                                                                                 *
 * Descrição:                                                                      *
 * Classe de associação que vincula um Usuario a um Livro.                         *
 * Representa o relacionamento da tabela "emprestimos" no banco: além das          *
 * referências, guarda a data do empréstimo, a data de devolução e o status.       *
 ***********************************************************************************/

public class Emprestimo {
    public static final String ATIVO = "ATIVO";
    public static final String DEVOLVIDO = "DEVOLVIDO";

    // Formato único de data/hora usado em todo o sistema (empréstimo e devolução).
    private static final DateTimeFormatter FORMATO_DATA_HORA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    // Carimbo de data/hora atual no padrão brasileiro. Centralizado aqui para não
    // duplicar o formatador no serviço e no repositório (DRY).
    public static String agora() {
        return LocalDateTime.now().format(FORMATO_DATA_HORA);
    }

    private Usuario usuario;
    private Livro livro;
    private String dataEmprestimo;
    private String dataDevolucao; // nulo enquanto o empréstimo está ativo
    private String status;

    // Empréstimo recém-criado (ainda ativo, sem data de devolução).
    public Emprestimo(Usuario usuario, Livro livro, String dataEmprestimo) {
        this.usuario = usuario;
        this.livro = livro;
        this.dataEmprestimo = dataEmprestimo;
        this.dataDevolucao = null;
        this.status = ATIVO;
    }

    // Empréstimo reconstruído a partir do banco (pode já estar devolvido).
    public Emprestimo(Usuario usuario, Livro livro, String dataEmprestimo,
                      String dataDevolucao, String status) {
        this.usuario = usuario;
        this.livro = livro;
        this.dataEmprestimo = dataEmprestimo;
        this.dataDevolucao = dataDevolucao;
        this.status = status;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public Livro getLivro() {
        return livro;
    }

    public String getDataEmprestimo() {
        return dataEmprestimo;
    }

    public String getDataDevolucao() {
        return dataDevolucao;
    }

    public String getStatus() {
        return status;
    }

    public boolean isAtivo() {
        return ATIVO.equals(status);
    }

    @Override
    public String toString() {
        String base = "Livro: " + livro.getTitulo()
                + " | Usuário: " + usuario.getNome() + " (" + usuario.getTipoUsuario() + ")"
                + " | Empréstimo: " + dataEmprestimo
                + " | Status: " + status;
        if (DEVOLVIDO.equals(status) && dataDevolucao != null) {
            base += " | Devolução: " + dataDevolucao;
        }
        return base;
    }
}
