package biblioteca.control;

import biblioteca.dao.AreaDAO;
import biblioteca.dao.AutorDAO;
import biblioteca.dao.LivroDAO;
import biblioteca.dao.TituloDAO;
import biblioteca.model.Area;
import biblioteca.model.Autor;
import biblioteca.model.Livro;
import biblioteca.model.Titulo;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class LivroController {

    @FXML
    private TextField nomeTituloField;

    @FXML
    private TextField isbnField;

    @FXML
    private TextField edicaoField;

    @FXML
    private TextField anoField;

    @FXML
    private TextField prazoField; // Campo para o prazo

    @FXML
    private ComboBox<String> autorComboBox;

    @FXML
    private ComboBox<String> areaComboBox;

    @FXML
    private CheckBox exemplarBibliotecaCheckBox;

    @FXML
    private Button cadastrarLivroButton;

    private LivroDAO livroDAO;
    private TituloDAO tituloDAO;
    private AutorDAO autorDAO;
    private AreaDAO areaDAO;

    private boolean carregandoAreas = false; // Flag para evitar loops no ComboBox de áreas

    public LivroController() {
        this.livroDAO = new LivroDAO();
        this.tituloDAO = new TituloDAO();
        this.autorDAO = new AutorDAO();
        this.areaDAO = new AreaDAO();
    }

    @FXML
    public void initialize() {
        carregarAutores();
        carregarAreas();

        // Prevenir loop infinito na seleção de áreas
        areaComboBox.setOnAction(event -> {
            if (carregandoAreas) return;

            String areaSelecionada = areaComboBox.getValue();
            System.out.println("Área selecionada: " + areaSelecionada);
        });

        cadastrarLivroButton.setOnAction(event -> cadastrarLivro());
    }

    /**
     * Carrega autores no ComboBox.
     */
    private void carregarAutores() {
        List<Autor> autores = autorDAO.findAll();
        autorComboBox.setItems(FXCollections.observableArrayList(
                autores.stream()
                        .map(autor -> String.format("%d - %s %s", autor.getId(), autor.getNome(), autor.getSobrenome()))
                        .toList()
        ));
    }

    /**
     * Carrega áreas no ComboBox.
     */
    private void carregarAreas() {
        try {
            carregandoAreas = true; // Ativa flag de carregamento
            List<Area> areas = areaDAO.findAll();

            if (areas != null && !areas.isEmpty()) {
                areaComboBox.setItems(FXCollections.observableArrayList(
                        areas.stream()
                                .map(area -> String.format("%d - %s", area.getId(), area.getNome()))
                                .toList()
                ));
            } else {
                exibirAlerta("Aviso", "Nenhuma área disponível para seleção.");
            }
        } finally {
            carregandoAreas = false; // Desativa flag após carregamento
        }
    }

    /**
     * Cadastra um novo livro.
     */
    public void cadastrarLivro() {
        try {
            // Validação dos campos
            if (nomeTituloField.getText().isEmpty() || isbnField.getText().isEmpty() ||
                    edicaoField.getText().isEmpty() || anoField.getText().isEmpty() ||
                    prazoField.getText().isEmpty() || autorComboBox.getValue() == null ||
                    areaComboBox.getValue() == null) {
                exibirAlerta("Erro", "Todos os campos devem ser preenchidos!");
                return;
            }

            // Dados do título
            String nomeTitulo = nomeTituloField.getText();
            String isbn = isbnField.getText();
            int edicao = Integer.parseInt(edicaoField.getText());
            int ano = Integer.parseInt(anoField.getText());
            int prazo = Integer.parseInt(prazoField.getText());

            // Extração do Autor e Área selecionados
            int autorId = Integer.parseInt(autorComboBox.getValue().split(" - ")[0]);
            int areaId = Integer.parseInt(areaComboBox.getValue().split(" - ")[0]);

            Autor autor = autorDAO.findById(autorId);
            Area area = areaDAO.findById(areaId);

            // Verifica se o título já existe no banco
            Titulo titulo = tituloDAO.findByTitulo(nomeTitulo, isbn, edicao, ano, autorId, areaId);
            if (titulo == null) {
                // Caso o título não exista, cria um novo
                titulo = new Titulo();
                titulo.setNome(nomeTitulo);
                titulo.setIsbn(isbn);
                titulo.setEdicao(edicao);
                titulo.setAno(ano);
                titulo.setPrazo(prazo);
                titulo.setAutor(autor);
                titulo.setArea(area);

                tituloDAO.save(titulo);
            }

            // Criação do livro associado ao título
            Livro livro = new Livro();
            livro.setDisponivel(true);
            livro.setExemplarBiblioteca(exemplarBibliotecaCheckBox.isSelected());
            livro.setTitulo(titulo);

            // Verifica unicidade do livro
            if (livroDAO.isLivroDuplicado(livro)) {
                exibirAlerta("Erro", "Este livro já foi cadastrado.");
                return;
            }

            // Salva o livro no banco de dados
            livroDAO.save(livro);

            exibirAlerta("Sucesso", "Livro cadastrado com sucesso!");
            limparCampos();

        } catch (NumberFormatException e) {
            exibirAlerta("Erro", "Os campos Edição, Ano e Prazo devem ser números válidos.");
        } catch (Exception e) {
            exibirAlerta("Erro", "Erro ao cadastrar livro: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Exibe um alerta na tela.
     */
    private void exibirAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    /**
     * Limpa os campos após cadastro.
     */
    private void limparCampos() {
        nomeTituloField.clear();
        isbnField.clear();
        edicaoField.clear();
        anoField.clear();
        prazoField.clear();
        autorComboBox.getSelectionModel().clearSelection();
        areaComboBox.getSelectionModel().clearSelection();
        exemplarBibliotecaCheckBox.setSelected(false);
    }
}
