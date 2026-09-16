package com.example.servlet.Model;

public class Endereco {
    // atributos
    private int idEndereco;
    private String rua;
    private String bairro;
    private String cidade;
    private String uf;
    private String pais;
    private String cep;
    private String numero;
    private String nomeUnidade;

    // construtor vazio

    public Endereco() {
    }

    // construtor com id


    public Endereco(int idEndereco, String rua, String bairro, String cidade, String uf, String pais, String cep, String numero, String nomeUnidade) {
        this.idEndereco = idEndereco;
        this.rua = rua;
        this.bairro = bairro;
        this.cidade = cidade;
        this.uf = uf;
        this.pais = pais;
        this.cep = cep;
        this.numero = numero;
        this.nomeUnidade = nomeUnidade;
    }

    // construtor sem id

    public Endereco(String rua, String bairro, String cidade, String uf, String pais, String cep, String numero, String nomeUnidade) {
        this.rua = rua;
        this.bairro = bairro;
        this.cidade = cidade;
        this.uf = uf;
        this.pais = pais;
        this.cep = cep;
        this.numero = numero;
        this.nomeUnidade = nomeUnidade;
    }

    // getter e setters

    public int getIdEndereco() {
        return idEndereco;
    }

    public void setIdEndereco(int idEndereco) {
        this.idEndereco = idEndereco;
    }

    public String getRua() {
        return rua;
    }

    public void setRua(String rua) {
        this.rua = rua;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getNomeUnidade() {
        return nomeUnidade;
    }

    public void setNomeUnidade(String nomeUnidade) {
        this.nomeUnidade = nomeUnidade;
    }
    // toString


    @Override
    public String toString() {
        return "Endereco{" +
                "Id do Endereco: " + idEndereco + "\n" +
                "Rua: " + rua + "\n" +
                "Bairro: " + bairro + "\n" +
                "Cidade: " + cidade + "\n" +
                "UF: " + uf + "\n" +
                "Pais: " + pais + "\n" +
                "CEP: " + cep + "\n" +
                "Numero: " + numero + "\n" +
                "Nome da unidade: " + nomeUnidade + "\n" +
                '}';
    }
}
