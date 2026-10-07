package com.example.servlet.Model;

// Atributos
public class Empresa {
    private String cnpj;
    private String nome;
    private String email;
    private String senha;

    // Construtor vazio
    public Empresa() {
    }

    // construtor
    public Empresa(String cnpj, String nome, String email, String senha) {
        this.cnpj = cnpj;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
    }


    // GETTERS E SETTERS

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }


    @Override
    public String toString() {
        return "Empresa{" +
                "CNPJ da Empresa: " + cnpj + "\n" +
                "Nome: " + nome + "\n" +
                "E-mail: " + email + "\n" +
                '}';
    }
}
