package com.example.servlet.Dao;

import com.example.servlet.Model.Empresa;
import com.example.servlet.Model.Endereco;
import com.example.servlet.Model.Frigorificos;
import com.example.servlet.Model.Geladeira;
import com.example.servlet.Util.Conexao;
import com.example.servlet.Util.Enum.StatusArmazenamento;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FrigorificosDAO {
    // INSERIR
    public int inserirFrigorifico(Frigorificos frigorifico){
        Conexao conexao = new Conexao(); // abrindo a conexão
        Connection conn = conexao.conectar();

        try{
            String comandoSql = "insert into frigorificos (idEmpresa, status, idEndereco, tipo) values (?,?,?,?);";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql, Statement.RETURN_GENERATED_KEYS);
            // setando os valores para o comando
            pstmt.setString(1, frigorifico.getEmpresa().getCnpj());
            pstmt.setString(2, frigorifico.getStatus().name());
            pstmt.setInt(3, frigorifico.getEndereco().getIdEndereco());
            pstmt.setString(4, frigorifico.getTipo());

            if (pstmt.executeUpdate() > 0){
                ResultSet chaves = pstmt.getGeneratedKeys();// adicionando a chave criada pelo banco
                if(chaves.next()) frigorifico.setIdFrigorifico(chaves.getInt(1));
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
    public int alterarFrigorifico(Frigorificos frigorifico){
        Conexao conexao = new Conexao(); // abrindo a conexão
        Connection conn = conexao.conectar();

        try{
            String comandoSql = "update Frigorificos set idEmpresa = ?, status = ?, idEndereco = ?, tipo = ?  where id = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // setando os valores para o comando
            pstmt.setString(1, frigorifico.getEmpresa().getCnpj());
            pstmt.setString(2, frigorifico.getStatus().name());
            pstmt.setInt(3, frigorifico.getEndereco().getIdEndereco());
            pstmt.setString(4, frigorifico.getTipo());
            pstmt.setInt(5, frigorifico.getIdFrigorifico());

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




    // ================ ALTERAR STATUS ================
    public int atualizarStatus(Frigorificos frigorificos){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();// abrindo a conexão com o banco

        try{
            String comandoSlq = "update frigorificos set status = ? where id = ?";
            PreparedStatement pstmt = conn.prepareStatement(comandoSlq);
            // setando os valores para a operação
            pstmt.setString(1, frigorificos.getStatus().name());
            pstmt.setInt(2, frigorificos.getIdFrigorifico());
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



    // ==============BUSCAR TODOS OS FRIGORIFICOS DA EMPRESA PELO ID DA MESMA ===============
    public List<Frigorificos> buscarFrigorificoEmpresa(String cnpj){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();// abrindo a conexao

        ResultSet rset = null;

        List<Frigorificos> listaFrigorificos = new ArrayList<>();

        try{
            String comandoSql = "select * from frigorificos where idEmpresa = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // setando o valor do id da empresa
            pstmt.setString(1, cnpj);
            rset = pstmt.executeQuery();
            while (rset.next()){
                // criando o objeto endereço
                Endereco endereco = new Endereco();
                endereco.setIdEndereco(rset.getInt("idEndereco"));

                // criando o objeto empresa
                Empresa empresa = new Empresa();
                empresa.setCnpj(rset.getString("idEmpresa"));
                // criando o objeto frigorifico
                Frigorificos frigorifico = new Frigorificos(
                        rset.getInt("id"),
                        empresa,
                        StatusArmazenamento.valueOf(rset.getString("status")),
                        endereco,
                        rset.getString("tipo")
                );
                listaFrigorificos.add(frigorifico);
            }
        }catch (SQLException sqle){
            sqle.printStackTrace();
        }finally {
            conexao.desconectar(conn);// fechando a conexao
        }
        return listaFrigorificos;
    }// ==============BUSCAR TODOS OS FRIGORIFICOS DA EMPRESA PELO ID DA MESMA ===============
}
