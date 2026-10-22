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
        Produto produto = ingrediente.getProduto();
        Receita receita = ingrediente.getReceita();

        try{
            conn.setAutoCommit(false); // nada é salvo de verdade até o commit

            // insere o produto
            String comandoSqlProduto = "insert into Produto(nome, descricao, preco, categoria, marca, unidademedida) values (?,?,?,?,?,?);";
            PreparedStatement pstmtProduto = conn.prepareStatement(comandoSqlProduto, Statement.RETURN_GENERATED_KEYS);
            pstmtProduto.setString(1, produto.getNome());
            pstmtProduto.setString(2, produto.getDescricao());
            pstmtProduto.setDouble(3, produto.getPreco());
            pstmtProduto.setString(4, produto.getCategoria().name());
            pstmtProduto.setString(5, produto.getMarca());
            pstmtProduto.setString(6, produto.getUnidadeMedida());

            if (pstmtProduto.executeUpdate() <= 0){
                conn.rollback();
                return 0; // rejeitado!!
            }
            ResultSet chavesProduto = pstmtProduto.getGeneratedKeys();// adicionando a chave criada pelo banco
            if (chavesProduto.next()) produto.setIdProdutos(chavesProduto.getInt(1));

            // insere a receita, do usuário ou da empresa
            String comandoSqlReceita;
            if (receita.getUsuario() != null){
                comandoSqlReceita = "insert into receita (idUsuario, nome, descricao, modoPreparo, dtReceita, foiFeita) values (?,?,?,?,?,?);";
            }
            else {
                comandoSqlReceita = "insert into receita (idEmpresa, nome, descricao, modoPreparo, dtReceita, foiFeita) values (?,?,?,?,?,?);";
            }
            PreparedStatement pstmtReceita = conn.prepareStatement(comandoSqlReceita, Statement.RETURN_GENERATED_KEYS);
            if (receita.getUsuario() != null){
                pstmtReceita.setString(1, receita.getUsuario().getCpf());
            }
            else {
                pstmtReceita.setString(1, receita.getEmpresa().getCnpj());
            }
            pstmtReceita.setString(2, receita.getNome());
            pstmtReceita.setString(3, receita.getDescricao());
            pstmtReceita.setString(4, receita.getModoPreparo());
            pstmtReceita.setObject(5, receita.getDtReceita());
            pstmtReceita.setBoolean(6, receita.isFoiFeita());

            if (pstmtReceita.executeUpdate() <= 0){
                conn.rollback();
                produto.setIdProdutos(0);
                return 0; // rejeitado!!
            }
            ResultSet chavesReceita = pstmtReceita.getGeneratedKeys();// adicionando a chave criada pelo banco
            if (chavesReceita.next()) receita.setIdReceita(chavesReceita.getInt(1));

            String comandoSql = "insert into Ingrediente (idReceita, idProduto, quantidade) values (?,?,?);";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql, Statement.RETURN_GENERATED_KEYS);

            // setando os valores para a operação
            pstmt.setInt(1, receita.getIdReceita());
            pstmt.setInt(2, produto.getIdProdutos());
            pstmt.setInt(3, ingrediente.getQuantidade());

            if (pstmt.executeUpdate() <= 0){
                conn.rollback();
                produto.setIdProdutos(0);
                receita.setIdReceita(0);
                return 0; // rejeitado!!
            }
            ResultSet chaves = pstmt.getGeneratedKeys();// adicionando a chave criada pelo banco
            if (chaves.next()) ingrediente.setIdIngredientes(chaves.getInt(1));

            conn.commit();
            return 1; // verifica se deu certo
        }
        catch (SQLException sqle){
            sqle.printStackTrace();
            try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            // o rollback torna esses ids falsos
            produto.setIdProdutos(0);
            receita.setIdReceita(0);
            ingrediente.setIdIngredientes(0);
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

        // o ingrediente continua ligado ao mesmo produto e à mesma receita, então os ids vêm do antigo
        int idProduto = ingredienteAntigo.getProduto().getIdProdutos();
        int idReceita = ingredienteAntigo.getReceita().getIdReceita();
        Produto produtoNovo = ingredienteNovo.getProduto();
        Receita receitaNova = ingredienteNovo.getReceita();

        try{
            conn.setAutoCommit(false); // nada é salvo de verdade até o commit

            // altera as informações do produto
            String comandoSqlProduto = "update Produto set nome = ?, descricao = ?, preco = ?, categoria = ?, marca = ?, unidademedida = ? where id = ?;";
            PreparedStatement pstmtProduto = conn.prepareStatement(comandoSqlProduto);
            pstmtProduto.setString(1, produtoNovo.getNome());
            pstmtProduto.setString(2, produtoNovo.getDescricao());
            pstmtProduto.setDouble(3, produtoNovo.getPreco());
            pstmtProduto.setString(4, produtoNovo.getCategoria().name());
            pstmtProduto.setString(5, produtoNovo.getMarca());
            pstmtProduto.setString(6, produtoNovo.getUnidadeMedida());
            pstmtProduto.setInt(7, idProduto);

            if (pstmtProduto.executeUpdate() <= 0){
                conn.rollback();
                return 0; // rejeitado!!
            }

            // altera a receita, do usuário ou da empresa
            String comandoSqlReceita;
            if (receitaNova.getUsuario() != null){
                comandoSqlReceita = "update receita set idUsuario = ?, nome = ?, descricao = ?, modoPreparo = ?, dtReceita = ?, foiFeita = ? where id = ? and idUsuario is not null;";
            }
            else {
                comandoSqlReceita = "update receita set idEmpresa = ?, nome = ?, descricao = ?, modoPreparo = ?, dtReceita = ?, foiFeita = ? where id = ? and idEmpresa is not null;";
            }
            PreparedStatement pstmtReceita = conn.prepareStatement(comandoSqlReceita);
            if (receitaNova.getUsuario() != null){
                pstmtReceita.setString(1, receitaNova.getUsuario().getCpf());
            }
            else {
                pstmtReceita.setString(1, receitaNova.getEmpresa().getCnpj());
            }
            pstmtReceita.setString(2, receitaNova.getNome());
            pstmtReceita.setString(3, receitaNova.getDescricao());
            pstmtReceita.setString(4, receitaNova.getModoPreparo());
            pstmtReceita.setObject(5, receitaNova.getDtReceita());
            pstmtReceita.setBoolean(6, receitaNova.isFoiFeita());
            pstmtReceita.setInt(7, idReceita);

            if (pstmtReceita.executeUpdate() <= 0){
                conn.rollback();
                return 0; // rejeitado!!
            }

            String comandoSql = "update Ingrediente set idReceita = ?, idProduto = ?, quantidade = ? where id = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);

            // setando os valores para a operação
            pstmt.setInt(1, idReceita);
            pstmt.setInt(2, idProduto);
            pstmt.setInt(3, ingredienteNovo.getQuantidade());
            pstmt.setInt(4, ingredienteAntigo.getIdIngredientes());

            if (pstmt.executeUpdate() <= 0){
                conn.rollback();
                return 0; // rejeitado!!
            }

            conn.commit();
            return 1; // verifica se deu certo
        }
        catch (SQLException sqle){
            sqle.printStackTrace();
            try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
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
            conn.setAutoCommit(false); // nada é salvo de verdade até o commit

            // o ingrediente sai primeiro porque ele referencia o produto e a receita
            String comandoSql = "delete from Ingrediente where id = ?";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);

            // setando os valores para a operação
            pstmt.setInt(1, ingrediente.getIdIngredientes());

            if (pstmt.executeUpdate() <= 0){
                conn.rollback();
                return 0; // rejeitado!!
            }

            String comandoSqlProduto = "delete from Produto where id = ? ";
            PreparedStatement pstmtProduto = conn.prepareStatement(comandoSqlProduto);
            pstmtProduto.setInt(1, ingrediente.getProduto().getIdProdutos());

            if (pstmtProduto.executeUpdate() <= 0){
                conn.rollback();
                return 0; // rejeitado!!
            }

            String comandoSqlReceita = "delete from receita where id = ?";
            PreparedStatement pstmtReceita = conn.prepareStatement(comandoSqlReceita);
            pstmtReceita.setInt(1, ingrediente.getReceita().getIdReceita());

            if (pstmtReceita.executeUpdate() <= 0){
                conn.rollback();
                return 0; // rejeitado!!
            }

            conn.commit();
            return 1; // verifica se deu certo
        }
        catch (SQLException sqle){
            sqle.printStackTrace();
            try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
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
