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
    public int alterarIngrediente(Ingredientes ingredienteAntigo, Ingredientes ingredienteNovo){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();// abrindo a conexao com o banco

        try{
            String comandoSql = "update Ingrediente set idReceita = ?, idProduto = ?, quantidade = ? where id = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);

            // setando os valores para a operação
            pstmt.setInt(1, ingredienteNovo.getReceita().getIdReceita());
            pstmt.setInt(2, ingredienteNovo.getProduto().getIdProdutos());
            pstmt.setInt(3, ingredienteNovo.getQuantidade());
            pstmt.setInt(4, ingredienteAntigo.getIdIngredientes());

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
            String comandoSql = "delete from Ingrediente where id = ?";
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
    public Ingredientes buscarIngrediente(int idIngrediente){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();// abrindo a conexao com o banco

        ResultSet rset = null;
        Ingredientes ingrediente = null;

        try {
            String comandoSql = "SELECT " +
                    "i.id AS idIngrediente, " +
                    "i.quantidade AS quantidadeIngrediente, " +

                    "p.id AS idProduto, " +
                    "p.nome AS nomeProduto, " +
                    "p.descricao AS descricaoProduto, " +
                    "p.preco, " +
                    "p.categoria, " +
                    "p.marca, " +
                    "p.unidadeMedida, " +

                    "r.id AS idReceita, " +
                    "r.nome AS nomeReceita, " +
                    "r.descricao AS descricaoReceita, " +
                    "r.modoPreparo, " +
                    "r.dtReceita, " +
                    "r.foiFeita "  +
                    " from Ingrediente i join Produto p on p.id = i.idProduto join receita r on r.id = i.idReceita where i.id = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // setando o id
            pstmt.setInt(1, idIngrediente);
            rset = pstmt.executeQuery();

            if (rset.next()){
                // criando o objeto receita
                Receita receita = new Receita(
                        rset.getInt("idReceita"),
                        rset.getString("nomeReceita"),
                        rset.getString("descricaoReceita"),
                        rset.getString("modoPreparo"),
                        rset.getObject("dtReceita", LocalDate.class),
                        rset.getBoolean("foiFeita")
                );
                // criando o objeto Produto
                Produto produto = new Produto(
                        rset.getInt("idProduto"),
                        rset.getString("nomeProduto"),
                        rset.getString("descricaoProduto"),
                        rset.getDouble("preco"),
                        Categoria.valueOf(rset.getString("categoria")),
                        rset.getString("marca"),
                        rset.getString("unidadeMedida")
                );
                // criando o objeto ingrediente
                ingrediente = new Ingredientes(
                        rset.getInt("idIngrediente"),
                        receita,
                        produto,
                        rset.getInt("quantidadeIngrediente")
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
    public List<Ingredientes> buscarReceita(String nome){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();// abrindo a conexao com o banco

        ResultSet rset = null;
        List<Ingredientes> lista = new ArrayList<>();

        try {
            String comandoSql = "SELECT " +
                    "i.id AS idIngrediente, " +
                    "i.quantidade AS quantidadeIngrediente, " +

                    "p.id AS idProduto, " +
                    "p.nome AS nomeProduto, " +
                    "p.descricao AS descricaoProduto, " +
                    "p.preco, " +
                    "p.categoria, " +
                    "p.marca, " +
                    "p.unidadeMedida, " +

                    "r.id AS idReceita, " +
                    "r.nome AS nomeReceita, " +
                    "r.descricao AS descricaoReceita, " +
                    "r.modoPreparo, " +
                    "r.dtReceita, " +
                    "r.foiFeita " +

                    "FROM Ingrediente i " +
                    "JOIN Produto p ON p.id = i.idProduto " +
                    "JOIN receita r ON r.id = i.idReceita " +
                    "WHERE lower(r.nome) like ?";

            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // setando o id
            pstmt.setString(1, nome);
            rset = pstmt.executeQuery();

            while (rset.next()){
                // criando o objeto produto
                Produto produto = new Produto(
                        rset.getInt("idProduto"),
                        rset.getString("nomeProduto"),
                        rset.getString("descricaoProduto"),
                        rset.getDouble("preco"),
                        Categoria.valueOf(rset.getString("categoria")),
                        rset.getString("marca"),
                        rset.getString("unidadeMedida")
                );

                // criando o objeto receita
                Receita receita = new Receita(
                        rset.getInt("idReceita"),
                        rset.getString("nomeReceita"),
                        rset.getString("descricaoReceita"),
                        rset.getString("modoPreparo"),
                        rset.getObject("dtReceita", LocalDate.class),
                        rset.getBoolean("foiFeita")
                );
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
    }//METODO PARA BUSCAR 1 RECEITA



    // metodo para buscar as 10 primeiras receitas possiveis ordenadas ALEATORIAMENTE
    public List<Ingredientes> buscarVariasReceitas(){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();// abrindo a conexao com o banco

        ResultSet rset = null;
        List<Ingredientes> receitas = new ArrayList<>();

        try{
            String comandoSql = "SELECT " +
                    "i.id AS idIngrediente, " +
                    "i.quantidade AS quantidadeIngrediente, " +

                    "p.id AS idProduto, " +
                    "p.nome AS nomeProduto, " +
                    "p.descricao AS descricaoProduto, " +
                    "p.preco, " +
                    "p.categoria, " +
                    "p.marca, " +
                    "p.unidadeMedida, " +

                    "r.id AS idReceita, " +
                    "r.nome AS nomeReceita, " +
                    "r.descricao AS descricaoReceita, " +
                    "r.modoPreparo, " +
                    "r.dtReceita, " +
                    "r.foiFeita " +

                    "FROM Ingrediente i " +
                    "JOIN Produto p ON p.id = i.idProduto " +
                    "JOIN (" +
                    "select * from receita order by random() limit 10 )" +
                    " r ON r.id = i.idReceita;";

            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            rset = pstmt.executeQuery();

            while (rset.next()){
                // criando o objeto produto
                Produto produto = new Produto(
                        rset.getInt("idProduto"),
                        rset.getString("nomeProduto"),
                        rset.getString("descricaoProduto"),
                        rset.getDouble("preco"),
                        Categoria.valueOf(rset.getString("categoria")),
                        rset.getString("marca"),
                        rset.getString("unidadeMedida")
                );

                // criando o objeto receita
                Receita receita = new Receita(
                        rset.getInt("idReceita"),
                        rset.getString("nomeReceita"),
                        rset.getString("descricaoReceita"),
                        rset.getString("modoPreparo"),
                        rset.getObject("dtReceita", LocalDate.class),
                        rset.getBoolean("foiFeita")
                );
                // criando o objeto ingrediente
                Ingredientes ingredientes = new Ingredientes(
                        rset.getInt("idIngrediente"),
                        receita,
                        produto,
                        rset.getInt("quantidadeIngrediente")
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
