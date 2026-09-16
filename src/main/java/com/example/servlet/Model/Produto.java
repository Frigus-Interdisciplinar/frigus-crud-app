package com.example.servlet.Model;

public class Produto {
    // Atributos
    private int idProdutos;
    private String nome;
    private String descricao;
    private double preco;
    private String categoria;
    private String marca;
    private String unidadeMedida;

    // Construtor vazio
    public Produto() {
    }

    // Construtor sem id
    public Produto(String nome, String descricao, double preco, String categoria, String marca, String unidadeMedida) {
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.categoria = categoria;
        this.marca = marca;
        this.unidadeMedida = unidadeMedida;
    }


    // Construtor com id
    public Produto(int idProdutos, String nome, String descricao, double preco, String categoria, String marca, String unidadeMedida) {
        this.idProdutos = idProdutos;
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.categoria = categoria;
        this.marca = marca;
        this.unidadeMedida = unidadeMedida;
    }

    // GETTERS E SETTERS
    public int getIdProdutos() {
        return idProdutos;
    }

    public void setIdProdutos(int idProdutos) {
        this.idProdutos = idProdutos;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getUnidadeMedida() {
        return unidadeMedida;
    }

    public void setUnidadeMedida(String unidadeMedida) {
        this.unidadeMedida = unidadeMedida;
    }

    @Override
    public String toString() {
        return "Produto{" +
                "Id do Produto: " + idProdutos + "\n" +
                "Nome: " + nome + "\n" +
                "Descricao: " + descricao + "\n" +
                "Preco: " + preco + "\n" +
                "Categoria: " + categoria + "\n" +
                "Marca: " + marca + "\n" +
                "Unidade de Medida: " + unidadeMedida + "\n" +
                "}";
    }
}
