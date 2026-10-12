package com.example.servlet.Dao;

import com.example.servlet.Model.ListaCompras;
import com.example.servlet.Model.Produto;
import com.example.servlet.Model.ProdutoLista;
import com.example.servlet.Model.Usuario;
import com.example.servlet.Util.Conexao;

import java.sql.*;

public class ProdutoListaDAO {

    // ================ INSERIR ================
    public int adicionarProdutoListas(ProdutoLista produtoLista){
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
    public int alterarProdutoListas(ProdutoLista produtoListaAntiga, ProdutoLista produtoListaNova){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();// abrindo a conexao com o banco

        try{
            String comandoSql = "update produtoLista set idListaCompras = ?, idProdutos = ?, quantidade = ? where id = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // setando os valores para a execução
            pstmt.setInt(1, produtoListaNova.getListaCompras().getIdLista());
            pstmt.setInt(2, produtoListaNova.getProduto().getIdProdutos());
            pstmt.setInt(3, produtoListaNova.getQuantidade());
            pstmt.setInt(4, produtoListaAntiga.getIdProdutoLista());

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
    public int deletarProdutoListas(ProdutoLista produtoLista){
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



    // ============ BUSCAR PRODUTO POR ID ============
    public ProdutoLista buscarProdutoNumaLista(int idProdutoLista, int idCompra){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();// abrindo a conexao com o banco

        ResultSet rset = null;
        ProdutoLista produtoLista = null;

        try {
            String comandoSql = "select * from produtoLista where id = ? and idListaCompras = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // sentando os valores dos ids
            pstmt.setInt(1, idProdutoLista);
            pstmt.setInt(2, idCompra);
            rset = pstmt.executeQuery();
            if (rset.next()){
                // criando o objeto lista de compras
                ListaCompras compras = new ListaCompras();
                compras.setIdLista( rset.getInt("idListaCompras"));
                // criando o objeto produto
                Produto produto = new Produto();
                produto.setIdProdutos(rset.getInt("idProdutos"));
                produtoLista = new ProdutoLista(
                        rset.getInt("id"),
                        compras,
                        produto,
                        rset.getInt("quantidade")
                );
            }

        }catch (SQLException sqle){
            sqle.printStackTrace();
        }
        finally {
            conexao.desconectar(conn);// fechando a conexao com o banco
        }
        return produtoLista;
    }
    // ============ BUSCAR PRODUTO POR ID ============


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
