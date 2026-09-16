package com.example.servlet.Model;

// Atributos
public class Funcionario {
    private String cpf;
    private Empresa empresa;
    private String email;
    private String nome;
    private String sobrenome;
    private String cargo;
    private String telefone;
    private String senha;

    // Construtor vazio
    public Funcionario() {
    }

    // Construtor sem id
    public Funcionario(String cpf, Empresa empresa, String email, String nome, String sobrenome, String cargo, String telefone, String senha) {
        this.cpf = cpf;
        this.empresa = empresa;
        this.email = email;
        this.nome = nome;
        this.sobrenome = sobrenome;
        this.cargo = cargo;
        this.telefone = telefone;
        this.senha = senha;
    }

    // GETTERS E SETTERS


    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    @Override
    public String toString() {
        return "Funcionario{" +
                "Id do Funcionario: " + cpf + "\n" +
                "Empresa: " + empresa.toString() + "\n" +
                "Nome: " + nome + "\n" +
                "Sobrenome: " + sobrenome + "\n" +
                "Cargo: " + cargo + "\n" +
                "Email: " + email + "\n" +
                "Telefone: " + telefone + "\n" +
                '}';
    }
}
