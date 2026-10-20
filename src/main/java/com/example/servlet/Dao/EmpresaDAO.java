package com.example.servlet.Dao;

import com.example.servlet.Model.Empresa;
import com.example.servlet.Model.Usuario;
import com.example.servlet.Util.Conexao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EmpresaDAO {
    // INSERIR
    public int cadastrarEmpresa(Empresa empresa){
        Conexao conexao = new Conexao(); // abrindo a conexão
        Connection conn = conexao.conectar();

        try{
            String comandoSql = "insert into empresa (cnpj, nome, email, senha) values (?,?,?,?);";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // setando os valores para o comando
            pstmt.setString(1, empresa.getCnpj());
            pstmt.setString(2, empresa.getNome());
            pstmt.setString(3, empresa.getEmail());
            pstmt.setString(4, empresa.getSenha());

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





    //==============ALTERAR=============
    public int alterarEmpresa(Empresa empresaAntiga, Empresa empresaNova){
        Conexao conexao = new Conexao(); // abrindo a conexão
        Connection conn = conexao.conectar();

        try{
            String comandoSql = "update empresa set nome = ?, email = ?, senha = ? where cnpj = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // setando os valores para o comando
            pstmt.setString(1, empresaNova.getNome());
            pstmt.setString(2, empresaNova.getEmail());
            pstmt.setString(3, empresaNova.getSenha());
            pstmt.setString(4, empresaAntiga.getCnpj());

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


    //============== ALTERAR E-MAIL =============
    public int alterarEmailEmpresa(Empresa empresa, String email){
        Conexao conexao = new Conexao(); // abrindo a conexão
        Connection conn = conexao.conectar();

        try{
            String comandoSql = "update empresa set email = ? where cnpj = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // setando os valores para o comando
            pstmt.setString(1, email);
            pstmt.setString(2, empresa.getCnpj());

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
    }// ==============ALTERAR E-MAIL===============




    // ================ ALTERAR SENHA ================
    public int alterarSenhaEmpresa(Empresa empresa, String senha){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();// abrindo a conexao com o banco

        try{
            String comandoSql = "update empresa set senha = ? where cnpj = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // setando os valores para a execução
            pstmt.setString(1, senha);
            pstmt.setString(2, empresa.getCnpj());

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
    }// ================ ALTERAR SENHA ================



    //==============DELETAR=============
    public int excluirEmpresa(String email){
        Conexao conexao = new Conexao(); // abrindo a conexão
        Connection conn = conexao.conectar();

        try{
            String comandoSql = "delete from empresa where email = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // setando os valores para o comando
            pstmt.setString(1, email);

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
    }// ==============DELETAR===============



    // ================ BUSCAR ================
    public Empresa buscarEmpresa(String email){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();

        Empresa empresa = null;
        ResultSet rset = null; // declarando o Result Set

        try{
            String comandoSql = "select * from empresa where email = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // setando os valores para a execução
            pstmt.setString(1, email);
            rset = pstmt.executeQuery();

            if (rset.next()){
                empresa = new Empresa(
                        rset.getString("cnpj"),
                        rset.getString("nome"),
                        rset.getString("email"),
                        rset.getString("senha")
                );
            }


        }catch (SQLException sqle){
            sqle.printStackTrace();
        }
        finally {
            conexao.desconectar(conn);
        }
        return empresa;
    }// ================ BUSCAR ================
}
