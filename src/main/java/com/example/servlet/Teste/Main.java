package com.example.servlet.Teste;

import com.example.servlet.Dao.ProdutoDAO;
import com.example.servlet.Dao.ProdutosGeladeiraDAO;
import com.example.servlet.Model.Geladeira;
import com.example.servlet.Model.Produto;
import com.example.servlet.Model.ProdutosGeladeira;
import com.example.servlet.Util.Enum.Categoria;
import com.example.servlet.Util.Enum.StatusProduto;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        Produto prod = new Produto(
                "Batata inglesa",
                "tuberculo",
                6.99,
                Categoria.TUBERCULOS,
                "in natura",
                "KG");

        Produto prodaluterado = new Produto(
                "Batata frita",
                "tuberculo que passa por varios processo de industrialização",
                6.99,
                Categoria.TUBERCULOS,
                "in natura",
                "KG");
        ProdutoDAO produtoDAO = new ProdutoDAO();
        System.out.println(produtoDAO.alterarProduto(prod, prodaluterado));
        //System.out.println(produtoDAO.inserirProduto(prod));
//        List<Produto> todosOsProdutos = produtoDAO.buscarTodosprodutos();
//        for(Produto produto: todosOsProdutos){
//            System.out.println(produto);
//        }
        //System.out.println(produtoDAO.buscarprodutoPorId(42));
//        int criarProduto = produtoDAO.inserirProduto(prod);
//        System.out.println(criarProduto);

////        Geladeira g = new Geladeira();
////        g.setIdGeladeira(1);
//
//
//
////        ProdutosGeladeira pg = new ProdutosGeladeira(
////                1,
////                g,
////                prod,
////                LocalDate.of(2026, 9, 14),
////                "proximo da validade",
////                134
////        );
//
//        System.out.println(produtoDAO.inserirProduto(prod));
//        produtoDAO.buscarproduto(prod);
//        produtoDAO.buscarproduto(prod);
//
//        ProdutosGeladeiraDAO pgdao = new ProdutosGeladeiraDAO();
////
////        pgdao.inserirProdutoGeladeira(pg);
////        System.out.println(pgdao.inserirProdutoGeladeira(pg));
        //StatusProduto.values();
    }
}
