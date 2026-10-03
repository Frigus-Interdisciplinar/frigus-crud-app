package com.example.servlet.Dao;

import com.example.servlet.Model.Geladeira;
import com.example.servlet.Model.Usuario;
import com.example.servlet.Util.Conexao;
import com.example.servlet.Util.Enum.StatusArmazenamento;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GeladeiraDAO {
    //===========INSERIR===========
    public int inserirGeladeira(Geladeira geladeira){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();

        try{
            String comandoSql = "insert into geladeira(idUsuario, status, tipo) values (?,?,?);";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql, Statement.RETURN_GENERATED_KEYS);
            // setando valores para o comando
            pstmt.setString(1, geladeira.getUsuario().getCpf());
            pstmt.setString(2, geladeira.getStatus().name());
            pstmt.setString(3, geladeira.getTipo());

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




    // ================ ALTERAR STATUS ================
    public int atualizarStatus(Geladeira geladeira){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();// abrindo a conexão com o banco

        try{
            String comandoSlq = "update geladeira set status = ? where id = ?";
            PreparedStatement pstmt = conn.prepareStatement(comandoSlq);
            // setando os valores para a operação
            pstmt.setString(1, geladeira.getStatus().name());
            pstmt.setInt(2, geladeira.getIdGeladeira());
            // verifica se foi possivel alterar o banco
            if (pstmt.executeUpdate() > 0){
                return 1;// deu certo
            }else {
                return 0;// não deu certo
            }

        } catch (SQLException sqle) {
            sqle.printStackTrace();
            return -1;// deu erro!!!
        }
        finally {
            conexao.desconectar(conn);// fechando a conexão com o banco
        }
    }// ================ ALTERAR STATUS ================




    // ==============BUSCAR TODAS AS GELADEIRAS PELO ID DO USUARIO ===============
    public List<Geladeira> buscarTodasGeladeira(String cpf){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();// abrindo a conexao

        ResultSet rset = null;
        List<Geladeira> listaGeladeira = new ArrayList<>();

        try{
            String comandoSql = "select * from geladeira where idUsuario = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // setando o valor do id da empresa
            pstmt.setString(1, cpf);
            rset = pstmt.executeQuery();
            while (rset.next()){
                // criando o objeto usuario
                Usuario usuario = new Usuario();
                usuario.setCpf(rset.getString("idUsuario"));

                // criando o objeto geladeira
                Geladeira geladeira = new Geladeira(
                    rset.getInt("id"),
                    usuario,
                    StatusArmazenamento.valueOf(rset.getString("status")),
                    rset.getString("tipo")
                );
                listaGeladeira.add(geladeira);
            }
        }catch (SQLException sqle){
            sqle.printStackTrace();
        }finally {
            conexao.desconectar(conn);// fechando a conexao
        }
        return listaGeladeira;
    }// ==============BUSCAR TODAS AS GELADEIRAS PELO ID DO USUARIO ===============





    // ==============BUSCAR UMA GELADEIRA PELO ID DO USUARIO E PELO ID DA GELADEIRA ===============
    public Geladeira buscarUnicaGeladeira(String cpf, int idGeladeira){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();// abrindo a conexao

        ResultSet rset = null;
        Geladeira geladeira = null;

        try{
            String comandoSql = "select * from geladeira where idUsuario = ? and id = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // setando o valor do id da empresa
            pstmt.setString(1, cpf);
            pstmt.setInt(2, idGeladeira);
            rset = pstmt.executeQuery();
            while (rset.next()){
                // criando o objeto usuario
                Usuario usuario = new Usuario();
                usuario.setCpf(rset.getString("idUsuario"));

                // criando o objeto geladeira
                 geladeira = new Geladeira(
                        rset.getInt("id"),
                        usuario,
                        StatusArmazenamento.valueOf(rset.getString("status")),
                        rset.getString("tipo")
                );

            }
        }catch (SQLException sqle){
            sqle.printStackTrace();
        }finally {
            conexao.desconectar(conn);// fechando a conexao
        }
        return geladeira;
    }// ==============BUSCAR UMA GELADEIRA PELO ID DO USUARIO E PELO ID DA GELADEIRA ===============
}
