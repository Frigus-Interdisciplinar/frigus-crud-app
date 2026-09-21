package com.example.servlet.Model;

// Atributos
public class Empresa {
    private String cnpj;
    private String nome;
    private String email;
    private String senha;
    private String descricao;

    // Construtor vazio
    public Empresa() {
    }

    // Construtor
    public Empresa(String cnpj, String nome, String email, String senha, String descricao) {
        this.cnpj = cnpj;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.descricao = descricao;
    }

    // construtor sem descricao
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

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }


    @Override
    public String toString() {
        return "Empresa{" +
                "CNPJ da Empresa: " + cnpj + "\n" +
                "Nome: " + nome + "\n" +
                "E-mail: " + email + "\n" +
                "Descricao: " + descricao + "\n" +
                '}';
    }
}
