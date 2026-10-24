package com.example.servlet.Dao;

import com.example.servlet.Model.Empresa;
import com.example.servlet.Util.Conexao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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



    //==============DELETAR=============
    public int excluirEmpresa(Empresa empresa){
        Conexao conexao = new Conexao(); // abrindo a conexão
        Connection conn = conexao.conectar();

        try{
            // apagar a empresa apaga tudo dela pelo ON DELETE CASCADE, mas os produtos e os enderecos ficariam soltos
            // por isso tudo acontece na mesma transação
            conn.setAutoCommit(false); // nada é salvo de verdade até o commit

            // guarda os produtos dos itens da empresa antes de apagar ela
            String comandoSqlProdutos = "select itf.idProduto from ItemFrigorifico itf join Frigorifico f on f.id = itf.idFrigorifico where f.idEmpresa = ? " +
                    "union select il.idProduto from ItemLista il join ListaCompras lc on lc.id = il.idListaCompras where lc.idEmpresa = ? " +
                    "union select i.idProduto from Ingrediente i join Receita r on r.id = i.idReceita where r.idEmpresa = ?;";
            List<Integer> idsProdutos = ProdutosGeladeiraDAO.buscarIds(conn, comandoSqlProdutos, empresa.getCnpj());

            // guarda os enderecos dos frigorificos da empresa
            String comandoSqlEnderecos = "select idEndereco from Frigorifico where idEmpresa = ?;";
            List<Integer> idsEnderecos = ProdutosGeladeiraDAO.buscarIds(conn, comandoSqlEnderecos, empresa.getCnpj());

            String comandoSql = "delete from empresa where cnpj = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // setando os valores para o comando
            pstmt.setString(1, empresa.getCnpj());

            if (pstmt.executeUpdate() <= 0){
                conn.rollback();
                return 0;// não deu certo
            }

            // apaga o que sobrou sem ninguém usando
            ProdutosGeladeiraDAO.apagarProdutosOrfaos(conn, idsProdutos);
            EnderecoDAO.apagarEnderecos(conn, idsEnderecos);

            conn.commit();
            return 1;// verifica se deu certo
        }
        catch (SQLException sqle){
            sqle.printStackTrace();
            try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            return -1;
        }
        finally {
            conexao.desconectar(conn);// fechando a conexão
        }
    }// ==============DELETAR===============



    // ================ BUSCAR UMA EMPRESA PELO CNPJ ================
    public Empresa buscarEmpresaPorCnpj(String cnpj){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();

        Empresa empresa = null;
        ResultSet rset = null; // declarando o Result Set

        try{
            String comandoSql = "select * from empresa where cnpj = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // setando os valores para a execução
            pstmt.setString(1, cnpj);
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
    }// ================ BUSCAR UMA EMPRESA PELO CNPJ ================



    // ================ BUSCAR TODAS AS EMPRESAS ================
    public List<Empresa> buscarTodasEmpresa(){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();

        List<Empresa> lista = new ArrayList<>();
        ResultSet rset = null; // declarando o Result Set

        try{
            String comandoSql = "select * from empresa order by nome;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            rset = pstmt.executeQuery();

            while (rset.next()){
                Empresa empresa = new Empresa(
                        rset.getString("cnpj"),
                        rset.getString("nome"),
                        rset.getString("email"),
                        rset.getString("senha")
                );
                lista.add(empresa);
            }


        }catch (SQLException sqle){
            sqle.printStackTrace();
        }
        finally {
            conexao.desconectar(conn);
        }
        return lista;
    }// ================ BUSCAR TODAS AS EMPRESAS ================



    // ================ TOTAL DE EMPRESAS ================
    public int totalEmpresas(){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();

        ResultSet rset = null;
        int total = 0;

        try{
            String comandoSql = "select count(*) as total from empresa;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            rset = pstmt.executeQuery();

            if (rset.next()) total = rset.getInt("total");

        }catch (SQLException sqle){
            sqle.printStackTrace();
        }
        finally {
            conexao.desconectar(conn);
        }
        return total;
    }// ================ TOTAL DE EMPRESAS ================
}
