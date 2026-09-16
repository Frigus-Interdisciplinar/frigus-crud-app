package com.example.servlet.Teste;

import com.example.servlet.Dao.ProdutoDAO;
import com.example.servlet.Dao.ProdutosGeladeiraDAO;
import com.example.servlet.Model.Geladeira;
import com.example.servlet.Model.Produto;
import com.example.servlet.Model.ProdutosGeladeira;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {

        Produto prod = new Produto(
                "arroz",
                "é um cereal",
                23.3,
                "Aminoacido",
                "Friboi",
                "KG");

        ProdutoDAO produtoDAO = new ProdutoDAO();

//        Geladeira g = new Geladeira();
//        g.setIdGeladeira(1);



//        ProdutosGeladeira pg = new ProdutosGeladeira(
//                1,
//                g,
//                prod,
//                LocalDate.of(2026, 9, 14),
//                "proximo da validade",
//                134
//        );

        System.out.println(produtoDAO.inserirProduto(prod));
        produtoDAO.buscarproduto(prod);
        //produtoDAO.buscarproduto()

//        ProdutosGeladeiraDAO pgdao = new ProdutosGeladeiraDAO();
//
//        pgdao.inserirProdutoGeladeira(pg);
//        System.out.println(pgdao.inserirProdutoGeladeira(pg));
    }
}
