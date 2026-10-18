package com.example.servlet.Dto;

public class SugestaoReceita {
    private int id;
    private String nome;
    private int totalIngredientes;
    private int ingredientesDisponiveis;

    public SugestaoReceita(int id, String nome, int totalIngredientes, int ingredientesDisponiveis) {
        this.id = id;
        this.nome = nome;
        this.totalIngredientes = totalIngredientes;
        this.ingredientesDisponiveis = ingredientesDisponiveis;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getTotalIngredientes() {
        return totalIngredientes;
    }

    public void setTotalIngredientes(int totalIngredientes) {
        this.totalIngredientes = totalIngredientes;
    }

    public int getIngredientesDisponiveis() {
        return ingredientesDisponiveis;
    }

    public void setIngredientesDisponiveis(int ingredientesDisponiveis) {
        this.ingredientesDisponiveis = ingredientesDisponiveis;
    }

    public boolean isPronta() { return ingredientesDisponiveis == totalIngredientes; }
    public int getFaltam() { return totalIngredientes - ingredientesDisponiveis; }
}