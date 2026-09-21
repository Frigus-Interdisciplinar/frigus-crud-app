package com.example.servlet.Dao;

import com.example.servlet.Model.Empresa;
import com.example.servlet.Model.MarcasParcerias;
import com.example.servlet.Model.Produto;
import com.example.servlet.Util.Conexao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MarcasParceirasDAO {

    // ========== INSERIR ==========
    public int inserirParceira(MarcasParcerias parcerias){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();// abrindo a conexao com o banco

        try{
            String comandoSql = "insert into marcasParceiras (idEmpresa, idProdutos, observacoes) values (?,?,?);";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql, Statement.RETURN_GENERATED_KEYS);
            // setando os valores para a operação
            pstmt.setString(1, parcerias.getEmpresa().getCnpj());
            pstmt.setInt(2, parcerias.getProduto().getIdProdutos());
            pstmt.setString(3, parcerias.getObservacoes());

            // verifica se deu certo
            if(pstmt.executeUpdate() > 0){
                ResultSet chaves =pstmt.getGeneratedKeys();// adicionando a chave criada pelo banco
                if(chaves.next()) parcerias.setIdMarcasParcerias(chaves.getInt(1));
                return 1;// deu certo
            }else{
                return 0;// deu errado!!
            }
        }catch (SQLException sqle){
            sqle.printStackTrace();
            return -1;// deu erro!!
        }finally {
            conexao.desconectar(conn);// fechando a conexao com o banco
        }
    }// ========== INSERIR ==========



    // ========== ALTERAR ==========
    public int alterarParceira(MarcasParcerias parcerias){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();// abrindo a conexao com o banco

        try{
            String comandoSql = "update marcasParceiras set idEmpresa = ?, idProdutos = ?, observacoes = ? where id = ?";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // setando os valores para a operação
            pstmt.setString(1, parcerias.getEmpresa().getCnpj());
            pstmt.setInt(2, parcerias.getProduto().getIdProdutos());
            pstmt.setString(3, parcerias.getObservacoes());
            pstmt.setInt(4, parcerias.getIdMarcasParcerias());

            // verifica se deu certo
            if(pstmt.executeUpdate() > 0){
                return 1;// deu certo
            }else{
                return 0;// deu errado!!
            }
        }catch (SQLException sqle){
            sqle.printStackTrace();
            return -1;// deu erro!!
        }finally {
            conexao.desconectar(conn);// fechando a conexao com o banco
        }
    }// ========== ALTERAR ==========




    // ========== DELETAR ==========
    public int deletarParceira(MarcasParcerias parcerias){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();// abrindo a conexao com o banco

        try{
            String comandoSql = "delete from marcasParceiras where id = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // setando os valores para a operação
            pstmt.setInt(1, parcerias.getIdMarcasParcerias());
            // verifica se deu certo
            if(pstmt.executeUpdate() > 0){
                return 1;// deu certo
            }else{
                return 0;// deu errado!!
            }
        }catch (SQLException sqle){
            sqle.printStackTrace();
            return -1;// deu erro!!
        }finally {
            conexao.desconectar(conn);// fechando a conexao com o banco
        }
    }// ========== DELETAR ==========




    // =========== BUSCAR TODAS AS PARCERIAS COM O CNPJ DA EMPRESA ===========
    public List<MarcasParcerias> buscarParceriasPorEmpresa(MarcasParcerias parcerias){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();// abrindo a conexao com o banco

        ResultSet rset = null;
        List<MarcasParcerias> listaParcerias = new ArrayList<>();

        try{
            String comandoSql = "select p.*, e.nome as Empresa, mp.observacoes from marcasParceiras mp " +
                    "join produtos p on p.id = mp.idProdutos join empresa e on e.cnpj = mp.idEmpresa " +
                    "where e.cnpj = ?";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // setando o valor do cnpj para a operação
            pstmt.setString(1, parcerias.getEmpresa().getCnpj());
            rset = pstmt.executeQuery();

            while (rset.next()){
                // criando o objeto produto
                Produto produto = new Produto();
                produto.setIdProdutos(rset.getInt("idProdutos"));
                // criando o objeto empresa
                Empresa empresa = new Empresa();
                empresa.setCnpj(rset.getString("cnpj"));
                // criando o objeto parcerias
                parcerias = new MarcasParcerias(
                        rset.getInt("id"),
                        empresa,
                        produto,
                        rset.getString("observacoes")
                );
                listaParcerias.add(parcerias);
            }
        }catch(SQLException sqle){
            sqle.printStackTrace();
        }
        finally {
            conexao.desconectar(conn);// fechando a conexao com o banco
        }
        return  listaParcerias;
    }// =========== BUSCAR TODAS AS PARCERIAS COM O CNPJ DA EMPRESA ===========


    // ============== VERIFICAR SE A EMPRESA TEM PARCERIA ==============
    public boolean temParceria(Empresa empresa){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();// abrindo a conexao com o banco

        ResultSet rset = null;

        try{
            String comandoSql = "select 1 from marcasParcerias mp where mp.idEmpresa = ?";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);

            // setando o valor do cnpj
            pstmt.setString(1, empresa.getCnpj());
            rset = pstmt.executeQuery();

            // verifica se tem parcerias ou não
            return rset.next();

        }catch (SQLException sqle){
            throw new RuntimeException(
                    "Erro ao verificar parceria da empresa.", sqle);
        }finally {
            conexao.desconectar(conn);//fechando a conexao com o banco
        }
    }// ============== VERIFICAR SE A EMPRESA TEM PARCERIA ==============
}
