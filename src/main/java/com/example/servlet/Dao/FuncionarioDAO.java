package com.example.servlet.Dao;

import com.example.servlet.Model.Empresa;
import com.example.servlet.Model.Funcionario;
import com.example.servlet.Util.Conexao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FuncionarioDAO {
    // ==============INSERIR===============
    public int cadastrarFuncionario(Funcionario funcionario){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();

        try{
            String comandosSql = "insert into funcionario(cpf, idEmpresa, email, nome, sobrenome, cargo, telefone, senha) values (?,?,?,?,?,?,?,?);";
            PreparedStatement pstmt = conn.prepareStatement(comandosSql);
            // dand valores para o comando
            pstmt.setString(1, funcionario.getCpf());
            pstmt.setString(2, funcionario.getEmpresa().getCnpj());
            pstmt.setString(3, funcionario.getEmail());
            pstmt.setString(4, funcionario.getNome());
            pstmt.setString(5, funcionario.getSobrenome());
            pstmt.setString(6, funcionario.getCargo());
            pstmt.setString(7, funcionario.getTelefone());
            pstmt.setString(8, funcionario.getSenha());

            if(pstmt.executeUpdate() > 0){
                return 1;
            }else {
                return 0;
            }
        }catch (SQLException sqle){
            sqle.printStackTrace();
            return -1;
        }
        finally {
            conexao.desconectar(conn);
        }
    }// ==============INSERIR===============



    // ==============ALTERAR===============
    public int alterarFuncionario(Funcionario funcionario){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();

        try{
            String comandosSql = "update funcionario set idEmpresa = ?, email = ?, nome = ?, sobrenome = ?, cargo = ?, telefone = ?, senha = ? where cpf = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandosSql);
            // dand valores para o comando
            pstmt.setString(1, funcionario.getEmpresa().getCnpj());
            pstmt.setString(2, funcionario.getEmail());
            pstmt.setString(3, funcionario.getNome());
            pstmt.setString(4, funcionario.getSobrenome());
            pstmt.setString(5, funcionario.getCargo());
            pstmt.setString(6, funcionario.getTelefone());
            pstmt.setString(7, funcionario.getSenha());
            pstmt.setString(8, funcionario.getCpf());

            if(pstmt.executeUpdate() > 0){
                return 1;
            }else {
                return 0;
            }
        }catch (SQLException sqle){
            sqle.printStackTrace();
            return -1;
        }
        finally {
            conexao.desconectar(conn);
        }
    }// ==============ALTERAR===============




    // ==============DELETAR===============
    public int excluirFuncionario(Funcionario funcionario){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();

        try{
            String comandosSql = "delete from funcionario where cpf = ? ";
            PreparedStatement pstmt = conn.prepareStatement(comandosSql);
            // dand valores para o comando
            pstmt.setString(1, funcionario.getCpf());

            if(pstmt.executeUpdate() > 0){
                return 1;
            }else {
                return 0;
            }
        }catch (SQLException sqle){
            sqle.printStackTrace();
            return -1;
        }
        finally {
            conexao.desconectar(conn);
        }
    }// ==============DELETAR===============


    // VER O FUNCIONARIO PELO CPF
    public Funcionario buscarFuncionarioPorcpf(Funcionario funcionario){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();

        ResultSet rset = null;

        try{
            String comandoSql ="select * from funcionario where cpf =? ;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // setando o valor do cpf para a operação
            pstmt.setString(1, funcionario.getCpf());
            rset = pstmt.executeQuery();
            if(rset.next()){
                // criando o objeto empresa com o id vindo do result set
                Empresa empresa = new Empresa();
                empresa.setCnpj(rset.getString("idEmpresa"));

                // criando o objeto funcionario
                funcionario = new Funcionario(
                        rset.getString("cpf"),
                        empresa,
                        rset.getString("email"),
                        rset.getString("nome"),
                        rset.getString("sobrenome"),
                        rset.getString("cargo"),
                        rset.getString("telefone"),
                        rset.getString("senha")
                );
            }
        }catch (SQLException sqle){
            sqle.printStackTrace();
        }
        finally {
            conexao.desconectar(conn);//fechando a conexao com o banco
        }
        return funcionario;
    }// VER O FUNCIONARIO PELO ID



    // VER TODOS OS  FUNCIONARIOS DE UMA EMPRESA
    public List<Funcionario> buscarFuncionariosPorEmpresa(Funcionario funcionario){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();

        ResultSet rset = null;
        List<Funcionario> lista = new ArrayList<>();

        try{
            String comandoSql = "select f.* from funcionario f join empresa e on f.idEmpresa = e.cnpj where e.cnpj = ? ;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // setando o valor do cnpj da empresa para a operação
            pstmt.setString(1, funcionario.getEmpresa().getCnpj());
            rset = pstmt.executeQuery();

            while (rset.next()){
                // criando o objeto empresa com o id vindo do result set
                Empresa empresa = new Empresa();
                empresa.setCnpj(rset.getString("idEmpresa"));
                // criando o objeto funcionario com o atributos vindos do result set
                funcionario = new Funcionario(
                        rset.getString("cpf"),
                        empresa,
                        rset.getString("email"),
                        rset.getString("nome"),
                        rset.getString("sobrenome"),
                        rset.getString("cargo"),
                        rset.getString("telefone"),
                        rset.getString("senha")
                );
                lista.add(funcionario);
            }

        }catch (SQLException sqle){
            sqle.printStackTrace();
        }finally {
            conexao.desconectar(conn);//fechando a conexao com o banco
        }
        return lista;
    }// VER TODOS OS  FUNCIONARIOS DE UMA EMPRESA
}
