package com.example.servlet.Model;

import java.time.LocalDate;

// Atributos
public class ListaCompras {
    private int idLista;
    private Usuario usuario;
    private Empresa empresa;
    private LocalDate dtLista;

    // Construtor vazio
    public ListaCompras() {
    }

    // Construtor para usuario sem id
    public ListaCompras(Usuario usuario, LocalDate dtLista) {
        this.usuario = usuario;
        this.dtLista = dtLista;
    }


    // Construtor para usuario com id
    public ListaCompras(int idLista, Usuario usuario, LocalDate dtLista) {
        this.idLista = idLista;
        this.usuario = usuario;
        this.dtLista = dtLista;
    }

    // construtor para empresa sem id
    public ListaCompras(Empresa empresa, LocalDate dtLista) {
        this.empresa = empresa;
        this.dtLista = dtLista;
    }

    // construtor para empresa com id
    public ListaCompras(int idLista, Empresa empresa, LocalDate dtLista) {
        this.idLista = idLista;
        this.empresa = empresa;
        this.dtLista = dtLista;
    }


    // GETTERS E SETTERS

    public int getIdLista() {
        return idLista;
    }

    public void setIdLista(int idLista) {
        this.idLista = idLista;
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

    public LocalDate getDtLista() {
        return dtLista;
    }

    public void setDtLista(LocalDate dtLista) {
        this.dtLista = dtLista;
    }

    @Override
    public String toString() {
        return "Lista de Compras{" +
                "Id da Lista: " + idLista + "\n" +
                "Usuario: \t" + String.valueOf(usuario.toString()) + "\n" +
                "Empresa: \t" + String.valueOf(empresa.toString()) + "\n" +
                "Data da Lista: " + dtLista + "\n" +
                '}';
    }
}
