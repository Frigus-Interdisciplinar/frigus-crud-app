package com.example.servlet.Dao;

import com.example.servlet.Model.Ingredientes;
import com.example.servlet.Model.Produto;
import com.example.servlet.Model.Receita;
import com.example.servlet.Util.Conexao;
import com.example.servlet.Util.Enum.Categoria;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class IngredienteDAO {
    // ========== INSERIR ==========
    public int inserirIngrediente(Ingredientes ingrediente){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();// abrindo a conexao com o banco

        try{
            String comandoSql = "insert into ingredientes (idReceita, idProdutos, quantidade) values (?,?,?);";
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
            else{
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
    public int alterarIngrediente(Ingredientes ingrediente){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();// abrindo a conexao com o banco

        try{
            String comandoSql = "update ingredientes set idReceita = ?, idProdutos = ?, quantidade = ? where id = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);

            // setando os valores para a operação
            pstmt.setInt(1, ingrediente.getReceita().getIdReceita());
            pstmt.setInt(2, ingrediente.getProduto().getIdProdutos());
            pstmt.setInt(3, ingrediente.getQuantidade());
            pstmt.setInt(4, ingrediente.getIdIngredientes());

            if (pstmt.executeUpdate() > 0){
                return 1; // verifica se deu certo
            }
            else{
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
            String comandoSql = "delete from ingredientes where id = ?";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);

            // setando os valores para a operação
            pstmt.setInt(1, ingrediente.getIdIngredientes());

            if (pstmt.executeUpdate() > 0){
                return 1; // verifica se deu certo
            }
            else{
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

    // ============= BUSCAR 1 INGREDIENTE(PRODUTO) POR ID =============
    public Ingredientes buscarIngrediente(Ingredientes ingrediente){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();// abrindo a conexao com o banco

        ResultSet rset = null;

        try {
            String comandoSql = "Select i.id as idIngrediente, i.idReceita, i.idProdutos, i.quantidade, p.*, r.* from ingredientes i join produtos p on p.id = i.idProdutos join receita r on r.id = i.idReceita where i.id = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // setando o id
            pstmt.setInt(1, ingrediente.getIdIngredientes());
            rset = pstmt.executeQuery();

            if (rset.next()){
                // criando o objeto receita
                Receita receita = new Receita();
                receita.setIdReceita(rset.getInt("idReceita"));
                // criando o objeto Produto
                Produto produto = new Produto();
                produto.setIdProdutos(rset.getInt("idProdutos"));
                // criando o objeto ingrediente
                ingrediente = new Ingredientes(
                        rset.getInt("idIngrediente"),
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
    }// ============= BUSCAR 1 INGREDIENTE(PRODUTO) POR ID =============


    //METODO PARA BUSCAR 1 RECEITA
    public Ingredientes buscarReceita(Receita receita){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();// abrindo a conexao com o banco

        ResultSet rset = null;
        Ingredientes ingrediente = null;

        try {
            String comandoSql = "Select i.*, p.*, r.id, r.nome, r.descricao, r.modoPreparo, r.dtReceita, r.foiFeita from ingredientes i join produtos p on p.id = i.idProdutos join receita r on r.id = i.idReceita where r.id = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // setando o id
            pstmt.setInt(1, receita.getIdReceita());
            rset = pstmt.executeQuery();

            if (rset.next()){
                // criando o objeto produto
                Produto produto = new Produto(
                        rset.getInt("idProdutos"),
                        rset.getString("nome"),
                        rset.getString("descricao"),
                        rset.getDouble("preco"),
                        Categoria.valueOf(rset.getString("categoria")),
                        rset.getString("marca"),
                        rset.getString("unidadeMedida")
                );

                // criando o objeto receita
                receita = new Receita(
                        rset.getInt("idReceita"),
                        rset.getString("nome"),
                        rset.getString("descricao"),
                        rset.getString("modoPreparo"),
                        rset.getObject("dtReceita", LocalDate.class),
                        rset.getBoolean("foiFeita")
                );
                // criando o objeto ingrediente
                ingrediente = new Ingredientes(
                        rset.getInt("idIngredientes"),
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
    }//METODO PARA BUSCAR 1 RECEITA



    // metodo para buscar as 10 primeiras receitas possiveis ordenadas ALEATORIAMENTE
    public List<Ingredientes> buscarVariasReceitas(){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();// abrindo a conexao com o banco

        ResultSet rset = null;
        List<Ingredientes> receitas = new ArrayList<>();

        try{
            String comandoSql = "Select i.*, p.*, r.id, r.nome, r.descricao, r.modoPreparo, r.dtReceita, r.foiFeita "
            + "from ingredientes i join produtos p on p.id = i.idProdutos join receita r on r.id = i.idReceita "
            + "order by random() limit 10;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            rset = pstmt.executeQuery();

            while (rset.next()){
                // criando o objeto produto
                Produto produto = new Produto(
                        rset.getInt("idProdutos"),
                        rset.getString("nome"),
                        rset.getString("descricao"),
                        rset.getDouble("preco"),
                        Categoria.valueOf(rset.getString("categoria")),
                        rset.getString("marca"),
                        rset.getString("unidadeMedida")
                );

                // criando o objeto receita
                Receita receita = new Receita(
                        rset.getInt("idReceita"),
                        rset.getString("nome"),
                        rset.getString("descricao"),
                        rset.getString("modoPreparo"),
                        rset.getObject("dtReceita", LocalDate.class),
                        rset.getBoolean("foiFeita")
                );
                // criando o objeto ingrediente
                Ingredientes ingredientes = new Ingredientes(
                        rset.getInt("idIngredientes"),
                        receita,
                        produto,
                        rset.getInt("quantidade")
                );
                receitas.add(ingredientes);
            }
        }catch (SQLException sqle){
            sqle.printStackTrace();
        }finally {
            conexao.desconectar(conn); // fechando a conexao com o banco
        }
        return receitas;
    }// metodo para buscar as 10 primeiras receitas possiveis ordenadas ALEATORIAMENTE
}
