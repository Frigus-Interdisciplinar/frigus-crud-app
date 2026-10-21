package com.example.servlet.Dao;

import com.example.servlet.Model.*;
import com.example.servlet.Util.Conexao;
import com.example.servlet.Util.Enum.Categoria;
import com.example.servlet.Util.Enum.StatusArmazenamento;
import com.example.servlet.Util.Enum.StatusProduto;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProdutosFrigorificoDAO {

    // ===========INSERIR===========
    public int inserirProdutoFrigorifico(ProdutosFrigorifico itf){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();
        Produto produto = itf.getProduto();

        try{
            conn.setAutoCommit(false); // nada é salvo de verdade até o commit

            // insere o produto
            String comandoSql = "insert into Produto(nome, descricao, preco, categoria, marca, unidademedida) values (?,?,?,?,?,?);";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql, Statement.RETURN_GENERATED_KEYS);

            pstmt.setString(1, produto.getNome());
            pstmt.setString(2, produto.getDescricao());
            pstmt.setDouble(3, produto.getPreco());
            pstmt.setString(4, produto.getCategoria().name());
            pstmt.setString(5, produto.getMarca());
            pstmt.setString(6, produto.getUnidadeMedida());

            if (pstmt.executeUpdate() <= 0){
                conn.rollback();
                return 0;
            }
            ResultSet chaves = pstmt.getGeneratedKeys();// adicionando a chave criada pelo banco
            if (chaves.next()) produto.setIdProdutos(chaves.getInt(1));

            // insere o item usando o id do produto recém criado
            String comandoSqlItem = "insert into ItemFrigorifico (idNotificacao, idFrigorifico, idProduto, dtValidade, status, lote, quantidade) values (?,?,?,?,?,?,?);";
            PreparedStatement pstmtItem = conn.prepareStatement(comandoSqlItem, Statement.RETURN_GENERATED_KEYS);

            // setando os valores pra a instrução
            pstmtItem.setInt(1, itf.getIdNotificacao());
            pstmtItem.setInt(2, itf.getFrigorificos().getIdFrigorifico());
            pstmtItem.setInt(3, produto.getIdProdutos());
            pstmtItem.setObject(4, itf.getDtValidade());
            pstmtItem.setString(5, itf.getStatus().name());
            pstmtItem.setInt(6, itf.getLote());
            pstmtItem.setInt(7, itf.getQuantidade());

            if (pstmtItem.executeUpdate() <= 0){
                conn.rollback();
                produto.setIdProdutos(0);
                return 0;
            }
            chaves = pstmtItem.getGeneratedKeys();
            if (chaves.next()) itf.setIdProdutosFrigorifico(chaves.getInt(1));

            conn.commit();
            return 1;
        }
        catch (SQLException sqle){
            sqle.printStackTrace();
            try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            // o rollback torna esses ids falsos
            produto.setIdProdutos(0);
            itf.setIdProdutosFrigorifico(0);
            return -1;
        }
        finally {
            conexao.desconectar(conn);
        }
    }// ===========INSERIR===========




    // ===========ALTERAR===========
    public int alterarProdutoFrigorifico(ProdutosFrigorifico pfAntigo, ProdutosFrigorifico pfNovo){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();

        // o item continua apontando para o mesmo produto, então o id vem do item antigo
        int idProduto = pfAntigo.getProduto().getIdProdutos();
        Produto produtoNovo = pfNovo.getProduto();

        try{
            conn.setAutoCommit(false); // nada é salvo de verdade até o commit

            // altera as informações do produto
            String comandoSql = "update Produto set nome = ?, descricao = ?, preco = ?, categoria = ?, marca = ?, unidademedida = ? where id = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);

            pstmt.setString(1, produtoNovo.getNome());
            pstmt.setString(2, produtoNovo.getDescricao());
            pstmt.setDouble(3, produtoNovo.getPreco());
            pstmt.setString(4, produtoNovo.getCategoria().name());
            pstmt.setString(5, produtoNovo.getMarca());
            pstmt.setString(6, produtoNovo.getUnidadeMedida());
            pstmt.setInt(7, idProduto);

            if (pstmt.executeUpdate() <= 0){
                conn.rollback();
                return 0;
            }

            String comandoSqlItem = "update ItemFrigorifico set idNotificacao = ?, idFrigorifico = ?, idProduto = ?, dtValidade = ?, status = ?, lote = ?, quantidade  =? where id = ?;";
            PreparedStatement pstmtItem = conn.prepareStatement(comandoSqlItem);

            // setando os valores pra a instrução
            pstmtItem.setInt(1, pfNovo.getIdNotificacao());
            pstmtItem.setInt(2, pfNovo.getFrigorificos().getIdFrigorifico());
            pstmtItem.setInt(3, idProduto);
            pstmtItem.setObject(4, pfNovo.getDtValidade());
            pstmtItem.setString(5, pfNovo.getStatus().name());
            pstmtItem.setInt(6, pfNovo.getLote());
            pstmtItem.setInt(7, pfNovo.getQuantidade());
            pstmtItem.setInt(8, pfAntigo.getIdProdutosFrigorifico());

            if (pstmtItem.executeUpdate() <= 0){
                conn.rollback();
                return 0;
            }

            conn.commit();
            return 1;
        }
        catch (SQLException sqle){
            sqle.printStackTrace();
            try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            return -1;
        }
        finally {
            conexao.desconectar(conn);
        }
    }// ===========ALTERAR===========



    // ===========DELETAR===========
    public int deletarProdutoFrigorifico(ProdutosFrigorifico itf){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();

        try{
            conn.setAutoCommit(false); // nada é salvo de verdade até o commit

            // o item sai primeiro porque ele referencia o produto
            String comandoSql = "delete from ItemFrigorifico where id = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);

            // setando os valores pra a instrução
            pstmt.setInt(1, itf.getIdProdutosFrigorifico());

            if (pstmt.executeUpdate() <= 0){
                conn.rollback();
                return 0;
            }

            comandoSql = "delete from Produto where id = ? ";
            PreparedStatement pstmtProduto = conn.prepareStatement(comandoSql);

            // definido os parâmetros da instrução
            pstmtProduto.setInt(1, itf.getProduto().getIdProdutos());

            if (pstmtProduto.executeUpdate() <= 0){
                conn.rollback();
                return 0;
            }

            conn.commit();
            return 1;
        }
        catch (SQLException sqle){
            sqle.printStackTrace();
            try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            return -1;
        }
        finally {
            conexao.desconectar(conn);
        }
    }// ===========DELETAR===========






    // ========= BUSCAR =========

    // LISTA DE TODOS OS PRODUTOS
    public List<ProdutosFrigorifico> buscarProdutoFrigorificos(String cnpj){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar(); // abrindo a conexão com o banco

        List<ProdutosFrigorifico> lista = new ArrayList<>(); // cria a lista de todos os produtos
        ResultSet rset = null; // consulta a tabela

        try{
            String comandoSql = "select p.id as idProduto, p.nome, p.descricao, p.preco, p.categoria, p.marca, p.unidadeMedida, " +
                    "        if.id as idProdutoFrigorifico, if.idNotificacao, if.dtValidade, if.lote, if.status, if.quantidade as quantidadeProduto, " +
                    "        f.id as idFrigorifico, f.idEmpresa as cnpj, f.nome as nomeFrigorifico, f.status as statusFrigorifico, f.tipo, " +
                    "         en.id as idEndereco, en.rua, en.bairro, en.cidade, en.uf, en.pais, en.cep, en.numero, en.nomeUnidade " +
                    "from Produto p " +
                    "join ItemFrigorifico if on p.id = if.idProduto " +
                    "join Frigorifico f on if.idFrigorifico = f.id " +
                    "join endereco en on f.idEndereco = en.id " +
                    "where f.idEmpresa = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            pstmt.setString(1, cnpj);
            rset = pstmt.executeQuery(); // executa a consulta SQL

            // armazena os valores em um list<>
            while (rset.next()){
                Produto produto = new Produto(
                        rset.getInt("idProduto"),
                        rset.getString("nome"),
                        rset.getString("descricao"),
                        rset.getDouble("preco"),
                        Categoria.valueOf(rset.getString("categoria")),
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
                        StatusArmazenamento.valueOf(rset.getString("statusFrigorifico")),
                        endereco,
                        rset.getString("tipo"),
                        rset.getString("nomeFrigorifico")
                );
                // criando o objeto Produto no frigorifico com o ID vindo do ResultSet
                ProdutosFrigorifico itf = new ProdutosFrigorifico(
                        rset.getInt("idProdutoFrigorifico"),
                        rset.getInt("idNotificacao"),
                        frigorifico,
                        produto,
                        rset.getObject("dtValidade", LocalDate.class),
                        StatusProduto.valueOf(rset.getString("status")),
                        rset.getInt("lote"),
                        rset.getInt("quantidadeProduto")
                );
                lista.add(itf);
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
            String comandoSql = "select p.id as idProduto, p.nome, p.descricao, p.preco, p.categoria, p.marca, p.unidadeMedida, " +
                    "        if.id as idProdutoFrigorifico, if.idNotificacao, if.dtValidade, if.lote, if.status, if.quantidade as quantidadeProduto, " +
                    "        f.id as idFrigorifico, f.idEmpresa as cnpj, f.nome as nomeFrigorifico, f.status as statusFrigorifico, f.tipo, " +
                    "         en.id as idEndereco, en.rua, en.bairro, en.cidade, en.uf, en.pais, en.cep, en.numero, en.nomeUnidade " +
                    "from Produto p " +
                    "join ItemFrigorifico if on p.id = if.idProduto " +
                    "join Frigorifico f on if.idFrigorifico = f.id " +
                    "join endereco en on f.idEndereco = en.id " +
                    "where if.lote = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            pstmt.setInt(1, lote);
            rset = pstmt.executeQuery();

            while (rset.next()){
                Produto produto = new Produto(
                        rset.getInt("idProduto"),
                        rset.getString("nome"),
                        rset.getString("descricao"),
                        rset.getDouble("preco"),
                        Categoria.valueOf(rset.getString("categoria")),
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
                        StatusArmazenamento.valueOf(rset.getString("statusFrigorifico")),
                        endereco,
                        rset.getString("tipo"),
                        rset.getString("nomeFrigorifico")
                );
                // criando o objeto Produto no frigorifico com o ID vindo do ResultSet
                ProdutosFrigorifico itf = new ProdutosFrigorifico(
                        rset.getInt("idProdutoFrigorifico"),
                        rset.getInt("idNotificacao"),
                        frigorifico,
                        produto,
                        rset.getObject("dtValidade", LocalDate.class),
                        StatusProduto.valueOf(rset.getString("status")),
                        rset.getInt("lote"),
                        rset.getInt("quantidadeProduto")
                );
                lista.add(itf);
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
            String comandoSql =  "select p.id as idProduto, p.nome, p.descricao, p.preco, p.categoria, p.marca, p.unidadeMedida, " +
                    "        if.id as idProdutoFrigorifico, if.idNotificacao, if.dtValidade, if.lote, if.status, if.quantidade as quantidadeProduto, " +
                    "        f.id as idFrigorifico, f.idEmpresa as cnpj, f.nome as nomeFrigorifico, f.status as statusFrigorifico, f.tipo, " +
                    "         en.id as idEndereco, en.rua, en.bairro, en.cidade, en.uf, en.pais, en.cep, en.numero, en.nomeUnidade " +
                    "from Produto p " +
                    "join ItemFrigorifico if on p.id = if.idProduto " +
                    "join Frigorifico f on if.idFrigorifico = f.id " +
                    "join endereco en on f.idEndereco = en.id " +
                    "where if.DtValidade = ?;";
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
                        Categoria.valueOf(rset.getString("categoria")),
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
                        StatusArmazenamento.valueOf(rset.getString("statusFrigorifico")),
                        endereco,
                        rset.getString("tipo"),
                        rset.getString("nomeFrigorifico")
                );
                // criando o objeto Produto no frigorifico com o ID vindo do ResultSet
                ProdutosFrigorifico itf = new ProdutosFrigorifico(
                        rset.getInt("idProdutoFrigorifico"),
                        rset.getInt("idNotificacao"),
                        frigorifico,
                        produto,
                        rset.getObject("dtValidade", LocalDate.class),
                        StatusProduto.valueOf(rset.getString("status")),
                        rset.getInt("lote"),
                        rset.getInt("quantidadeProduto")
                );
                lista.add(itf);
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
    public ProdutosFrigorifico buscarProdutoFrigorificoPorId(int idItemFrigorifico){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();

        ResultSet rset = null;
        ProdutosFrigorifico itf = null;

        try{
            String comandoSql = "select p.id as idProduto, p.nome, p.descricao, p.preco, p.categoria, p.marca, p.unidadeMedida, " +
                    "        if.id as idProdutoFrigorifico, if.idNotificacao, if.dtValidade, if.lote, if.status, if.quantidade as quantidadeProduto, " +
                    "        f.id as idFrigorifico, f.idEmpresa as cnpj, f.nome as nomeFrigorifico, f.status as statusFrigorifico, f.tipo, " +
                    "         en.id as idEndereco, en.rua, en.bairro, en.cidade, en.uf, en.pais, en.cep, en.numero, en.nomeUnidade " +
                    "from Produto p " +
                    "join ItemFrigorifico if on p.id = if.idProduto " +
                    "join Frigorifico f on if.idFrigorifico = f.id " +
                    "join endereco en on f.idEndereco = en.id " +
                    "where if.id = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            pstmt.setInt(1, idItemFrigorifico);// setando os parâmetros da instrução
            rset = pstmt.executeQuery();
            if (rset.next()){
                Produto produto = new Produto(
                        rset.getInt("idProduto"),
                        rset.getString("nome"),
                        rset.getString("descricao"),
                        rset.getDouble("preco"),
                        Categoria.valueOf(rset.getString("categoria")),
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
                        StatusArmazenamento.valueOf(rset.getString("statusFrigorifico")),
                        endereco,
                        rset.getString("tipo"),
                        rset.getString("nomeFrigorifico")
                );
                // criando o objeto Produto no frigorifico com o ID vindo do ResultSet
                itf = new ProdutosFrigorifico(
                        rset.getInt("idProdutoFrigorifico"),
                        rset.getInt("idNotificacao"),
                        frigorifico,
                        produto,
                        rset.getObject("dtValidade", LocalDate.class),
                        StatusProduto.valueOf(rset.getString("status")),
                        rset.getInt("lote"),
                        rset.getInt("quantidadeProduto")
                );
            }
        }
        catch (SQLException sqle){
            sqle.printStackTrace();
        }
        finally {
            conexao.desconectar(conn);
        }
        return itf;
    }// BUSCAR 1 PRODUTO NA TABELA produtoFrigorificos POR ID






    // BUSCAR 1 PRODUTO NA TABELA produtoFrigorificos PELO NOME
    public ProdutosFrigorifico buscarProdutoFrigorificoPeloNome(String nomeProduto){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();

        ResultSet rset = null;
        ProdutosFrigorifico itf = null;

        try{
            String comandoSql =  "select p.id as idProduto, p.nome, p.descricao, p.preco, p.categoria, p.marca, p.unidadeMedida, " +
                    "        if.id as idProdutoFrigorifico, if.idNotificacao, if.dtValidade, if.lote, if.status, if.quantidade as quantidadeProduto, " +
                    "        f.id as idFrigorifico, f.idEmpresa as cnpj, f.nome as nomeFrigorifico, f.status as statusFrigorifico, f.tipo, " +
                    "         en.id as idEndereco, en.rua, en.bairro, en.cidade, en.uf, en.pais, en.cep, en.numero, en.nomeUnidade " +
                    "from Produto p " +
                    "join ItemFrigorifico if on p.id = if.idProduto " +
                    "join Frigorifico f on if.idFrigorifico = f.id " +
                    "join endereco en on f.idEndereco = en.id " +
                    "where lower(p.nome) like ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            pstmt.setString(1, "%" + nomeProduto.toLowerCase() + "%");// setando os parâmetros da instrução
            rset = pstmt.executeQuery();
            if (rset.next()){
                Produto produto = new Produto(
                        rset.getInt("idProduto"),
                        rset.getString("nome"),
                        rset.getString("descricao"),
                        rset.getDouble("preco"),
                        Categoria.valueOf(rset.getString("categoria")),
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
                        StatusArmazenamento.valueOf(rset.getString("statusFrigorifico")),
                        endereco,
                        rset.getString("tipo"),
                        rset.getString("nomeFrigorifico")
                );
                // criando o objeto Produto no frigorifico com o ID vindo do ResultSet
                itf = new ProdutosFrigorifico(
                        rset.getInt("idProdutoFrigorifico"),
                        rset.getInt("idNotificacao"),
                        frigorifico,
                        produto,
                        rset.getObject("dtValidade", LocalDate.class),
                        StatusProduto.valueOf(rset.getString("status")),
                        rset.getInt("lote"),
                        rset.getInt("quantidadeProduto")
                );
            }
        }
        catch (SQLException sqle){
            sqle.printStackTrace();
        }
        finally {
            conexao.desconectar(conn);
        }
        return itf;
    }// BUSCAR 1 PRODUTO NA TABELA produtoFrigorificos PELO NOME


    // BUSCAR OS 5 PRIMEIROS PRODUTOS QUE ESTÃO PRESTES A VENECER OU JÁ VENCERAM
    public List<ProdutosFrigorifico> produtosPertoValidade(String cnpj){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();// abrindo a conexão do o banco

        ResultSet rset = null;
        List<ProdutosFrigorifico> lista = new ArrayList<>();

        try {
            String comandoSql = "select p.nome as produto, f.nome as frigorifico, if.quantidade, if.status, (if.dtValidade - current_date) as tempoRestante " +
                    "from ItemFrigorifico if " +
                    "join Frigorifico f on if.idFrigorifico = f.id " +
                    "join Produto p on if.idProduto = p.id " +
                    "where f.idEmpresa = ? " +
                    "order by if.dtValidade " +
                    "limit 5;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // setando os valores para a operação
            pstmt.setString(1, cnpj);

            rset = pstmt.executeQuery();
            while (rset.next()){
                Produto p = new Produto();
                p.setNome(rset.getString("produto"));

                Frigorificos f = new Frigorificos();
                f.setNome(rset.getString("frigorifico"));

                ProdutosFrigorifico itf = new ProdutosFrigorifico();
                itf.setProduto(p);
                itf.setFrigorificos(f);
                itf.setQuantidade(rset.getInt("quantidade"));
                itf.setStatus(StatusProduto.valueOf(rset.getString("status")));
                itf.setTempoRestante(rset.getInt("tempoRestante"));
                lista.add(itf);
            }


        }catch (SQLException sqle){
            sqle.printStackTrace();
        }finally {
            conexao.desconectar(conn);// fechado a conexão com o banco
        }
        return lista;
    }
    // BUSCAR OS 5 PRIMEIROS PRODUTOS QUE ESTÃO PRESTES A VENECER OU JÁ VENCERAM




    // TOTAL DE PRODUTOS VENCIDOS
    public int totalProdutosVencidos(String cnpj){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();// abrindo a conexão do o banco

        ResultSet rset = null;
        int total = 0;

        try {
            String comandoSql = "select count(*) as total " +
                    "from ItemFrigorifico if " +
                    "join Frigorifico f on if.idFrigorifico = f.id " +
                    "join Produto p on if.idProduto = p.id " +
                    "where f.idEmpresa = ? and if.status = 'VENCIDO';";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // setando os valores para a operação
            pstmt.setString(1, cnpj);

            rset = pstmt.executeQuery();
            if (rset.next()) total = rset.getInt("total");

        }catch (SQLException sqle){
            sqle.printStackTrace();
        }finally {
            conexao.desconectar(conn);// fechado a conexão com o banco
        }
        return total;
    }
    // TOTAL DE PRODUTOS VENCIDOS



    // TOTAL DE PRODUTOS ARMAZENADOS
    public int totalProdutosArmazenados(String cnpj){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();// abrindo a conexão do o banco

        ResultSet rset = null;
        int total = 0;

        try {
            String comandoSql = "select count(*) as total " +
                    "from ItemFrigorifico if " +
                    "join Frigorifico f on if.idFrigorifico = f.id " +
                    "join Produto p on if.idProduto = p.id " +
                    "where f.idEmpresa = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // setando os valores para a operação
            pstmt.setString(1, cnpj);

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



    // TOTAL DE PRODUTOS PROXIMOS DO VENCIMENTO
    public int totalProdutosProximoVencimento(String cnpj){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();// abrindo a conexão do o banco

        ResultSet rset = null;
        int total = 0;

        try {
            String comandoSql = "select count(*) as total " +
                    "from ItemFrigorifico if " +
                    "join Frigorifico f on if.idFrigorifico = f.id " +
                    "join Produto p on if.idProduto = p.id " +
                    "where f.idEmpresa = ? and if.status in ('ATENCAO', 'PROXIMO_VENCIMENTO');";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // setando os valores para a operação
            pstmt.setString(1, cnpj);

            rset = pstmt.executeQuery();
            if (rset.next()) total = rset.getInt("total");

        }catch (SQLException sqle){
            sqle.printStackTrace();
        }finally {
            conexao.desconectar(conn);// fechado a conexão com o banco
        }
        return total;
    }
    // TOTAL DE PRODUTOS PROXIMOS DO VENCIMENTO
}
