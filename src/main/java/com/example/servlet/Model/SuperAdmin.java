package com.example.servlet.Model;

// Atributos
public class SuperAdmin {
    private int idSuperAdmin;
    private String nome;
    private String email;
    private String senha;

    // Construtor vazio
    public SuperAdmin() {
    }

    // Construtor para o banco
    public SuperAdmin(int idSuperAdmin, String nome, String email, String senha) {
        this.idSuperAdmin = idSuperAdmin;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
    }


    // GETTERS E SETTERS

    public int getIdSuperAdmin() {
        return idSuperAdmin;
    }

    public void setIdSuperAdmin(int idSuperAdmin) {
        this.idSuperAdmin = idSuperAdmin;
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
        return "SuperAdmin{" +
                "Id do Super Admin: " + idSuperAdmin + "\n" +
                "Nome: " + nome + "\n" +
                "E-mail: " + email + "\n" +
                '}';
    }
}
