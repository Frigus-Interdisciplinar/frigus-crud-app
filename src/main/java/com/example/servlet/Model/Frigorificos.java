package com.example.servlet.Model;

// Atributos
public class Frigorificos {
    private int idFrigorifico;
    private Empresa empresa;
    private int capacidade;
    private String status;
    private int quantidade;
    private Endereco endereco;
    private String tipo;

    // Construtor vazio
    public Frigorificos() {
    }

    // Construtor sem id

    public Frigorificos(Empresa empresa, int capacidade, String status, int quantidade, Endereco endereco, String tipo) {
        this.empresa = empresa;
        this.capacidade = capacidade;
        this.status = status;
        this.quantidade = quantidade;
        this.endereco = endereco;
        this.tipo = tipo;
    }


    // Construtor com id

    public Frigorificos(int idFrigorifico, Empresa empresa, int capacidade, String status, int quantidade, Endereco endereco, String tipo) {
        this.idFrigorifico = idFrigorifico;
        this.empresa = empresa;
        this.capacidade = capacidade;
        this.status = status;
        this.quantidade = quantidade;
        this.endereco = endereco;
        this.tipo = tipo;
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

    public int getCapacidade() {
        return capacidade;
    }

    public void setCapacidade(int capacidade) {
        this.capacidade = capacidade;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    @Override
    public String toString() {
        return "Frigorificos{" + "\n" +
                "Id do Frigorifico: " + idFrigorifico + "\n" +
                "Empresa: \t" + empresa.toString() + "\n" +
                "Capacidade: " + capacidade + "\n" +
                "Status: " + status + "\n" +
                "Quantidade: " + quantidade + "\n" +
                "Endereço: \t" + endereco.toString() + "\n" +
                "Tipo: " + tipo + "\n" +
                '}';
    }
}
