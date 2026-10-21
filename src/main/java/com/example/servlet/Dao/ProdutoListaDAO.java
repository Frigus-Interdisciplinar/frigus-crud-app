package com.example.servlet.Dao;

import com.example.servlet.Model.ListaCompras;
import com.example.servlet.Model.Produto;
import com.example.servlet.Model.ProdutoLista;
import com.example.servlet.Model.Usuario;
import com.example.servlet.Util.Conexao;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;

public class ProdutoListaDAO {

    // ================ INSERIR ================
    public int adicionarProdutoListas(ProdutoLista produtoLista){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();// abrindo a conexao com o banco
        Produto produto = produtoLista.getProduto();
        ListaCompras listaCompras = produtoLista.getListaCompras();
        // id 0 indica que a compra ainda não existe, então ela é criada junto com o produto e o item
        boolean listaNova = listaCompras.getIdLista() == 0;

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
                return 0; // não foi possível adicionar
            }
            ResultSet chavesProduto = pstmtProduto.getGeneratedKeys();// adicionando a chave criada pelo banco
            if (chavesProduto.next()) produto.setIdProdutos(chavesProduto.getInt(1));

            // insere a lista de compras, do usuário ou da empresa
            if (listaNova){
                String comandoSqlLista;
                if (listaCompras.getUsuario() != null){
                    comandoSqlLista = "insert into listaCompras (idUsuario, dtLista) values(?,?);";
                }
                else {
                    comandoSqlLista = "insert into listaCompras (idEmpresa, dtLista) values(?,?);";
                }
                PreparedStatement pstmtLista = conn.prepareStatement(comandoSqlLista, Statement.RETURN_GENERATED_KEYS);
                if (listaCompras.getUsuario() != null){
                    pstmtLista.setString(1, listaCompras.getUsuario().getCpf());
                }
                else {
                    pstmtLista.setString(1, listaCompras.getEmpresa().getCnpj());
                }
                pstmtLista.setObject(2, listaCompras.getDtLista());

                if (pstmtLista.executeUpdate() <= 0){
                    conn.rollback();
                    produto.setIdProdutos(0);
                    return 0; // não foi possível adicionar
                }
                ResultSet chavesLista = pstmtLista.getGeneratedKeys();// adicionando a chave criada pelo banco
                if (chavesLista.next()) listaCompras.setIdLista(chavesLista.getInt(1));
            }

