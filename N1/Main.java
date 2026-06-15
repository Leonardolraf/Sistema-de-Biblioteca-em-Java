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
 * Classe principal do sistema de gerenciamento de biblioteca.                    *
 * Responsável pela execução do programa e interação com o usuário via terminal.   *
 ***********************************************************************************/

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Biblioteca biblioteca = new Biblioteca();
        AcoesMenu acoesMenu = new AcoesMenu();

        int opcao;

        System.out.println("*********************************************");
        System.out.println("*   Bem-vindo ao Sistema de Biblioteca!     *");
        System.out.println("*   Universidade Católica de Brasília       *");
        System.out.println("*********************************************");

        do {
            acoesMenu.imprimirMenuPrincipal();
            opcao = scanner.nextInt();

            switch (opcao) {
                case 1:
                    acoesMenu.cadastrarLivro(scanner, biblioteca);
                    break;
                case 2:
                    acoesMenu.cadastrarUsuario(scanner, biblioteca);
                    break;
                case 3:
                    acoesMenu.realizarEmprestimo(scanner, biblioteca);
                    break;
                case 4:
                    acoesMenu.realizarDevolucao(scanner, biblioteca);
                    break;
                case 5:
                    biblioteca.listarLivros();
                    break;
                case 6:
                    biblioteca.listarUsuarios();
                    break;
                case 7:
                    biblioteca.listarEmprestimos();
                    break;
                case 8:
                    acoesMenu.exibirMenuHistorico(scanner, biblioteca);
                    break;
                case 0:
                    System.out.println("\nSistema encerrado. Obrigado por utilizar a Biblioteca!");
                    break;
                default:
                    System.out.println("Opção inválida! Por favor, escolha entre 0 e 8.");
                    break;
            }

        } while (opcao != 0);

        scanner.close();
    }
}
