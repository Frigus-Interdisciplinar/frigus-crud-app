package com.example.servlet.Util.Enum;

public enum StatusArmazenamento {
    VAZIO,
    OCUPACAO_BAIXA,
    OCUPACAO_MODERADA,
    OCUPACAO_ALTA,
    CHEIA,
    ILIMITADO;


    // metodo para calcular a quantidade ocupada na geladeira ou frigorifico
    public static StatusArmazenamento DefinirStatus(int quantidadeProd, int totalArmazenamento){
        if (totalArmazenamento <= 0){
            return ILIMITADO;
        } else if(quantidadeProd <= 0){
            return VAZIO;
        } else if (quantidadeProd < (totalArmazenamento / 3)) {
            return OCUPACAO_BAIXA;
        } else if (quantidadeProd < ((totalArmazenamento / 3) * 2)) {
            return OCUPACAO_MODERADA;
        } else if (quantidadeProd < (totalArmazenamento -1)) {
            return OCUPACAO_ALTA;
        }else {
            return CHEIA;
        }
    }
}
