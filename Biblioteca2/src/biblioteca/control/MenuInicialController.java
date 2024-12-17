package biblioteca.control;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;

public class MenuInicialController {

    @FXML
    private Button cadastrarAlunoButton;

    @FXML
    private Button cadastrarLivroButton;

    @FXML
    private Button realizarEmprestimoButton;

    @FXML
    private Button realizarDevolucaoButton;

    /**
     * Inicializa os eventos de clique nos botões
     */
    @FXML
    public void initialize() {
        cadastrarAlunoButton.setOnAction(event -> redirecionarParaJanela("cadastrarAluno.fxml", "Cadastrar Aluno"));
        cadastrarLivroButton.setOnAction(event -> redirecionarParaJanela("cadastrarLivro.fxml", "Cadastrar Livro"));
        realizarEmprestimoButton.setOnAction(event -> redirecionarParaJanela("RealizarEmprestimo.fxml", "Realizar Empréstimo"));
        realizarDevolucaoButton.setOnAction(event -> redirecionarParaJanela("RealizarDevolucao.fxml", "Realizar Devolução"));
    }

    /**
     * Redireciona para a janela especificada
     *
     * @param caminhoFXML O caminho do arquivo FXML
     * @param titulo      O título da nova janela
     */
    private void redirecionarParaJanela(String caminhoFXML, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(caminhoFXML));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(titulo);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erro ao carregar a janela: " + caminhoFXML);
        }
    }
}
