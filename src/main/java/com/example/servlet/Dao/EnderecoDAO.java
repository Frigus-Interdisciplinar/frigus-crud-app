package com.example.servlet.Dao;

import com.example.servlet.Model.Empresa;
import com.example.servlet.Model.Endereco;
import com.example.servlet.Model.Frigorificos;
import com.example.servlet.Util.Conexao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EnderecoDAO {

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
