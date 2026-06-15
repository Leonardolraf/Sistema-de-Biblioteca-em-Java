package biblioteca.ui;

import java.util.Scanner;

import biblioteca.factory.UsuarioFactory;
import biblioteca.model.Livro;
import biblioteca.model.Usuario;
import biblioteca.service.Biblioteca;

/***********************************************************************************
 * Universidade Católica de Brasília - UCB                                         *
 * Disciplina: Programação Orientada a Objetos                                     *
 * Professor: Alexandre S. D. Santos                                               *
 * Atividade: N2 - Evolução do Sistema de Biblioteca                               *
 * Data: 14/06/2026                                                                *
 *                                                                                 *
 * Camada de APRESENTAÇÃO (CLI).                                                    *
 * Responsável por exibir o menu e coletar os dados via Scanner, delegando toda    *
 * a lógica para a camada de serviço (Biblioteca). Usa a UsuarioFactory para       *
 * criar o usuário do tipo escolhido.                                              *
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
        System.out.println("8 - Ver histórico de empréstimos");
        System.out.println("0 - Sair");
        System.out.println("============================================");
        System.out.print("Escolha uma opção: ");
    }

    public void cadastrarLivro(Scanner scanner, Biblioteca biblioteca) {
        System.out.println("\n--- Cadastro de Livro ---");

        System.out.print("ID do livro: ");
        int idLivro = scanner.nextInt();
        scanner.nextLine();

        // Validação antecipada: verifica duplicidade antes de pedir os demais dados.
        if (biblioteca.buscarLivro(idLivro) != null) {
            System.out.println("Erro: Já existe um livro cadastrado com o ID "
                    + idLivro + ". Cadastro cancelado.");
            return;
        }

        System.out.print("Título: ");
        String titulo = scanner.nextLine();

        System.out.print("Autor: ");
        String autor = scanner.nextLine();

        if (biblioteca.adicionarLivro(new Livro(idLivro, titulo, autor))) {
            System.out.println("Livro cadastrado com sucesso!");
        }
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

        // Validação antecipada de duplicidade.
        if (biblioteca.buscarUsuario(idUsuario) != null) {
            System.out.println("Erro: Já existe um usuário cadastrado com o ID "
                    + idUsuario + ". Cadastro cancelado.");
            return;
        }

        System.out.print("Nome: ");
        String nome = scanner.nextLine();

        String rotulo = (tipoUsuario == 1) ? "Matrícula" : "Departamento";
        System.out.print(rotulo + ": ");
        String detalhe = scanner.nextLine();

        // A criação da subclasse correta é responsabilidade da Factory.
        Usuario usuario = UsuarioFactory.criarPorOpcao(tipoUsuario, idUsuario, nome, detalhe);
        if (biblioteca.adicionarUsuario(usuario)) {
            System.out.println(usuario.getTipoUsuario() + " cadastrado com sucesso!");
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
        int idLivro = scanner.nextInt();

        biblioteca.realizarDevolucao(idLivro);
    }
}
