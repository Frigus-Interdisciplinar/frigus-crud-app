package com.example.servlet.Dao;

import com.example.servlet.Model.*;
import com.example.servlet.Util.Conexao;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;

public class ListaComprasDAO {
    // ================= USUARIO =================
    //========== INSERIR LISTA DE COMPRA PARA O USUARIO ==========
    public int inserirListaComprasUsuario(ListaCompras listaCompras){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();// abrindo a conexao com o banco

        try{
            String comandoSql = "insert into listaCompras (idUsuario, dtLista) values(?,?);";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql, Statement.RETURN_GENERATED_KEYS);
            // setando os valores para a operação
            pstmt.setString(1, listaCompras.getUsuario().getCpf());
            pstmt.setObject(2, listaCompras.getDtLista());
            // verificando se deu foi possivel executar
            if (pstmt.executeUpdate() > 0 ){
                ResultSet chaves = pstmt.getGeneratedKeys();// adicionando a chave criada pelo banco
                if (chaves.next()) listaCompras.setIdLista(chaves.getInt(1));
                return 1;// deu certo
            }
            else{
                return 0;// não foi possivel!!
            }

        }catch (SQLException sqle){
            sqle.printStackTrace();
            return -1;// deu erro!!
        }
        finally {
            conexao.desconectar(conn);// fechando a conexao com o banco
        }
    }//========== INSERIR LISTA DE COMPRA PARA O USUARIO ==========



    //========== ALTERAR LISTA DE COMPRA PARA O USUARIO ==========
    public int alterarListaComprasUsuario(ListaCompras listaCompras){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();// abrindo a conexao com o banco

        try{
            String comandoSql = "update listaCompras set idUsuario = ?, dtLista = ? where id = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // setando os valores para a operação
            pstmt.setString(1, listaCompras.getUsuario().getCpf());
            pstmt.setObject(2, listaCompras.getDtLista());
            pstmt.setInt(3, listaCompras.getIdLista());
            // verificando se deu foi possivel executar
            if (pstmt.executeUpdate() > 0 ){
                return 1;// deu certo
            }
            else{
                return 0;// não foi possivel!!
            }

        }catch (SQLException sqle){
            sqle.printStackTrace();
            return -1;// deu erro!!
        }
        finally {
            conexao.desconectar(conn);// fechando a conexao com o banco
        }
    }//========== ALTERAR LISTA DE COMPRA PARA O USUARIO ==========

    // ================= USUARIO =================






    // ================= EMPRESA =================
    //========== INSERIR LISTA DE COMPRA PARA A EMPRESA ==========
    public int inserirListaComprasEmpresa(ListaCompras listaCompras){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();// abrindo a conexao com o banco

        try{
            String comandoSql = "insert into listaCompras (idEmpresa, dtLista) values(?,?);";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // setando os valores para a operação
            pstmt.setString(1, listaCompras.getEmpresa().getCnpj());
            pstmt.setObject(2, listaCompras.getDtLista());
            // verificando se deu foi possivel executar
            if (pstmt.executeUpdate() > 0 ){
                return 1;// deu certo
            }
            else{
                return 0;// não foi possivel!!
            }

        }catch (SQLException sqle){
            sqle.printStackTrace();
            return -1;// deu erro!!
        }
        finally {
            conexao.desconectar(conn);// fechando a conexao com o banco
        }
    }//========== INSERIR LISTA DE COMPRA PARA A EMPRESA ==========



    //========== ALTERAR LISTA DE COMPRA PARA A EMPRESA ==========
    public int alterarListaComprasEmpresa(ListaCompras listaCompras){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();// abrindo a conexao com o banco

        try{
            String comandoSql = "update listaCompras set idEmpresa = ?, dtLista = ? where id = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // setando os valores para a operação
            pstmt.setString(1, listaCompras.getEmpresa().getCnpj());
            pstmt.setObject(2, listaCompras.getDtLista());
            pstmt.setInt(3, listaCompras.getIdLista());
            // verificando se deu foi possivel executar
            if (pstmt.executeUpdate() > 0 ){
                return 1;// deu certo
            }
            else{
                return 0;// não foi possivel!!
            }

        }catch (SQLException sqle){
            sqle.printStackTrace();
            return -1;// deu erro!!
        }
        finally {
            conexao.desconectar(conn);// fechando a conexao com o banco
        }
    }//========== ALTERAR LISTA DE COMPRA PARA A EMPRESA ==========



    // ================= EMPRESA =================


    // ================= UNIVERSAL =================
    // ================= DELETAR ===================

    public int deletarListaCompras(ListaCompras listaCompras) {
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();// abrindo a conexao com o banco

        try {
            String comandoSql = "delete from listaCompras where id = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // setando os valores para a operação;
            pstmt.setInt(1, listaCompras.getIdLista());
            // verificando se deu foi possivel executar
            if (pstmt.executeUpdate() > 0) {
                return 1;// deu certo
            } else {
                return 0;// não foi possivel!!
            }

        } catch (SQLException sqle) {
            sqle.printStackTrace();
            return -1;// deu erro!!
        } finally {
            conexao.desconectar(conn);// fechando a conexao com o banco
        }
    }
    // ================= DELETAR ===================
    // ================= UNIVERSAL =================


    // ========== BUSCAR LISTA DE COMPRAS PELA DATA E PELO USUARIO==========
    public ProdutoLista buscarListaPelaDataUsuario(ListaCompras listaCompras){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();// abrindo a conexao com o banco

        ResultSet rset = null;
        ProdutoLista produtoLista = null;

        try{
            String comandoSql = "select pl.idProdutos,pl.quantidade, lc.dtLista from produtoLista pl join listaCompras lc on lc.id = pl.idListaCompras where lc.dtLista = ? and lc.idUsuario = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // setando o valor da data
            pstmt.setObject(1, listaCompras.getDtLista());
            pstmt.setString(2, listaCompras.getUsuario().getCpf());
            rset = pstmt.executeQuery();

            if(rset.next()){
                // criando o objeto produto
                Produto produto = new Produto();
                produto.setIdProdutos(rset.getInt("idProdutos"));

                // criando o objeto usuario
                Usuario usuario = new Usuario();
                usuario.setCpf(rset.getString("cpf"));

                // criando o objeto lista de compras
                listaCompras = new ListaCompras();
                listaCompras.setDtLista(rset.getObject("dtLista", LocalDate.class));
                listaCompras.setUsuario(usuario);

                // criando o objeto produto na lista
                produtoLista = new ProdutoLista();
                produtoLista.setProduto(produto);
                produtoLista.setQuantidade(rset.getInt("quantidade"));
                produtoLista.setListaCompras(listaCompras);
            }

        }catch (SQLException sqle){
            sqle.printStackTrace();
        }finally {
            conexao.desconectar(conn);// fechando a conexao com o banco
        }
        return produtoLista;
    }// ========== BUSCAR LISTA DE COMPRAS PELA DATA E PELO USUARIO==========




    // ========== BUSCAR LISTA DE COMPRAS PELA DATA E PELA EMPRESA ==========
    public ProdutoLista buscarListaPelaDataEmpresa(ListaCompras listaCompras){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();// abrindo a conexao com o banco

        ResultSet rset = null;
        ProdutoLista produtoLista = null;

        try{
            String comandoSql = "select pl.idProdutos,pl.quantidade, lc.dtLista from produtoLista pl join listaCompras lc on lc.id = pl.idListaCompras where lc.dtLista = ? and lc.idEmpresa = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // setando o valor da data
            pstmt.setObject(1, listaCompras.getDtLista());
            pstmt.setString(2, listaCompras.getEmpresa().getCnpj());
            rset = pstmt.executeQuery();

            if(rset.next()){
                // criando o objeto produto
                Produto produto = new Produto();
                produto.setIdProdutos(rset.getInt("idProdutos"));

                // criando o objeto usuario
                Empresa empresa = new Empresa();
                empresa.setCnpj(rset.getString("cnpj"));

                // criando o objeto lista de compras
                listaCompras = new ListaCompras();
                listaCompras.setDtLista(rset.getObject("dtLista", LocalDate.class));
                listaCompras.setEmpresa(empresa);

                // criando o objeto produto na lista
                produtoLista = new ProdutoLista();
                produtoLista.setProduto(produto);
                produtoLista.setQuantidade(rset.getInt("quantidade"));
                produtoLista.setListaCompras(listaCompras);
            }

        }catch (SQLException sqle){
            sqle.printStackTrace();
        }finally {
            conexao.desconectar(conn);// fechando a conexao com o banco
        }
        return produtoLista;
    }// ========== BUSCAR LISTA DE COMPRAS PELA DATA E PELA EMPRESA==========
}
