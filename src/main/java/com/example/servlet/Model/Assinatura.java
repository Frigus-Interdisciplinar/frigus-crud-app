package com.example.servlet.Model;

import com.example.servlet.Util.Enum.TipoAssinatura;

public class Assinatura {
    // atributos
    private int idAssinatura;
    private Usuario usuario;
    private Empresa empresa;
    private TipoAssinatura tipoAssinatura;
    private double valor;
    private String descricao;


    // construtor vazio
    public Assinatura() {
    }

    // construtor para planos dos usuarios sem id
    public Assinatura(Usuario usuario, TipoAssinatura tipoAssinatura) {
        this.usuario = usuario;
        this.tipoAssinatura = tipoAssinatura;
        this.valor = TipoAssinatura.definirValorPlano(tipoAssinatura);
        this.descricao = TipoAssinatura.descricaoPlano(tipoAssinatura);
    }

    // construtor para constuir o objeto vindo do banco com foco no usuario
    public Assinatura(int idAssinatura, Usuario usuario, TipoAssinatura tipoAssinatura, double valor, String descricao) {
        this.idAssinatura = idAssinatura;
        this.usuario = usuario;
        this.tipoAssinatura = tipoAssinatura;
        this.valor = valor;
        this.descricao = descricao;
    }

    // construtor para planos das empresas sem id
    public Assinatura(Empresa empresa, TipoAssinatura tipoAssinatura) {
        this.empresa = empresa;
        this.tipoAssinatura = tipoAssinatura;
        this.valor = TipoAssinatura.definirValorPlano(tipoAssinatura);
        this.descricao = TipoAssinatura.descricaoPlano(tipoAssinatura);
    }

    // construtor para ver o objeto no banco com foco na empresa
    public Assinatura(int idAssinatura, Empresa empresa, TipoAssinatura tipoAssinatura, double valor, String descricao) {
        this.idAssinatura = idAssinatura;
        this.empresa = empresa;
        this.tipoAssinatura = tipoAssinatura;
        this.valor = valor;
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

    public TipoAssinatura getTipoAssinatura() {
        return tipoAssinatura;
    }

    public void setTipoAssinatura(TipoAssinatura tipoAssinatura) {
        this.tipoAssinatura = tipoAssinatura;
        this.valor = TipoAssinatura.definirValorPlano(tipoAssinatura);
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
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
                "Descricao: " + String.valueOf(descricao) + "\n" +
                '}';
    }
}
