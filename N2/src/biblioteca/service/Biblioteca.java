package biblioteca.service;

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

    // Retorna a mensagem (sucesso ou erro) para a camada de apresentação imprimir.
    // A regra de negócio fica aqui; quem exibe é a UI (separação de responsabilidades).
    public String realizarEmprestimo(int idLivro, int idUsuario) {
        Livro livro = livroRepository.buscarPorId(idLivro);
        Usuario usuario = usuarioRepository.buscarPorId(idUsuario);

        // Regra 1: existência.
        if (livro == null || usuario == null) {
            return "Erro: Livro ou usuário não encontrado.";
        }

        // Regra 2: disponibilidade. A tabela de empréstimos é a ÚNICA fonte de verdade:
        // o livro está emprestado se existir um empréstimo ATIVO para ele. O flag
        // "disponivel" é apenas um espelho exibido nas listagens, por isso NÃO participa
        // da decisão (assim um flag eventualmente dessincronizado nunca trava o livro).
        if (emprestimoRepository.livroTemEmprestimoAtivo(idLivro)) {
            return "Erro: O livro '" + livro.getTitulo() + "' já está emprestado.";
        }

        // Regra 3: limite por tipo de usuário. O contador vem do banco (COUNT),
        // garantindo que reflete o estado real mesmo após reiniciar o programa.
        usuario.setLivrosEmprestados(emprestimoRepository.contarAtivosPorUsuario(idUsuario));
        if (!usuario.podeEmprestar()) {
            return "Erro: O usuário '" + usuario.getNome()
                    + "' atingiu o limite de " + usuario.getLimiteEmprestimos() + " empréstimos.";
        }

        // Efetiva: registra o empréstimo e atualiza a disponibilidade do livro.
        emprestimoRepository.registrar(new Emprestimo(usuario, livro, Emprestimo.agora()));
        livroRepository.atualizarDisponibilidade(idLivro, false);

        return "Empréstimo realizado com sucesso!\n"
                + "Livro: " + livro.getTitulo() + " -> Usuário: " + usuario.getNome();
    }

    // ---------------------------- DEVOLUÇÃO ------------------------------

    public String realizarDevolucao(int idLivro) {
        // Valida existência primeiro, para distinguir "livro inexistente" de
        // "livro existe, mas não está emprestado" (mensagens mais claras).
        Livro livro = livroRepository.buscarPorId(idLivro);
        if (livro == null) {
            return "Erro: Livro com ID " + idLivro + " não existe.";
        }

        boolean devolvido = emprestimoRepository.devolverPorLivro(idLivro);
        if (!devolvido) {
            return "Erro: Empréstimo ativo não encontrado para o livro '" + livro.getTitulo() + "'.";
        }

        // Atualiza automaticamente a disponibilidade do livro após a devolução.
        livroRepository.atualizarDisponibilidade(idLivro, true);

        return "Devolução realizada com sucesso!\n"
                + "Livro '" + livro.getTitulo() + "' devolvido.";
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

    public void listarEmprestimosDevolvidos() {
        List<Emprestimo> devolvidos = emprestimoRepository.listarDevolvidos();
        if (devolvidos.isEmpty()) {
            System.out.println("Nenhum empréstimo devolvido.");
            return;
        }
        System.out.println("\n========== EMPRÉSTIMOS DEVOLVIDOS ==========");
        for (Emprestimo emprestimo : devolvidos) {
            System.out.println(emprestimo);
        }
        System.out.println("===========================================");
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
}
