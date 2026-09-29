package com.example.servlet.Dao;

import com.example.servlet.Model.Usuario;
import com.example.servlet.Util.Conexao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAO {

    // ================ INSERIR ================
    public int adicionarUsuario(Usuario usuario){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();// abrindo a conexao com o banco

        try{
            String comandoSql = "Insert into usuario (cpf, nome, sobrenome, email, senha, telefone) values (?,?,?,?,?,?);";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // setando os valores para a execução
            pstmt.setString(1, usuario.getCpf());
            pstmt.setString(2, usuario.getNome());
            pstmt.setString(3, usuario.getSobrenome());
            pstmt.setString(4, usuario.getEmail());
            pstmt.setString(5, usuario.getSenha());
            pstmt.setString(6, usuario.getTelefone());

            if (pstmt.executeUpdate() > 0){
                return 1; // inseriu as informações no banco
            }
            else{
                return 0; // não foi possível adicionar
            }

        }catch (SQLException sqle){
            sqle.printStackTrace();
            return -1;
        }
        finally {
            conexao.desconectar(conn);// fechando a conexao com o banco
        }
    }// ================ INSERIR ================


    // ================ ALTERAR ================
    public int alterarUsuario(Usuario usuarioAntigo, Usuario usuarioNovo){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();// abrindo a conexao com o banco

        try{
            String comandoSql = "update usuario set nome = ?, sobrenome = ?, email = ?, senha = ?, telefone = ? where cpf = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // setando os valores para a execução
            pstmt.setString(1, usuarioNovo.getNome());
            pstmt.setString(2, usuarioNovo.getSobrenome());
            pstmt.setString(3, usuarioNovo.getEmail());
            pstmt.setString(4, usuarioNovo.getSenha());
            pstmt.setString(5, usuarioNovo.getTelefone());
            pstmt.setString(6, usuarioAntigo.getCpf());

            if (pstmt.executeUpdate() > 0){
                return 1; // inseriu as informações no banco
            }
            else{
                return 0; // não foi possível adicionar
            }

        }catch (SQLException sqle){
            sqle.printStackTrace();
            return -1;
        }
        finally {
            conexao.desconectar(conn);// fechando a conexao com o banco
        }
    }// ================ ALTERAR ================


    // ================ ALTERAR E-MAIL ================
    public int alterarEmailUsuario(Usuario usuario, String email){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();// abrindo a conexao com o banco

        try{
            String comandoSql = "update usuario set email = ? where cpf = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // setando os valores para a execução
            pstmt.setString(1, email);
            pstmt.setString(2, usuario.getCpf());

            if (pstmt.executeUpdate() > 0){
                return 1; // inseriu as informações no banco
            }
            else{
                return 0; // não foi possível adicionar
            }

        }catch (SQLException sqle){
            sqle.printStackTrace();
            return -1;
        }
        finally {
            conexao.desconectar(conn);// fechando a conexao com o banco
        }
    }// ================ ALTERAR E-MAIL ================




    // ================ DELETAR ================
    public int deletarUsuario(Usuario usuario){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();// abrindo a conexao com o banco

        try{
            String comandoSql = "delete from usuario where cpf = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // setando os valores para a execução
            pstmt.setString(1, usuario.getCpf());

            if (pstmt.executeUpdate() > 0){
                return 1; // inseriu as informações no banco
            }
            else{
                return 0; // não foi possível adicionar
            }

        }catch (SQLException sqle){
            sqle.printStackTrace();
            return -1;
        }
        finally {
            conexao.desconectar(conn);// fechando a conexao com o banco
        }
    }// ================ DELETAR ================


    // ================ BUSCAR ================
    public Usuario buscarUsuario(String cpf){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();// abrindo a conexao com o banco

        ResultSet rset = null;
        Usuario usuario = null;

        try{
            String comandoSql = "select * from usuario where cpf = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // setando os valores para a execução
            pstmt.setString(1, cpf);
            rset = pstmt.executeQuery();

            if (rset.next()){
                usuario = new Usuario(
                        rset.getString("cpf"),
                        rset.getString("nome"),
                        rset.getString("sobrenome"),
                        rset.getString("email"),
                        rset.getString("senha"),
                        rset.getString("telefone")
                );
            }


        }catch (SQLException sqle){
            sqle.printStackTrace();
        }
        finally {
            conexao.desconectar(conn); // fechando a conexao com o banco
        }
        return usuario;
    }// ================ BUSCAR ================

}
