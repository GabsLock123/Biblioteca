package biblioteca.model;

import java.util.Date;

public class Reserva {
    private int id; // ID persistente no banco de dados
    private Date data; // Data da reserva
    private Aluno aluno; // Associação com Aluno (1:1)
    private Livro livro; // Associação com Livro (1:1)
    private boolean ativa; // Indica se a reserva ainda está ativa

    // Construtor vazio
    public Reserva() {}

    // Construtor com inicialização
    public Reserva(int id, Date data, Aluno aluno, Livro livro) {
        this.id = id;
        this.data = data;
        this.aluno = aluno;
        this.livro = livro;
        this.ativa = true; // A reserva é ativa ao ser criada
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public Aluno getAluno() {
        return aluno;
    }

    public void setAluno(Aluno aluno) {
        this.aluno = aluno;
    }

    public Livro getLivro() {
        return livro;
    }

    public void setLivro(Livro livro) {
        this.livro = livro;
    }

    public boolean isAtiva() {
        return ativa;
    }

    public void setAtiva(boolean ativa) {
        this.ativa = ativa;
    }
}
