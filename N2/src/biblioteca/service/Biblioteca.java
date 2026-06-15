package biblioteca.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import biblioteca.model.Emprestimo;
import biblioteca.model.Livro;
import biblioteca.model.Usuario;
import biblioteca.repository.EmprestimoRepository;
import biblioteca.repository.LivroRepository;
import biblioteca.repository.UsuarioRepository;

/***********************************************************************************
 * Universidade Católica de Brasília - UCB                                         *
 * Disciplina: Programação Orientada a Objetos                                     *
 * Professor: Alexandre S. D. Santos                                               *
 * Atividade: N2 - Evolução do Sistema de Biblioteca                               *
 * Data: 14/06/2026                                                                *
 *                                                                                 *
 * Camada de SERVIÇO (regras de negócio).                                          *
 * Não conhece SQLite: trabalha apenas com as INTERFACES dos repositórios,         *
 * recebidas no construtor (injeção de dependência). Toda a validação de negócio   *
 * da etapa anterior continua aqui, agora apoiada na persistência em banco.        *
 ***********************************************************************************/

public class Biblioteca {

    private final LivroRepository livroRepository;
    private final UsuarioRepository usuarioRepository;
    private final EmprestimoRepository emprestimoRepository;

    private static final DateTimeFormatter FORMATO_DATA_HORA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public Biblioteca(LivroRepository livroRepository,
                      UsuarioRepository usuarioRepository,
                      EmprestimoRepository emprestimoRepository) {
        this.livroRepository = livroRepository;
        this.usuarioRepository = usuarioRepository;
        this.emprestimoRepository = emprestimoRepository;
    }

    public Livro buscarLivro(int id) {
        return livroRepository.buscarPorId(id);
    }

    public Usuario buscarUsuario(int id) {
        return usuarioRepository.buscarPorId(id);
    }

    // ----------------------------- CADASTROS -----------------------------

    // Regra: não permitir IDs duplicados. Retorna false se já existir.
    public boolean adicionarLivro(Livro livro) {
        if (livroRepository.buscarPorId(livro.getId()) != null) {
            System.out.println("Erro: Já existe um livro cadastrado com o ID "
                    + livro.getId() + ".");
            return false;
        }
        livroRepository.salvar(livro);
        return true;
    }

    public boolean adicionarUsuario(Usuario usuario) {
        if (usuarioRepository.buscarPorId(usuario.getId()) != null) {
            System.out.println("Erro: Já existe um usuário cadastrado com o ID "
                    + usuario.getId() + ".");
            return false;
        }
        usuarioRepository.salvar(usuario);
        return true;
    }

    // --------------------------- EMPRÉSTIMO ------------------------------

    public void realizarEmprestimo(int idLivro, int idUsuario) {
        Livro livro = livroRepository.buscarPorId(idLivro);
        Usuario usuario = usuarioRepository.buscarPorId(idUsuario);

        // Regra 1: existência.
        if (livro == null || usuario == null) {
            System.out.println("Erro: Livro ou usuário não encontrado.");
            return;
        }

        // Regra 2: disponibilidade. A tabela de empréstimos é a fonte de verdade;
        // o flag "disponivel" é apenas um espelho do estado do livro.
        if (!livro.isDisponivel() || emprestimoRepository.livroTemEmprestimoAtivo(idLivro)) {
            System.out.println("Erro: O livro '" + livro.getTitulo() + "' já está emprestado.");
            return;
        }

        // Regra 3: limite por tipo de usuário. O contador vem do banco (COUNT),
        // garantindo que reflete o estado real mesmo após reiniciar o programa.
        usuario.setLivrosEmprestados(emprestimoRepository.contarAtivosPorUsuario(idUsuario));
        if (!usuario.podeEmprestar()) {
            System.out.println("Erro: O usuário '" + usuario.getNome()
                    + "' atingiu o limite de " + usuario.getLimiteEmprestimos() + " empréstimos.");
            return;
        }

        // Efetiva: registra o empréstimo e atualiza a disponibilidade do livro.
        emprestimoRepository.registrar(new Emprestimo(usuario, livro, agora()));
        livroRepository.atualizarDisponibilidade(idLivro, false);

        System.out.println("Empréstimo realizado com sucesso!");
        System.out.println("Livro: " + livro.getTitulo() + " -> Usuário: " + usuario.getNome());
    }

    // ---------------------------- DEVOLUÇÃO ------------------------------

    public void realizarDevolucao(int idLivro) {
        boolean devolvido = emprestimoRepository.devolverPorLivro(idLivro);
        if (!devolvido) {
            System.out.println("Erro: Empréstimo ativo não encontrado para o livro informado.");
            return;
        }
        // Atualiza automaticamente a disponibilidade do livro após a devolução.
        livroRepository.atualizarDisponibilidade(idLivro, true);

        Livro livro = livroRepository.buscarPorId(idLivro);
        String titulo = (livro != null) ? livro.getTitulo() : ("ID " + idLivro);
        System.out.println("Devolução realizada com sucesso!");
        System.out.println("Livro '" + titulo + "' devolvido.");
    }

    // ----------------------------- LISTAGENS -----------------------------

    public void listarLivros() {
        List<Livro> livros = livroRepository.listarTodos();
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
        List<Usuario> usuarios = usuarioRepository.listarTodos();
        if (usuarios.isEmpty()) {
            System.out.println("Nenhum usuário cadastrado.");
            return;
        }
        System.out.println("\n========== USUÁRIOS CADASTRADOS ==========");
        for (Usuario usuario : usuarios) {
            // Preenche o contador a partir do banco para exibir "x/limite" correto.
            usuario.setLivrosEmprestados(
                    emprestimoRepository.contarAtivosPorUsuario(usuario.getId()));
            System.out.println(usuario);
        }
        System.out.println("==========================================");
    }

    public void listarEmprestimosAtivos() {
        List<Emprestimo> ativos = emprestimoRepository.listarAtivos();
        if (ativos.isEmpty()) {
            System.out.println("Nenhum empréstimo ativo.");
            return;
        }
        System.out.println("\n========== EMPRÉSTIMOS ATIVOS ==========");
        for (Emprestimo emprestimo : ativos) {
            System.out.println(emprestimo);
        }
        System.out.println("=========================================");
    }

    // Histórico completo = todos os empréstimos (ativos e devolvidos) no banco.
    public void listarHistoricoEmprestimos() {
        List<Emprestimo> todos = emprestimoRepository.listarTodos();
        if (todos.isEmpty()) {
            System.out.println("Nenhum empréstimo registrado no histórico.");
            return;
        }
        System.out.println("\n========== HISTÓRICO DE EMPRÉSTIMOS ==========");
        for (Emprestimo emprestimo : todos) {
            System.out.println(emprestimo);
        }
        System.out.println("=============================================");
    }

    private String agora() {
        return LocalDateTime.now().format(FORMATO_DATA_HORA);
    }
}
