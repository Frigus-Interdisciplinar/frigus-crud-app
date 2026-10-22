package com.example.servlet.Teste;

import com.example.servlet.Dao.*;
import com.example.servlet.Model.*;
import com.example.servlet.Util.Conexao;
import com.example.servlet.Util.Validacao;
import com.example.servlet.Util.Enum.Categoria;
import com.example.servlet.Util.Enum.StatusProduto;
import com.example.servlet.Util.Enum.StatusArmazenamento;
import com.example.servlet.Util.Enum.TipoAssinatura;
import com.example.servlet.Util.Service.ArmazenamentoService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class Main {
    public static void main(String[] args) {

//        // testando a conexao
//        Conexao conexao = new Conexao();
//        Connection conn = conexao.conectar();
//
//        try {
//
//            if (conn != null && !conn.isClosed()) {
//                System.out.println("Conexão realizada com sucesso!");
//            } else {
//                System.out.println("Não foi possível conectar.");
//            }
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//
//        } finally {
//            conexao.desconectar(conn);
//        }

//
//        // criando o objeto produto
        Produto prod = new Produto(
                "Batata inglesa",
                "tuberculo",
                6.99,
                Categoria.TUBERCULOS,
                "in natura",
                "KG");
//
//        // criando o objeto para atualizar o produto
//        Produto prodAlterado = new Produto(
//                "Batata frita",
//                "tuberculo que passa por varios processo de industrialização",
//                6.99,
//                Categoria.TUBERCULOS,
//                "in natura",
//                "KG");
//
//        // instanciando o objeto produto dao para fazer as operações do crud
        //System.out.println(produtoDAO.inserirProduto(prod));
//
//        // criar o objeto para realizar os metodos de alterar e deletar
//        Produto produtoSelecionado = produtoDAO.buscarprodutoPorId(29);
//
//
//        // inserir produto
//        System.out.println(produtoDAO.inserirProduto(prod));
//
//        // alterando produto
//        System.out.println(produtoSelecionado);
//        // realizando a alteração
//        System.out.println(produtoDAO.alterarProduto(produtoSelecionado, prodAlterado));
//        // vendo o produto já alterado
//        Produto produtoAtualizado = produtoDAO.buscarprodutoPorId(34);
//        System.out.println(produtoAtualizado);
//
//        // deletando produto
//        System.out.println(produtoDAO.removerProduto(produtoSelecionado));
//


        // testando o metodo de ver todos os produtos
//        List<Produto> todosOsProdutos = produtoDAO.buscarTodosprodutos();
//        for(Produto produto: todosOsProdutos){
//            System.out.println(produto);
//        }

        // usuario
        Usuario usuario = new Usuario(
                "12345678910",
                "Mauro",
                "silvestre Cavaliere",
                "mauro.silvestre@hotmail.com",
                "123456"
        );

        // instanciando o objeto dao
        //UsuarioDAO usuarioDAO = new UsuarioDAO();

//        // inserir usuario
//        System.out.println(usuarioDAO.adicionarUsuario(usuarioStelefone));;

        // objeto para alterar
        //Usuario usuConsultaAlterar = usuarioDAO.buscarUsuario("35779032289");

        // alterar
//        Usuario usuarioAlterado = new Usuario(
//                "76315414427",
//                "Sofia",
//                "Almeida",
//                "sofia.almeida@gmail.com",
//                "127776",
//                "11987354321"
//        );
//        System.out.println(usuConsultaAlterar);
//        usuarioDAO.alterarUsuario(usuConsultaAlterar,usuarioAlterado);
//        // usuario atualizado
//        Usuario usuAtualizado = usuarioDAO.buscarUsuario("76315414427");
//        System.out.println(usuAtualizado);
//        String email = "tropadozeca.obrabo@gmail.com";
//        System.out.println(usuConsultaAlterar);
//        System.out.println(usuarioDAO.alterarEmailUsuario(usuConsultaAlterar, email));
//        Usuario usuConsultaEmail = usuarioDAO.buscarUsuario("35779032289");
//        System.out.println(usuConsultaEmail);
//
//        // objeto para deletar
//        Usuario usuConsultaDeletar = usuarioDAO.buscarUsuario("76315414427");
//        System.out.println(usuarioDAO.deletarUsuario(usuConsultaDeletar));
//


//
//
//
//        Geladeira g = new Geladeira(
//                "12345867887",
//                12,
//                "Frost free"
//        );
//
//
//
//
//        ProdutosGeladeira ig = new ProdutosGeladeira(
//                1,
//                g,
//                prod,
//                LocalDate.of(2026, 9, 14),
//                "proximo da validade",
//                134
//        );
//
//        System.out.println(produtoDAO.inserirProduto(prod));
//        produtoDAO.buscarproduto(prod);
//        produtoDAO.buscarproduto(prod);
//
//        ProdutosGeladeiraDAO pgdao = new ProdutosGeladeiraDAO();
////
//        pgdao.inserirProdutoGeladeira(ig);
//        System.out.println(pgdao.inserirProdutoGeladeira(ig));
//        StatusProduto.values();
//


        // empresa
//
//        Empresa empresa = new Empresa(
//                "77310109000510",
//                "Dogão de osasco",
//                "melhordogao.top@outlook.com",
//                "7654367",
//                "Melhor lugar para levar a gata num sabado a noite"
//        );
//        Empresa empresaSdescricao = new Empresa(
//                "75410109000510",
//                "Pastelaria do seu osmar",
//                "pasteldobom.top@outlook.com",
//                "7698367"
//        );
//
//        Empresa empresaAtualizada = new Empresa(
//                "75410109000510",
//                "Pastelaria do seu osmar v2",
//                "pasteldobom.top@outlook.com",
//                "7698367",
//                "Top lugares para dar um role no sabado de manhã"
//        );
        // EmpresaDAO empresaDAO = new EmpresaDAO();
//        System.out.println(empresaDAO.cadastrarEmpresa(empresa));
//        System.out.println(empresaDAO.cadastrarEmpresa(empresaSdescricao));

        // Empresa empresaBuscaAlterar = empresaDAO.buscarEmpresa("75410109000510");

//        System.out.println(empresaBuscaAlterar);
//        System.out.println(empresaDAO.alterarEmpresa(empresaBuscaAlterar, empresaAtualizada));
//        Empresa empresaBuscaAtualizada = empresaDAO.buscarEmpresa("75410109000510");
//        System.out.println(empresaBuscaAtualizada);

//        String email = "omelhorpastelda.regiao@gmail.com";
//        System.out.println(empresaDAO.alterarEmailEmpresa(empresaSdescricao, email));
//        Empresa empresaBuscaDeletar = empresaDAO.buscarEmpresa("73758788000173");
//        System.out.println(empresaDAO.excluirEmpresa(empresaBuscaDeletar));


        UsuarioDAO usuarioDAO = new UsuarioDAO();

        //System.out.println(usuarioDAO.adicionarUsuario(usuario));

//        // assinatura
//
        Assinatura assiUsuario = new Assinatura(
                usuario,
                TipoAssinatura.PLUS
        );
        AssinaturaDAO assinaturaDAO = new AssinaturaDAO();
        // System.out.println(assinaturaDAO.inserirAssinaturaUsuario(assiUsuario));
//
//        Assinatura assiUsuarioSdescricao = new Assinatura(
//                usuarioStelefone,
//                TipoAssinatura.FAMILIA
//                //"Assinatura familia para uma grande alcateia"
//        );
//
//        AssinaturaDAO assinaturaDAO = new AssinaturaDAO();
//        //System.out.println(assinaturaDAO.inserirAssinaturaUsuario(assiUsuarioSdescricao));
//
//        UsuarioDAO usuarioDAO = new UsuarioDAO();
//        Assinatura assinaturaConsulta = assinaturaDAO.buscarAssinaturaUsuario("42935404904");
//
//        Usuario usuarioConsulta = usuarioDAO.buscarUsuario("34566778910");
//        Assinatura assiUsuarioAlterado = new Assinatura(
//                usuarioConsulta,
//                TipoAssinatura.PLUS,
//                "Assinatura familia para uma grande alcateia"
//        );
//        //System.out.println(assinaturaDAO.alterarAssinUsuario(assinaturaConsulta, assiUsuarioAlterado));
//
//        //System.out.println(assinaturaDAO.deletarAssinaturaUsuario(assinaturaConsulta));
//
//
//        Empresa empresa = new Empresa(
//                "07310122000510",
//                "Dogão de osasco",
//                "melhordogao.top@outlook.com",
//                "7654367",
//                "Melhor lugar para levar a gata num sabado a noite"
//        );
//        EmpresaDAO empresaDAO = new EmpresaDAO();
//
//
//        Assinatura assiEmpresa = new Assinatura(
//                empresa,
//                TipoAssinatura.COMERCIAL
//
//        );
//        Assinatura assiEmpresaALterada = new Assinatura(
//                empresa,
//                TipoAssinatura.COMERCIAL,
//                "Plano top para o seu restaurante"
//        );
//
//        Assinatura assinaturaEmpreConsulta = assinaturaDAO.buscarAssinaturaEmpresa("42839744000144");
//        System.out.println(assinaturaEmpreConsulta);
//
//        System.out.println("alterar\n" + assinaturaDAO.alterarAssinEmpresa(assinaturaEmpreConsulta,assiEmpresaALterada));
//
//        System.out.println(assinaturaDAO.deletarAssinaturaEmpresa(assinaturaEmpreConsulta));
        //System.out.println(usuarioDAO.adicionarUsuario(usuario));
        Geladeira geladeira = new Geladeira(
                usuario,
                "geladeira de teste"
        );
        //GeladeiraDAO geladeiraDao = new GeladeiraDAO();
        //System.out.println(geladeiraDao.inserirGeladeira(geladeira2));
        //System.out.println(geladeiraDao.inserirGeladeira(geladeira));

//        Geladeira geladeiraConsulta = geladeiraDao.buscarUnicaGeladeira("12345678910", 52);
//        ArmazenamentoService service = new ArmazenamentoService();
//        System.out.println(geladeiraConsulta);
//        System.out.println("total prod: " +ArmazenamentoService.totalProdutosGeladeira(geladeiraConsulta.getIdGeladeira()));
//        ArmazenamentoService.calcularStatusGeladeira(geladeiraConsulta, assiUsuario);
//        System.out.println(geladeiraConsulta);
//        System.out.println(geladeiraDao.atualizarStatus(geladeiraConsulta));


//        Geladeira geladeiraConsulta = geladeiraDao.buscarUnicaGeladeira("12345678910",52);
//        System.out.println(geladeiraConsulta + "\n" + ArmazenamentoService.totalProdutosGeladeira(geladeiraConsulta.getIdGeladeira()));
//        ArmazenamentoService.calcularStatusGeladeira(geladeiraConsulta, assiUsuario);
//        System.out.println(geladeiraDao.atualizarStatus(geladeiraConsulta));
//        System.out.println(geladeiraConsulta);


        //System.out.println(geladeiraDao.buscarTodasGeladeira("12345678910"));


        // produto geladeira

        //System.out.println(produtoDAO.inserirProduto(prod));

        GeladeiraDAO gelaDao = new GeladeiraDAO();

        //System.out.println(gelaDao.inserirGeladeira(geladeira));

//        Produto prodConsulta = produtoDAO.buscarprodutoPorId(51);
//
//        Geladeira gelaConsulta = gelaDao.buscarUnicaGeladeira("12345678910", 51);
//        //System.out.println(gelaConsulta);
//
//        ProdutosGeladeira ig = new ProdutosGeladeira(
//                1,
//                gelaConsulta,
//                prodConsulta,
//                LocalDate.of(2026,9, 20),
//                12,
//                5
//        );
//        ProdutosGeladeiraDAO pgDao = new ProdutosGeladeiraDAO();
//        String coxa = "Coxa de ";
//        ProdutosGeladeira pgConsulta = pgDao.buscarProdutoGeladeiraPorId(77);
//        //System.out.println(pgConsulta);
////        for (ProdutosGeladeira pgn : pgConsulta){
////
////            System.out.println(pgConsulta);
////        }
//
//        Geladeira gelaNova = gelaDao.buscarUnicaGeladeira("78537787671", 4);
//        Produto prodNova = produtoDAO.buscarprodutoPorId(4);
//        ProdutosGeladeira pgNova = new ProdutosGeladeira(
//                4,
//                gelaNova,
//                prodNova,
//                LocalDate.of(2026,9, 20),
//                12,
//                5
//        );
//        System.out.println(pgDao.alterarProdutoGeladeira(pgConsulta, pgNova));
        //System.out.println(pgDao.inserirProdutoGeladeira(ig));

    }
}