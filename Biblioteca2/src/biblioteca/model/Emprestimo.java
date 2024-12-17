package biblioteca.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Emprestimo {
    private int id; 
    private Date dataEmprestimo; 
    private Date dataPrevista; 
    private Devolucao devolucao; 
    private List<ItemEmprestimo> itensEmprestimo; 
    private Aluno aluno;
    private boolean concluido;

    // Construtor vazio
    public Emprestimo() {
        this.itensEmprestimo = new ArrayList<>();
    }
    
     // Construtor para inicialização parcial
    public Emprestimo(int id) {
        this.id = id;
    }

    // Construtor com inicialização
    public Emprestimo(int id, Date dataEmprestimo, Date dataPrevista, boolean concluido) {
        this();
        this.id = id;
        this.dataEmprestimo = dataEmprestimo;
        this.dataPrevista = dataPrevista;
        this.concluido = concluido;
    }
    
 // Construtor reduzido para permitir nulos (ajustado para resolver o erro atual)
    public Emprestimo(int id, Date dataEmprestimo, Date dataPrevista) {
        this.id = id;
        this.dataEmprestimo = dataEmprestimo;
        this.dataPrevista = dataPrevista;
    }
    
    public Emprestimo(int id, Date dataEmprestimo, Date dataPrevista, Aluno aluno) {
        this.id = id;
        this.dataEmprestimo = dataEmprestimo;
        this.dataPrevista = dataPrevista;
        this.aluno = aluno;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDataEmprestimo() {
        return dataEmprestimo;
    }

    public void setDataEmprestimo(Date dataEmprestimo) {
        this.dataEmprestimo = dataEmprestimo;
    }

    public Date getDataPrevista() {
        return dataPrevista;
    }

    public void setDataPrevista(Date dataPrevista) {
        this.dataPrevista = dataPrevista;
    }
    
    public boolean getConcluido() {
        return concluido;
    }

    public void setConcluido(boolean concluido) {
        this.concluido = concluido;
    }

    public Devolucao getDevolucao() {
        return devolucao;
    }

    public void setDevolucao(Devolucao devolucao) {
        this.devolucao = devolucao;
    }

    public List<ItemEmprestimo> getItensEmprestimo() {
        return itensEmprestimo;
    }

    public void setItensEmprestimo(List<ItemEmprestimo> itensEmprestimo) {
        this.itensEmprestimo = itensEmprestimo;
    }
    
    public Aluno getAluno() {
        return aluno;
    }

    public void setAluno(Aluno aluno) { 
        this.aluno = aluno;
    }

    public Date calcularDataDevolucao() {
        long seteDiasEmMilissegundos = 7L * 24 * 60 * 60 * 1000; // 7 dias
        return new Date(this.dataEmprestimo.getTime() + seteDiasEmMilissegundos);
    }
    
    public List<Livro> getLivros() {
        List<Livro> livros = new ArrayList<>();
        for (ItemEmprestimo item : itensEmprestimo) {
            if (item.getLivros() != null) {
                livros.addAll(item.getLivros());
            }
        }
        return livros;
    }

}
