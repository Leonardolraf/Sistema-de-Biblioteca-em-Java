import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
 * Classe responsável por registrar e exibir o histórico de operações             *
 * do sistema em um arquivo de texto (historico.txt).                              *
 ***********************************************************************************/

public class Historico {
    private static final String NOME_ARQUIVO = "historico.txt";
    private static final DateTimeFormatter FORMATO_DATA_HORA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public Historico() {
        inicializarArquivo();
    }

    // Cria o arquivo com cabeçalho se não existir
    private void inicializarArquivo() {
        File arquivo = new File(NOME_ARQUIVO);
        if (!arquivo.exists()) {
            try (BufferedWriter escritor = new BufferedWriter(new FileWriter(arquivo))) {
                escritor.write("==========================================================");
                escritor.newLine();
                escritor.write("     HISTÓRICO DO SISTEMA DE BIBLIOTECA - UCB             ");
                escritor.newLine();
                escritor.write("==========================================================");
                escritor.newLine();
                escritor.newLine();
            } catch (IOException excecao) {
                System.out.println("Erro ao criar arquivo de histórico: " + excecao.getMessage());
            }
        }
    }

    // Escreve uma linha no arquivo (modo append)
    private void registrarEntrada(String tipoOperacao, String descricao) {
        try (BufferedWriter escritor = new BufferedWriter(new FileWriter(NOME_ARQUIVO, true))) {
            String dataHoraAtual = LocalDateTime.now().format(FORMATO_DATA_HORA);
            escritor.write("[" + dataHoraAtual + "] " + tipoOperacao + " - " + descricao);
            escritor.newLine();
        } catch (IOException excecao) {
            System.out.println("Erro ao registrar no histórico: " + excecao.getMessage());
        }
    }

    public void registrarCadastroLivro(int idLivro, String titulo, String autor) {
        registrarEntrada("CADASTRO_LIVRO",
                "Livro cadastrado -> ID: " + idLivro + " | Título: " + titulo + " | Autor: " + autor);
    }

    public void registrarCadastroUsuario(int idUsuario, String nome, String tipoUsuario, String detalhe) {
        registrarEntrada("CADASTRO_USUARIO",
                "Usuário cadastrado -> ID: " + idUsuario + " | Nome: " + nome
                        + " | Tipo: " + tipoUsuario + " | " + detalhe);
    }

    public void registrarEmprestimo(String nomeUsuario, String tipoUsuario, String tituloLivro, int idLivro) {
        registrarEntrada("EMPRESTIMO",
                "Empréstimo realizado -> Usuário: " + nomeUsuario + " (" + tipoUsuario
                        + ") | Livro: " + tituloLivro + " (ID: " + idLivro + ")");
    }

    public void registrarDevolucao(String nomeUsuario, String tipoUsuario, String tituloLivro, int idLivro) {
        registrarEntrada("DEVOLUCAO",
                "Devolução realizada -> Usuário: " + nomeUsuario + " (" + tipoUsuario
                        + ") | Livro: " + tituloLivro + " (ID: " + idLivro + ")");
    }

    // Exibe todo o conteúdo do arquivo de histórico
    public void exibirHistoricoCompleto() {
        File arquivo = new File(NOME_ARQUIVO);
        if (!arquivo.exists()) {
            System.out.println("Nenhum histórico registrado ainda.");
            return;
        }

        System.out.println("\n============ HISTÓRICO COMPLETO ============");
        try (BufferedReader leitor = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            boolean temConteudo = false;
            while ((linha = leitor.readLine()) != null) {
                System.out.println(linha);
                temConteudo = true;
            }
            if (!temConteudo) {
                System.out.println("Histórico vazio.");
            }
        } catch (IOException excecao) {
            System.out.println("Erro ao ler o histórico: " + excecao.getMessage());
        }
        System.out.println("============================================");
    }

    public void exibirHistoricoEmprestimos() {
        exibirHistoricoFiltrado("EMPRESTIMO", "HISTÓRICO DE EMPRÉSTIMOS");
    }

    public void exibirHistoricoDevolucoes() {
        exibirHistoricoFiltrado("DEVOLUCAO", "HISTÓRICO DE DEVOLUÇÕES");
    }

    // Exibe registros filtrados por tipo de operação
    private void exibirHistoricoFiltrado(String filtro, String titulo) {
        File arquivo = new File(NOME_ARQUIVO);
        if (!arquivo.exists()) {
            System.out.println("Nenhum histórico registrado ainda.");
            return;
        }

        System.out.println("\n============ " + titulo + " ============");
        try (BufferedReader leitor = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            boolean encontrou = false;
            while ((linha = leitor.readLine()) != null) {
                if (linha.contains(filtro)) {
                    System.out.println(linha);
                    encontrou = true;
                }
            }
            if (!encontrou) {
                System.out.println("Nenhum registro encontrado.");
            }
        } catch (IOException excecao) {
            System.out.println("Erro ao ler o histórico: " + excecao.getMessage());
        }
        System.out.println("================================================");
    }
}
