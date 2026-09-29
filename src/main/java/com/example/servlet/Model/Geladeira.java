package com.example.servlet.Model;

import com.example.servlet.Util.Enum.StatusArmazenamento;

// Atributos
public class Geladeira {
    private int idGeladeira;
    private Usuario usuario;
    private StatusArmazenamento status;
    private String tipo;

    // Construtor vazio
    public Geladeira() {
    }

    // Construtor sem id e status
    public Geladeira(Usuario usuario, String tipo) {
        this.usuario = usuario;
        this.status = StatusArmazenamento.VAZIO;
        this.tipo = tipo;
    }

    // Construtor sem id e status e tipo
    public Geladeira(Usuario usuario) {
        this.usuario = usuario;
        this.status = StatusArmazenamento.VAZIO;
    }


    // Construtor para o banco
    public Geladeira(int idGeladeira, Usuario usuario, StatusArmazenamento status, String tipo) {
        this.idGeladeira = idGeladeira;
        this.usuario = usuario;
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


    public StatusArmazenamento getStatus() {
        return status;
    }

    public void setStatus(StatusArmazenamento status) {
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
                "Status: " + status.name() + "\n" +
                "Tipo: " + tipo + "\n" +
                '}';
    }
}
