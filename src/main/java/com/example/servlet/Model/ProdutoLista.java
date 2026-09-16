package com.example.servlet.Model;

// Atributos
public class ProdutoLista {
    private int idProdutoLista;
    private ListaCompras listaCompras;
    private Produto produto;
    private int quantidade;

    // Construtor vazio
    public ProdutoLista() {
    }

    // Construtor sem id
    public ProdutoLista(ListaCompras listaCompras, Produto produto, int quantidade) {
        this.listaCompras = listaCompras;
        this.produto = produto;
        this.quantidade = quantidade;
    }


    // Construtor com id
    public ProdutoLista(int idProdutoLista, ListaCompras listaCompras, Produto produto, int quantidade) {
        this.idProdutoLista = idProdutoLista;
        this.listaCompras = listaCompras;
        this.produto = produto;
        this.quantidade = quantidade;
    }

    // GETTERS E SETTERS

    public int getIdProdutoLista() {
        return idProdutoLista;
    }

    public void setIdProdutoLista(int idProdutoLista) {
        this.idProdutoLista = idProdutoLista;
    }

    public ListaCompras getListaCompras() {
        return listaCompras;
    }

    public void setListaCompras(ListaCompras listaCompras) {
        this.listaCompras = listaCompras;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    @Override
    public String toString() {
        return "ProdutoLista{" +
                "Id do Produto na lista: " + idProdutoLista + "\n" +
                "Lista de Produtos: " + listaCompras.toString() + "\n" +
                "Produto: \t" + produto.toString() + "\n" +
                "Quantidade: " + quantidade + "\n" +
                '}';
    }
}
