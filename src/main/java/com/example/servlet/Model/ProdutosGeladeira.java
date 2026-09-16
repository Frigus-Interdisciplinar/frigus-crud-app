package com.example.servlet.Model;

import java.time.LocalDate;

// Atributos
public class ProdutosGeladeira {
    private int idProdutosGeladeira;
    private int idNotificacao;
    private Geladeira geladeira;
    private Produto produto;
    private LocalDate dtValidade;
    private String status;
    private int lote;

    // Construtor vazio
    public ProdutosGeladeira() {
    }

    // Construtor sem id
    public ProdutosGeladeira(int idNotificacao, Geladeira geladeira, Produto produto, LocalDate dtValidade, String status, int lote) {
        this.idNotificacao = idNotificacao;
        this.geladeira = geladeira;
        this.produto = produto;
        this.dtValidade = dtValidade;
        this.status = status;
        this.lote = lote;
    }

    // Construtor com id
    public ProdutosGeladeira(int idProdutosGeladeira, int idNotificacao, Geladeira geladeira, Produto produto, LocalDate dtValidade, String status, int lote) {
        this.idProdutosGeladeira = idProdutosGeladeira;
        this.idNotificacao = idNotificacao;
        this.geladeira = geladeira;
        this.produto = produto;
        this.dtValidade = dtValidade;
        this.status = status;
        this.lote = lote;
    }

    // GETTERS E SETTERS
    public int getIdProdutosGeladeira() {
        return idProdutosGeladeira;
    }

    public void setIdProdutosGeladeira(int idProdutosGeladeira) {
        this.idProdutosGeladeira = idProdutosGeladeira;
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

    public Geladeira getGeladeira() {
        return geladeira;
    }

    public void setGeladeira(Geladeira geladeira) {
        this.geladeira = geladeira;
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
        return "Produto na Geladeira{" +
                "Id do Produto na Geladeira: " + idProdutosGeladeira + "\n" +
                "Notificação: \t" + idNotificacao + "\n" +
                "Geladeira: \t" + geladeira.toString() + "\n" +
                "Produto: \t" + produto.toString() + "\n" +
                "Data da Validade: " + dtValidade + "\n" +
                "Status: " + status + "\n" +
                "Lote: " + lote + "\n" +
                '}';
    }
}
