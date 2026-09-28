package com.example.servlet.Model;

import com.example.servlet.Util.Enum.StatusProduto;

import java.time.LocalDate;

// Atributos
public class ProdutosFrigorifico {
    private int idProdutosFrigorifico;
    private int idNotificacao;
    private Frigorificos frigorificos;
    private Produto produto;
    private LocalDate dtValidade;
    private StatusProduto status;
    private int lote;
    private int quantidade;

    // Construtor vazio
    public ProdutosFrigorifico() {
    }

    // Construtor sem id
    public ProdutosFrigorifico(int idNotificacao, Frigorificos frigorificos, Produto produto, LocalDate dtValidade, int lote, int quantidade) {
        this.idNotificacao = idNotificacao;
        this.frigorificos = frigorificos;
        this.produto = produto;
        this.dtValidade = dtValidade;
        this.status = StatusProduto.calcularStatus(dtValidade);
        this.lote = lote;
        this.quantidade = quantidade;
    }

    // Construtor com id
    public ProdutosFrigorifico(int idProdutosFrigorifico, int idNotificacao, Frigorificos frigorificos, Produto produto, LocalDate dtValidade, StatusProduto status, int lote,  int quantidade) {
        this.idProdutosFrigorifico = idProdutosFrigorifico;
        this.idNotificacao = idNotificacao;
        this.frigorificos = frigorificos;
        this.produto = produto;
        this.dtValidade = dtValidade;
        this.status = status;
        this.lote = lote;
        this.quantidade = quantidade;
    }

    // construtor sem lote
    public ProdutosFrigorifico(int idNotificacao, Frigorificos frigorificos, Produto produto, LocalDate dtValidade, int quantidade) {
        this.idNotificacao = idNotificacao;
        this.frigorificos = frigorificos;
        this.produto = produto;
        this.dtValidade = dtValidade;
        this.status = StatusProduto.calcularStatus(dtValidade);
        this.quantidade = quantidade;
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
        this.status = StatusProduto.calcularStatus(dtValidade);
    }

    public StatusProduto getStatus() {
        return status;
    }

    public void setStatus(StatusProduto status) {
        this.status = status;
    }

    public int getLote() {
        return lote;
    }

    public void setLote(int lote) {
        this.lote = lote;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }


    @Override
    public String toString() {
        return "ProdutosFrigorifico{" +
                "idProdutosFrigorifico: " + idProdutosFrigorifico + "\n" +
                "Id da Notificação: " + idNotificacao + "\n" +
                "Frigorifico: \t" + frigorificos.toString() + "\n" +
                "Produto: \t" + produto.toString() + "\n" +
                "dtValidade: " + dtValidade + "\n" +
                "status: " + status.name() + "\n" +
                "Quantidade: " + quantidade + "\n" +
                '}';
    }
}
