package biblioteca.model;

public class Livro {
    private int id;
    private boolean disponivel;
    private boolean exemplarBiblioteca;
    private Titulo titulo;

    public Livro(int id) {
        this.id = id;
    }

    public Livro() {}

    public Livro(int id, boolean disponivel, boolean exemplarBiblioteca, Titulo titulo) {
        this.id = id;
        this.disponivel = disponivel;
        this.exemplarBiblioteca = exemplarBiblioteca;
        this.titulo = titulo;
    }

    // Getters e Setters
    public boolean isDisponivel() {
        return disponivel;
    }

    public void setDisponivel(boolean disponivel) {
        this.disponivel = disponivel;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isExemplarBiblioteca() {
        return exemplarBiblioteca;
    }

    public void setExemplarBiblioteca(boolean exemplarBiblioteca) {
        this.exemplarBiblioteca = exemplarBiblioteca;
    }

    public Titulo getTitulo() {
        return titulo;
    }

    public void setTitulo(Titulo titulo) {
        this.titulo = titulo;
    }
}
