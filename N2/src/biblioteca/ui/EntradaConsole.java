package biblioteca.ui;

import java.util.Scanner;

/***********************************************************************************
 * Universidade Católica de Brasília - UCB                                         *
 * Disciplina: Programação Orientada a Objetos                                     *
 * Professor: Alexandre S. D. Santos                                               *
 * Atividade: N2 - Evolução do Sistema de Biblioteca                               *
 *                                                                                 *
 * Auxiliar de LEITURA SEGURA do teclado (camada de apresentação).                 *
 * Centraliza a validação de entrada para que um erro de digitação faça o sistema  *
 * RE-PERGUNTAR em vez de quebrar (antes, um nextInt() com letra lançava           *
 * InputMismatchException e derrubava o programa inteiro). É usado tanto pelo       *
 * Main quanto pelo AcoesMenu, evitando duplicar a lógica de leitura.              *
 ***********************************************************************************/

public class EntradaConsole {

    // Classe utilitária: só expõe métodos estáticos, não precisa ser instanciada.
    private EntradaConsole() {
    }

    // Lê um inteiro, repetindo a pergunta enquanto a entrada não for numérica.
    public static int lerInteiro(Scanner scanner, String mensagem) {
        System.out.print(mensagem);
        while (!scanner.hasNextInt()) {
            scanner.next(); // descarta o token inválido (ex.: letra)
            System.out.print("Entrada inválida. Digite um número: ");
        }
        int valor = scanner.nextInt();
        scanner.nextLine(); // limpa o restante da linha (importante antes de ler texto)
        return valor;
    }

    // Lê um inteiro positivo (> 0), repetindo a pergunta até ser válido.
    // Usado para IDs, que não fazem sentido como zero ou negativos.
    public static int lerInteiroPositivo(Scanner scanner, String mensagem) {
        int valor;
        do {
            valor = lerInteiro(scanner, mensagem);
            if (valor <= 0) {
                System.out.println("O valor deve ser maior que zero.");
            }
        } while (valor <= 0);
        return valor;
    }

    // Lê um texto obrigatório (não vazio), aplicando trim e re-perguntando se em branco.
    public static String lerTextoObrigatorio(Scanner scanner, String rotulo) {
        String valor;
        do {
            System.out.print(rotulo + ": ");
            valor = scanner.nextLine().trim();
            if (valor.isEmpty()) {
                System.out.println("Este campo não pode ficar em branco.");
            }
        } while (valor.isEmpty());
        return valor;
    }
}
