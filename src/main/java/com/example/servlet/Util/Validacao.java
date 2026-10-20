package com.example.servlet.Util;

import java.time.LocalDate;
import java.util.List;

public class Validacao {
    // =========== FORMATAR ===========

    // CPF
    public String formatarCpf(String cpf){
        StringBuilder cpfPuro = new StringBuilder();
        for (char caracter : cpf.toCharArray()){
            if ( (caracter != '.') && (caracter != '-')){
                cpfPuro.append(caracter);
            }
        }
        return cpfPuro.toString();
    }

    // CNPJ
    public String formatarCnpj(String cnpj){
        StringBuilder cnpjPuro = new StringBuilder();
        for (char caracter : cnpj.toCharArray()){
            if ((caracter != '.') && (caracter != '/') && (caracter != '-') ){
                cnpjPuro.append(caracter);
            }
        }
        return cnpjPuro.toString();
    }


    // CEP
    public String formatarCep(String cep){
        StringBuilder cepPuro = new StringBuilder();
        for (char caracter : cep.toCharArray()){
            if (caracter != '-' ){
                cepPuro.append(caracter);
            }
        }
        return cepPuro.toString();
    }




    // =========== FORMATAR ===========





    // =========== VALIDAR ===========

//    // validar cpf
//    public boolean validarCpf(String cpf){
//        if (cpf.length() != 11){
//            return false;
//        }else{
//            // numero de caracters iguais ( tem que ter a mesma quantidade que o tamanho do cpf)
//            int numIgual = 0;
//            char[] caracteresPermitidos = {'1', '2', '3', '4', '5', '6', '7', '8', '9', '0'};
//
//            for (char caracter : cpf.toCharArray()){
//                for (char permitido : caracteresPermitidos){
//                    if (caracter == permitido){
//                        numIgual++;
//                    }
//                }
//            }
//            return numIgual == cpf.length();
//        }
//    }

    // validar cpf
    public boolean validarCpf(String cpf){
        return cpf.matches("[0-9]{11}");
    }

//
//    // validar cnpj
//    public boolean validarCnpj(String cnpj){
//        if (cnpj.length() != 14){
//            return false;
//        }else{
//            // numero de caracters iguais ( tem que ter a mesma quantidade que o tamanho do cnpj)
//            int numIgual = 0;
//            char[] caracteresPermitidos = {'1', '2', '3', '4', '5', '6', '7', '8', '9', '0', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
//
//            for (char caracter : cnpj.toCharArray()){
//                for (char permitido : caracteresPermitidos){
//                    if (caracter == permitido){
//                        numIgual++;
//                    }
//                }
//            }
//            return numIgual == cnpj.length();
//        }
//    }

    // validar cnpj
    public boolean validarCnpj(String cnpj){
        return cnpj.matches("[0-9]{14}");
    }

//
//
//    // validar cep
//    public boolean validarCep(String cep){
//        if (cep.length() != 8){
//            return false;
//        }else{
//            // numero de caracters iguais ( tem que ter a mesma quantidade que o tamanho do cnpj)
//            int numIgual = 0;
//            char[] caracteresPermitidos = {'1', '2', '3', '4', '5', '6', '7', '8', '9', '0'};
//
//            for (char caracter : cep.toCharArray()){
//                for (char permitido : caracteresPermitidos){
//                    if (caracter == permitido){
//                        numIgual++;
//                    }
//                }
//            }
//            return numIgual == cep.length();
//        }
//    }

    // validar cep
    public boolean validarCep(String cep){
        return cep.matches("[0-9]{8}");
    }

//
//
//    // nome so podem ter letras
//    public boolean validarNome(String nome){
//        nome = nome.toLowerCase();
//        // numero para ver se é igual
//        int numerosIguais = 0;
//
//        // caracteres permitidos
//        char[] caracteres = {'a','b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
//        // caracteres do nome
//        for (char caracterNome : nome.toCharArray()){
//            for (char permitido : caracteres){
//                if (caracterNome == permitido){
//                    numerosIguais++;
//                }
//            }
//        }
//        return numerosIguais == nome.length();
//    }


    // validar nome
    public boolean validarNome(String nome){
        return nome.matches("[A-Za-z ]+");
    }

    // validar nome do produto
    public boolean validarNomeProduto(String nomeProduto){
        return nomeProduto.matches("[A-Za-zÀ-ÖØ-öø-ÿ0-9 .-]+");
    }


//    // verificar se é nulo
//    public boolean isNull(Object valor){
//        return valor == null;
//    }

    // vendo se a senha tem 8 digitos
    // e se a senha é forte ( tem letra maiuscula e caracter especial )

    public  boolean validarSenha(String senha){
        if (senha.length() != 8){
            return false;
        }else {
            return senha.matches(".*[A-Z].*") && senha.matches(".*[^a-zA-Z0-9].*");
        }
    }

    // verfifcar se a quantidade é maior que zero
    public boolean validarQuantidade(int quantidade){
        return quantidade > 0;
    }

    // verfifcar se o preço é maior que zero
    public boolean validarPreco(double preco){
        return preco > 0;
    }

    // validar data de validade
    public boolean validarValidade(LocalDate validade) {
        return validade.isAfter(LocalDate.now());
    }

    // email
    public boolean validarEmail(String email){
        return email.matches("[a-z0-9_.]+@[a-z]+\\.[a-z]+");
    }

    // datas de cadastro de produtos
    public boolean validarDatas1Ano(LocalDate data){
        LocalDate hoje = LocalDate.now();
        LocalDate minimo = hoje.minusMonths(6);
        LocalDate maximo = hoje.plusMonths(6);
        return !data.isBefore(minimo) && !data.isAfter(maximo);
    }
    // =========== VALIDAR ===========
}
