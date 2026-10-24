package com.example.servlet.Util;

import java.text.Collator;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Ordenar {

    // compara textos em português ( ignora maiúsculas e acentos: "Ágata" fica junto do "A" )
    private static final Collator texto = Collator.getInstance(Locale.of("pt", "BR"));

    static {
        texto.setStrength(Collator.PRIMARY);
    }

    public static int texto(String a, String b) {
        return texto.compare(a == null ? "" : a, b == null ? "" : b);
    }


    /**
     * Guarda uma ordem nas duas direções: "campo:asc" e "campo:desc".
     */
    public static <T> void campo(Map<String, Comparator<T>> ordens, String campo, Comparator<T> crescente) {
        ordens.put(campo + ":asc", crescente);
        ordens.put(campo + ":desc", crescente.reversed());
    }


    /**
     * Ordena a lista pela ordem escolhida no select "Ordenar por".
     *
     * Se a ordem pedida não existir ( ou vier vazia ), usa a padrão.
     *
     * @return a ordem que foi usada, para o select mostrar ela marcada
     */
    public static <T> String ordenar(List<T> lista, String pedida, String padrao, Map<String, Comparator<T>> ordens) {
        String usada = (pedida != null && ordens.containsKey(pedida)) ? pedida : padrao;
        lista.sort(ordens.get(usada));
        return usada;
    }
}
