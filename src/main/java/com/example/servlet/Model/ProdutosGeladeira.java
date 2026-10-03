package com.example.servlet.Model;

import com.example.servlet.Util.Enum.StatusProduto;

import java.time.LocalDate;

// Atributos
public class ProdutosGeladeira {
    private int idProdutosGeladeira;
    private int idNotificacao;
    private Geladeira geladeira;
    private Produto produto;
    private LocalDate dtValidade;
    private StatusProduto status;
    private int lote;
    private int quantidade;

    // Construtor vazio
    public ProdutosGeladeira() {
    }

    // Construtor sem id
    public ProdutosGeladeira(int idNotificacao, Geladeira geladeira, Produto produto, LocalDate dtValidade, int lote,  int quantidade) {
        this.idNotificacao = idNotificacao;
        this.geladeira = geladeira;
        this.produto = produto;
        this.dtValidade = dtValidade;
        this.status = StatusProduto.calcularStatus(dtValidade);
        this.lote = lote;
        this.quantidade = quantidade;
    }

    // construtor sem id e lote
    public ProdutosGeladeira( int idNotificacao, Geladeira geladeira, Produto produto, LocalDate dtValidade, int quantidade) {
        this.idNotificacao = idNotificacao;
        this.geladeira = geladeira;
        this.produto = produto;
        this.dtValidade = dtValidade;
        this.status = StatusProduto.calcularStatus(dtValidade);
        this.quantidade = quantidade;
    }

    // Construtor com id
    public ProdutosGeladeira(int idProdutosGeladeira, int idNotificacao, Geladeira geladeira, Produto produto, LocalDate dtValidade, int lote, StatusProduto status,  int quantidade) {
        this.idProdutosGeladeira = idProdutosGeladeira;
        this.idNotificacao = idNotificacao;
        this.geladeira = geladeira;
        this.produto = produto;
        this.dtValidade = dtValidade;
        this.status = status;
        this.lote = lote;
        this.quantidade = quantidade;
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
        return "Produto na Geladeira{" +"\n" +
                "Id do Produto na Geladeira: " + idProdutosGeladeira + "\n" +
                "Notificação: \t" + idNotificacao + "\n" +
                "Geladeira: \t" + geladeira.toString() + "\n" +
                "Produto: \t" + produto.toString() + "\n" +
                "Data da Validade: " + dtValidade + "\n" +
                "Status: " + status.name() + "\n" +
                "Lote: " + lote + "\n" +
                "Quantidade: " + quantidade + "\n" +
                '}';
    }
}
