package com.example.servlet.Dao;


import com.example.servlet.Model.Produto;
import com.example.servlet.Util.Conexao;
import com.example.servlet.Util.Enum.Categoria;

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
            String comandoSql = "insert into Produto(nome, descricao, preco, categoria, marca, unidademedida) values (?,?,?,?,?,?);";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql, Statement.RETURN_GENERATED_KEYS);

            // definindo parâmetros da instrução
            pstmt.setString(1, produto.getNome());
            pstmt.setString(2, produto.getDescricao());
            pstmt.setDouble(3, produto.getPreco());
            pstmt.setString(4, produto.getCategoria().name());
            pstmt.setString(5, produto.getMarca());
            pstmt.setString(6, produto.getUnidadeMedida());

            if (pstmt.executeUpdate() > 0) {
                ResultSet chaves = pstmt.getGeneratedKeys();// adicionando a chave criada pelo banco
                if (chaves.next()) produto.setIdProdutos(chaves.getInt(1));
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
    public int alterarProduto(Produto produtoAntigo, Produto novoProduto) {
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();

        try {
            // altera as informações do produto
            String comandoSql = "update Produto set nome = ?, descricao = ?, preco = ?, categoria = ?, marca = ?, unidademedida = ? where id = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);

            // definido os parâmetros da instrução
            pstmt.setString(1, novoProduto.getNome());
            pstmt.setString(2, novoProduto.getDescricao());
            pstmt.setDouble(3, novoProduto.getPreco());
            pstmt.setString(4, novoProduto.getCategoria().name());
            pstmt.setString(5, novoProduto.getMarca());
            pstmt.setString(6, novoProduto.getUnidadeMedida());
            pstmt.setInt(7, produtoAntigo.getIdProdutos());

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
            String comandoSql = "delete from Produto where id = ? ";
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




    // =========== BUSCAR UM UNICO PRODUTO ===========
    public Produto buscarprodutoPorId(int idPorduto){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();// abrindo a conexao com o banco

        ResultSet rset = null;
        Produto produto = null;
        try{
            String comandoSql = "select * from Produto where id = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // setando os valores para a execução
            pstmt.setInt(1, idPorduto);
            rset = pstmt.executeQuery();

            if (rset.next()) {
                produto = new Produto(
                        rset.getInt("id"),
                        rset.getString("nome"),
                        rset.getString("descricao"),
                        rset.getDouble("preco"),
                        Categoria.valueOf(rset.getString("categoria")),
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
        return produto;
    }// =========== BUSCAR UM UNICO PRODUTO ===========



    // =========== BUSCAR TODOS OS PRODUTO ===========
    public List<Produto> buscarTodosprodutos(){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();// abrindo a conexao com o banco

        ResultSet rset = null;
        List<Produto> lista = new ArrayList<>();
        Produto produto = null;
        try{
            String comandoSql = "select * from Produto;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            rset = pstmt.executeQuery();

            while (rset.next()) {
                produto = new Produto(
                        rset.getInt("id"),
                        rset.getString("nome"),
                        rset.getString("descricao"),
                        rset.getDouble("preco"),
                        Categoria.valueOf(rset.getString("categoria")),
                        rset.getString("marca"),
                        rset.getString("unidademedida")
                );
                lista.add(produto);
            }
        }catch (SQLException sqle){
            sqle.printStackTrace();
        }
        finally {
            conexao.desconectar(conn); // fechando a conexao com o banco
        }
        return lista;
    }// =========== BUSCAR TODOS OS PRODUTO ===========

}

