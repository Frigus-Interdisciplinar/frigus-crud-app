package com.example.servlet.Model;

// Atributos
public class Geladeira {
    private int idGeladeira;
    private Usuario usuario;
    private int quantidade;
    private String status;
    private String tipo;

    // Construtor vazio
    public Geladeira() {
    }

    // Construtor sem id
    public Geladeira(Usuario usuario, int quantidade, String status, String tipo) {
        this.usuario = usuario;
        this.quantidade = quantidade;
        this.status = status;
        this.tipo = tipo;
    }


    // Construtor com id
    public Geladeira(int idGeladeira, Usuario usuario, int quantidade, String status, String tipo) {
        this.idGeladeira = idGeladeira;
        this.usuario = usuario;
        this.quantidade = quantidade;
        this.status = status;
        this.tipo = tipo;
    }

    // GETTERS E SETTERS
    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public int getIdGeladeira() {
        return idGeladeira;
    }

    public void setIdGeladeira(int idGeladeira) {
        this.idGeladeira = idGeladeira;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    @Override
    public String toString() {
        return "Geladeira{" +
                "Id da Geladeira: " + idGeladeira + "\n" +
                "Usuario: \t" + usuario.toString() + "\n" +
                "Quantidade: " + quantidade + "\n" +
                "Status: " + status + "\n" +
                "Tipo: " + tipo + "\n" +
                '}';
    }
}
