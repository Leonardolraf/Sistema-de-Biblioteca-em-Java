import java.util.ArrayList;

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
 * Classe responsável pelo controle do sistema da biblioteca.                     *
 * Gerencia livros, usuários e empréstimos.                                       *
 ***********************************************************************************/

public class Biblioteca {
    private ArrayList<Livro> livros = new ArrayList<>();
    private ArrayList<Usuario> usuarios = new ArrayList<>();
    private ArrayList<Emprestimo> emprestimos = new ArrayList<>();
    private Historico historico;

    public Biblioteca() {
        this.historico = new Historico();
    }

    public Historico getHistorico() {
        return historico;
    }

    // Cadastra um livro, retorna false se o ID já existir
    public boolean adicionarLivro(Livro livro) {
        if (buscarLivro(livro.getId()) != null) {
            System.out.println("Erro: Já existe um livro cadastrado com o ID " + livro.getId() + ".");
            return false;
        }
        livros.add(livro);
        historico.registrarCadastroLivro(livro.getId(), livro.getTitulo(), livro.getAutor());
        return true;
    }

    // Cadastra um usuário, retorna false se o ID já existir
    public boolean adicionarUsuario(Usuario usuario) {
        if (buscarUsuario(usuario.getId()) != null) {
            System.out.println("Erro: Já existe um usuário cadastrado com o ID " + usuario.getId() + ".");
            return false;
        }
        usuarios.add(usuario);

        String detalhe = "";
        if (usuario instanceof Aluno) {
            detalhe = "Matrícula: " + ((Aluno) usuario).getMatricula();
        } else if (usuario instanceof Professor) {
            detalhe = "Departamento: " + ((Professor) usuario).getDepartamento();
        }
        historico.registrarCadastroUsuario(usuario.getId(), usuario.getNome(), usuario.getTipoUsuario(), detalhe);
        return true;
    }

    public Livro buscarLivro(int id) {
        for (Livro livro : livros) {
            if (livro.getId() == id) {
                return livro;
            }
        }
        return null;
    }

    public Usuario buscarUsuario(int id) {
        for (Usuario usuario : usuarios) {
            if (usuario.getId() == id) {
                return usuario;
            }
        }
        return null;
    }

    public void realizarEmprestimo(int idLivro, int idUsuario) {
        Livro livro = buscarLivro(idLivro);
        Usuario usuario = buscarUsuario(idUsuario);

        if (livro == null || usuario == null) {
            System.out.println("Erro: Livro ou usuário não encontrado.");
            return;
        }

        if (!livro.isDisponivel()) {
            System.out.println("Erro: O livro '" + livro.getTitulo() + "' já está emprestado.");
            return;
        }

        if (!usuario.podeEmprestar()) {
            System.out.println("Erro: O usuário '" + usuario.getNome() + "' atingiu o limite de "
                    + usuario.getLimiteEmprestimos() + " empréstimos.");
            return;
        }

        livro.setDisponivel(false);
        usuario.setLivrosEmprestados(usuario.getLivrosEmprestados() + 1);
        emprestimos.add(new Emprestimo(usuario, livro));
        historico.registrarEmprestimo(usuario.getNome(), usuario.getTipoUsuario(), livro.getTitulo(), livro.getId());

        System.out.println("Empréstimo realizado com sucesso!");
        System.out.println("Livro: " + livro.getTitulo() + " -> Usuário: " + usuario.getNome());
    }

    public void realizarDevolucao(int idLivro) {
        for (Emprestimo emprestimo : emprestimos) {
            if (emprestimo.getLivro().getId() == idLivro) {
                Livro livro = emprestimo.getLivro();
                Usuario usuario = emprestimo.getUsuario();

                livro.setDisponivel(true);
                usuario.setLivrosEmprestados(usuario.getLivrosEmprestados() - 1);
                emprestimos.remove(emprestimo);
                historico.registrarDevolucao(usuario.getNome(), usuario.getTipoUsuario(), livro.getTitulo(), livro.getId());

                System.out.println("Devolução realizada com sucesso!");
                System.out.println("Livro '" + livro.getTitulo() + "' devolvido por " + usuario.getNome());
                return;
            }
        }
        System.out.println("Erro: Empréstimo não encontrado para o livro informado.");
    }

    public void listarLivros() {
        if (livros.isEmpty()) {
            System.out.println("Nenhum livro cadastrado.");
            return;
        }
        System.out.println("\n========== ACERVO DA BIBLIOTECA ==========");
        for (Livro livro : livros) {
            System.out.println(livro);
        }
        System.out.println("==========================================");
    }

    public void listarUsuarios() {
        if (usuarios.isEmpty()) {
            System.out.println("Nenhum usuário cadastrado.");
            return;
        }
        System.out.println("\n========== USUÁRIOS CADASTRADOS ==========");
        for (Usuario usuario : usuarios) {
            System.out.println(usuario);
        }
        System.out.println("==========================================");
    }

    public void listarEmprestimos() {
        if (emprestimos.isEmpty()) {
            System.out.println("Nenhum empréstimo ativo.");
            return;
        }
        System.out.println("\n========== EMPRÉSTIMOS ATIVOS ==========");
        for (Emprestimo emprestimo : emprestimos) {
            System.out.println(emprestimo);
        }
        System.out.println("=========================================");
    }
}
