package biblioteca.model;

import java.util.Date;

public class Debito {
    private int id; 
    private int valor; 
    private Date data; 
    private Aluno aluno; 
    private RegraCalculoDebito regraCalculo;

    // Construtor vazio
    public Debito() {}

    // Construtor com inicialização
    public Debito(int id, int valor, Date data, Aluno aluno) {
        this.id = id;
        this.valor = valor;
        this.data = data;
        this.aluno = aluno;
    }
    
    public Debito(int valor, Date data, RegraCalculoDebito regraCalculo) {
        this.valor = valor;
        this.data = data;
        this.regraCalculo = regraCalculo;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getValor() {
        return valor;
    }

    public void setValor(int valor) {
        this.valor = valor;
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
    
    public RegraCalculoDebito getRegraCalculo() {
        return regraCalculo;
    }

    public void setRegraCalculo(RegraCalculoDebito regraCalculo) {
        this.regraCalculo = regraCalculo;
    }

    public int calcularValorFinal(int valor) {
    	if (regraCalculo == null) {
            throw new IllegalStateException("Nenhuma regra de cálculo definida para este débito.");
        }
        return regraCalculo.calcularDebito(valor);
    }
    
}
