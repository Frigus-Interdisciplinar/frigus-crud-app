package com.example.servlet.Util;

import jakarta.servlet.http.HttpServletRequest;

public class Voltar {

    /**
     * Endereço da tela para onde voltar depois de um erro no formulario.
     *
     * Os formularios mandam o campo "voltar" com o endereço da propria tela ( ex: "novo" ou "editar=5" ).
     * Só aceita letras, numeros e os simbolos de uma query string, qualquer outra coisa volta para a listagem.
     */
    public static String destino(HttpServletRequest req, String base) {
        String voltar = req.getParameter("voltar");
        if (voltar != null && !voltar.isBlank() && voltar.matches("[A-Za-z0-9_=&.%-]{1,80}")) {
            return req.getContextPath() + base + "?" + voltar;
        }
        return req.getContextPath() + base;
    }
}
