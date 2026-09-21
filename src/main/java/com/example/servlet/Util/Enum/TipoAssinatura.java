package com.example.servlet.Util.Enum;

public enum TipoAssinatura {
    FREE,
    PLUS,
    FAMILIA,
    COMERCIAL;

    public static int quantidadePorPlano(TipoAssinatura plano){
        switch (plano){
            case FREE:
                return 40;
            case PLUS:
                return 80;
            case FAMILIA:
                return -1;
            case COMERCIAL:
                return -1;
            default:
                return 0;
        }
    }
}


