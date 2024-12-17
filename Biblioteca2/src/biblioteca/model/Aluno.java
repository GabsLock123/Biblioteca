package biblioteca.model;

import java.util.ArrayList;
import java.util.List;

public class Aluno {
    private int id; 
    private String nome;
    private String cpf;
    private String endereco;
    private int matricula;

    // Associações
    private List<Debito> debitos; 
    private List<Reserva> reservas; 
    private List<Emprestimo> emprestimos; 

    public Aluno() {
        this.debitos = new ArrayList<>();
        this.reservas = new ArrayList<>();
        this.emprestimos = new ArrayList<>();
    }

    
    public Aluno(int id, String nome, String cpf, String endereco, int matricula) {
        this();
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
        this.endereco = endereco;
        this.matricula = matricula;
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

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public int getMatricula() {
        return matricula;
    }

    public void setMatricula(int matricula) {
        this.matricula = matricula;
    }

    public List<Debito> getPendencias() {
        return debitos;
    }

    public void setPendencias(List<Debito> debitos) {
        this.debitos = debitos;
    }

    public List<Reserva> getReservas() {
        return reservas;
    }

    public void setReservas(List<Reserva> reservas) {
        this.reservas = reservas;
    }

    public List<Emprestimo> getEmprestimos() {
        return emprestimos;
    }

    public void setEmprestimos(List<Emprestimo> emprestimos) {
        this.emprestimos = emprestimos;
    }

    public boolean temPendencias() {
        return debitos != null && !debitos.isEmpty();
    }
}
