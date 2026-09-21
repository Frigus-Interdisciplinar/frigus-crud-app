package com.example.servlet.Model;

public class MarcasParcerias {
    // atributos
    private int idMarcasParcerias;
    private Empresa empresa;
    private Produto produto;
    private String observacoes;

    // construtor vazio

    public MarcasParcerias() {
    }

    // construtor sem id
    public MarcasParcerias(Empresa empresa, Produto produto, String observacoes) {
        this.empresa = empresa;
        this.produto = produto;
        this.observacoes = observacoes;
    }

    // construtor com id
    public MarcasParcerias(int idMarcasParcerias, Empresa empresa, Produto produto, String observacoes) {
        this.idMarcasParcerias = idMarcasParcerias;
        this.empresa = empresa;
        this.produto = produto;
        this.observacoes = observacoes;
    }

    // getters e setters

    public int getIdMarcasParcerias() {
        return idMarcasParcerias;
    }

    public void setIdMarcasParcerias(int idMarcasParcerias) {
        this.idMarcasParcerias = idMarcasParcerias;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    @Override
    public String toString() {
        return "Marcas com Parcerias{" +
                "Id da Marca; " + idMarcasParcerias + '\n' +
                "Empresa: \t" + empresa.toString() + '\n' +
                "Produto: \t" + produto.toString() + '\n' +
                "Observações: " + observacoes + '\n' +
                '}';
    }
}
