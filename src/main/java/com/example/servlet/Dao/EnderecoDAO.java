package com.example.servlet.Dao;

import com.example.servlet.Model.Empresa;
import com.example.servlet.Model.Endereco;
import com.example.servlet.Model.Frigorificos;
import com.example.servlet.Util.Conexao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EnderecoDAO {

    // ============== INSEIR ==============
    public int inserirEndereco(Endereco endereco){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();// abrindo a conexao com o banco

        try{
            String comandoSql = "insert into endereco (rua, bairro, cidade, uf, pais, cep, numero, nomeUnidade) values (?,?,?,?,?,?,?,?);";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql, Statement.RETURN_GENERATED_KEYS);
            // setando os valores para a operação
            pstmt.setString(1, endereco.getRua());
            pstmt.setString(2, endereco.getBairro());
            pstmt.setString(3, endereco.getCidade());
            pstmt.setString(4, endereco.getUf());
            pstmt.setString(5, endereco.getPais());
            pstmt.setString(6, endereco.getCep());
            pstmt.setString(7, endereco.getNumero());
            pstmt.setString(8, endereco.getNomeUnidade());
            // verifica se conseguiu executar o comando e se sim adiciona a chave criada pelo banco
            if(pstmt.executeUpdate() > 0){
                ResultSet chaves = pstmt.getGeneratedKeys();
                if (chaves.next()) endereco.setIdEndereco(chaves.getInt(1));// adicionando a chave criada pelo banco
                return 1;// sim
            }
            else {
                return 0;// não
            }
        }catch (SQLException sqle){
            sqle.printStackTrace();
            return -1;// deu erro!!
        }finally {
            conexao.desconectar(conn);// fechando a conexao com o banco
        }
    }// ============== INSEIR ==============




    // ============== ALTERAR ==============
    public int alterarEndereco(Endereco enderecoAntigo, Endereco enderecoNovo ){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();// abrindo a conexao com o banco

        try{
            String comandoSql = "update endereco set rua = ?, bairro = ?, cidade = ?, uf = ?, pais = ?, cep = ?, numero = ?, nomeUnidade = ? where id = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // setando os valores para a operação
            pstmt.setString(1, enderecoNovo.getRua());
            pstmt.setString(2, enderecoNovo.getBairro());
            pstmt.setString(3, enderecoNovo.getCidade());
            pstmt.setString(4, enderecoNovo.getUf());
            pstmt.setString(5, enderecoNovo.getPais());
            pstmt.setString(6, enderecoNovo.getCep());
            pstmt.setString(7, enderecoNovo.getNumero());
            pstmt.setString(8, enderecoNovo.getNomeUnidade());
            pstmt.setInt(9, enderecoAntigo.getIdEndereco());
            // verifica se conseguiuexecutar o comando
            if(pstmt.executeUpdate() > 0){
                return 1;// sim
            }
            else {
                return 0;// não
            }
        }catch (SQLException sqle){
            sqle.printStackTrace();
            return -1;// deu erro!!
        }finally {
            conexao.desconectar(conn);// fechando a conexao com o banco
        }
    }// ============== ALTERAR ==============





    // ============== DELETAR ==============
    public int deletarEndereco(Endereco endereco){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();// abrindo a conexao com o banco

        try{
            String comandoSql = "delete from endereco where id = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // setando os valores para a operação
            pstmt.setInt(1, endereco.getIdEndereco());
            // verifica se conseguiuexecutar o comando
            if(pstmt.executeUpdate() > 0){
                return 1;// sim
            }
            else {
                return 0;// não
            }
        }catch (SQLException sqle){
            sqle.printStackTrace();
            return -1;// deu erro!!
        }finally {
            conexao.desconectar(conn);// fechando a conexao com o banco
        }
    }// ============== DELETAR ==============



    // ============= BUSCAR TODOS OS ENDEREÇOS USANDO O ID DA EMPRESA ===============
    public List<Endereco> buscarEnderecos(Empresa empresa){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar(); // abre a conexao com o banco

        ResultSet rset = null;
        List<Endereco> lista = new ArrayList<>();

        try{
            String comandoSql = "select distinct en.* " +
            "from Frigorifico f " +
            "join endereco en on en.id = f.idEndereco " +
            "where f.idEmpresa = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // setando o valore para a operação
            pstmt.setString(1, empresa.getCnpj());
            rset = pstmt.executeQuery();

            while (rset.next()){
                // criando o objeto endereco
                Endereco endereco = new Endereco(
                        rset.getInt("id"),
                        rset.getString("rua"),
                        rset.getString("bairro"),
                        rset.getString("cidade"),
                        rset.getString("uf"),
                        rset.getString("pais"),
                        rset.getString("cep"),
                        rset.getString("numero"),
                        rset.getString("nomeUnidade")
                );
                lista.add(endereco);
            }
        }catch (SQLException sqle){
            sqle.printStackTrace();
        }finally {
            conexao.desconectar(conn);// fechando a conexao com o banco
        }
        return lista;
    }// ============= BUSCAR TODOS OS ENDEREÇOS USANDO O ID DA EMPRESA ===============


    // ============= BUSCAR O ENDEREÇO DE UM FRIGORIFICO ===============
    public Endereco buscarEnderecoDeUmFrigorifico(String cnpj, int idFrigorifico){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar(); // abre a conexao com o banco

        ResultSet rset = null;
        Endereco endereco = null;

        try{
            String comandoSql = "select idEndereco " +
                    "from Frigorifico " +
                    "where idEmpresa = ? and id = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // setando o valore para a operação
            pstmt.setString(1, cnpj);
            pstmt.setInt(2, idFrigorifico);
            rset = pstmt.executeQuery();

            if (rset.next()){
                // criando o objeto endereco
                    endereco = new Endereco(
                        rset.getInt("id"),
                        rset.getString("rua"),
                        rset.getString("bairro"),
                        rset.getString("cidade"),
                        rset.getString("uf"),
                        rset.getString("pais"),
                        rset.getString("cep"),
                        rset.getString("numero"),
                        rset.getString("nomeUnidade")
                );
            }
        }catch (SQLException sqle){
            sqle.printStackTrace();
        }finally {
            conexao.desconectar(conn);// fechando a conexao com o banco
        }
        return endereco;
    }// ============= BUSCAR O ENDEREÇO DE UM FRIGORIFICO ===============
}
