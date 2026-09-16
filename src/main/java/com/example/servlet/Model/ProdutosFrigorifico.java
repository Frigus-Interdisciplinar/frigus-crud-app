package com.example.servlet.Model;

import java.time.LocalDate;

// Atributos
public class ProdutosFrigorifico {
    private int idProdutosFrigorifico;
    private int idNotificacao;
    private Frigorificos frigorificos;
    private Produto produto;
    private LocalDate dtValidade;
    private String status;
    private int lote;

    // Construtor vazio
    public ProdutosFrigorifico() {
    }

    // Construtor sem id
    public ProdutosFrigorifico(int idNotificacao, Frigorificos frigorificos, Produto produto, LocalDate dtValidade, String status, int lote) {
        this.idNotificacao = idNotificacao;
        this.frigorificos = frigorificos;
        this.produto = produto;
        this.dtValidade = dtValidade;
        this.status = status;
        this.lote = lote;
    }

    // Construtor com id
    public ProdutosFrigorifico(int idProdutosFrigorifico, int idNotificacao, Frigorificos frigorificos, Produto produto, LocalDate dtValidade, String status, int lote) {
        this.idProdutosFrigorifico = idProdutosFrigorifico;
        this.idNotificacao = idNotificacao;
        this.frigorificos = frigorificos;
        this.produto = produto;
        this.dtValidade = dtValidade;
        this.status = status;
        this.lote = lote;
    }

    // GETTERS E SETTERS

    public Frigorificos getFrigorificos() {
        return frigorificos;
    }

    public void setFrigorificos(Frigorificos frigorificos) {
        this.frigorificos = frigorificos;
    }

    public int getIdProdutosFrigorifico() {
        return idProdutosFrigorifico;
    }

    public void setIdProdutosFrigorifico(int idProdutosFrigorifico) {
        this.idProdutosFrigorifico = idProdutosFrigorifico;
    }

    public int getIdNotificacao() {
        return idNotificacao;
    }

    public void setIdNotificacao(int idNotificacao) {
        this.idNotificacao = idNotificacao;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public LocalDate getDtValidade() {
        return dtValidade;
    }

    public void setDtValidade(LocalDate dtValidade) {
        this.dtValidade = dtValidade;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getLote() {
        return lote;
    }

    public void setLote(int lote) {
        this.lote = lote;
    }

    @Override
    public String toString() {
        return "ProdutosFrigorifico{" +
                "idProdutosFrigorifico: " + idProdutosFrigorifico + "\n" +
                "Id da Notificação: " + idNotificacao + "\n" +
                "Frigorifico: \t" + frigorificos.toString() + "\n" +
                "Produto: \t" + produto.toString() + "\n" +
                "dtValidade: " + dtValidade + "\n" +
                "status: " + status + "\n" +
                "Lote: " + lote + "\n" +
                '}';
    }
}
