package com.example.servlet.Model;
import java.time.LocalDate;
// Atributos
public class Usuario {
    private String cpf;
    private String nome;
    private String sobrenome;
    private String email;
    private String senha;
    private LocalDate dtCadastro;

    // Construtor vazio
    public Usuario() {
    }

    // Construtor
    public Usuario(String cpf, String nome, String sobrenome, String email, String senha) {
        this.cpf = cpf;
        this.nome = nome;
        this.sobrenome = sobrenome;
        this.email = email;
        this.senha = senha;
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

    @Override
    public String toString() {
        return "Usuario{" +
                "CPF: " + cpf + "\n" +
                // tem que mostrar a senha aqui ou é melhor não por questões de segurança???
                "Nome: " + nome + "\n" +
                "Sobrenome: " + sobrenome + "\n" +
                "E-mail: " + email + "\n" +
                '}';
    }

    // data em que a conta foi cadastrada ( o banco preenche sozinho )
    public LocalDate getDtCadastro() {
        return dtCadastro;
    }

    public void setDtCadastro(LocalDate dtCadastro) {
        this.dtCadastro = dtCadastro;
    }
}
