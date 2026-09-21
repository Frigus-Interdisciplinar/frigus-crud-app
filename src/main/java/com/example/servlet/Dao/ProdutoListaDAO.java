package com.example.servlet.Dao;

import com.example.servlet.Model.ListaCompras;
import com.example.servlet.Model.Produto;
import com.example.servlet.Model.ProdutoLista;
import com.example.servlet.Model.Usuario;
import com.example.servlet.Util.Conexao;

import java.sql.*;

public class ProdutoListaDAO {

    // ================ INSERIR ================
    public int adicionarProdutoLists(ProdutoLista produtoLista){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();// abrindo a conexao com o banco

        try{
            String comandoSql = "Insert into produtoLista (idListaCompras, idProdutos, quantidade) values(?,?,?);";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql, Statement.RETURN_GENERATED_KEYS);
            // setando os valores para a execução
            pstmt.setInt(1, produtoLista.getListaCompras().getIdLista());
            pstmt.setInt(2, produtoLista.getProduto().getIdProdutos());
            pstmt.setInt(3, produtoLista.getQuantidade());

            if (pstmt.executeUpdate() > 0){
                ResultSet chaves = pstmt.getGeneratedKeys();// adicionando a chave criada pelo banco
                if (chaves.next()) produtoLista.setIdProdutoLista(chaves.getInt(1));
                return 1; // inseriu as informações no banco
            }
            else{
                return 0; // não foi possível adicionar
            }

        }catch (SQLException sqle){
            sqle.printStackTrace();
            return -1;
        }
        finally {
            conexao.desconectar(conn);// fechando a conexao com o banco
        }
    }// ================ INSERIR ================


    // ================ ALTERAR ================
    public int alterarProdutoLists(ProdutoLista produtoLista){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();// abrindo a conexao com o banco

        try{
            String comandoSql = "update produtoLista set idListaCompras = ?, idProdutos = ?, quantidade = ? where id = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // setando os valores para a execução
            pstmt.setInt(1, produtoLista.getListaCompras().getIdLista());
            pstmt.setInt(2, produtoLista.getProduto().getIdProdutos());
            pstmt.setInt(3, produtoLista.getQuantidade());
            pstmt.setInt(4, produtoLista.getIdProdutoLista());

            if (pstmt.executeUpdate() > 0){
                return 1; // inseriu as informações no banco
            }
            else{
                return 0; // não foi possível adicionar
            }

        }catch (SQLException sqle){
            sqle.printStackTrace();
            return -1;
        }
        finally {
            conexao.desconectar(conn);// fechando a conexao com o banco
        }
    }// ================ ALTERAR ================



    // ================ DELETAR ================
    public int deletarProdutoLists(ProdutoLista produtoLista){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();// abrindo a conexao com o banco

        try{
            String comandoSql = "delete from produtoLista where id = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // setando os valores para a execução
            pstmt.setInt(1, produtoLista.getIdProdutoLista());

            if (pstmt.executeUpdate() > 0){
                return 1; // inseriu as informações no banco
            }
            else{
                return 0; // não foi possível adicionar
            }

        }catch (SQLException sqle){
            sqle.printStackTrace();
            return -1;
        }
        finally {
            conexao.desconectar(conn);// fechando a conexao com o banco
        }
    }// ================ DELETAR ================



    // ============== VERIFICAR SE O PRODUTO ESTÁ NA LISTA ==============
    public boolean estaNaLista(Produto produto, ListaCompras lista){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();// abrindo a conexao com o banco

        ResultSet rset = null;
        try{
            String comandoSql = "select 1 from produtoLista where idProdutos = ? and idListaCompras = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // primeiro parâmetro: produto
            pstmt.setInt(1, produto.getIdProdutos());

            // segundo parâmetro: lista
            pstmt.setInt(2, lista.getIdLista());
            rset = pstmt.executeQuery();

            // verifica se tem parcerias ou não
            return rset.next();

        }catch (SQLException sqle){
            throw new RuntimeException("Erro ao verificar esse produto na lista.", sqle);
        }finally {
            conexao.desconectar(conn);//fechando a conexao com o banco
        }
    }// ============== VERIFICAR SE O PRODUTO ESTÁ NA LISTA ==============


}
