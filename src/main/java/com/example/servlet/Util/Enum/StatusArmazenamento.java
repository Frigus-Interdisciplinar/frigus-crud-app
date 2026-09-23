package com.example.servlet.Util.Enum;

public enum StatusArmazenamento {
    VAZIO,
    POUCA_OCUPACAO,
    OCUPACAO_MODERADA,
    ALTA_OCUPACAO,
    CHEIA,
    ILIMITADO;


    // metodo para calcular a quantidade ocupada na geladeira ou frigorifico
    public static StatusArmazenamento DefinirStatus(int quantidadeProd, int totalArmazenamento){
        if (totalArmazenamento <= 0){
            return ILIMITADO;
        } else if(quantidadeProd <= 0){
            return VAZIO;
        } else if (quantidadeProd < (totalArmazenamento / 3)) {
            return POUCA_OCUPACAO;
        } else if (quantidadeProd < ((totalArmazenamento / 3) * 2)) {
            return OCUPACAO_MODERADA;
        } else if (quantidadeProd < (totalArmazenamento -1)) {
            return ALTA_OCUPACAO;
        }else {
            return CHEIA;
        }
    }
}
