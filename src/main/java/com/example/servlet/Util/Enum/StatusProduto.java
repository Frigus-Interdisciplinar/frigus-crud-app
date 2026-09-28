package com.example.servlet.Util.Enum;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public enum StatusProduto {
    VALIDADE_LONGA,
    REGULAR,
    PROXIMO_VENCIMENTO,
    ATENCAO,
    VENCIDO;


    // metodo para decidir qual a classificação certa
    public static StatusProduto calcularStatus(LocalDate dtValidade){
        if (dtValidade == null) {
            throw new IllegalArgumentException(
                    "Data de validade não pode ser nula"
            );
        }
        long dias = ChronoUnit.DAYS.between(
                LocalDate.now(),// pega a data atual
                dtValidade
        );
        if (dias < 0){
            return VENCIDO;
        }
        else if (dias <= 3){
            return ATENCAO;
        } else if (dias <= 7) {
            return PROXIMO_VENCIMENTO;
        } else if (dias <= 20) {
            return REGULAR;
        }
        else {
            return VALIDADE_LONGA;
        }
    }
}