            String comandoSql = "Insert into ItemLista (idListaCompras, idProduto, quantidade) values(?,?,?);";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql, Statement.RETURN_GENERATED_KEYS);
            // setando os valores para a execução
            pstmt.setInt(1, listaCompras.getIdLista());
            pstmt.setInt(2, produto.getIdProdutos());
            pstmt.setInt(3, produtoLista.getQuantidade());

            if (pstmt.executeUpdate() <= 0){
                conn.rollback();
                produto.setIdProdutos(0);
                if (listaNova) listaCompras.setIdLista(0);
                return 0; // não foi possível adicionar
            }
            ResultSet chaves = pstmt.getGeneratedKeys();// adicionando a chave criada pelo banco
            if (chaves.next()) produtoLista.setIdProdutoLista(chaves.getInt(1));

            conn.commit();
            return 1; // inseriu as informações no banco

        }catch (SQLException sqle){
            sqle.printStackTrace();
            try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            // o rollback torna esses ids falsos
            produto.setIdProdutos(0);
            if (listaNova) listaCompras.setIdLista(0);
            produtoLista.setIdProdutoLista(0);
            return -1;
        }
        finally {
            conexao.desconectar(conn);// fechando a conexao com o banco
        }
    }// ================ INSERIR ================


    // ================ ALTERAR ================
    public int alterarProdutoListas(ProdutoLista produtoListaAntiga, ProdutoLista produtoListaNova){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();// abrindo a conexao com o banco

        // o item continua apontando para o mesmo produto, então o id vem do item antigo
        int idProduto = produtoListaAntiga.getProduto().getIdProdutos();
        Produto produtoNovo = produtoListaNova.getProduto();

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
                return 0; // não foi possível adicionar
            }

            String comandoSql = "update ItemLista set idListaCompras = ?, idProduto = ?, quantidade = ? where id = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // setando os valores para a execução
            pstmt.setInt(1, produtoListaNova.getListaCompras().getIdLista());
            pstmt.setInt(2, idProduto);
            pstmt.setInt(3, produtoListaNova.getQuantidade());
            pstmt.setInt(4, produtoListaAntiga.getIdProdutoLista());

            if (pstmt.executeUpdate() <= 0){
                conn.rollback();
                return 0; // não foi possível adicionar
            }

            conn.commit();
            return 1; // inseriu as informações no banco

        }catch (SQLException sqle){
            sqle.printStackTrace();
            try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            return -1;
        }
        finally {
            conexao.desconectar(conn);// fechando a conexao com o banco
        }
    }// ================ ALTERAR ================



    // ================ DELETAR ================
    public int deletarProdutoListas(ProdutoLista produtoLista){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();// abrindo a conexao com o banco

        try{
            conn.setAutoCommit(false); // nada é salvo de verdade até o commit

            // o item sai primeiro porque ele referencia o produto
            String comandoSql = "delete from ItemLista where id = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // setando os valores para a execução
            pstmt.setInt(1, produtoLista.getIdProdutoLista());

            if (pstmt.executeUpdate() <= 0){
                conn.rollback();
                return 0; // não foi possível adicionar
            }

            String comandoSqlProduto = "delete from Produto where id = ? ";
            PreparedStatement pstmtProduto = conn.prepareStatement(comandoSqlProduto);
            pstmtProduto.setInt(1, produtoLista.getProduto().getIdProdutos());

            if (pstmtProduto.executeUpdate() <= 0){
                conn.rollback();
                return 0; // não foi possível adicionar
            }

            conn.commit();
            return 1; // inseriu as informações no banco

        }catch (SQLException sqle){
            sqle.printStackTrace();
            try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            return -1;
        }
        finally {
            conexao.desconectar(conn);// fechando a conexao com o banco
        }
    }// ================ DELETAR ================



    // ============ BUSCAR PRODUTO POR ID ============
    public ProdutoLista buscarProdutoNumaLista(int idProdutoLista, int idCompra){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();// abrindo a conexao com o banco

        ResultSet rset = null;
        ProdutoLista produtoLista = null;

        try {
            String comandoSql = "select * from ItemLista where id = ? and idListaCompras = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // sentando os valores dos ids
            pstmt.setInt(1, idProdutoLista);
            pstmt.setInt(2, idCompra);
            rset = pstmt.executeQuery();
            if (rset.next()){
                // criando o objeto lista de compras
                ListaCompras compras = new ListaCompras();
                compras.setIdLista( rset.getInt("idListaCompras"));
                // criando o objeto produto
                Produto produto = new Produto();
                produto.setIdProdutos(rset.getInt("idProduto"));
                produtoLista = new ProdutoLista(
                        rset.getInt("id"),
                        compras,
                        produto,
                        rset.getInt("quantidade")
                );
            }

        }catch (SQLException sqle){
            sqle.printStackTrace();
        }
        finally {
            conexao.desconectar(conn);// fechando a conexao com o banco
        }
        return produtoLista;
    }
    // ============ BUSCAR PRODUTO POR ID ============


    // ============== VERIFICAR SE O PRODUTO ESTÁ NA LISTA ==============
    public boolean estaNaLista(Produto produto, ListaCompras lista){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();// abrindo a conexao com o banco

        ResultSet rset = null;
        try{
            String comandoSql = "select 1 from ItemLista where idProduto = ? and idListaCompras = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // primeiro parâmetro: produto
            pstmt.setInt(1, produto.getIdProdutos());

            // segundo parâmetro: lista
            pstmt.setInt(2, lista.getIdLista());
            rset = pstmt.executeQuery();

            // verifica se tem parcerias ou não
            return rset.next();

        }catch (SQLException sqle){
            throw new RuntimeException("Erro ao verificar esse produto na lista.", sqle);
        }finally {
            conexao.desconectar(conn);//fechando a conexao com o banco
        }
    }// ============== VERIFICAR SE O PRODUTO ESTÁ NA LISTA ==============

    // TODOS OS PRODUTOS EM UMA LISTA USUARIO
    public int totalItensNumaCompraUsuario(String cpf){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();

        ResultSet rset = null;
        int total = 0;

        try{
            String comandoSql = "select count(pl.*) as total " +
                    "from ItemLista pl " +
                    "join ListaCompras lc on pl.idListaCompras = lc.id " +
                    "where lc.idUsuario = ? and lc.dtLista = (select dtLista from ListaCompras where idUsuario = ? order by dtLista desc limit 1);";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            pstmt.setString(1, cpf);
            pstmt.setString(2, cpf);
            rset = pstmt.executeQuery();
            if (rset.next()) total = rset.getInt("total");

        }catch (SQLException sqle){
            sqle.printStackTrace();
        }
        finally {
            conexao.desconectar(conn);
        }
        return total;
    }
    // TODOS OS PRODUTOS EM UMA LISTA USUARIO





    // TODOS OS PRODUTOS EM UMA LISTA EMPRESA
    public int totalItensNumaCompraEmpresa(String cnpj){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();

        ResultSet rset = null;
        int total = 0;

        try{
            String comandoSql = "select count(pl.*) as total " +
                    "from ItemLista pl " +
                    "join ListaCompras lc on pl.idListaCompras = lc.id " +
                    "where lc.idEmpresa = ? and lc.dtLista = (select dtLista from ListaCompras where idEmpresa = ? order by dtLista desc limit 1);";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            pstmt.setString(1, cnpj);
            pstmt.setString(2, cnpj);
            rset = pstmt.executeQuery();
            if (rset.next()) total = rset.getInt("total");

        }catch (SQLException sqle){
            sqle.printStackTrace();
        }
        finally {
            conexao.desconectar(conn);
        }
        return total;
    }
    // TODOS OS PRODUTOS EM UMA LISTA EMPRESA
}
