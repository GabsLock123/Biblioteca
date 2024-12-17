package biblioteca.model;

import java.util.Date;

public class Devolucao {
    private int id; // ID persistente no banco de dados
    private boolean atraso; // Indica se houve atraso
    private Date dataDevolucao; // Data em que o item foi devolvido
    private Double multa; // Valor da multa, se houver
    private Emprestimo emprestimo; // Associação com Empréstimo (1:1)

    // Construtor vazio
    public Devolucao() {}

    // Construtor com inicialização
    public Devolucao(int id, boolean atraso, Date dataDevolucao, Double multa, Emprestimo emprestimo) {
        this.id = id;
        this.atraso = atraso;
        this.dataDevolucao = dataDevolucao;
        this.multa = multa;
        this.emprestimo = emprestimo;
    }
    
    public Devolucao(boolean atraso, java.sql.Date dataDevolucao, double multa, Emprestimo emprestimo) {
        this.atraso = atraso;
        this.dataDevolucao = dataDevolucao;
        this.multa = multa;
        this.emprestimo = emprestimo;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isAtraso() {
        return atraso;
    }

    public void setAtraso(boolean atraso) {
        this.atraso = atraso;
    }

    public Date getDataDevolucao() {
        return dataDevolucao;
    }

    public void setDataDevolucao(Date dataDevolucao) {
        this.dataDevolucao = dataDevolucao;
    }

    public Double getMulta() {
        return multa;
    }

    public void setMulta(Double multa) {
        this.multa = multa;
    }

    public Emprestimo getEmprestimo() {
        return emprestimo;
    }

    public void setEmprestimo(Emprestimo emprestimo) {
        this.emprestimo = emprestimo;
    }

    // Método para verificar se está em atraso
    public boolean isEmAtraso() {
        return atraso;
    }
}
