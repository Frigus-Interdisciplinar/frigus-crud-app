package com.example.servlet.Dao;

import com.example.servlet.Model.Frigorificos;
import com.example.servlet.Model.Geladeira;
import com.example.servlet.Util.Conexao;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class GeladeiraDAO {
    //===========INSERIR===========
    public int inserirGeladeira(Geladeira geladeira){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();

        try{
            String comandoSql = "insert into geladeira(idUsuario, status, quantidade, tipo) values (?,?,?,?);";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql, Statement.RETURN_GENERATED_KEYS);
            // setando valores para o comando
            pstmt.setString(1, geladeira.getUsuario().getCpf());
            pstmt.setString(2, geladeira.getStatus().name());
            pstmt.setInt(3, geladeira.getQuantidade());
            pstmt.setString(4, geladeira.getTipo());

            if (pstmt.executeUpdate() > 0){
                ResultSet chaves = pstmt.getGeneratedKeys();// adicionando a chave criada pelo banco
                if (chaves.next()) geladeira.setIdGeladeira(chaves.getInt(1));
                return 1;// verifica se deu certo
            }else {
                return 0;// não deu certo
            }
        }
        catch (SQLException sqle){
            sqle.printStackTrace();
            return -1;
        }
        finally {
            conexao.desconectar(conn);
        }

    }//===========INSERIR===========




    //==============ALTERAR=============
    public int alterarGeradeira(Geladeira geladeira){
        Conexao conexao = new Conexao(); // abrindo a conexão
        Connection conn = conexao.conectar();

        try{
            String comandoSql = "update geladeira set idUsuario = ?, status = ?, quantidade = ?, tipo = ?  where id = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // setando os valores para o comando
            pstmt.setString(1, geladeira.getUsuario().getCpf());
            pstmt.setString(2, geladeira.getStatus().name());
            pstmt.setInt(3, geladeira.getQuantidade());
            pstmt.setString(4, geladeira.getTipo());
            pstmt.setInt(5, geladeira.getIdGeladeira());
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
