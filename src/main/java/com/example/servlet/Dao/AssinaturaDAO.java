package com.example.servlet.Dao;

import com.example.servlet.Model.Assinatura;
import com.example.servlet.Model.Empresa;
import com.example.servlet.Model.Usuario;
import com.example.servlet.Util.Conexao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AssinaturaDAO {


   // ========================================== USUARIO  ==========================================

    // ============ INSERIR ASSINATURA PARA O USUARIO ============
    public int inserirAssinaturaUsuario(Assinatura assinatura){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();

        try{
            String comandoSql = "insert into assinatura (cpf, tipoAssinatura, valor, nome, descricao) values(?,?,?,?,?);";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);

            // setando os valores para a operação
            pstmt.setString(1, assinatura.getUsuario().getCpf());
            pstmt.setString(2, assinatura.getTipoAssinatura());
            pstmt.setDouble(3, assinatura.getValor());
            pstmt.setString(4, assinatura.getNome());
            pstmt.setString(5, assinatura.getDescricao());

            if(pstmt.executeUpdate() > 0){
                return 1; // verifica se deu certo
            }else{
                return 0; // deu errado!!
            }
        }catch (SQLException sqle){
            sqle.printStackTrace();
            return -1;
        }
        finally {
            conexao.desconectar(conn);
        }
    }// ============ INSERIR ASSINATURA PARA O USUARIO ============




    // ============ DELETAR ASSINATURA DO USUARIO ============
    public int deletarAssinaturaUsuario(Assinatura assinatura){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();

        try{
            String comandoSql = "delete from assinatura where id = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);

            // setando os valores para a operação
            pstmt.setInt(1, assinatura.getIdAssinatura());

            if(pstmt.executeUpdate() > 0){
                return 1; // verifica se deu certo
            }else{
                return 0; // deu errado!!
            }
        }catch (SQLException sqle){
            sqle.printStackTrace();
            return -1;
        }
        finally {
            conexao.desconectar(conn);
        }
    }// ============ DELETAR ASSINATURA DO USUARIO ============



    // ============ BUSCANDO A ASSINATURA DO USUARIO POR ID ============
    public Assinatura buscarAssinaturaUsuario(Assinatura assinatura){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();

        ResultSet rset = null;

        try{
            String comandoSql = "select * from assinatura where cpf = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // sentado o valor do cpf
            pstmt.setString(1, assinatura.getUsuario().getCpf());
            rset = pstmt.executeQuery();

            if (rset.next()){
                // Criando o objeto usuario com o ID vindo do ResultSet
                Usuario usuario = new Usuario();
                usuario.setCpf(rset.getString("cpf"));

                // criando o objeto Assinatura
                assinatura = new Assinatura(
                        usuario,
                        rset.getString("tipoAssinatura"),
                        rset.getDouble("valor"),
                        rset.getString("nome"),
                        rset.getString("descricao")
                );
            }

        }catch (SQLException sqle){
            sqle.printStackTrace();
        }
        finally {
            conexao.desconectar(conn); // fechando a conexão
        }
        return assinatura;
    }// ============ BUSCANDO A ASSINATURA DO USUARIO POR ID ============



    // ========================================== USUARIO  ==========================================








    // ========================================== EMPRESA  ==========================================


    // ============ INSERIR ASSINATURA PARA A EMPRESA ============
    public int inserirAssinaturaEmpresa(Assinatura assinatura){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();

        try{
            String comandoSql = "insert into assinatura (cnpj, tipoAssinatura, valor, nome, descricao) values(?,?,?,?,?);";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);

            // setando os valores para a operação
            pstmt.setString(1, assinatura.getEmpresa().getCnpj());
            pstmt.setString(2, assinatura.getTipoAssinatura());
            pstmt.setDouble(3, assinatura.getValor());
            pstmt.setString(4, assinatura.getNome());
            pstmt.setString(5, assinatura.getDescricao());

            if(pstmt.executeUpdate() > 0){
                return 1; // verifica se deu certo
            }else{
                return 0; // deu errado!!
            }
        }catch (SQLException sqle){
            sqle.printStackTrace();
            return -1;
        }
        finally {
            conexao.desconectar(conn);
        }
    }// ============ INSERIR ASSINATURA PARA A EMPRESA ============




    // ============ DELETAR ASSINATURA DA EMPRESA ============
    public int deletarAssinaturaEmpresa(Assinatura assinatura){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();

        try{
            String comandoSql = "delete from assinatura where id = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);

            // setando os valores para a operação
            pstmt.setInt(1, assinatura.getIdAssinatura());

            if(pstmt.executeUpdate() > 0){
                return 1; // verifica se deu certo
            }else{
                return 0; // deu errado!!
            }
        }catch (SQLException sqle){
            sqle.printStackTrace();
            return -1;
        }
        finally {
            conexao.desconectar(conn);
        }
    }// ============ DELETAR ASSINATURA DA EMPRESA ============



    // ============ BUSCANDO A ASSINATURA DA EMPRESA POR ID ============
    public Assinatura buscarAssinaturaEmpresa(Assinatura assinatura){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();

        ResultSet rset = null;

        try{
            String comandoSql = "select * from assinatura where idEmpresa = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // sentado o valor do cnpj
            pstmt.setString(1, assinatura.getEmpresa().getCnpj());
            rset = pstmt.executeQuery();

            if (rset.next()){
                // Criando o objeto usuario com o ID vindo do ResultSet
                Empresa empresa = new Empresa();
                empresa.setCnpj(rset.getString("cnpj"));

                // criando o objeto Assinatura
                assinatura = new Assinatura(
                        empresa,
                        rset.getString("tipoAssinatura"),
                        rset.getDouble("valor"),
                        rset.getString("nome"),
                        rset.getString("descricao")
                );
            }

        }catch (SQLException sqle){
            sqle.printStackTrace();
        }
        finally {
            conexao.desconectar(conn); // fechando a conexão
        }
        return assinatura;
    }// ============ BUSCANDO A ASSINATURA DA EMPRESA POR ID ============

    // ========================================== EMPRESA  ==========================================




    // ============ ALTERAR UNIVERSAL ============
    public int alterarAssinatura(Assinatura assinatura){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();

        try{
            String comandoSql = "update assinatura set tipoAssinatura = ?, valor = ?, nome = ?, descricao = ? where id = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);

            // setando os valores para a operação
            pstmt.setString(1, assinatura.getTipoAssinatura());
            pstmt.setDouble(2, assinatura.getValor());
            pstmt.setString(3, assinatura.getNome());
            pstmt.setString(4, assinatura.getDescricao());
            pstmt.setInt(5, assinatura.getIdAssinatura());

            if(pstmt.executeUpdate() > 0){
                return 1; // verifica se deu certo
            }else{
                return 0; // deu errado!!
            }
        }catch (SQLException sqle){
            sqle.printStackTrace();
            return -1;
        }
        finally {
            conexao.desconectar(conn);
        }
    }// ============ ALTERAR UNIVERSAL ============
}
