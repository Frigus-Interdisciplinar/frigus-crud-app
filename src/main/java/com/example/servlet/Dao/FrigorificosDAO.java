package com.example.servlet.Dao;

import com.example.servlet.Model.Frigorificos;
import com.example.servlet.Util.Conexao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FrigorificosDAO {
    // INSERIR
    public int inserirFrigorifico(Frigorificos frigorifico){
        Conexao conexao = new Conexao(); // abrindo a conexão
        Connection conn = conexao.conectar();

        try{
            String comandoSql = "insert into frigorificos (idEmpresa, capacidade, status, quantidade, endereco, tipo) values (?,?,?,?,?,?);";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // setando os valores para o comando
            pstmt.setString(1, frigorifico.getEmpresa().getCnpj());
            pstmt.setInt(2, frigorifico.getCapacidade());
            pstmt.setString(3, frigorifico.getStatus());
            pstmt.setInt(4, frigorifico.getQuantidade());
            pstmt.setInt(5, frigorifico.getEndereco().getIdEndereco());
            pstmt.setString(6, frigorifico.getTipo());

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
    }// INSERIR





    // ALTERAR CADA ATRIBUTO SEPARADAMENTE
    // ALTERAR CADA ATRIBUTO SEPARADAMENTE
    // ALTERAR CADA ATRIBUTO SEPARADAMENTE
    // ALTERAR CADA ATRIBUTO SEPARADAMENTE
    // ALTERAR CADA ATRIBUTO SEPARADAMENTE

    //==============ALTERAR=============
    public int alterarFrigorifico(Frigorificos frigorifico){
        Conexao conexao = new Conexao(); // abrindo a conexão
        Connection conn = conexao.conectar();

        try{
            String comandoSql = "update Frigorificos set idEmpresa = ?, capacidade = ?, status = ?, quantidade = ?, endereco = ?, tipo = ?  where id = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // setando os valores para o comando
            pstmt.setString(1, frigorifico.getEmpresa().getCnpj());
            pstmt.setInt(2, frigorifico.getCapacidade());
            pstmt.setString(3, frigorifico.getStatus());
            pstmt.setInt(4, frigorifico.getQuantidade());
            pstmt.setInt(5, frigorifico.getEndereco().getIdEndereco());
            pstmt.setString(6, frigorifico.getTipo());
            pstmt.setInt(7, frigorifico.getIdFrigorifico());

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
