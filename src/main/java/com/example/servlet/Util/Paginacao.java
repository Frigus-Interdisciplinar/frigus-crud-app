package com.example.servlet.Util;

import jakarta.servlet.http.HttpServletRequest;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Paginacao {

    // quantas linhas aparecem em cada pagina da lista
    public static final int por_pagina = 10;

    // parametros que nao fazem parte dos filtros ( nao voltam nos links de pagina )
    private static final Set<String> ignorados = Set.of("pagina", "ver", "editar", "editarItem", "action");


    /**
     * Devolve só a página pedida da lista ( parametro "pagina" da URL, começando em 1 ).
     *
     * Também guarda na requisição o que a tela precisa para mostrar os botões:
     * pagina, totalPaginas, totalFiltrado e queryPagina ( os filtros atuais, para os links manterem o filtro ).
     */
    public static <T> List<T> aplicar(HttpServletRequest req, List<T> lista) {
        int total = lista.size();
        int totalPaginas = Math.max(1, (total + por_pagina - 1) / por_pagina);

        int pagina = 1;
        try {
            pagina = Integer.parseInt(req.getParameter("pagina").trim());
        } catch (Exception e) {
            // sem parametro ou valor invalido, fica na primeira
        }
        if (pagina < 1) pagina = 1;
        if (pagina > totalPaginas) pagina = totalPaginas;

        int inicio = (pagina - 1) * por_pagina;
        int fim = Math.min(inicio + por_pagina, total);

        req.setAttribute("pagina", pagina);
        req.setAttribute("totalPaginas", totalPaginas);
        req.setAttribute("totalFiltrado", total);
        req.setAttribute("queryPagina", montarFiltros(req));

        return lista.subList(inicio, fim);
    }


    // monta "&nome=Ana&uf=SP" com os filtros que vieram na URL ( só no GET, no POST os campos são do formulario )
    private static String montarFiltros(HttpServletRequest req) {
        if (!"GET".equals(req.getMethod())) {
            return "";
        }
        StringBuilder query = new StringBuilder();
        for (Map.Entry<String, String[]> par : req.getParameterMap().entrySet()) {
            if (ignorados.contains(par.getKey())) {
                continue;
            }
            for (String valor : par.getValue()) {
                if (valor == null || valor.isBlank()) {
                    continue;
                }
                query.append('&')
                        .append(URLEncoder.encode(par.getKey(), StandardCharsets.UTF_8))
                        .append('=')
                        .append(URLEncoder.encode(valor, StandardCharsets.UTF_8));
            }
        }
        return query.toString();
    }
}
