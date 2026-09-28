package com.example.servlet.Dao;

import com.example.servlet.Model.Assinatura;
import com.example.servlet.Model.Empresa;
import com.example.servlet.Model.Usuario;
import com.example.servlet.Util.Conexao;
import com.example.servlet.Util.Enum.TipoAssinatura;

import java.sql.*;


public class AssinaturaDAO {


   // ========================================== USUARIO  ==========================================

    // ============ INSERIR ASSINATURA PARA O USUARIO ============
    public int inserirAssinaturaUsuario(Assinatura assinatura){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();

        try{
            String comandoSql = "insert into assinatura (idUsuario, tipoAssinatura, valor, descricao) values(?,?,?,?);";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql, Statement.RETURN_GENERATED_KEYS);

            // setando os valores para a operação
            pstmt.setString(1, assinatura.getUsuario().getCpf());
            pstmt.setString(2, assinatura.getTipoAssinatura().name());
            pstmt.setDouble(3, assinatura.getValor());
            pstmt.setString(4, assinatura.getDescricao());

            if(pstmt.executeUpdate() > 0){
                ResultSet chaves = pstmt.getGeneratedKeys();
                if (chaves.next()) assinatura.setIdAssinatura(chaves.getInt(1));
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



    // ============ ALTERAR ASSINATURA PARA O USUARIO ============
    public int alterarAssinUsuario(Assinatura assinatura){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();

        try{
            String comandoSql = "update assinatura set idUsuario = ?, tipoAssinatura = ?, valor = ?, descricao = ? where id = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // setando os valor para a operação
            pstmt.setString(1, assinatura.getUsuario().getCpf());
            pstmt.setString(2, assinatura.getTipoAssinatura().name());
            pstmt.setDouble(3, assinatura.getValor());
            pstmt.setString(4, assinatura.getDescricao());
            pstmt.setInt(5, assinatura.getIdAssinatura());

            if (pstmt.executeUpdate() > 0){
                return 1;// deu certo
            }else{
                return 0;// nao foi possivel realizar o comando
            }

        }catch (SQLException sqle){
            sqle.printStackTrace();
            return -1;// erro!!
        }finally {
            conexao.desconectar(conn);// fechando a conexao com o banco
        }
    }// ============ ALTERAR ASSINATURA PARA O USUARIO ============


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



    // ============ BUSCANDO A ASSINATURA DO USUARIO POR CPF ============
    public Assinatura buscarAssinaturaUsuario(String cpf){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();

        Assinatura assinatura = null;
        ResultSet rset = null;

        try{
            String comandoSql = "select * from assinatura where idUsuario = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // sentado o valor do cpf
            pstmt.setString(1, cpf);
            rset = pstmt.executeQuery();

            if (rset.next()){
                // Criando o objeto usuario com o ID vindo do ResultSet
                Usuario usuario = new Usuario();
                usuario.setCpf(rset.getString("idUsuario"));

                // criando o objeto Assinatura
                assinatura = new Assinatura(
                        rset.getInt("id"),
                        usuario,
                        TipoAssinatura.valueOf(rset.getString("tipoAssinatura")),
                        rset.getDouble("valor"),
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
    }// ============ BUSCANDO A ASSINATURA DO USUARIO POR CPF ============



    // ========================================== USUARIO  ==========================================








    // ========================================== EMPRESA  ==========================================


    // ============ INSERIR ASSINATURA PARA A EMPRESA ============
    public int inserirAssinaturaEmpresa(Assinatura assinatura){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();

        try{
            String comandoSql = "insert into assinatura (idEmpresa, tipoAssinatura, valor, descricao) values(?,?,?,?);";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql, Statement.RETURN_GENERATED_KEYS);

            // setando os valores para a operação
            pstmt.setString(1, assinatura.getEmpresa().getCnpj());
            pstmt.setString(2, assinatura.getTipoAssinatura().name());
            pstmt.setDouble(3, assinatura.getValor());
            pstmt.setString(4, assinatura.getDescricao());

            if(pstmt.executeUpdate() > 0){
                ResultSet chaves = pstmt.getGeneratedKeys();
                if(chaves.next()) assinatura.setIdAssinatura(chaves.getInt(1));
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



    // ============ ALTERAR ASSINATURA PARA A EMPRESA ============
    public int alterarAssinEmpresa(Assinatura assinatura){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();

        try{
            String comandoSql = "update assinatura set idEmpresa = ?, tipoAssinatura = ?, valor = ?, descricao = ? where id = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // setando os valor para a operação
            pstmt.setString(1, assinatura.getEmpresa().getCnpj());
            pstmt.setString(2, assinatura.getTipoAssinatura().name());
            pstmt.setDouble(3, assinatura.getValor());
            pstmt.setString(4, assinatura.getDescricao());
            pstmt.setInt(5, assinatura.getIdAssinatura());

            if (pstmt.executeUpdate() > 0){
                return 1;// deu certo
            }else{
                return 0;// nao foi possivel realizar o comando
            }

        }catch (SQLException sqle){
            sqle.printStackTrace();
            return -1;// erro!!
        }finally {
            conexao.desconectar(conn);// fechando a conexao com o banco
        }
    }// ============ ALTERAR ASSINATURA PARA A EMPRESA============




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



    // ============ BUSCANDO A ASSINATURA DA EMPRESA POR CNPJ ============
    public Assinatura buscarAssinaturaEmpresa(String cnpj){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();

        Assinatura assinatura = null;
        ResultSet rset = null;

        try{
            String comandoSql = "select * from assinatura where idEmpresa = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // sentado o valor do cnpj
            pstmt.setString(1, cnpj);
            rset = pstmt.executeQuery();

            if (rset.next()){
                // Criando o objeto usuario com o ID vindo do ResultSet
                Empresa empresa = new Empresa();
                empresa.setCnpj(rset.getString("idEmpresa"));

                // criando o objeto Assinatura
                 assinatura = new Assinatura(
                        rset.getInt("id"),
                        empresa,
                        TipoAssinatura.valueOf(rset.getString("tipoAssinatura")),
                        rset.getDouble("valor"),
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
    }// ============ BUSCANDO A ASSINATURA DA EMPRESA POR CNPJ ============

    // ========================================== EMPRESA  ==========================================
}
