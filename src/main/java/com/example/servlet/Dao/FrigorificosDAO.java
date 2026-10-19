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
            String comandoSql = "insert into Frigorifico (idEmpresa, status, idEndereco, tipo, nome) values (?,?,?,?,?);";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql, Statement.RETURN_GENERATED_KEYS);
            // setando os valores para o comando
            pstmt.setString(1, frigorifico.getEmpresa().getCnpj());
            pstmt.setString(2, frigorifico.getStatus().name());
            pstmt.setInt(3, frigorifico.getEndereco().getIdEndereco());
            pstmt.setString(4, frigorifico.getTipo());
            pstmt.setString(5, frigorifico.getNome());

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
    public int alterarFrigorifico(Frigorificos frigorificoAntigo, Frigorificos frigorificoNovo){
        Conexao conexao = new Conexao(); // abrindo a conexão
        Connection conn = conexao.conectar();

        try{
            String comandoSql = "update Frigorifico set idEmpresa = ?, status = ?, idEndereco = ?, tipo = ?, nome = ?  where id = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // setando os valores para o comando
            pstmt.setString(1, frigorificoNovo.getEmpresa().getCnpj());
            pstmt.setString(2, frigorificoNovo.getStatus().name());
            pstmt.setInt(3, frigorificoNovo.getEndereco().getIdEndereco());
            pstmt.setString(4, frigorificoNovo.getTipo());
            pstmt.setString(5, frigorificoNovo.getNome());
            pstmt.setInt(6, frigorificoAntigo.getIdFrigorifico());

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
            String comandoSlq = "update Frigorifico set status = ? where id = ?";
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
            String comandoSql = "select * from Frigorifico where idEmpresa = ?;";
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
                        rset.getString("tipo"),
                        rset.getString("nome")
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


    // TOTAL DE FRIGORIFICOS
    public int totalFrigorificosEmpresa(String cnpj){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();
        int total = 0;
        ResultSet rset = null;
        try {
            String comandoSql = "select count(*) as total from Frigorifico where idEmpresa = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            pstmt.setString(1, cnpj);
            rset = pstmt.executeQuery();

            if(rset.next()) total = rset.getInt("total");

        }catch (SQLException sqle){
            sqle.printStackTrace();
        }
        finally {
            conexao.desconectar(conn);
        }
        return total;
    }
    // TOTAL DE FRIGORIFICOS
}
