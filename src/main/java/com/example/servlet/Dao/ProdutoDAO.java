package com.example.servlet.Dao;


import com.example.servlet.Model.Produto;
import com.example.servlet.Model.Usuario;
import com.example.servlet.Util.Conexao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProdutoDAO {
    // ==============INSERIR===============
    public int inserirProduto(Produto produto) {
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();

        try {
            // insere as informações do produto
            String comandoSql = "insert into produtos(nome, descricao, preco, categoria, marca, unidademedida) values (?,?,?,?,?,?);";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);

            // definindo parâmetros da instrução
            pstmt.setString(1, produto.getNome());
            pstmt.setString(2, produto.getDescricao());
            pstmt.setDouble(3, produto.getPreco());
            pstmt.setString(4, produto.getCategoria());
            pstmt.setString(5, produto.getMarca());
            pstmt.setString(6, produto.getUnidadeMedida());

            if (pstmt.executeUpdate() > 0) {
                return 1; // verifica se deu certo
            } else {
                return 0; // não foi possivel executar
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            return -1;
        } finally {
            conexao.desconectar(conn); // desconectar
        }
    }// ==============INSERIR===============o

    // ==============ALTERAR===============
    public int alterarProduto(Produto produto) {
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();

        try {
            // altera as informações do produto
            String comandoSql = "update produtos set nome = ?, descricao = ?, preco = ?, categoria = ?, marca = ?, unidademedida = ? where id = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);

            // definido os parâmetros da instrução
            pstmt.setString(1, produto.getNome());
            pstmt.setString(2, produto.getDescricao());
            pstmt.setDouble(3, produto.getPreco());
            pstmt.setString(4, produto.getCategoria());
            pstmt.setString(5, produto.getMarca());
            pstmt.setString(6, produto.getUnidadeMedida());
            pstmt.setInt(7, produto.getIdProdutos());

            if (pstmt.executeUpdate() > 0) {
                return 1; // verifica se deu certo
            } else {
                return 0; // não foi possivel executar
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            return -1;
        } finally {
            conexao.desconectar(conn); // desconectar
        }
    }// ==============ALTERAR===============

    // ==============DELETAR===============
    public int removerProduto(Produto produto) {
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();

        try {
            // altera as informações do produto
            String comandoSql = "delete from produtos where id = ? ";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);

            // definido os parâmetros da instrução
            pstmt.setInt(1, produto.getIdProdutos());

            if (pstmt.executeUpdate() > 0) {
                return 1; // verifica se alterou o produduto
            } else {
                return 0; // não foi possivel alterar
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            return -1;
        } finally {
            conexao.desconectar(conn); // desconectar
        }
    } // ==============DELETAR===============






    public List<Produto> buscarproduto(Produto produto){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();// abrindo a conexao com o banco

        ResultSet rset = null;
        List<Produto> usuario = new ArrayList<>();
        try{
            String comandoSql = "select * from produtos where id = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // setando os valores para a execução
            pstmt.setInt(1, produto.getIdProdutos());
            rset = pstmt.executeQuery();

            if (rset.next()) {
                produto = new Produto(
                        rset.getInt("idProduto"),
                        rset.getString("nome"),
                        rset.getString("descricao"),
                        rset.getDouble("preco"),
                        rset.getString("categoria"),
                        rset.getString("marca"),
                        rset.getString("unidademedida")
                );
            }
        }catch (SQLException sqle){
            sqle.printStackTrace();
        }
        finally {
            conexao.desconectar(conn); // fechando a conexao com o banco
        }
        return usuario;
    }
}

