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

    // definir o valor de cada plano
    public static double definirValorPlano(TipoAssinatura plano){
        switch (plano){
            case FREE:
                return 0;
            case PLUS:
                return 29.9;
            case FAMILIA:
                return 49.9;
            case COMERCIAL:
                return 99.9;
            default:
                return 0;
        }
    }
}


