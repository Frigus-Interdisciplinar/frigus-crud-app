package com.example.servlet.Dao;

import com.example.servlet.Model.SuperAdmin;
import com.example.servlet.Util.Conexao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SuperAdminDAO {

    // ================ INSERIR ================
    public int adicionarSuperAdmin(SuperAdmin superAdmin){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();// abrindo a conexao com o banco

        try{
            String comandoSql = "insert into superadmin (nome, email, senha) values (?,?,?);";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql, Statement.RETURN_GENERATED_KEYS);
            // setando os valores para a execução
            pstmt.setString(1, superAdmin.getNome());
            pstmt.setString(2, superAdmin.getEmail());
            pstmt.setString(3, superAdmin.getSenha());

            if (pstmt.executeUpdate() > 0){
                ResultSet chaves = pstmt.getGeneratedKeys();// adicionando a chave criada pelo banco
                if (chaves.next()) superAdmin.setIdSuperAdmin(chaves.getInt(1));
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
    public int alterarSuperAdmin(SuperAdmin superAdminAntigo, SuperAdmin superAdminNovo){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();// abrindo a conexao com o banco

        try{
            String comandoSql = "update superadmin set nome = ?, email = ?, senha = ? where id = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // setando os valores para a execução
            pstmt.setString(1, superAdminNovo.getNome());
            pstmt.setString(2, superAdminNovo.getEmail());
            pstmt.setString(3, superAdminNovo.getSenha());
            pstmt.setInt(4, superAdminAntigo.getIdSuperAdmin());

            if (pstmt.executeUpdate() > 0){
                return 1; // alterou as informações no banco
            }
            else{
                return 0; // não foi possível alterar
            }

        }catch (SQLException sqle){
            sqle.printStackTrace();
            return -1;
        }
        finally {
            conexao.desconectar(conn);// fechando a conexao com o banco
        }
    }// ================ ALTERAR ================


    // ================ DELETAR ================
    public int deletarSuperAdmin(SuperAdmin superAdmin){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();// abrindo a conexao com o banco

        try{
            String comandoSql = "delete from superadmin where id = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // setando os valores para a execução
            pstmt.setInt(1, superAdmin.getIdSuperAdmin());

            if (pstmt.executeUpdate() > 0){
                return 1; // apagou as informações no banco
            }
            else{
                return 0; // não foi possível apagar
            }

        }catch (SQLException sqle){
            sqle.printStackTrace();
            return -1;
        }
        finally {
            conexao.desconectar(conn);// fechando a conexao com o banco
        }
    }// ================ DELETAR ================


    // ================ BUSCAR O SUPER ADMIN PELO EMAIL ================
    public SuperAdmin buscarSuperAdmin(String email){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();// abrindo a conexao com o banco

        ResultSet rset = null;
        SuperAdmin superAdmin = null;

        try{
            String comandoSql = "select * from superadmin where email = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // setando os valores para a execução
            pstmt.setString(1, email);
            rset = pstmt.executeQuery();

            if (rset.next()){
                superAdmin = new SuperAdmin(
                        rset.getInt("id"),
                        rset.getString("nome"),
                        rset.getString("email"),
                        rset.getString("senha")
                );
            }
        }catch (SQLException sqle){
            sqle.printStackTrace();
        }
        finally {
            conexao.desconectar(conn); // fechando a conexao com o banco
        }
        return superAdmin;
    }// ================ BUSCAR O SUPER ADMIN PELO EMAIL ================

}
