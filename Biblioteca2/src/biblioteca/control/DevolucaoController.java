package biblioteca.control;

import biblioteca.dao.DevolucaoDAO;
import biblioteca.dao.EmprestimoDAO;
import biblioteca.dao.LivroDAO;
import biblioteca.model.Devolucao;
import biblioteca.model.Emprestimo;
import biblioteca.model.Livro;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public class DevolucaoController {

    @FXML
    private ComboBox<Emprestimo> emprestimosComboBox;

    @FXML
    private DatePicker dataDevolucaoPicker;

    @FXML
    private Button registrarDevolucaoButton;

    @FXML
    private Label feedbackLabel;

    private final EmprestimoDAO emprestimoDAO = new EmprestimoDAO();
    private final LivroDAO livroDAO = new LivroDAO();
    private final DevolucaoDAO devolucaoDAO = new DevolucaoDAO();

    @FXML
    public void initialize() {
        carregarEmprestimos();
        configurarDatePicker();
        registrarDevolucaoButton.setOnAction(event -> registrarDevolucao());
    }

    private void carregarEmprestimos() {
        List<Emprestimo> emprestimosAtivos = emprestimoDAO.findAllAtivos();
        ObservableList<Emprestimo> observableEmprestimos = FXCollections.observableArrayList(emprestimosAtivos);
        emprestimosComboBox.setItems(observableEmprestimos);

        emprestimosComboBox.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(Emprestimo emprestimo, boolean empty) {
                super.updateItem(emprestimo, empty);
                if (empty || emprestimo == null) {
                    setText(null);
                } else {
                    setText("Empréstimo ID: " + emprestimo.getId() + " | Aluno: " + emprestimo.getAluno().getNome());
                }
            }
        });

        emprestimosComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Emprestimo emprestimo, boolean empty) {
                super.updateItem(emprestimo, empty);
                if (empty || emprestimo == null) {
                    setText("Selecione um empréstimo");
                } else {
                    setText("Empréstimo ID: " + emprestimo.getId() + " | Aluno: " + emprestimo.getAluno().getNome());
                }
            }
        });
    }

    private void configurarDatePicker() {
        dataDevolucaoPicker.setValue(LocalDate.now());
        dataDevolucaoPicker.setEditable(false);
    }
    
//    private void calcularMulta() {
//        Emprestimo emprestimoSelecionado = emprestimosComboBox.getSelectionModel().getSelectedItem();
//        LocalDate dataDevolucao = dataDevolucaoPicker.getValue();
//
//        if (emprestimoSelecionado == null || dataDevolucao == null) {
//            multaLabel.setText("Selecione um empréstimo e uma data de devolução.");
//            return;
//        }
//
//        LocalDate dataPrevista = emprestimoSelecionado.getDataPrevista().toInstant()
//                .atZone(java.time.ZoneId.systemDefault()).toLocalDate();
//
//        long diasAtraso = ChronoUnit.DAYS.between(dataPrevista, dataDevolucao);
//
//        if (diasAtraso > 0) {
//            double multa = diasAtraso * MULTA_POR_DIA;
//            multaLabel.setText(String.format("Multa: R$ %.2f", multa));
//        } else {
//            multaLabel.setText("Sem multa.");
//        }
//    }

    @FXML
    private void registrarDevolucao() {
        feedbackLabel.setText("");

        Emprestimo emprestimoSelecionado = emprestimosComboBox.getSelectionModel().getSelectedItem();
        if (emprestimoSelecionado == null) {
            feedbackLabel.setText("Erro: Nenhum empréstimo selecionado.");
            return;
        }

        LocalDate dataDevolucaoSelecionada = dataDevolucaoPicker.getValue();
        if (dataDevolucaoSelecionada == null) {
            feedbackLabel.setText("Erro: A data de devolução não pode estar vazia.");
            return;
        }

        Date dataDevolucao = java.sql.Date.valueOf(dataDevolucaoSelecionada);

        Devolucao devolucao = new Devolucao();
        devolucao.setDataDevolucao(dataDevolucao);
        devolucao.setEmprestimo(emprestimoSelecionado);
        devolucaoDAO.save(devolucao);

        emprestimoDAO.concluirEmprestimo(emprestimoSelecionado.getId());

        for (Livro livro : emprestimoDAO.findLivrosByEmprestimoId(emprestimoSelecionado.getId())) {
            livroDAO.atualizarDisponibilidade(livro.getId(), true);
        }

        feedbackLabel.setStyle("-fx-text-fill: green;");
        feedbackLabel.setText("Devolução registrada com sucesso!");
        carregarEmprestimos();
    }
}
