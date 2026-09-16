package com.example.servlet.Model;

// Atributos
public class Usuario {
    private String cpf;
    private String nome;
    private String sobrenome;
    private String email;
    private String senha;
    private String telefone;

    // Construtor vazio
    public Usuario() {
    }

    // Construtor
    public Usuario(String cpf, String nome, String sobrenome, String email, String senha, String telefone) {
        this.cpf = cpf;
        this.nome = nome;
        this.sobrenome = sobrenome;
        this.email = email;
        this.senha = senha;
        this.telefone = telefone;
    }


    // GETTERS E SETTERS
    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
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

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "CPF: " + cpf + "\n" +
                // tem que mostrar a senha aqui ou é melhor não por questões de segurança???
                "Nome: " + nome + "\n" +
                "Sobrenome: " + sobrenome + "\n" +
                "E-mail: " + email + "\n" +
                "Telefone: " + telefone + "\n" +
                '}';
    }



}
