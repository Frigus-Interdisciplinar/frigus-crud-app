package com.example.servlet.Dao;

import com.example.servlet.Model.Ingredientes;
import com.example.servlet.Model.Produto;
import com.example.servlet.Model.Receita;
import com.example.servlet.Util.Conexao;
import com.example.servlet.Util.Enum.Categoria;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class IngredienteDAO {
    // o ingrediente é um produto que ja existe usado numa receita ( ele nao cria nem apaga o produto )

    // ========== INSERIR ==========
    public int adicionarIngrediente(Ingredientes ingrediente){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();// abrindo a conexao com o banco

        try{
            String comandoSql = "insert into Ingrediente (idReceita, idProduto, quantidade) values (?,?,?);";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql, Statement.RETURN_GENERATED_KEYS);

            // setando os valores para a operação
            pstmt.setInt(1, ingrediente.getReceita().getIdReceita());
            pstmt.setInt(2, ingrediente.getProduto().getIdProdutos());
            pstmt.setInt(3, ingrediente.getQuantidade());

            if (pstmt.executeUpdate() > 0){
                ResultSet chaves = pstmt.getGeneratedKeys();// adicionando a chave criada pelo banco
                if (chaves.next()) ingrediente.setIdIngredientes(chaves.getInt(1));
                return 1; // verifica se deu certo
            }
            else {
                return 0; // rejeitado!!
            }
        }
        catch (SQLException sqle){
            sqle.printStackTrace();
            return -1;
        }
        finally {
            conexao.desconectar(conn);// fechando a conexao com o banco
        }

    }// ========== INSERIR ==========



    // ========== ALTERAR ==========
    public int alterarIngrediente(Ingredientes ingredienteAntigo, Ingredientes ingredienteNovo){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();// abrindo a conexao com o banco

        try{
            String comandoSql = "update Ingrediente set idProduto = ?, quantidade = ? where id = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);

            // setando os valores para a operação
            pstmt.setInt(1, ingredienteNovo.getProduto().getIdProdutos());
            pstmt.setInt(2, ingredienteNovo.getQuantidade());
            pstmt.setInt(3, ingredienteAntigo.getIdIngredientes());

            if (pstmt.executeUpdate() > 0){
                return 1; // verifica se deu certo
            }
            else {
                return 0; // rejeitado!!
            }
        }
        catch (SQLException sqle){
            sqle.printStackTrace();
            return -1;
        }
        finally {
            conexao.desconectar(conn);// fechando a conexao com o banco
        }

    }// ========== ALTERAR ==========


    // ========== DELETAR ==========
    public int deletarIngrediente(Ingredientes ingrediente){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();// abrindo a conexao com o banco

        try{
            // so o ingrediente sai da receita, o produto continua existindo
            String comandoSql = "delete from Ingrediente where id = ?";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);

            // setando os valores para a operação
            pstmt.setInt(1, ingrediente.getIdIngredientes());

            if (pstmt.executeUpdate() > 0){
                return 1; // verifica se deu certo
            }
            else {
                return 0; // rejeitado!!
            }
        }
        catch (SQLException sqle){
            sqle.printStackTrace();
            return -1;
        }
        finally {
            conexao.desconectar(conn);// fechando a conexao com o banco
        }

    }// ========== DELETAR ==========


    // ========== BUSCAR ==========

    // ============= BUSCAR 1 INGREDIENTE POR ID =============
    public Ingredientes buscarIngrediente(int idIngrediente){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();// abrindo a conexao com o banco

        ResultSet rset = null;
        Ingredientes ingrediente = null;

        try {
            String comandoSql = "select * from Ingrediente where id = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // setando o id
            pstmt.setInt(1, idIngrediente);
            rset = pstmt.executeQuery();

            if (rset.next()){
                // criando o objeto receita
                Receita receita = new Receita();
                receita.setIdReceita(rset.getInt("idReceita"));
                // criando o objeto Produto
                Produto produto = new Produto();
                produto.setIdProdutos(rset.getInt("idProduto"));
                // criando o objeto ingrediente
                ingrediente = new Ingredientes(
                        rset.getInt("id"),
                        receita,
                        produto,
                        rset.getInt("quantidade")
                );
            }
        }catch (SQLException sqle){
            sqle.printStackTrace();
        }finally {
            conexao.desconectar(conn);// fechando a conexao com o banco
        }
        return ingrediente;
    }// ============= BUSCAR 1 INGREDIENTE POR ID =============


    // ============= BUSCAR OS INGREDIENTES DE UMA RECEITA =============
    public List<Ingredientes> buscarIngredientesDaReceita(int idReceita){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();// abrindo a conexao com o banco

        ResultSet rset = null;
        List<Ingredientes> lista = new ArrayList<>();

        try{
            String comandoSql = "select i.id as idIngrediente, i.quantidade as quantidadeIngrediente, " +
                    "p.id as idProduto, p.nome, p.descricao, p.preco, p.categoria, p.marca, p.unidadeMedida " +
                    "from Ingrediente i " +
                    "join Produto p on p.id = i.idProduto " +
                    "where i.idReceita = ? " +
                    "order by i.id;";

            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // setando o id da receita
            pstmt.setInt(1, idReceita);
            rset = pstmt.executeQuery();

            while (rset.next()){
                // criando o objeto produto
                Produto produto = new Produto(
                        rset.getInt("idProduto"),
                        rset.getString("nome"),
                        rset.getString("descricao"),
                        rset.getDouble("preco"),
                        Categoria.valueOf(rset.getString("categoria")),
                        rset.getString("marca"),
                        rset.getString("unidadeMedida")
                );
                // criando a receita so com o id
                Receita receita = new Receita();
                receita.setIdReceita(idReceita);
                // criando o objeto ingrediente
                Ingredientes ingrediente = new Ingredientes(
                        rset.getInt("idIngrediente"),
                        receita,
                        produto,
                        rset.getInt("quantidadeIngrediente")
                );
                lista.add(ingrediente);
            }
        }catch (SQLException sqle){
            sqle.printStackTrace();
        }finally {
            conexao.desconectar(conn);// fechando a conexao com o banco
        }
        return lista;
    }// ============= BUSCAR OS INGREDIENTES DE UMA RECEITA =============


    // ============= BUSCAR OS PRODUTOS QUE PODEM VIRAR INGREDIENTE =============
    public List<Produto> buscarTodosProdutos(){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();// abrindo a conexao com o banco

        ResultSet rset = null;
        List<Produto> lista = new ArrayList<>();

        try{
            String comandoSql = "select * from Produto order by nome, marca, id;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            rset = pstmt.executeQuery();

            while (rset.next()){
                Produto produto = new Produto(
                        rset.getInt("id"),
                        rset.getString("nome"),
                        rset.getString("descricao"),
                        rset.getDouble("preco"),
                        Categoria.valueOf(rset.getString("categoria")),
                        rset.getString("marca"),
                        rset.getString("unidadeMedida")
                );
                lista.add(produto);
            }
        }catch (SQLException sqle){
            sqle.printStackTrace();
        }finally {
            conexao.desconectar(conn);// fechando a conexao com o banco
        }
        return lista;
    }// ============= BUSCAR OS PRODUTOS QUE PODEM VIRAR INGREDIENTE =============
}
