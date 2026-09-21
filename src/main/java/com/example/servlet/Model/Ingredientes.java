package com.example.servlet.Model;

// Atributos
public class Ingredientes {
    private int idIngredientes;
    private Receita receita;
    private Produto produto;
    private int quantidade;

    // Construtor vazio
    public Ingredientes() {
    }

    // Construtor sem id
    public Ingredientes(Receita receita, Produto produto, int quantidade) {
        this.receita = receita;
        this.produto = produto;
        this.quantidade = quantidade;
    }


    // Construtor com id
    public Ingredientes(int idIngredientes, Receita receita, Produto produto, int quantidade) {
        this.idIngredientes = idIngredientes;
        this.receita = receita;
        this.produto = produto;
        this.quantidade = quantidade;
    }


    // GETTERS E SETTERS


    public int getIdIngredientes() {
        return idIngredientes;
    }

    public void setIdIngredientes(int idIngredientes) {
        this.idIngredientes = idIngredientes;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public Receita getReceita() {
        return receita;
    }

    public void setReceita(Receita receita) {
        this.receita = receita;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    @Override
    public String toString() {
        return "Ingredientes{" +
                "Id do Ingrediente: " + idIngredientes + "\n" +
                "Receita: \t" + receita.toString() + "\n" +
                "Produto: \t" + produto.toString() + "\n" +
                "Quantidade: " + quantidade + "\n" +
                '}';
    }
}
