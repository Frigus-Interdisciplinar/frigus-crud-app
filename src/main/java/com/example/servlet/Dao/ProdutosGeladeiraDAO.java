package com.example.servlet.Dao;

import com.example.servlet.Model.Geladeira;
import com.example.servlet.Model.Produto;
import com.example.servlet.Model.ProdutosGeladeira;
import com.example.servlet.Model.Usuario;
import com.example.servlet.Util.Conexao;
import com.example.servlet.Util.Enum.Categoria;
import com.example.servlet.Util.Enum.StatusArmazenamento;
import com.example.servlet.Util.Enum.StatusProduto;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProdutosGeladeiraDAO {

    //==============INSERIR===============
    public int inserirProdutoGeladeira(ProdutosGeladeira ig) {
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();
        Produto produto = ig.getProduto();

        try {
            conn.setAutoCommit(false); // nada é salvo de verdade até o commit

            // insere o produto
            String comandoSql = "insert into Produto(nome, descricao, preco, categoria, marca, unidademedida) values (?,?,?,?,?,?);";
            PreparedStatement pstmtProduto = conn.prepareStatement(comandoSql, Statement.RETURN_GENERATED_KEYS);

            pstmtProduto.setString(1, produto.getNome());
            pstmtProduto.setString(2, produto.getDescricao());
            pstmtProduto.setDouble(3, produto.getPreco());
            pstmtProduto.setString(4, produto.getCategoria().name());
            pstmtProduto.setString(5, produto.getMarca());
            pstmtProduto.setString(6, produto.getUnidadeMedida());

            if (pstmtProduto.executeUpdate() <= 0) {
                conn.rollback();
                return 0;// não foi posivel executar
            }
            ResultSet chaves = pstmtProduto.getGeneratedKeys();// adicionando a chave criada pelo banco
            if (chaves.next()) produto.setIdProdutos(chaves.getInt(1));

            // insere as informações relacionada aos produtos domesticos
            String comandoSqlItem = "insert into ItemGeladeira (idNotificacao, idGeladeira, idProduto, DtValidade, status, lote, quantidade) values (?,?,?,?,?,?,?);";
            PreparedStatement pstmt = conn.prepareStatement(comandoSqlItem, Statement.RETURN_GENERATED_KEYS);
            // definindo parâmetros da instrução
            pstmt.setInt(1, ig.getIdNotificacao());
            pstmt.setInt(2, ig.getGeladeira().getIdGeladeira());
            pstmt.setInt(3, produto.getIdProdutos());
            pstmt.setObject(4, ig.getDtValidade());
            pstmt.setString(5, ig.getStatus().name());
            pstmt.setInt(6, ig.getLote());
            pstmt.setInt(7, ig.getQuantidade());

            if (pstmt.executeUpdate() <= 0) {
                conn.rollback();
                produto.setIdProdutos(0);
                return 0;// não foi posivel executar
            }
            chaves = pstmt.getGeneratedKeys();
            if (chaves.next()) ig.setIdProdutosGeladeira(chaves.getInt(1));

            conn.commit();
            return 1;// verifica se deu certo
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            // o rollback torna esses ids falsos
            produto.setIdProdutos(0);
            ig.setIdProdutosGeladeira(0);
            return -1;// erro!!
        } finally {
            conexao.desconectar(conn);// desconectar
        }
    }//==============INSERIR===============


    //==============ATUALIZAR===============
    public int alterarProdutoGeladeira(ProdutosGeladeira pgAntiga, ProdutosGeladeira pgNova) {
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();

        // o item continua apontando para o mesmo produto, então o id vem do item antigo
        int idProduto = pgAntiga.getProduto().getIdProdutos();
        Produto produtoNovo = pgNova.getProduto();

        try {
            conn.setAutoCommit(false); // nada é salvo de verdade até o commit

            // altera as informações do produto
            String comandoSql = "update Produto set nome = ?, descricao = ?, preco = ?, categoria = ?, marca = ?, unidademedida = ? where id = ?;";
            PreparedStatement pstmtProduto = conn.prepareStatement(comandoSql);

            pstmtProduto.setString(1, produtoNovo.getNome());
            pstmtProduto.setString(2, produtoNovo.getDescricao());
            pstmtProduto.setDouble(3, produtoNovo.getPreco());
            pstmtProduto.setString(4, produtoNovo.getCategoria().name());
            pstmtProduto.setString(5, produtoNovo.getMarca());
            pstmtProduto.setString(6, produtoNovo.getUnidadeMedida());
            pstmtProduto.setInt(7, idProduto);

            if (pstmtProduto.executeUpdate() <= 0) {
                conn.rollback();
                return 0;// não foi posivel executar
            }

            // altera as informações relacionada aos produtos domesticos
            String comandoSqlItem = "Update ItemGeladeira set idNotificacao = ?, idGeladeira = ?, idProduto = ?, DtValidade = ?, status = ?, lote = ?, quantidade = ? where id = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSqlItem);
            // definindo parâmetros da instrução
            pstmt.setInt(1, pgNova.getIdNotificacao());
            pstmt.setInt(2, pgNova.getGeladeira().getIdGeladeira());
            pstmt.setInt(3, idProduto);
            pstmt.setObject(4, pgNova.getDtValidade());
            pstmt.setString(5, pgNova.getStatus().name());
            pstmt.setInt(6, pgNova.getLote());
            pstmt.setInt(7, pgNova.getQuantidade());
            pstmt.setInt(8, pgAntiga.getIdProdutosGeladeira());

            if (pstmt.executeUpdate() <= 0) {
                conn.rollback();
                return 0;// não foi posivel executar
            }

            conn.commit();
            return 1;// verifica se deu certo
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            return -1;
        } finally {
            conexao.desconectar(conn);// desconectar
        }
    }//==============ATUALIZAR===============


    //==============DELETAR===============
    public int deletarProdutoGeladeira(ProdutosGeladeira ig) {
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();

        try {
            conn.setAutoCommit(false); // nada é salvo de verdade até o commit

            // o item sai primeiro porque ele referencia o produto
            String comandoSql = "delete from ItemGeladeira where id = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // definindo parâmetros da instrução
            pstmt.setInt(1, ig.getIdProdutosGeladeira());

            if (pstmt.executeUpdate() <= 0) {
                conn.rollback();
                return 0;// não foi posivel executar
            }

            comandoSql = "delete from Produto where id = ? ";
            PreparedStatement pstmtProduto = conn.prepareStatement(comandoSql);
            // definido os parâmetros da instrução
            pstmtProduto.setInt(1, ig.getProduto().getIdProdutos());

            if (pstmtProduto.executeUpdate() <= 0) {
                conn.rollback();
                return 0;// não foi posivel executar
            }

            conn.commit();
            return 1;// verifica se deu certo
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            return -1;
        } finally {
            conexao.desconectar(conn);// desconectar
        }
    }//==============DELETAR===============


    // ========= BUSCAR =========


    // LISTA DE TODOS OS PRODUTOS DE TODAS AS GELADEIRAS ( COM O NOME DA GELADEIRA E DO DONO ) - TABELA produtoGeladeira
    public List<ProdutosGeladeira> buscarTodosProdutosGeladeira() {
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar(); // abrindo a conexão com o banco

        List<ProdutosGeladeira> lista = new ArrayList<>();// cria a lista de todos os produtos
        ResultSet rset = null; // consulta a tabela

        try {
            String comandoSql = "select p.id as idProduto, p.nome, p.descricao, p.preco, p.categoria, p.marca, p.unidademedida, " +
                    "       ig.id as idProdutosGeladeira, ig.idNotificacao, ig.dtValidade, ig.lote, ig.quantidade, ig.status as statusItem, " +
                    "       g.id as idGeladeira, g.idUsuario, g.nome as nomeGeladeira, g.status, g.tipo, " +
                    "       u.nome as nomeUsuario, u.sobrenome " +
                    "from Produto p " +
                    "join ItemGeladeira ig on p.id = ig.idProduto " +
                    "join geladeira g on g.id = ig.idGeladeira " +
                    "join usuario u on u.cpf = g.idUsuario " +
                    "order by ig.id;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            rset = pstmt.executeQuery(); // executa a consulta SQL

            // armazena os valores em um list<>
            while (rset.next()) {
                Produto produto = new Produto(
                        rset.getInt("idProduto"),
                        rset.getString("nome"),
                        rset.getString("descricao"),
                        rset.getDouble("preco"),
                        Categoria.valueOf(rset.getString("categoria")),
                        rset.getString("marca"),
                        rset.getString("unidademedida")
                );

                // criando o objeto usuario com id vindo do resultSet
                Usuario usuario = new Usuario();
                usuario.setCpf(rset.getString("idUsuario"));
                usuario.setNome(rset.getString("nomeUsuario"));
                usuario.setSobrenome(rset.getString("sobrenome"));

                // Criando o objeto geladeira com o ID vindo do ResultSet
                Geladeira geladeira = new Geladeira(
                        rset.getInt("idGeladeira"),
                        usuario,
                        StatusArmazenamento.valueOf(rset.getString("status")),
                        rset.getString("Tipo"),
                        rset.getString("nomeGeladeira")
                );
                // Criando o objeto Produto geladeira com o ID vindo do ResultSet
                ProdutosGeladeira ig = new ProdutosGeladeira(
                        rset.getInt("idProdutosGeladeira"),
                        rset.getInt("idNotificacao"),
                        geladeira,
                        produto,
                        rset.getObject("dtValidade", LocalDate.class),
                        rset.getInt("lote"),
                        StatusProduto.valueOf(rset.getString("statusItem")),
                        rset.getInt("quantidade")
                );
                lista.add(ig);
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        } finally {
            conexao.desconectar(conn); // fechando a conexão
        }
        return lista;
    } // LISTA DE TODOS OS PRODUTOS DE TODAS AS GELADEIRAS ( COM O NOME DA GELADEIRA E DO DONO ) - TABELA produtoGeladeira


    // BUSCAR 1 PRODUTO DA GELADEIRA POR ID DO PRODUTO
    public ProdutosGeladeira buscarProdutoGeladeiraPorId(int idProdutoGeladeira) {
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();

        ResultSet rset = null;
        ProdutosGeladeira ig = null;

        try {
            String comandoSql = "select p.id as idProduto, p.nome, p.descricao, p.preco, p.categoria, p.marca, p.unidademedida, " +
                    "       ig.id as idProdutosGeladeira, ig.idNotificacao, ig.dtValidade, ig.lote, ig.quantidade, " +
                    "       g.id as idGeladeira, g.idUsuario, g.nome as nomeGeladeira, g.status, g.tipo " +
                    "from Produto p " +
                    "join ItemGeladeira ig on p.id = ig.idProduto " +
                    "join geladeira g on g.id = ig.idGeladeira " +
                    "where ig.id = ?";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            pstmt.setInt(1, idProdutoGeladeira);// setando os parâmetros da instrução
            rset = pstmt.executeQuery();
            if (rset.next()) {
                Produto produto = new Produto(
                        rset.getString("nome"),
                        rset.getString("descricao"),
                        rset.getDouble("preco"),
                        Categoria.valueOf(rset.getString("categoria")),
                        rset.getString("marca"),
                        rset.getString("unidademedida")
                );
                produto.setIdProdutos(rset.getInt("idProduto"));

                // criando o objeto usuario com id vindo do resultSet
                Usuario usuario = new Usuario();
                usuario.setCpf(rset.getString("idUsuario"));

                // Criando o objeto geladeira com o ID vindo do ResultSet
                Geladeira geladeira = new Geladeira(
                        rset.getInt("idGeladeira"),
                        usuario,
                        StatusArmazenamento.valueOf(rset.getString("status")),
                        rset.getString("Tipo"),
                        rset.getString("nomeGeladeira")
                );
                geladeira.setIdGeladeira(rset.getInt("idGeladeira"));
                // Criando o objeto Produto geladeira com o ID vindo do ResultSet
                ig = new ProdutosGeladeira(
                        rset.getInt("idNotificacao"),
                        geladeira,
                        produto,
                        rset.getObject("dtValidade", LocalDate.class),
                        rset.getInt("lote"),
                        rset.getInt("quantidade")
                );
                ig.setIdProdutosGeladeira(rset.getInt("idProdutosGeladeira"));
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        } finally {
            conexao.desconectar(conn);
        }
        return ig;
    }// BUSCAR 1 PRODUTO DA GELADEIRA POR ID DO PRODUTO


    // TOTAL DE PRODUTOS ARMAZENADOS
    public int totalProdutosArmazenados(){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();// abrindo a conexão do o banco

        ResultSet rset = null;
        int total = 0;

        try {
            String comandoSql = "select count(*) as total from ItemGeladeira;";

            PreparedStatement pstmt = conn.prepareStatement(comandoSql);

            rset = pstmt.executeQuery();
            if (rset.next()) total = rset.getInt("total");


        }catch (SQLException sqle){
            sqle.printStackTrace();
        }finally {
            conexao.desconectar(conn);// fechado a conexão com o banco
        }
        return total;
    }
    // TOTAL DE PRODUTOS ARMAZENADOS



    // ========= PRODUTOS SOLTOS =========
    // os dois métodos abaixo usam a conexão de quem chamou, porque fazem parte da transação de apagar um dono

    // pega os ids ( primeira coluna ) de uma consulta, o valor é usado em todos os ? do comando
    public static List<Integer> buscarIds(Connection conn, String comandoSql, Object valor) throws SQLException {
        List<Integer> ids = new ArrayList<>();
        PreparedStatement pstmt = conn.prepareStatement(comandoSql);
        int totalParametros = pstmt.getParameterMetaData().getParameterCount();
        for (int i = 1; i <= totalParametros; i++){
            pstmt.setObject(i, valor);
        }
        ResultSet rset = pstmt.executeQuery();
        while (rset.next()) ids.add(rset.getInt(1));
        return ids;
    }

    // apaga os produtos que nenhum item, ingrediente ou marca parceira usa mais
    public static void apagarProdutosOrfaos(Connection conn, List<Integer> idsProdutos) throws SQLException {
        String comandoSql = "delete from Produto where id = ? " +
                "and not exists (select 1 from ItemGeladeira where idProduto = ?) " +
                "and not exists (select 1 from ItemFrigorifico where idProduto = ?) " +
                "and not exists (select 1 from ItemLista where idProduto = ?) " +
                "and not exists (select 1 from Ingrediente where idProduto = ?) " +
                "and not exists (select 1 from MarcaParceira where idProduto = ?);";
        PreparedStatement pstmt = conn.prepareStatement(comandoSql);
        for (int idProduto : idsProdutos){
            for (int i = 1; i <= 6; i++){
                pstmt.setInt(i, idProduto);
            }
            pstmt.executeUpdate();
        }
    }
    // ========= PRODUTOS SOLTOS =========

}
