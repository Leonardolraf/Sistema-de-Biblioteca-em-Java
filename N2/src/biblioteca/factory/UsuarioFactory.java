package biblioteca.factory;

import biblioteca.model.Aluno;
import biblioteca.model.Professor;
import biblioteca.model.Usuario;

/***********************************************************************************
 * Universidade Católica de Brasília - UCB                                         *
 * Disciplina: Programação Orientada a Objetos                                     *
 * Professor: Alexandre S. D. Santos                                               *
 * Atividade: N2 - Evolução do Sistema de Biblioteca                               *
 * Data: 14/06/2026                                                                *
 *                                                                                 *
 * PADRÃO DE PROJETO: FACTORY                                                       *
 *                                                                                 *
 * Centraliza em UM ÚNICO lugar a decisão de qual subclasse de Usuario instanciar  *
 * (Aluno ou Professor). Quem precisa de um usuário pede à fábrica e recebe o      *
 * objeto pronto, sem conhecer as classes concretas.                               *
 *                                                                                 *
 * Problema que resolve:                                                            *
 *   Antes, o "if tipo == 1 new Aluno() else new Professor()" aparecia espalhado   *
 *   (no menu e ao reconstruir do banco). Duplicar essa decisão é frágil: ao criar *
 *   um novo tipo de usuário seria preciso caçar todos esses "ifs".                *
 *                                                                                 *
 * Alternativa sem Factory:                                                         *
 *   Repetir a lógica de criação em cada ponto que precisa de um Usuario,          *
 *   acoplando o menu e o repositório às classes Aluno/Professor.                   *
 *                                                                                 *
 * Vantagem:                                                                        *
 *   Um só ponto de mudança. O mesmo método é usado tanto no cadastro pelo menu    *
 *   quanto na reconstrução dos objetos vindos do banco (coluna "tipo").           *
 ***********************************************************************************/

public class UsuarioFactory {

    // Construtor privado: a fábrica só expõe métodos estáticos, não precisa ser instanciada.
    private UsuarioFactory() {
    }

    // Cria o usuário a partir do nome do tipo ("Aluno" ou "Professor").
    // Usado tanto pelo cadastro quanto pelo repositório ao ler o banco.
    public static Usuario criar(String tipo, int id, String nome, String detalhe) {
        if (tipo == null) {
            throw new IllegalArgumentException("Tipo de usuário não informado.");
        }
        switch (tipo.trim().toLowerCase()) {
            case "aluno":
                return new Aluno(id, nome, detalhe);     // detalhe = matrícula
            case "professor":
                return new Professor(id, nome, detalhe);  // detalhe = departamento
            default:
                throw new IllegalArgumentException("Tipo de usuário inválido: " + tipo);
        }
    }

    // Sobrecarga de conveniência para o menu, que trabalha com a opção numérica
    // (1 = Aluno, 2 = Professor). Converte o número e delega ao método principal.
    public static Usuario criarPorOpcao(int opcao, int id, String nome, String detalhe) {
        switch (opcao) {
            case 1:
                return criar("Aluno", id, nome, detalhe);
            case 2:
                return criar("Professor", id, nome, detalhe);
            default:
                throw new IllegalArgumentException("Opção de tipo inválida: " + opcao);
        }
    }
}
