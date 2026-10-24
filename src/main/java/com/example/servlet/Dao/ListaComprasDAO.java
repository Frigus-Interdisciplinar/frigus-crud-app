package com.example.servlet.Dao;

import com.example.servlet.Model.*;
import com.example.servlet.Util.Conexao;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ListaComprasDAO {
    //========== INSERIR LISTA DE COMPRA ==========
    public int inserirListaCompras(ListaCompras listaCompras){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();// abrindo a conexao com o banco

        try{
            String comandoSql = "insert into listaCompras (idUsuario, idEmpresa, dtLista) values (?,?,?);";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql, Statement.RETURN_GENERATED_KEYS);
            // setando os valores para a operação
            // o dono é o usuario ou a empresa, o outro vai como NULL
            if (listaCompras.getUsuario() != null){
                pstmt.setString(1, listaCompras.getUsuario().getCpf());
                pstmt.setNull(2, Types.VARCHAR);
            }
            else {
                pstmt.setNull(1, Types.VARCHAR);
                pstmt.setString(2, listaCompras.getEmpresa().getCnpj());
            }
            pstmt.setObject(3, listaCompras.getDtLista());
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
    }//========== INSERIR LISTA DE COMPRA ==========


    //========== ALTERAR LISTA DE COMPRA ==========
    public int alterarListaCompras(ListaCompras listaComprasAntiga, ListaCompras listaComprasNova){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();// abrindo a conexao com o banco

        try{
            String comandoSql = "update listaCompras set idUsuario = ?, idEmpresa = ?, dtLista = ? where id = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // setando os valores para a operação
            // o dono é o usuario ou a empresa, o outro vai como NULL
            if (listaComprasNova.getUsuario() != null){
                pstmt.setString(1, listaComprasNova.getUsuario().getCpf());
                pstmt.setNull(2, Types.VARCHAR);
            }
            else {
                pstmt.setNull(1, Types.VARCHAR);
                pstmt.setString(2, listaComprasNova.getEmpresa().getCnpj());
            }
            pstmt.setObject(3, listaComprasNova.getDtLista());
            pstmt.setInt(4, listaComprasAntiga.getIdLista());
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
    }//========== ALTERAR LISTA DE COMPRA ==========


    // ============== BUSCAR LISTA POR ID ===============
    public ListaCompras buscarListaComprasPorId(int idListaCompra){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();// abrindo a conexao com o banco

        ListaCompras compra = null;
        ResultSet rset = null;
        try {
            String comandoSql = "select * from listaCompras where id = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // setando os valores para a operação
            pstmt.setInt(1, idListaCompra);
            // usando o result set para pegar as informações
            rset = pstmt.executeQuery();
            if (rset.next()){
                compra = new ListaCompras();
                compra.setIdLista(rset.getInt("id"));
                compra.setDtLista(rset.getObject("dtLista", LocalDate.class));

                // criando o dono: usuario ou empresa
                if (rset.getString("idUsuario") != null){
                    Usuario usuario = new Usuario();
                    usuario.setCpf(rset.getString("idUsuario"));
                    compra.setUsuario(usuario);
                }
                else {
                    Empresa empresa = new Empresa();
                    empresa.setCnpj(rset.getString("idEmpresa"));
                    compra.setEmpresa(empresa);
                }
            }
        }catch (SQLException sqle){
            sqle.printStackTrace();
        }finally {
            conexao.desconectar(conn);// fechando a conexao com o banco
        }
        return compra;
    }
    // ============== BUSCAR LISTA POR ID ===============


    // ============== BUSCAR TODAS AS LISTAS ( COM O NOME DO DONO ) ===============
    public List<ListaCompras> buscarTodasListaCompras(){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();// abrindo a conexao com o banco

        List<ListaCompras> lista = new ArrayList<>();
        ResultSet rset = null;
        try {
            String comandoSql = "select lc.*, u.nome as nomeUsuario, u.sobrenome, e.nome as nomeEmpresa " +
                    "from listaCompras lc " +
                    "left join usuario u on u.cpf = lc.idUsuario " +
                    "left join empresa e on e.cnpj = lc.idEmpresa " +
                    "order by lc.id;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // usando o result set para pegar as informações
            rset = pstmt.executeQuery();
            while (rset.next()){
                ListaCompras compra = new ListaCompras();
                compra.setIdLista(rset.getInt("id"));
                compra.setDtLista(rset.getObject("dtLista", LocalDate.class));

                // criando o dono: usuario ou empresa
                if (rset.getString("idUsuario") != null){
                    Usuario usuario = new Usuario();
                    usuario.setCpf(rset.getString("idUsuario"));
                    usuario.setNome(rset.getString("nomeUsuario"));
                    usuario.setSobrenome(rset.getString("sobrenome"));
                    compra.setUsuario(usuario);
                }
                else {
                    Empresa empresa = new Empresa();
                    empresa.setCnpj(rset.getString("idEmpresa"));
                    empresa.setNome(rset.getString("nomeEmpresa"));
                    compra.setEmpresa(empresa);
                }
                lista.add(compra);
            }
        }catch (SQLException sqle){
            sqle.printStackTrace();
        }finally {
            conexao.desconectar(conn);// fechando a conexao com o banco
        }
        return lista;
    }
    // ============== BUSCAR TODAS AS LISTAS ( COM O NOME DO DONO ) ===============


    // ============== TOTAL DE LISTAS ===============
    public int totalListasCompras(){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();// abrindo a conexao com o banco

        ResultSet rset = null;
        int total = 0;
        try {
            String comandoSql = "select count(*) as total from listaCompras;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            rset = pstmt.executeQuery();
            if (rset.next()) total = rset.getInt("total");
        }catch (SQLException sqle){
            sqle.printStackTrace();
        }finally {
            conexao.desconectar(conn);// fechando a conexao com o banco
        }
        return total;
    }
    // ============== TOTAL DE LISTAS ===============


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
}
