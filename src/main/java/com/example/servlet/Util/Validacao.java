package com.example.servlet.Util;

public class Validacao {

    /*
    Formato → Regex

    CPF
    e-mail
    telefone
    nome
    senha
    código/CEP

    Tipo e tamanho → Java/SQL

    preço não pode ser negativo
    quantidade deve ser maior que 0
    texto não pode ultrapassar X caracteres
    ID deve ser inteiro

    Regra de negócio

    CPF precisa ser matematicamente válido
    data de validade não pode ser anterior à data de cadastro
    e-mail não pode estar duplicado
    produto precisa existir antes de ser colocado na geladeira







    CPF	Regex + algoritmo de CPF + UNIQUE
    Nome	Regex + tamanho
    Sobrenome	Regex + tamanho
    E-mail	Regex + tamanho + UNIQUE
    Senha	Regra de complexidade + tamanho
    Telefone	Regex + tamanho
    Produto	Não vazio + tamanho
    Descrição	Tamanho máximo
    Preço	> 0
    Quantidade	> 0
    Data de validade	Data válida + regra de negócio
    */

    // verificar se é nulo
    public boolean isnull(Object valor){
        return valor == null;
    }

    // verfifcar se a quantidade é maior que zero
    public boolean validarQuantidade(int quantidade){
        return quantidade > 0;
    }

    // verfifcar se o preço é maior que zero
    public boolean validarPreco(double preco){
        return preco > 0;
    }

    // validar data de

}
