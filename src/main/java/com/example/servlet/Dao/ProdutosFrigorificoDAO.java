package com.example.servlet.Dao;

import com.example.servlet.Model.*;
import com.example.servlet.Util.Conexao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProdutosFrigorificoDAO {

    // ===========INSERIR===========
    public int inserirProdutoFrigorifico(ProdutosFrigorifico pf){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();

        try{
            String comandoSql = "insert into ProdutosFrigorifico (idNotificacao, idFrigorificos, idProdutos, dtValidade, status, lote) values (?,?,?,?,?,?);";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);

            // setando os valores pra a instrução
            pstmt.setInt(1, pf.getIdNotificacao());
            pstmt.setInt(2, pf.getFrigorificos().getIdFrigorifico());
            pstmt.setInt(3, pf.getProduto().getIdProdutos());
            pstmt.setObject(4, pf.getDtValidade());
            pstmt.setString(5, pf.getStatus());
            pstmt.setInt(6, pf.getLote());

            if (pstmt.executeUpdate() > 0){
                return 1;
            }
            else{
                return 0;
            }
        }
        catch (SQLException sqle){
            sqle.printStackTrace();
            return -1;
        }
        finally {
            conexao.desconectar(conn);
        }
    }// ===========INSERIR===========




    // ===========ALTERAR===========
    public int alterarProdutoFrigorifico(ProdutosFrigorifico pf){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();

        try{
            String comandoSql = "update ProdutosFrigorifico set idNotificacao = ?, idFrigrificos = ?, idProdutos = ?, dtValidade = ?, status = ?, lote = ? where id = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);

            // setando os valores pra a instrução
            pstmt.setInt(1, pf.getIdNotificacao());
            pstmt.setInt(2, pf.getFrigorificos().getIdFrigorifico());
            pstmt.setInt(3, pf.getProduto().getIdProdutos());
            pstmt.setObject(4, pf.getDtValidade());
            pstmt.setString(5, pf.getStatus());
            pstmt.setInt(6, pf.getLote());
            pstmt.setInt(7, pf.getIdProdutosFrigorifico());

            if (pstmt.executeUpdate() > 0){
                return 1;
            }
            else{
                return 0;
            }
        }
        catch (SQLException sqle){
            sqle.printStackTrace();
            return -1;
        }
        finally {
            conexao.desconectar(conn);
        }
    }// ===========ALTERAR===========



    // ===========DELETAR===========
    public int deletarProdutoFrigorifico(ProdutosFrigorifico pf){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();

        try{
            String comandoSql = "delete from ProdutosFrigorifico where id = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);

            // setando os valores pra a instrução
            pstmt.setInt(1, pf.getIdProdutosFrigorifico());

            if (pstmt.executeUpdate() > 0){
                return 1;
            }
            else{
                return 0;
            }
        }
        catch (SQLException sqle){
            sqle.printStackTrace();
            return -1;
        }
        finally {
            conexao.desconectar(conn);
        }
    }// ===========DELETAR===========






    // ========= BUSCAR =========

    // LISTA DE TODOS OS PRODUTOS
    public List<ProdutosFrigorifico> buscarProdutoFrigorificos(){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar(); // abrindo a conexão com o banco

        List<ProdutosFrigorifico> lista = new ArrayList<>(); // cria a lista de todos os produtos
        ResultSet rset = null; // consulta a tabela

        try{
            String comandoSql = "select p.*, pf.*, f.* from produtos p join produtosFrigorifico pf on p.id = pf.idProdutos join frigorificos f on pf.idFrigorificos = f.id;"; // procura as informações dos produtos armazenados na tabela produtos Armazenados
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            rset = pstmt.executeQuery(); // executa a consulta SQL

            // armazena os valores em um list<>
            while (rset.next()){
                Produto produto = new Produto(
                        rset.getInt("idProduto"),
                        rset.getString("nome"),
                        rset.getString("descricao"),
                        rset.getDouble("preco"),
                        rset.getString("categoria"),
                        rset.getString("marca"),
                        rset.getString("unidademedida")
                );

                // criando o objeto endereco com id vindo do result set
                Endereco endereco = new Endereco();
                endereco.setIdEndereco(rset.getInt("idendereco"));

                // criando o objeto empresa com id vindo do result set
                Empresa empresa = new Empresa();
                empresa.setCnpj(rset.getString("cnpj"));

                // criando o objeto frigorifico com o ID vindo do ResultSet
                Frigorificos frigorifico = new Frigorificos(
                        rset.getInt("idFrigorifico"),
                        empresa,
                        rset.getInt("capacidade"),
                        rset.getString("status"),
                        rset.getInt("quantidade"),
                        endereco,
                        rset.getString("tipo")
                );
                // criando o objeto Produto no frigorifico com o ID vindo do ResultSet
                ProdutosFrigorifico pf = new ProdutosFrigorifico(
                        rset.getInt("idProdutosFrigorifico"),
                        rset.getInt("idNotificacao"),
                        frigorifico,
                        produto,
                        rset.getObject("dtValidade", LocalDate.class),
                        rset.getString("status"),
                        rset.getInt("lote")
                );
                lista.add(pf);
            }
        }
        catch (SQLException sqle){
            sqle.printStackTrace();
        }
        finally {
            conexao.desconectar(conn); // fechando a conexão
        }
        return lista;
    } // LISTA DE TODOS OS PRODUTOS





    // BUSCAR PRODUTOS NA TABELA produtoFrigorificos PELO LOTE
    public List<ProdutosFrigorifico> buscarProdutosFrigorificosPeloLote(int lote){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();

        List<ProdutosFrigorifico> lista = new ArrayList<>();
        ResultSet rset = null;

        try{
            String comandoSql = "select p.*, pf.*, f.* from produtos p join produtosFrigorifico pf on p.id = pf.idProdutos join frigorificos f on pf.idFrigorificos = f.id where pf.lote = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            pstmt.setInt(1, lote);
            rset = pstmt.executeQuery();

            while (rset.next()){
                Produto produto = new Produto(
                        rset.getInt("idProduto"),
                        rset.getString("nome"),
                        rset.getString("descricao"),
                        rset.getDouble("preco"),
                        rset.getString("categoria"),
                        rset.getString("marca"),
                        rset.getString("unidademedida")
                );

                // criando o objeto endereco com id vindo do result set
                Endereco endereco = new Endereco();
                endereco.setIdEndereco(rset.getInt("idendereco"));

                // criando o objeto empresa com id vindo do result set
                Empresa empresa = new Empresa();
                empresa.setCnpj(rset.getString("cnpj"));

                // criando o objeto frigorifico com o ID vindo do ResultSet
                Frigorificos frigorifico = new Frigorificos(
                        rset.getInt("idFrigorifico"),
                        empresa,
                        rset.getInt("capacidade"),
                        rset.getString("status"),
                        rset.getInt("quantidade"),
                        endereco,
                        rset.getString("tipo")
                );
                // criando o objeto Produto no frigorifico com o ID vindo do ResultSet
                ProdutosFrigorifico pf = new ProdutosFrigorifico(
                        rset.getInt("idProdutosFrigorifico"),
                        rset.getInt("idNotificacao"),
                        frigorifico,
                        produto,
                        rset.getObject("dtValidade", LocalDate.class),
                        rset.getString("status"),
                        rset.getInt("lote")
                );
                lista.add(pf);
            }
        }
        catch (SQLException sqle){
            sqle.printStackTrace();
        }
        finally {
            conexao.desconectar(conn);
        }
        return lista;
    } // BUSCAR PRODUTOS NA TABELA produtoFrigorificos PELO LOTE





    // BUSCAR PRODUTOS PELA DATA DE VALIDADE
    public List<ProdutosFrigorifico> buscarProdutosFrigorificoPorValidade(LocalDate dtValidade){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();

        List<ProdutosFrigorifico> lista = new ArrayList<>();
        ResultSet rset = null;

        try{
            String comandoSql = "select p.*, pf.*, f.* from produtos p join produtosFrigorifico pf on p.id = pf.idProdutos join frigorificos f on pf.idFrigorificos = f.id where pf.DtValidade = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // setando valores para o comando
            pstmt.setObject(1, dtValidade);
            rset = pstmt.executeQuery();

            while(rset.next()){
                Produto produto = new Produto(
                        rset.getInt("idProduto"),
                        rset.getString("nome"),
                        rset.getString("descricao"),
                        rset.getDouble("preco"),
                        rset.getString("categoria"),
                        rset.getString("marca"),
                        rset.getString("unidademedida")
                );

                // criando o objeto endereco com id vindo do result set
                Endereco endereco = new Endereco();
                endereco.setIdEndereco(rset.getInt("idendereco"));

                // criando o objeto empresa com id vindo do result set
                Empresa empresa = new Empresa();
                empresa.setCnpj(rset.getString("cnpj"));

                // criando o objeto frigorifico com o ID vindo do ResultSet
                Frigorificos frigorifico = new Frigorificos(
                        rset.getInt("idFrigorifico"),
                        empresa,
                        rset.getInt("capacidade"),
                        rset.getString("status"),
                        rset.getInt("quantidade"),
                        endereco,
                        rset.getString("tipo")
                );
                // criando o objeto Produto no frigorifico com o ID vindo do ResultSet
                ProdutosFrigorifico pf = new ProdutosFrigorifico(
                        rset.getInt("idProdutosFrigorifico"),
                        rset.getInt("idNotificacao"),
                        frigorifico,
                        produto,
                        rset.getObject("dtValidade", LocalDate.class),
                        rset.getString("status"),
                        rset.getInt("lote")
                );
                lista.add(pf);
            }

        }
        catch (SQLException sqle){
            sqle.printStackTrace();
        }
        finally {
            conexao.desconectar(conn);
        }
        return lista;
    }// BUSCAR PRODUTOS PELA DATA DE VALIDADE






    // BUSCAR 1 PRODUTO NA TABELA produtoFrigorificos POR ID
    public ProdutosFrigorifico buscarProdutoFrigorificoPorId(Produto produto){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();

        ResultSet rset = null;
        ProdutosFrigorifico pf = null;

        try{
            String comandoSql = "select p.*, pf.*, f.* from produtos p join produtosFrigorifico pf on p.id = pf.idProdutos join frigorificos f on pf.idFrigorificos = f.id where p.id = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            pstmt.setInt(1,produto.getIdProdutos());// setando os parâmetros da instrução
            rset = pstmt.executeQuery();
            if (rset.next()){
                produto = new Produto(
                        rset.getInt("idProduto"),
                        rset.getString("nome"),
                        rset.getString("descricao"),
                        rset.getDouble("preco"),
                        rset.getString("categoria"),
                        rset.getString("marca"),
                        rset.getString("unidademedida")
                );

                // criando o objeto endereco com id vindo do result set
                Endereco endereco = new Endereco();
                endereco.setIdEndereco(rset.getInt("idendereco"));

                // criando o objeto empresa com id vindo do result set
                Empresa empresa = new Empresa();
                empresa.setCnpj(rset.getString("cnpj"));

                // criando o objeto frigorifico com o ID vindo do ResultSet
                Frigorificos frigorifico = new Frigorificos(
                        rset.getInt("idFrigorifico"),
                        empresa,
                        rset.getInt("capacidade"),
                        rset.getString("status"),
                        rset.getInt("quantidade"),
                        endereco,
                        rset.getString("tipo")
                );
                // criando o objeto Produto no frigorifico com o ID vindo do ResultSet
                pf = new ProdutosFrigorifico(
                        rset.getInt("idProdutosFrigorifico"),
                        rset.getInt("idNotificacao"),
                        frigorifico,
                        produto,
                        rset.getObject("dtValidade", LocalDate.class),
                        rset.getString("status"),
                        rset.getInt("lote")
                );
            }
        }
        catch (SQLException sqle){
            sqle.printStackTrace();
        }
        finally {
            conexao.desconectar(conn);
        }
        return pf;
    }// BUSCAR 1 PRODUTO NA TABELA produtoFrigorificos POR ID






    // BUSCAR 1 PRODUTO NA TABELA produtoFrigorificos PELO NOME
    public ProdutosFrigorifico buscarProdutoFrigorificoPeloNome(Produto produto){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();

        ResultSet rset = null;
        ProdutosFrigorifico pf = null;

        try{
            String comandoSql = "select p.*, pf.*, f.* from produtos p join produtosFrigorifico pf on p.id = pf.idProdutos join frigorificos f on pf.idFrigorificos = f.id where lower(p.nome) like ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            pstmt.setString(1, "%" + produto.getNome().toLowerCase() + "%");// setando os parâmetros da instrução
            rset = pstmt.executeQuery();
            if (rset.next()){
                produto = new Produto(
                        rset.getInt("idProduto"),
                        rset.getString("nome"),
                        rset.getString("descricao"),
                        rset.getDouble("preco"),
                        rset.getString("categoria"),
                        rset.getString("marca"),
                        rset.getString("unidademedida")
                );

                // criando o objeto endereco com id vindo do result set
                Endereco endereco = new Endereco();
                endereco.setIdEndereco(rset.getInt("idendereco"));

                // criando o objeto empresa com id vindo do result set
                Empresa empresa = new Empresa();
                empresa.setCnpj(rset.getString("cnpj"));

                // criando o objeto frigorifico com o ID vindo do ResultSet
                Frigorificos frigorifico = new Frigorificos(
                        rset.getInt("idFrigorifico"),
                        empresa,
                        rset.getInt("capacidade"),
                        rset.getString("status"),
                        rset.getInt("quantidade"),
                        endereco,
                        rset.getString("tipo")
                );
                // criando o objeto Produto no frigorifico com o ID vindo do ResultSet
                pf = new ProdutosFrigorifico(
                        rset.getInt("idProdutosFrigorifico"),
                        rset.getInt("idNotificacao"),
                        frigorifico,
                        produto,
                        rset.getObject("dtValidade", LocalDate.class),
                        rset.getString("status"),
                        rset.getInt("lote")
                );
            }
        }
        catch (SQLException sqle){
            sqle.printStackTrace();
        }
        finally {
            conexao.desconectar(conn);
        }
        return pf;
    }// BUSCAR 1 PRODUTO NA TABELA produtoFrigorificos PELO NOME

}
