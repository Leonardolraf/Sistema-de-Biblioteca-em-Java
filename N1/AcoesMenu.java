import java.util.Scanner;

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
 * Classe de ações do menu principal.                                              *
 * Responsável pelas ações do menu e uso da classe controladora Biblioteca.        *
 ***********************************************************************************/

public class AcoesMenu {

    public void imprimirMenuPrincipal() {
        System.out.println("\n============================================");
        System.out.println("   SISTEMA DE GERENCIAMENTO DE BIBLIOTECA   ");
        System.out.println("============================================");
        System.out.println("1 - Cadastrar livro");
        System.out.println("2 - Cadastrar usuário");
        System.out.println("3 - Realizar empréstimo");
        System.out.println("4 - Realizar devolução");
        System.out.println("5 - Listar livros");
        System.out.println("6 - Listar usuários cadastrados");
        System.out.println("7 - Listar empréstimos ativos");
        System.out.println("8 - Ver histórico de operações");
        System.out.println("0 - Sair");
        System.out.println("============================================");
        System.out.print("Escolha uma opção: ");
    }

    public void cadastrarLivro(Scanner scanner, Biblioteca biblioteca) {
        System.out.println("\n--- Cadastro de Livro ---");

        System.out.print("ID do livro: ");
        int idLivro = scanner.nextInt();
        scanner.nextLine();

        // Verifica duplicidade antes de pedir os demais dados
        if (biblioteca.buscarLivro(idLivro) != null) {
            System.out.println("Erro: Já existe um livro cadastrado com o ID " + idLivro + ". Cadastro cancelado.");
            return;
        }

        System.out.print("Título: ");
        String titulo = scanner.nextLine();

        System.out.print("Autor: ");
        String autor = scanner.nextLine();

        biblioteca.adicionarLivro(new Livro(idLivro, titulo, autor));
        System.out.println("Livro cadastrado com sucesso!");
    }

    public void cadastrarUsuario(Scanner scanner, Biblioteca biblioteca) {
        System.out.println("\n--- Cadastro de Usuário ---");
        System.out.println("Tipo de usuário:");
        System.out.println("1 - Aluno");
        System.out.println("2 - Professor");
        System.out.print("Escolha: ");
        int tipoUsuario = scanner.nextInt();
        scanner.nextLine();

        if (tipoUsuario != 1 && tipoUsuario != 2) {
            System.out.println("Erro: Tipo de usuário inválido. Cadastro cancelado.");
            return;
        }

        System.out.print("ID do usuário: ");
        int idUsuario = scanner.nextInt();
        scanner.nextLine();

        // Verifica duplicidade antes de pedir os demais dados
        if (biblioteca.buscarUsuario(idUsuario) != null) {
            System.out.println("Erro: Já existe um usuário cadastrado com o ID " + idUsuario + ". Cadastro cancelado.");
            return;
        }

        System.out.print("Nome: ");
        String nomeUsuario = scanner.nextLine();

        if (tipoUsuario == 1) {
            System.out.print("Matrícula: ");
            String matricula = scanner.nextLine();
            biblioteca.adicionarUsuario(new Aluno(idUsuario, nomeUsuario, matricula));
            System.out.println("Aluno cadastrado com sucesso!");
        } else {
            System.out.print("Departamento: ");
            String departamento = scanner.nextLine();
            biblioteca.adicionarUsuario(new Professor(idUsuario, nomeUsuario, departamento));
            System.out.println("Professor cadastrado com sucesso!");
        }
    }

    public void realizarEmprestimo(Scanner scanner, Biblioteca biblioteca) {
        System.out.println("\n--- Realizar Empréstimo ---");

        System.out.print("ID do livro: ");
        int idLivro = scanner.nextInt();

        System.out.print("ID do usuário: ");
        int idUsuario = scanner.nextInt();

        biblioteca.realizarEmprestimo(idLivro, idUsuario);
    }

    public void realizarDevolucao(Scanner scanner, Biblioteca biblioteca) {
        System.out.println("\n--- Realizar Devolução ---");

        System.out.print("ID do livro a devolver: ");
        int idLivroDevolucao = scanner.nextInt();

        biblioteca.realizarDevolucao(idLivroDevolucao);
    }

    public void exibirMenuHistorico(Scanner scanner, Biblioteca biblioteca) {
        System.out.println("\n--- Histórico de Operações ---");
        System.out.println("1 - Histórico completo");
        System.out.println("2 - Apenas empréstimos");
        System.out.println("3 - Apenas devoluções");
        System.out.print("Escolha: ");
        int opcaoHistorico = scanner.nextInt();

        Historico historico = biblioteca.getHistorico();

        switch (opcaoHistorico) {
            case 1:
                historico.exibirHistoricoCompleto();
                break;
            case 2:
                historico.exibirHistoricoEmprestimos();
                break;
            case 3:
                historico.exibirHistoricoDevolucoes();
                break;
            default:
                System.out.println("Opção inválida.");
                break;
        }
    }
}
