package biblioteca.control;

import biblioteca.dao.AlunoDAO;
import biblioteca.dao.EmprestimoDAO;
import biblioteca.dao.ItemEmprestimoDAO;
import biblioteca.dao.LivroDAO;
import biblioteca.model.Aluno;
import biblioteca.model.Emprestimo;
import biblioteca.model.ItemEmprestimo;
import biblioteca.model.Livro;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public class EmprestimoController {

    @FXML
    private TextField matriculaField;

    @FXML
    private ListView<Livro> livrosListView;

    @FXML
    private Button confirmarButton;

    @FXML
    private Label feedbackLabel;

    @FXML
    private DatePicker dataEmprestimoPicker;

    private final LivroDAO livroDAO = new LivroDAO();
    private final AlunoDAO alunoDAO = new AlunoDAO();
    private final EmprestimoDAO emprestimoDAO = new EmprestimoDAO();
    private final ItemEmprestimoDAO itemEmprestimoDAO = new ItemEmprestimoDAO();

    @FXML
    public void initialize() {
        carregarTodosOsLivros();
        configurarDatePicker();
        configurarEventos();
    }

    private void carregarTodosOsLivros() {
        List<Livro> livros = livroDAO.findAll();
        ObservableList<Livro> observableLivros = FXCollections.observableArrayList(livros);
        livrosListView.setItems(observableLivros);
        livrosListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        livrosListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Livro livro, boolean empty) {
                super.updateItem(livro, empty);
                if (empty || livro == null) {
                    setText(null);
                } else {
                    String status = livro.isExemplarBiblioteca() ? " (Exemplar da Biblioteca)" :
                            (!livro.isDisponivel() ? " (Indisponível)" : " (Disponível)");
                    setText(livro.getTitulo().getNome() + " - ISBN: " + livro.getTitulo().getIsbn() + status);
                }
            }
        });
    }

    private void configurarDatePicker() {
        dataEmprestimoPicker.setValue(LocalDate.now());
        dataEmprestimoPicker.setEditable(false);
    }

    private void configurarEventos() {
        confirmarButton.setOnAction(event -> processarEmprestimo());
        livrosListView.setOnMouseClicked(this::verificarSelecaoLivro);
    }

    private void verificarSelecaoLivro(MouseEvent event) {
        if (livrosListView.getSelectionModel().getSelectedItems().isEmpty()) {
            feedbackLabel.setText("Erro: Nenhum livro selecionado.");
        } else {
            feedbackLabel.setText("");
        }
    }

    @FXML
    private void processarEmprestimo() {
        feedbackLabel.setText("");
        String matricula = matriculaField.getText();
        if (matricula == null || matricula.isBlank()) {
            feedbackLabel.setText("Erro: Informe a matrícula do aluno.");
            return;
        }

        Aluno aluno = alunoDAO.findByMatricula(Integer.parseInt(matricula));
        if (aluno == null) {
            feedbackLabel.setText("Erro: Aluno não encontrado.");
            return;
        }

        ObservableList<Livro> livrosSelecionados = livrosListView.getSelectionModel().getSelectedItems();
        if (livrosSelecionados.isEmpty()) {
            feedbackLabel.setText("Erro: Selecione ao menos um livro.");
            return;
        }

        Date dataEmprestimo = java.sql.Date.valueOf(dataEmprestimoPicker.getValue());
        Date dataDevolucao = calcularDataDevolucao(livrosSelecionados);

        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setAluno(aluno);
        emprestimo.setDataEmprestimo(dataEmprestimo);
        emprestimo.setDataPrevista(dataDevolucao);

        emprestimoDAO.save(emprestimo);

        for (Livro livro : livrosSelecionados) {
            ItemEmprestimo item = new ItemEmprestimo();
            item.setEmprestimo(emprestimo);
            item.associarLivro(livro);
            item.setDataPrevista(dataDevolucao);
            itemEmprestimoDAO.save(item);

            livroDAO.atualizarDisponibilidade(livro.getId(), false);
        }

        feedbackLabel.setText("Empréstimo registrado com sucesso!");
        carregarTodosOsLivros();
    }

    private Date calcularDataDevolucao(List<Livro> livrosSelecionados) {
        int diasAdicionais = livrosSelecionados.size() > 2 ? (livrosSelecionados.size() - 2) * 2 : 0;
        int maiorPrazo = livrosSelecionados.stream().mapToInt(livro -> livro.getTitulo().getPrazo()).max().orElse(0);
        long prazoFinalMillis = System.currentTimeMillis() + (maiorPrazo + diasAdicionais) * 24L * 60 * 60 * 1000;
        return new Date(prazoFinalMillis);
    }
}
