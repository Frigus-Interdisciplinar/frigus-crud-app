package com.example.servlet.Dao;

import com.example.servlet.Model.Geladeira;
import com.example.servlet.Model.Produto;
import com.example.servlet.Model.ProdutosGeladeira;
import com.example.servlet.Model.Usuario;
import com.example.servlet.Util.Conexao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProdutosGeladeiraDAO {

    //==============INSERIR===============
    public int inserirProdutoGeladeira(ProdutosGeladeira pg){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();

        try{
            // insere as informações relacionada aos produtos domesticos
            String comandoSql = "insert into ProdutosGeladeira (idNotificacao, idGeladeira, idProdutos, DtValidade, status, lote) values (?,?,?,?,?,?);";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // definindo parâmetros da instrução
            pstmt.setInt(1, pg.getIdNotificacao());
            pstmt.setInt(2, pg.getGeladeira().getIdGeladeira());
            pstmt.setInt(3, pg.getProduto().getIdProdutos());
            pstmt.setObject(4, pg.getDtValidade());
            pstmt.setString(5, pg.getStatus());
            pstmt.setInt(6, pg.getLote());

            if (pstmt.executeUpdate() > 0){
                return 1;// verifica se deu certo
            }
            else {
                return 0;// não foi posivel executar
            }
        }
        catch (SQLException sqle){
            sqle.printStackTrace();
            return -1;
        }
        finally {
            conexao.desconectar(conn);// desconectar
        }
    }//==============INSERIR===============



    //==============ATUALIZAR===============
    public int alterarProdutoGeladeira(ProdutosGeladeira pg){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();

        try{
            // altera as informações relacionada aos produtos domesticos
            String comandoSql = "Update ProdutosGeladeira set idNotificacao = ?, idGeladeira = ?, idProdutos = ?, DtValidade = ?, status = ?, lote = ? where id = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // definindo parâmetros da instrução
            pstmt.setInt(1, pg.getIdNotificacao());
            pstmt.setInt(2, pg.getGeladeira().getIdGeladeira());
            pstmt.setInt(3, pg.getProduto().getIdProdutos());
            pstmt.setObject(4, pg.getDtValidade());
            pstmt.setString(5, pg.getStatus());
            pstmt.setInt(6, pg.getLote());
            pstmt.setInt(7, pg.getIdProdutosGeladeira());

            if (pstmt.executeUpdate() > 0){
                return 1;// verifica se deu certo
            }
            else {
                return 0;// não foi posivel executar
            }
        }
        catch (SQLException sqle){
            sqle.printStackTrace();
            return -1;
        }
        finally {
            conexao.desconectar(conn);// desconectar
        }
    }//==============ATUALIZAR===============


    //==============DELETAR===============
    public int deletarProdutoGeladeira(ProdutosGeladeira pg){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();

        try{
            // deleta as informações relacionada aos produtos domesticos
            String comandoSql = "delete from ProdutosGeladeira where id = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // definindo parâmetros da instrução
            pstmt.setInt(1, pg.getIdProdutosGeladeira());


            if (pstmt.executeUpdate() > 0){
                return 1;// verifica se deu certo
            }
            else {
                return 0;// não foi posivel executar
            }
        }
        catch (SQLException sqle){
            sqle.printStackTrace();
            return -1;
        }
        finally {
            conexao.desconectar(conn);// desconectar
        }
    }//==============DELETAR===============









    // ========= BUSCAR =========


    // LISTA DE PRODUTOS PARA O DOMÉSTICO - TABELA produtoGeladeira
    public List<ProdutosGeladeira> buscarProdutoGeladeira(){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar(); // abrindo a conexão com o banco

        List<ProdutosGeladeira> lista = new ArrayList<>();// cria a lista de todos os produtos
        ResultSet rset = null; // consulta a tabela

        try{
            String comandoSql = "select p.*, pg.*, g.* from produtosGeladeira pg join produtos p on p.id = pg.idProdutos join geladeira g on g.id = pg.idGeladeira;"; // procura as informações dos produtos armazenados na tabela produtos Geladeira
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            rset = pstmt.executeQuery(); // executa a consulta SQL

            // armazena os valores em um list<>
            while (rset.next()){
                Produto produto = new Produto(
                        rset.getString("nome"),
                        rset.getString("descricao"),
                        rset.getDouble("preco"),
                        rset.getString("categoria"),
                        rset.getString("marca"),
                        rset.getString("unidademedida")
                );
                produto.setIdProdutos(rset.getInt("idProduto"));

                // criando o objeto usuario com id vindo do resultSet
                Usuario usuario = new Usuario();
                usuario.setCpf(rset.getString("cpf"));

                // Criando o objeto geladeira com o ID vindo do ResultSet
                Geladeira geladeira = new Geladeira(
                        usuario,
                        rset.getInt("Quantidade"),
                        rset.getString("Status"),
                        rset.getString("Tipo")
                );
                geladeira.setIdGeladeira(rset.getInt("idGeladeira"));
                // Criando o objeto Produto geladeira com o ID vindo do ResultSet
                ProdutosGeladeira pg = new ProdutosGeladeira(
                        rset.getInt("idNotificacao"),
                        geladeira,
                        produto,
                        rset.getObject("dtValidade", LocalDate.class),
                        rset.getString("status"),
                        rset.getInt("lote")
                );
                pg.setIdProdutosGeladeira(rset.getInt("idProdutosGeladeira"));
                lista.add(pg);
            }
        }
        catch (SQLException sqle){
            sqle.printStackTrace();
        }
        finally {
            conexao.desconectar(conn); // fechando a conexão
        }
        return lista;
    } // LISTA DE PRODUTOS PARA O DOMÉSTICO - TABELA produtoGeladeira





    // BUSCAR PRODUTOS NA TABELA produtoGeladeira PELO LOTE
    public List<ProdutosGeladeira> buscarProdutosGeladeiraPeloLote(int lote){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();

        List<ProdutosGeladeira> lista = new ArrayList<>();
        ResultSet rset = null;

        try{
            String comandoSql = "select p.*, pg.*, g.* from produtos p join produtosGeladeira pg on p.id = pg.idProdutos join geladeira g on g.id = pg.idGeladeira where pg.lote = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            pstmt.setInt(1, lote);
            rset = pstmt.executeQuery();

            while (rset.next()){
                Produto produto = new Produto(
                        rset.getString("nome"),
                        rset.getString("descricao"),
                        rset.getDouble("preco"),
                        rset.getString("categoria"),
                        rset.getString("marca"),
                        rset.getString("unidademedida")
                );
                produto.setIdProdutos(rset.getInt("idProduto"));

                // criando o objeto usuario com id vindo do resultSet
                Usuario usuario = new Usuario();
                usuario.setCpf(rset.getString("cpf"));

                // Criando o objeto geladeira com o ID vindo do ResultSet
                Geladeira geladeira = new Geladeira(
                        usuario,
                        rset.getInt("Quantidade"),
                        rset.getString("Status"),
                        rset.getString("Tipo")
                );
                geladeira.setIdGeladeira(rset.getInt("idGeladeira"));
                // Criando o objeto Produto geladeira com o ID vindo do ResultSet
                ProdutosGeladeira pg = new ProdutosGeladeira(
                        rset.getInt("idNotificacao"),
                        geladeira,
                        produto,
                        rset.getObject("dtValidade", LocalDate.class),
                        rset.getString("status"),
                        rset.getInt("lote")
                );
                pg.setIdProdutosGeladeira(rset.getInt("idProdutosGeladeira"));
                lista.add(pg);
            }
        }
        catch (SQLException sqle){
            sqle.printStackTrace();
        }
        finally {
            conexao.desconectar(conn);
        }
        return lista;
    } // BUSCAR PRODUTOS NA TABELA produtoGeladeira PELO LOTE


    // BUSCAR PRODUTOS PELA DATA DE VALIDADE
    public List<ProdutosGeladeira> buscarProdutosGeladeiraPorValidade(LocalDate dtValidade){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();

        List<ProdutosGeladeira> lista = new ArrayList<>();
        ResultSet rset = null;

        try{
            String comandoSql = "select p.*, pg.*, g.* from produtos p join produtosGeladeira pg on p.id = pg.idProdutos join geladeira g on g.id = pg.idGeladeira where pg.DtValidade = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // setando valores para o comando
            pstmt.setObject(1, dtValidade);
            rset = pstmt.executeQuery();

            while(rset.next()){
                Produto produto = new Produto(
                        rset.getString("nome"),
                        rset.getString("descricao"),
                        rset.getDouble("preco"),
                        rset.getString("categoria"),
                        rset.getString("marca"),
                        rset.getString("unidademedida")
                );
                produto.setIdProdutos(rset.getInt("idProduto"));

                // criando o objeto usuario com id vindo do resultSet
                Usuario usuario = new Usuario();
                usuario.setCpf(rset.getString("cpf"));

                // Criando o objeto geladeira com o ID vindo do ResultSet
                Geladeira geladeira = new Geladeira(
                        usuario,
                        rset.getInt("Quantidade"),
                        rset.getString("Status"),
                        rset.getString("Tipo")
                );
                geladeira.setIdGeladeira(rset.getInt("idGeladeira"));
                // Criando o objeto Produto geladeira com o ID vindo do ResultSet
                ProdutosGeladeira pg = new ProdutosGeladeira(
                        rset.getInt("idNotificacao"),
                        geladeira,
                        produto,
                        rset.getObject("dtValidade", LocalDate.class),
                        rset.getString("status"),
                        rset.getInt("lote")
                );
                pg.setIdProdutosGeladeira(rset.getInt("idProdutosGeladeira"));
                lista.add(pg);
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



    // BUSCAR 1 PRODUTO DA GELADEIRA POR ID DO PRODUTO
    public ProdutosGeladeira buscarProdutoGeladeiraPorId(Produto produto){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();

        ResultSet rset = null;
        ProdutosGeladeira pg = null;

        try{
            String comandoSql = "select p.*, pg.*, g.* from produtos p join produtosGeladeira pg on p.id = pg.idProdutos join geladeira g on g.id = pg.idGeladeira where p.id = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            pstmt.setInt(1,produto.getIdProdutos());// setando os parâmetros da instrução
            rset = pstmt.executeQuery();
            if (rset.next()){
                produto = new Produto(
                        rset.getString("nome"),
                        rset.getString("descricao"),
                        rset.getDouble("preco"),
                        rset.getString("categoria"),
                        rset.getString("marca"),
                        rset.getString("unidademedida")
                );
                produto.setIdProdutos(rset.getInt("idProduto"));

                // criando o objeto usuario com id vindo do resultSet
                Usuario usuario = new Usuario();
                usuario.setCpf(rset.getString("cpf"));

                // Criando o objeto geladeira com o ID vindo do ResultSet
                Geladeira geladeira = new Geladeira(
                        usuario,
                        rset.getInt("Quantidade"),
                        rset.getString("Status"),
                        rset.getString("Tipo")
                );
                geladeira.setIdGeladeira(rset.getInt("idGeladeira"));
                // Criando o objeto Produto geladeira com o ID vindo do ResultSet
                pg = new ProdutosGeladeira(
                        rset.getInt("idNotificacao"),
                        geladeira,
                        produto,
                        rset.getObject("dtValidade", LocalDate.class),
                        rset.getString("status"),
                        rset.getInt("lote")
                );
                pg.setIdProdutosGeladeira(rset.getInt("idProdutosGeladeira"));
            }
        }
        catch (SQLException sqle){
            sqle.printStackTrace();
        }
        finally {
            conexao.desconectar(conn);
        }
        return pg;
    }// BUSCAR 1 PRODUTO DA GELADEIRA POR ID DO PRODUTO




    // BUSCAR 1 PRODUTO NA TABELA produtoGeladeira PELO NOME
    public ProdutosGeladeira buscarProdutoGeladeiraPeloNome(Produto produto){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();

        ResultSet rset = null;
        ProdutosGeladeira pg = null;

        try{
            String comandoSql = "select p.*, pg.*, g.* from produtos p join produtosGeladeira pg on p.id = pg.idProdutos join geladeira g on g.id = pg.idGeladeira where lower(p.nome) like ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            pstmt.setString(1,  "%" + produto.getNome().toLowerCase() + "%");// setando os parâmetros da instrução
            rset = pstmt.executeQuery();
            if (rset.next()){
                produto = new Produto(
                        rset.getString("nome"),
                        rset.getString("descricao"),
                        rset.getDouble("preco"),
                        rset.getString("categoria"),
                        rset.getString("marca"),
                        rset.getString("unidademedida")
                );
                produto.setIdProdutos(rset.getInt("idProduto"));

                // criando o objeto usuario com id vindo do resultSet
                Usuario usuario = new Usuario();
                usuario.setCpf(rset.getString("cpf"));

                // Criando o objeto geladeira com o ID vindo do ResultSet
                Geladeira geladeira = new Geladeira(
                        usuario,
                        rset.getInt("Quantidade"),
                        rset.getString("Status"),
                        rset.getString("Tipo")
                );
                geladeira.setIdGeladeira(rset.getInt("idGeladeira"));
                // Criando o objeto Produto geladeira com o ID vindo do ResultSet
                pg = new ProdutosGeladeira(
                        rset.getInt("idNotificacao"),
                        geladeira,
                        produto,
                        rset.getObject("dtValidade", LocalDate.class),
                        rset.getString("status"),
                        rset.getInt("lote")
                );
                pg.setIdProdutosGeladeira(rset.getInt("idProdutosGeladeira"));
            }
        }
        catch (SQLException sqle){
            sqle.printStackTrace();
        }
        finally {
            conexao.desconectar(conn);
        }
        return pg;
    }// BUSCAR 1 PRODUTO NA TABELA produtoGeladeira PELO NOME
}
