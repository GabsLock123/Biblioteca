package biblioteca.model;

public class Titulo {
    private int id; // ID persistente no banco de dados
    private String nome; // Nome do título
    private int prazo; // Prazo de empréstimo em dias
    private String isbn; // Código ISBN do título
    private int edicao; // Edição do título
    private int ano; // Ano de publicação
    private Area area; // Associação com Área (1:1)
    private Autor autor; // Associação com Autor (1:1)

    // Construtor vazio
    public Titulo() {}

    public Titulo(int id, String nome, String isbn) {
    	this.id = id;
    	this.nome = nome;
    	this.isbn = isbn;
    }

    // Construtor com inicialização
    public Titulo(int id, String nome, int prazo, String isbn, int edicao, int ano, Area area, Autor autor) {
        this.id = id;
        this.nome = nome;
        this.prazo = prazo;
        this.isbn = isbn;
        this.edicao = edicao;
        this.ano = ano;
        this.area = area;
        this.autor = autor;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getPrazo() {
        return prazo;
    }

    public void setPrazo(int prazo) {
        this.prazo = prazo;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public int getEdicao() {
        return edicao;
    }

    public void setEdicao(int edicao) {
        this.edicao = edicao;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    public Autor getAutor() {
        return autor;
    }

    public void setAutor(Autor autor) {
        this.autor = autor;
    }
}
