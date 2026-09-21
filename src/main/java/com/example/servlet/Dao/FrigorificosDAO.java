package com.example.servlet.Dao;

import com.example.servlet.Model.Frigorificos;
import com.example.servlet.Util.Conexao;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FrigorificosDAO {
    // INSERIR
    public int inserirFrigorifico(Frigorificos frigorifico){
        Conexao conexao = new Conexao(); // abrindo a conexão
        Connection conn = conexao.conectar();

        try{
            String comandoSql = "insert into frigorificos (idEmpresa, status, quantidade, endereco, tipo) values (?,?,?,?,?);";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql, Statement.RETURN_GENERATED_KEYS);
            // setando os valores para o comando
            pstmt.setString(1, frigorifico.getEmpresa().getCnpj());
            pstmt.setString(2, frigorifico.getStatus().name());
            pstmt.setInt(3, frigorifico.getQuantidade());
            pstmt.setInt(4, frigorifico.getEndereco().getIdEndereco());
            pstmt.setString(5, frigorifico.getTipo());

            if (pstmt.executeUpdate() > 0){
                ResultSet chaves = pstmt.getGeneratedKeys();// adicionando a chave criada pelo banco
                if(chaves.next()) frigorifico.setIdFrigorifico(chaves.getInt(1));
                return 1;// verifica se deu certo
            }
            else{
                return 0;// não deu certo
            }
        }
        catch (SQLException sqle){
            sqle.printStackTrace();
            return -1;
        }
        finally {
            conexao.desconectar(conn);// fechando a conexão
        }
    }// INSERIR







    //==============ALTERAR=============
    public int alterarFrigorifico(Frigorificos frigorifico){
        Conexao conexao = new Conexao(); // abrindo a conexão
        Connection conn = conexao.conectar();

        try{
            String comandoSql = "update Frigorificos set idEmpresa = ?, status = ?, quantidade = ?, endereco = ?, tipo = ?  where id = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // setando os valores para o comando
            pstmt.setString(1, frigorifico.getEmpresa().getCnpj());
            pstmt.setString(2, frigorifico.getStatus().name());
            pstmt.setInt(3, frigorifico.getQuantidade());
            pstmt.setInt(4, frigorifico.getEndereco().getIdEndereco());
            pstmt.setString(5, frigorifico.getTipo());
            pstmt.setInt(6, frigorifico.getIdFrigorifico());

            if (pstmt.executeUpdate() > 0){
                return 1;// verifica se deu certo
            }
            else{
                return 0;// não deu certo
            }
        }
        catch (SQLException sqle){
            sqle.printStackTrace();
            return -1;
        }
        finally {
            conexao.desconectar(conn);// fechando a conexão
        }
    }// ==============ALTERAR===============
}
