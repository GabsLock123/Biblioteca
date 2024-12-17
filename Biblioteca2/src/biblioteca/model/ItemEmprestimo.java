package biblioteca.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ItemEmprestimo {
    private int id; // ID persistente no banco de dados
    private Date dataDevolucao; // Data efetiva de devolução
    private Date dataPrevista; // Data prevista para devolução
    private List<Livro> livros; // Associação com Livro (1:N)
    private Emprestimo emprestimo;
    private Livro livro;

     // Construtor básico (parcial)
    public ItemEmprestimo(int id) {
        this.id = id;
    }
    
    // Construtor vazio
    public ItemEmprestimo() {
        this.livros = new ArrayList<>();
    }

    // Construtor com inicialização
    public ItemEmprestimo(int id, Date dataDevolucao, Date dataPrevista) {
        this();
        this.id = id;
        this.dataDevolucao = dataDevolucao;
        this.dataPrevista = dataPrevista;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDataDevolucao() {
        return dataDevolucao;
    }

    public void setDataDevolucao(Date dataDevolucao) {
        this.dataDevolucao = dataDevolucao;
    }

    public Date getDataPrevista() {
        return dataPrevista;
    }

    public void setDataPrevista(Date dataPrevista) {
        this.dataPrevista = dataPrevista;
    }

    public List<Livro> getLivros() {
        return livros;
    }
    
    public Emprestimo getEmprestimo() {
        return emprestimo; // Corrigido para retornar o campo correto
    }


    public void setEmprestimo(Emprestimo emprestimo) { // Método adicionado
        this.emprestimo = emprestimo;
    }

    public Livro getLivro() {
        return livro;
    }

    public void setLivro(Livro livro) { // Método adicionado
        this.livro = livro;
    }

    public void setLivros(List<Livro> livros) {
        this.livros = livros;
    }

    // Método para associar um livro ao item de empréstimo
    public void associarLivro(Livro livro) {
        if (livro != null && !this.livros.contains(livro)) {
            this.livros.add(livro);
        }
    }
}
