package biblioteca.main;

import java.util.Scanner;

import biblioteca.database.DatabaseConnection;
import biblioteca.repository.EmprestimoRepository;
import biblioteca.repository.EmprestimoRepositorySQLite;
import biblioteca.repository.LivroRepository;
import biblioteca.repository.LivroRepositorySQLite;
import biblioteca.repository.UsuarioRepository;
import biblioteca.repository.UsuarioRepositorySQLite;
import biblioteca.service.Biblioteca;
import biblioteca.ui.AcoesMenu;
import biblioteca.ui.EntradaConsole;

/***********************************************************************************
 * Universidade Católica de Brasília - UCB                                         *
 * Disciplina: Programação Orientada a Objetos                                     *
 * Professor: Alexandre S. D. Santos                                               *
 * Atividade: N2 - Evolução do Sistema de Biblioteca                               *
 * Data: 14/06/2026                                                                *
 *                                                                                 *
 * Alunos:                                                                         *
 *   - Leonardo Rodrigues Amorim Filho                                             *
 *   - Caio Eduardo Moura dos Santos                                               *
 *   - Caio Monte Lopes                                                            *
 *   - Caio Gabriel Timotio Rodrigues de Lima                                      *
 *                                                                                 *
 * Descrição:                                                                      *
 * Ponto de entrada do sistema. Faz a "montagem" das dependências (wiring):        *
 * inicializa o Singleton de conexão, cria as implementações SQLite dos            *
 * repositórios e as injeta no serviço Biblioteca. Em seguida roda o menu.         *
 ***********************************************************************************/

public class Main {
    public static void main(String[] args) {

        // 1) Inicializa a conexão única (Singleton) e cria as tabelas se preciso.
        DatabaseConnection.getInstance();

        // 2) Cria as implementações concretas dos repositórios.
        //    O Main é o único lugar que conhece a tecnologia (SQLite). As outras
        //    camadas só enxergam as interfaces.
        LivroRepository livroRepository = new LivroRepositorySQLite();
        UsuarioRepository usuarioRepository = new UsuarioRepositorySQLite();
        EmprestimoRepository emprestimoRepository =
                new EmprestimoRepositorySQLite(livroRepository, usuarioRepository);

        // 3) Injeta os repositórios no serviço (regras de negócio).
        Biblioteca biblioteca = new Biblioteca(
                livroRepository, usuarioRepository, emprestimoRepository);

        Scanner scanner = new Scanner(System.in);
        AcoesMenu acoesMenu = new AcoesMenu();

        int opcao;

        System.out.println("*********************************************");
        System.out.println("*   Bem-vindo ao Sistema de Biblioteca!     *");
        System.out.println("*   Universidade Católica de Brasília       *");
        System.out.println("*   (dados persistidos em SQLite)           *");
        System.out.println("*********************************************");

        do {
            acoesMenu.imprimirMenuPrincipal();
            opcao = EntradaConsole.lerInteiro(scanner, "");

            // Protege o laço: um erro inesperado de banco em qualquer operação
            // exibe uma mensagem e volta ao menu, em vez de encerrar o programa.
            try {
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
                        biblioteca.listarEmprestimosAtivos();
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
            } catch (Exception erro) {
                System.out.println("Ocorreu um erro ao processar a operação: " + erro.getMessage());
            }

        } while (opcao != 0);

        // Encerramento limpo dos recursos.
        scanner.close();
        DatabaseConnection.getInstance().fechar();
    }
}
