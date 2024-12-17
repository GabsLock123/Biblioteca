package biblioteca.model;

import java.util.ArrayList;
import java.util.List;

public class Autor {
    private int id; 
    private String nome;
    private String sobrenome;
    private String titulacao;

    
    private List<Titulo> titulos;

    
    public Autor() {
        this.titulos = new ArrayList<>();
    }

    
    public Autor(int id, String nome, String sobrenome, String titulacao) {
        this();
        this.id = id;
        this.nome = nome;
        this.sobrenome = sobrenome;
        this.titulacao = titulacao;
    }

    
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

    public String getSobrenome() {
        return sobrenome;
    }

    public void setSobrenome(String sobrenome) {
        this.sobrenome = sobrenome;
    }

    public String getTitulacao() {
        return titulacao;
    }

    public void setTitulacao(String titulacao) {
        this.titulacao = titulacao;
    }

    public List<Titulo> getTitulos() {
        return titulos;
    }

    public void setTitulos(List<Titulo> titulos) {
        this.titulos = titulos;
    }
    
    public String getNomeCompleto(String nome, String sobrenome){
        return nome + sobrenome;
    }
}
