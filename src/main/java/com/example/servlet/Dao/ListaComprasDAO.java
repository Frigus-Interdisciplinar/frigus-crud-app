package com.example.servlet.Dao;

import com.example.servlet.Model.*;
import com.example.servlet.Util.Conexao;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ListaComprasDAO {
    // ================= USUARIO =================
    //========== ALTERAR LISTA DE COMPRA PARA O USUARIO ==========
    public int alterarListaComprasUsuario(ListaCompras listaComprasAntiga, ListaCompras listaComprasNova){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();// abrindo a conexao com o banco

        try{
            String comandoSql = "update listaCompras set idUsuario = ?, dtLista = ? where id = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // setando os valores para a operação
            pstmt.setString(1, listaComprasNova.getUsuario().getCpf());
            pstmt.setObject(2, listaComprasNova.getDtLista());
            pstmt.setInt(3, listaComprasAntiga.getIdLista());
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


    // ========== BUSCAR LISTA DE COMPRAS PELA DATA E PELO USUARIO==========
    public List<ProdutoLista> buscarListaPelaDataUsuario(String cpf, LocalDate dtValidade){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();// abrindo a conexao com o banco

        ResultSet rset = null;
        List<ProdutoLista> lista = new ArrayList<>();

        try{
            String comandoSql = "select pl.id as idProdutoLista, pl.idProduto,pl.quantidade, lc.id as idListaCompras, lc.idUsuario as cpf, lc.dtLista from ItemLista pl join listaCompras lc on lc.id = pl.idListaCompras where lc.dtLista = ? and lc.idUsuario = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // setando o valor da data
            pstmt.setObject(1, dtValidade);
            pstmt.setString(2, cpf);
            rset = pstmt.executeQuery();

            while(rset.next()){
                // criando o objeto produto
                Produto produto = new Produto();
                produto.setIdProdutos(rset.getInt("idProduto"));

                // criando o objeto usuario
                Usuario usuario = new Usuario();
                usuario.setCpf(rset.getString("cpf"));

                // criando o objeto lista de compras
                ListaCompras listaCompras = new ListaCompras();
                listaCompras.setIdLista(rset.getInt("idListaCompras"));
                listaCompras.setDtLista(rset.getObject("dtLista", LocalDate.class));
                listaCompras.setUsuario(usuario);

                // criando o objeto produto na lista
                ProdutoLista produtoLista = new ProdutoLista(
                        rset.getInt("IdProdutoLista"),
                        listaCompras,
                        produto,
                        rset.getInt("quantidade")
                );
                lista.add(produtoLista);
            }

        }catch (SQLException sqle){
            sqle.printStackTrace();
        }finally {
            conexao.desconectar(conn);// fechando a conexao com o banco
        }
        return lista;
    }// ========== BUSCAR LISTA DE COMPRAS PELA DATA E PELO USUARIO==========


    // ============== BUSCAR LISTA USUARIO POR ID ===============
    public ListaCompras buscarCompraUsuarioPorId(String cpf, int idListaCompra){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();// abrindo a conexao com o banco

        ListaCompras compra = null;
        ResultSet rset = null;
        try {
            String comandoSql = "select * from listaCompras where idUsuario = ? and id = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // setando os valores para a operação
            pstmt.setString(1, cpf);
            pstmt.setInt(2, idListaCompra);
            // usando o result set para pegar as informações
            rset = pstmt.executeQuery();
            if (rset.next()){

                // criando o usuario
                Usuario usuario = new Usuario();
                usuario.setCpf(cpf);
                compra = new ListaCompras(
                        rset.getInt("id"),
                        usuario,
                        rset.getObject("dtLista", LocalDate.class)
                );
            }
        }catch (SQLException sqle){
            sqle.printStackTrace();
        }finally {
            conexao.desconectar(conn);// fechando a conexao com o banco
        }
        return compra;
    }
    // ============== BUSCAR LISTA USUARIO POR ID ===============


    // ============== BUSCAR TODAS AS LISTAS DO USUARIO ===============
    public List<ListaCompras> buscarTodasComprasUsuario(String cpf){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();// abrindo a conexao com o banco

        List<ListaCompras> lista = new ArrayList<>();
        ResultSet rset = null;
        try {
            String comandoSql = "select * from listaCompras where idUsuario = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // setando os valores para a operação
            pstmt.setString(1, cpf);
            // usando o result set para pegar as informações
            rset = pstmt.executeQuery();
            while (rset.next()){

                // criando o usuario
                Usuario usuario = new Usuario();
                usuario.setCpf(cpf);
                ListaCompras compra = new ListaCompras(
                        rset.getInt("id"),
                        usuario,
                        rset.getObject("dtLista", LocalDate.class)
                );
                lista.add(compra);
            }
        }catch (SQLException sqle){
            sqle.printStackTrace();
        }finally {
            conexao.desconectar(conn);// fechando a conexao com o banco
        }
        return lista;
    }
    // ============== BUSCAR TODAS AS LISTAS DO USUARIO ===============

    // ================= USUARIO =================






    // ================= EMPRESA =================
    //========== ALTERAR LISTA DE COMPRA PARA A EMPRESA ==========
    public int alterarListaComprasEmpresa(ListaCompras listaComprasAntiga, ListaCompras listaComprasNova){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();// abrindo a conexao com o banco

        try{
            String comandoSql = "update listaCompras set idEmpresa = ?, dtLista = ? where id = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // setando os valores para a operação
            pstmt.setString(1, listaComprasNova.getEmpresa().getCnpj());
            pstmt.setObject(2, listaComprasNova.getDtLista());
            pstmt.setInt(3, listaComprasAntiga.getIdLista());
            // verificando se foi possivel executar
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


    // ========== BUSCAR LISTA DE COMPRAS PELA DATA E PELA EMPRESA ==========
    public List<ProdutoLista> buscarListaPelaDataEmpresa(String cnpj, LocalDate dtValideade){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();// abrindo a conexao com o banco

        ResultSet rset = null;
        List<ProdutoLista> lista = new ArrayList<>();
        ProdutoLista produtoLista = null;

        try{
            String comandoSql = "select pl.id as idProdutoLista, pl.idProduto,pl.quantidade, lc.id as idListaCompras, lc.idEmpresa as cnpj, lc.dtLista from ItemLista pl join listaCompras lc on lc.id = pl.idListaCompras where lc.dtLista = ? and lc.idEmpresa = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // setando o valor da data
            pstmt.setObject(1, dtValideade);
            pstmt.setString(2, cnpj);
            rset = pstmt.executeQuery();

            while(rset.next()){
                // criando o objeto produto
                Produto produto = new Produto();
                produto.setIdProdutos(rset.getInt("idProduto"));

                // criando o objeto usuario
                Empresa empresa = new Empresa();
                empresa.setCnpj(rset.getString("cnpj"));

                // criando o objeto lista de compras
                ListaCompras listaCompras = new ListaCompras();
                listaCompras.setIdLista(rset.getInt("idListaCompras"));
                listaCompras.setDtLista(rset.getObject("dtLista", LocalDate.class));
                listaCompras.setEmpresa(empresa);

                // criando o objeto produto na lista
                produtoLista = new ProdutoLista();
                produtoLista.setIdProdutoLista(rset.getInt("idProdutoLista"));
                produtoLista.setProduto(produto);
                produtoLista.setQuantidade(rset.getInt("quantidade"));
                produtoLista.setListaCompras(listaCompras);
                lista.add(produtoLista);
            }

        }catch (SQLException sqle){
            sqle.printStackTrace();
        }finally {
            conexao.desconectar(conn);// fechando a conexao com o banco
        }
        return lista;
    }// ========== BUSCAR LISTA DE COMPRAS PELA DATA E PELA EMPRESA==========


    // ============== BUSCAR LISTA EMPRESA POR ID ===============
    public ListaCompras buscarCompraEmpresaPorId(String cnpj, int idListaCompra){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();// abrindo a conexao com o banco

        ListaCompras compra = null;
        ResultSet rset = null;
        try {
            String comandoSql = "select * from listaCompras where idEmpresa = ? and id = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // setando os valores para a operação
            pstmt.setString(1, cnpj);
            pstmt.setInt(2, idListaCompra);
            // usando o result set para pegar as informações
            rset = pstmt.executeQuery();
            if (rset.next()){

                // criando o usuario
                Empresa empresa = new Empresa();
                empresa.setCnpj(cnpj);
                compra = new ListaCompras(
                        rset.getInt("id"),
                        empresa,
                        rset.getObject("dtLista", LocalDate.class)
                );
            }
        }catch (SQLException sqle){
            sqle.printStackTrace();
        }finally {
            conexao.desconectar(conn);// fechando a conexao com o banco
        }
        return compra;
    }
    // ============== BUSCAR LISTA EMPRESA POR ID ===============


    // ============== BUSCAR TODAS AS LISTAS DA EMPRESA ===============
    public List<ListaCompras> buscarTodasCompraEmpresa(String cnpj){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();// abrindo a conexao com o banco

        List<ListaCompras> lista = new ArrayList<>();
        ResultSet rset = null;
        try {
            String comandoSql = "select * from listaCompras where idEmpresa = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // setando os valores para a operação
            pstmt.setString(1, cnpj);
            // usando o result set para pegar as informações
            rset = pstmt.executeQuery();
            while (rset.next()){

                // criando o usuario
                Empresa empresa = new Empresa();
                empresa.setCnpj(cnpj);
                ListaCompras compra = new ListaCompras(
                        rset.getInt("id"),
                        empresa,
                        rset.getObject("dtLista", LocalDate.class)
                );
                lista.add(compra);
            }
        }catch (SQLException sqle){
            sqle.printStackTrace();
        }finally {
            conexao.desconectar(conn);// fechando a conexao com o banco
        }
        return lista;
    }
    // ============== BUSCAR TODAS AS LISTAS DA EMPRESA ===============


    // ================= EMPRESA =================


    // ================= UNIVERSAL =================
    // ================= DELETAR ===================

    public int deletarListaCompras(ListaCompras listaCompras) {
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();// abrindo a conexao com o banco

        try {
            conn.setAutoCommit(false); // nada é salvo de verdade até o commit

            // guarda os produtos da lista antes de apagar os itens que apontam para eles
            String comandoSqlBusca = "select idProduto from ItemLista where idListaCompras = ?;";
            PreparedStatement pstmtBusca = conn.prepareStatement(comandoSqlBusca);
            pstmtBusca.setInt(1, listaCompras.getIdLista());
            ResultSet rset = pstmtBusca.executeQuery();
            List<Integer> idsProdutos = new ArrayList<>();
            while (rset.next()) idsProdutos.add(rset.getInt("idProduto"));

            String comandoSqlItens = "delete from ItemLista where idListaCompras = ?;";
            PreparedStatement pstmtItens = conn.prepareStatement(comandoSqlItens);
            pstmtItens.setInt(1, listaCompras.getIdLista());
            pstmtItens.executeUpdate();// a lista pode estar vazia, por isso não exige linhas afetadas

            String comandoSqlProduto = "delete from Produto where id = ? ";
            PreparedStatement pstmtProduto = conn.prepareStatement(comandoSqlProduto);
            for (int idProduto : idsProdutos){
                pstmtProduto.setInt(1, idProduto);
                if (pstmtProduto.executeUpdate() <= 0){
                    conn.rollback();
                    return 0;// não foi possivel!!
                }
            }

            String comandoSql = "delete from listaCompras where id = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // setando os valores para a operação;
            pstmt.setInt(1, listaCompras.getIdLista());
            // verificando se deu foi possivel executar
            if (pstmt.executeUpdate() <= 0) {
                conn.rollback();
                return 0;// não foi possivel!!
            }

            conn.commit();
            return 1;// deu certo

        } catch (SQLException sqle) {
            sqle.printStackTrace();
            try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            return -1;// deu erro!!
        } finally {
            conexao.desconectar(conn);// fechando a conexao com o banco
        }
    }
    // ================= DELETAR ===================

    // ================= UNIVERSAL =================
}
