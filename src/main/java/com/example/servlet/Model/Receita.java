package com.example.servlet.Model;

import java.time.LocalDate;

// Atributos
public class Receita {
    private int idReceita;
    private Empresa empresa;
    private Usuario usuario;
    private String nome;
    private String descricao;
    private String modoPreparo;
    private LocalDate dtReceita;
    private boolean foiFeita;

    // Construtor vazio
    public Receita() {
    }

    // construtor para geral sem id do usuario ou empresa
    public Receita(int idReceita, String nome, String descricao, String modoPreparo, LocalDate dtReceita, boolean foiFeita) {
        this.idReceita = idReceita;
        this.nome = nome;
        this.descricao = descricao;
        this.modoPreparo = modoPreparo;
        this.dtReceita = dtReceita;
        this.foiFeita = foiFeita;
    }

    // Construtor para receitas de usuarios com id
    public Receita(int idReceita, Usuario usuario, String nome, String descricao, String modoPreparo, LocalDate dtReceita, boolean foiFeita) {
        this.idReceita = idReceita;
        this.usuario = usuario;
        this.nome = nome;
        this.descricao = descricao;
        this.modoPreparo = modoPreparo;
        this.dtReceita = dtReceita;
        this.foiFeita = foiFeita;
    }

    // Construtor para receitas de usuarios sem id
    public Receita(Usuario usuario, String nome, String descricao, String modoPreparo, LocalDate dtReceita, boolean foiFeita) {
        this.usuario = usuario;
        this.nome = nome;
        this.descricao = descricao;
        this.modoPreparo = modoPreparo;
        this.dtReceita = dtReceita;
        this.foiFeita = foiFeita;
    }

    // Construtor para receitas de restaurantes com id
    public Receita(int idReceita, Empresa empresa, String nome, String descricao, String modoPreparo, LocalDate dtReceita, boolean foiFeita) {
        this.idReceita = idReceita;
        this.empresa = empresa;
        this.nome = nome;
        this.descricao = descricao;
        this.modoPreparo = modoPreparo;
        this.dtReceita = dtReceita;
        this.foiFeita = foiFeita;
    }

    // Construtor para receitas de restaurantes sem id
    public Receita(Empresa empresa, String nome, String descricao, String modoPreparo, LocalDate dtReceita, boolean foiFeita) {
        this.empresa = empresa;
        this.nome = nome;
        this.descricao = descricao;
        this.modoPreparo = modoPreparo;
        this.dtReceita = dtReceita;
        this.foiFeita = foiFeita;
    }


    // GETTERS E SETTERS

    public int getIdReceita() {
        return idReceita;
    }

    public void setIdReceita(int idReceita) {
        this.idReceita = idReceita;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
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

    public String getModoPreparo() {
        return modoPreparo;
    }

    public void setModoPreparo(String modoPreparo) {
        this.modoPreparo = modoPreparo;
    }

    public LocalDate getDtReceita() {
        return dtReceita;
    }

    public void setDtReceita(LocalDate dtReceita) {
        this.dtReceita = dtReceita;
    }

    public boolean isFoiFeita() {
        return foiFeita;
    }

    public void setFoiFeita(boolean foiFeita) {
        this.foiFeita = foiFeita;
    }

    @Override
    public String toString() {
        return "Receita{" +
                "Id da Receita: " + idReceita + "\n" +
                "Empresa: \t" + String.valueOf(empresa.toString()) + "\n" +
                "Usuario: \t" + String.valueOf(usuario.toString()) + "\n" +
                "Nome: " + nome + "\n" +
                "Descrição: " + descricao + "\n" +
                "Modo de Preparo: " + modoPreparo + "\n" +
                "Data da Receita: " + dtReceita + "\n" +
                "Foi Feita? " + foiFeita + "\n" +
                '}';
    }
}
