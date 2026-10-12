package com.example.servlet.Dao;

import com.example.servlet.Model.Receita;
import com.example.servlet.Util.Conexao;

import java.sql.*;

public class ReceitaDAO {

    // ================== USUARIO ==================

    // ========== INSERIR RECEITA DO USUARIO ==========
    public int inserirReceitaUsuario(Receita receita){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();

        try{
            String comandoSql = "insert into receita (idUsuario, nome, descricao, modoPreparo, dtReceita, foiFeita) values (?,?,?,?,?,?);";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql, Statement.RETURN_GENERATED_KEYS);

            // setando os valores para a operação
            pstmt.setString(1, receita.getUsuario().getCpf());
            pstmt.setString(2, receita.getNome());
            pstmt.setString(3, receita.getDescricao());
            pstmt.setString(4, receita.getModoPreparo());
            pstmt.setObject(5, receita.getDtReceita());
            pstmt.setBoolean(6, receita.isFoiFeita());

            if (pstmt.executeUpdate() > 0){
                ResultSet chaves = pstmt.getGeneratedKeys();// adicionando a chave criada pelo banco
                if(chaves.next()) receita.setIdReceita(chaves.getInt(1));
                return 1; // verifica se deu certo
            }
            else{
                return 0; // rejeitado!!
            }
        }
        catch (SQLException sqle){
            sqle.printStackTrace();
            return -1;
        }
        finally {
            conexao.desconectar(conn);
        }

    }// ========== INSERIR RECEITA DO USUARIO ==========



    // ========== ALTERAR RECEITA DO USUARIO ==========
    public int alterarReceitaUsuario(Receita receitaAntiga, Receita receitaNova){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();

        try{
            String comandoSql = "update receita set idUsuario = ?, nome = ?, descricao = ?, modoPreparo = ?, dtReceita = ?, foiFeita = ? where id = ? and idUsuario is not null;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);

            // setando os valores para a operação
            pstmt.setString(1, receitaNova.getUsuario().getCpf());
            pstmt.setString(2, receitaNova.getNome());
            pstmt.setString(3, receitaNova.getDescricao());
            pstmt.setString(4, receitaNova.getModoPreparo());
            pstmt.setObject(5, receitaNova.getDtReceita());
            pstmt.setBoolean(6, receitaNova.isFoiFeita());
            pstmt.setInt(7, receitaAntiga.getIdReceita());

            if (pstmt.executeUpdate() > 0){
                return 1; // verifica se deu certo
            }
            else{
                return 0; // rejeitado!!
            }
        }
        catch (SQLException sqle){
            sqle.printStackTrace();
            return -1;
        }
        finally {
            conexao.desconectar(conn);
        }

    }// ========== ALTERAR RECEITA DO USUARIO ==========


    // ================== USUARIO ==================



    // ================== EMPRESA ==================


    // ========== INSERIR RECEITA DA EMPRESA ==========
    public int inserirReceitaEmpresa(Receita receita){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();

        try{
            String comandoSql = "insert into receita (idEmpresa, nome, descricao, modoPreparo, dtReceita, foiFeita) values (?,?,?,?,?,?);";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql, Statement.RETURN_GENERATED_KEYS);

            // setando os valores para a operação
            pstmt.setString(1, receita.getEmpresa().getCnpj());
            pstmt.setString(2, receita.getNome());
            pstmt.setString(3, receita.getDescricao());
            pstmt.setString(4, receita.getModoPreparo());
            pstmt.setObject(5, receita.getDtReceita());
            pstmt.setBoolean(6, receita.isFoiFeita());

            if (pstmt.executeUpdate() > 0){
                ResultSet chaves = pstmt.getGeneratedKeys();// adicionando a chave criada pelo banco
                if(chaves.next()) receita.setIdReceita(chaves.getInt(1));
                return 1; // verifica se deu certo
            }
            else{
                return 0; // rejeitado!!
            }
        }
        catch (SQLException sqle){
            sqle.printStackTrace();
            return -1;
        }
        finally {
            conexao.desconectar(conn);
        }

    }// ========== INSERIR RECEITA DA EMPRESA ==========



    // ========== ALTERAR RECEITA DA EMPRESA ==========
    public int alterarReceitaEmpresa(Receita receitaAntiga, Receita receitaNova){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();

        try{
            String comandoSql = "update receita set idEmpresa = ?, nome = ?, descricao = ?, modoPreparo = ?, dtReceita = ?, foiFeita = ? where id = ? and idEmpresa is not null;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);

            // setando os valores para a operação
            pstmt.setString(1, receitaNova.getEmpresa().getCnpj());
            pstmt.setString(2, receitaNova.getNome());
            pstmt.setString(3, receitaNova.getDescricao());
            pstmt.setString(4, receitaNova.getModoPreparo());
            pstmt.setObject(5, receitaNova.getDtReceita());
            pstmt.setBoolean(6, receitaNova.isFoiFeita());
            pstmt.setInt(7, receitaAntiga.getIdReceita());

            if (pstmt.executeUpdate() > 0){
                return 1; // verifica se deu certo
            }
            else{
                return 0; // rejeitado!!
            }
        }
        catch (SQLException sqle){
            sqle.printStackTrace();
            return -1;
        }
        finally {
            conexao.desconectar(conn);
        }

    }// ========== ALTERAR RECEITA DA EMPRESA ==========



    // ================== EMPRESA ==================


    // ========== DELETAR RECEITA UNIVERSAL ==========
    public int deletarReceita(Receita receita){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();

        try{
            String comandoSql = "delete from receita where id = ?";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);

            // setando os valores para a operação
            pstmt.setInt(1, receita.getIdReceita());

            if (pstmt.executeUpdate() > 0){
                return 1; // verifica se deu certo
            }
            else{
                return 0; // rejeitado!!
            }
        }
        catch (SQLException sqle){
            sqle.printStackTrace();
            return -1;
        }
        finally {
            conexao.desconectar(conn);
        }

    }// ========== DELETAR RECEITA UNIVERSAL ==========
}
