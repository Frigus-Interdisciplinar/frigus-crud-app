package com.example.servlet.Model;

import com.example.servlet.Util.Enum.StatusArmazenamento;

// Atributos
public class Frigorificos {
    private int idFrigorifico;
    private Empresa empresa;
    private StatusArmazenamento status;
    private Endereco endereco;
    private String tipo;
    private String nome;

    // Construtor vazio
    public Frigorificos() {
    }

    // Construtor sem id
    public Frigorificos(Empresa empresa, Endereco endereco, String tipo, String nome) {
        this.empresa = empresa;
        this.endereco = endereco;
        this.tipo = tipo;
        this.status = StatusArmazenamento.VAZIO;
        this.nome = nome;
    }

    public Frigorificos(Empresa empresa, Endereco endereco, String tipo, StatusArmazenamento status, String nome) {
        this.empresa = empresa;
        this.endereco = endereco;
        this.tipo = tipo;
        this.status = status;
        this.nome = nome;
    }


    // Construtor com id
    public Frigorificos(int idFrigorifico, Empresa empresa, StatusArmazenamento status, Endereco endereco, String tipo, String nome) {
        this.idFrigorifico = idFrigorifico;
        this.empresa = empresa;
        this.status = status;
        this.endereco = endereco;
        this.tipo = tipo;
        this.nome = nome;
    }


    // GETTERS E SETTERS


    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }

    public int getIdFrigorifico() {
        return idFrigorifico;
    }

    public void setIdFrigorifico(int idFrigorifico) {
        this.idFrigorifico = idFrigorifico;
    }


    public StatusArmazenamento getStatus() {
        return status;
    }

    public void setStatus(StatusArmazenamento status) {
        this.status = status;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public String toString() {
        return "Frigorificos{" + "\n" +
                "Id do Frigorifico: " + idFrigorifico + "\n" +
                "Empresa: \t" + empresa.toString() + "\n" +
                "Status: " + status.name() + "\n" +
                "Endereço: \t" + endereco.toString() + "\n" +
                "Tipo: " + tipo + "\n" +
                '}';
    }
}
