package biblioteca.main;

import biblioteca.util.ConnectionFactory;
import biblioteca.util.PostgreSQLManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Configura o DBManager com PostgreSQLManager antes de iniciar a aplicação
            ConnectionFactory.getInstance().setDBManager(PostgreSQLManager.getInstance());

            // Carrega o arquivo FXML para a tela inicial
            Parent root = FXMLLoader.load(getClass().getResource("/biblioteca/control/MenuInicial.fxml"));

            // Configura o Stage (janela principal)
            primaryStage.setTitle("Sistema de Biblioteca - Menu Inicial");
            primaryStage.setScene(new Scene(root, 600, 400));
            primaryStage.show();

            System.out.println("Aplicação iniciada com sucesso!");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erro ao iniciar a aplicação.");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
