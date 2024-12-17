package biblioteca.control;

import biblioteca.dao.AlunoDAO;
import biblioteca.model.Aluno;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class AlunoController {

    @FXML
    private TextField matriculaField;

    @FXML
    private TextField nomeField;

    @FXML
    private TextField cpfField;

    @FXML
    private TextField enderecoField;

    @FXML
    private Button cadastrarButton;

    private AlunoDAO alunoDAO;

    public AlunoController() {
        this.alunoDAO = new AlunoDAO();
    }

    @FXML
    public void initialize() {
        cadastrarButton.setOnAction(event -> cadastrarAluno());
    }

    /**
     * Método para cadastrar um novo aluno com validações.
     */
    public void cadastrarAluno() {
        // Verifica se os campos obrigatórios estão preenchidos
        if (matriculaField.getText().isEmpty() || nomeField.getText().isEmpty() ||
                cpfField.getText().isEmpty() || enderecoField.getText().isEmpty()) {
            exibirAlerta("Erro", "Todos os campos são obrigatórios!");
            return;
        }

        try {
            int matricula = Integer.parseInt(matriculaField.getText());
            String nome = nomeField.getText();
            String cpf = cpfField.getText();
            String endereco = enderecoField.getText();

            // Verifica duplicidade de matrícula ou CPF
            if (verificarCadastroPorMatricula(matricula)) {
                exibirAlerta("Erro", "Já existe um aluno com esta matrícula: " + matricula);
                return;
            }

            if (verificarCadastroPorCPF(cpf)) {
                exibirAlerta("Erro", "Já existe um aluno com este CPF: " + cpf);
                return;
            }

            // Cria e salva o novo aluno
            Aluno aluno = new Aluno();
            aluno.setMatricula(matricula);
            aluno.setNome(nome);
            aluno.setCpf(cpf);
            aluno.setEndereco(endereco);

            alunoDAO.save(aluno);
            exibirAlerta("Sucesso", "Aluno cadastrado com sucesso!");

            // Limpa os campos após o cadastro
            limparCampos();

        } catch (NumberFormatException e) {
            exibirAlerta("Erro", "A matrícula deve ser um número válido.");
        } catch (Exception e) {
            exibirAlerta("Erro", "Erro ao cadastrar aluno: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Verifica se já existe um aluno com a mesma matrícula.
     *
     * @param matricula Número de matrícula.
     * @return true se o aluno já existir, false caso contrário.
     */
    private boolean verificarCadastroPorMatricula(int matricula) {
        Aluno aluno = alunoDAO.findByMatricula(matricula);
        return aluno != null;
    }

    /**
     * Verifica se já existe um aluno com o mesmo CPF.
     *
     * @param cpf CPF do aluno.
     * @return true se o CPF já estiver registrado, false caso contrário.
     */
    private boolean verificarCadastroPorCPF(String cpf) {
        Aluno aluno = alunoDAO.findByCPF(cpf);
        return aluno != null;
    }

    /**
     * Exibe um alerta na tela.
     *
     * @param titulo   Título do alerta.
     * @param mensagem Mensagem exibida no alerta.
     */
    private void exibirAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    /**
     * Limpa os campos de entrada após o cadastro.
     */
    private void limparCampos() {
        matriculaField.clear();
        nomeField.clear();
        cpfField.clear();
        enderecoField.clear();
    }
}
