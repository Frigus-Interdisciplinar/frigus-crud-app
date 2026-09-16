package com.example.servlet.Model;

public class Assinatura {
    // atributos
    private int idAssinatura;
    private Usuario usuario;
    private Empresa empresa;
    private String tipoAssinatura;
    private double valor;
    private String nome;
    private String descricao;


    // construtor vazio
    public Assinatura() {
    }

    // construtor para planos dos usuarios sem id
    public Assinatura(Usuario usuario, String tipoAssinatura, double valor, String nome, String descricao) {
        this.usuario = usuario;
        this.tipoAssinatura = tipoAssinatura;
        this.valor = valor;
        this.nome = nome;
        this.descricao = descricao;
    }

    // construtor para planos dos usuarios com id
    public Assinatura(int idAssinatura, Usuario usuario, String tipoAssinatura, double valor, String nome, String descricao) {
        this.idAssinatura = idAssinatura;
        this.usuario = usuario;
        this.tipoAssinatura = tipoAssinatura;
        this.valor = valor;
        this.nome = nome;
        this.descricao = descricao;
    }

    // construtor para planos das empresas sem id
    public Assinatura(Empresa empresa, String tipoAssinatura, double valor, String nome, String descricao) {
        this.empresa = empresa;
        this.tipoAssinatura = tipoAssinatura;
        this.valor = valor;
        this.nome = nome;
        this.descricao = descricao;
    }


    // construtor para planos das empresas com id
    public Assinatura(int idAssinatura, Empresa empresa, String tipoAssinatura, double valor, String nome, String descricao) {
        this.idAssinatura = idAssinatura;
        this.empresa = empresa;
        this.tipoAssinatura = tipoAssinatura;
        this.valor = valor;
        this.nome = nome;
        this.descricao = descricao;
    }



    // getters e setters

    public int getIdAssinatura() {
        return idAssinatura;
    }

    public void setIdAssinatura(int idAssinatura) {
        this.idAssinatura = idAssinatura;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public String getTipoAssinatura() {
        return tipoAssinatura;
    }

    public void setTipoAssinatura(String tipoAssinatura) {
        this.tipoAssinatura = tipoAssinatura;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
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

    // toString

    @Override
    public String toString() {
        return "Assinatura{" +
                "Id da Assinatura" + idAssinatura + "\n" +
                "Usuario: " + usuario + "\n" +
                "Empresa: " + empresa + "\n" +
                "Tipo de Assinatura: " + tipoAssinatura + "\n" +
                "Valor: " + valor + "\n" +
                "Nome: " + nome + "\n" +
                "Descricao: " + descricao +
                '}';
    }
}
