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
    public int inserirProdutoFrigorifico(ProdutosFrigorifico pf){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();

        try{
            String comandoSql = "insert into ProdutosFrigorifico (idNotificacao, idFrigorificos, idProdutos, dtValidade, status, lote, quantidade) values (?,?,?,?,?,?,?);";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql, Statement.RETURN_GENERATED_KEYS);

            // setando os valores pra a instrução
            pstmt.setInt(1, pf.getIdNotificacao());
            pstmt.setInt(2, pf.getFrigorificos().getIdFrigorifico());
            pstmt.setInt(3, pf.getProduto().getIdProdutos());
            pstmt.setObject(4, pf.getDtValidade());
            pstmt.setString(5, pf.getStatus().name());
            pstmt.setInt(6, pf.getLote());
            pstmt.setInt(7, pf.getQuantidade());

            if (pstmt.executeUpdate() > 0){
                ResultSet chaves = pstmt.getGeneratedKeys();// adicionando a chave criada pelo banco
                if (chaves.next()) pf.setIdProdutosFrigorifico(chaves.getInt(1));
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
    public int alterarProdutoFrigorifico(ProdutosFrigorifico pfAntigo, ProdutosFrigorifico pfNovo){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();

        try{
            String comandoSql = "update ProdutosFrigorifico set idNotificacao = ?, idFrigorificos = ?, idProdutos = ?, dtValidade = ?, status = ?, lote = ?, quantidade  =? where id = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);

            // setando os valores pra a instrução
            pstmt.setInt(1, pfNovo.getIdNotificacao());
            pstmt.setInt(2, pfNovo.getFrigorificos().getIdFrigorifico());
            pstmt.setInt(3, pfNovo.getProduto().getIdProdutos());
            pstmt.setObject(4, pfNovo.getDtValidade());
            pstmt.setString(5, pfNovo.getStatus().name());
            pstmt.setInt(6, pfNovo.getLote());
            pstmt.setInt(7, pfNovo.getQuantidade());
            pstmt.setInt(8, pfAntigo.getIdProdutosFrigorifico());

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
            String comandoSql = "select p.id as idProduto, p.nome, p.descricao, p.preco, p.categoria, p.marca, p.unidadeMedida, " +
                    "        pf.id as idProdutoFrigorifico, pf.idNotificacao, pf.dtValidade, pf.lote, pf.quantidade as quantidadeProduto, " +
                    "        f.id as idFrigorifico, f.idEmpresa as cnpj, f.nome as nomeFrigorifico, f.status, f.tipo, " +
                    "         en.id as idEndereco, en.rua, en.bairro, en.cidade, en.uf, en.pais, en.cep, en.numero, en.nomeUnidade " +
                    "from produtos p " +
                    "join produtosFrigorifico pf on p.id = pf.idProdutos " +
                    "join frigorificos f on pf.idFrigorificos = f.id " +
                    "join endereco en on f.idEndereco = en.id;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
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
                        StatusArmazenamento.valueOf(rset.getString("status")),
                        endereco,
                        rset.getString("tipo"),
                        rset.getString("nomeFrigorifico")
                );
                // criando o objeto Produto no frigorifico com o ID vindo do ResultSet
                ProdutosFrigorifico pf = new ProdutosFrigorifico(
                        rset.getInt("idProdutoFrigorifico"),
                        rset.getInt("idNotificacao"),
                        frigorifico,
                        produto,
                        rset.getObject("dtValidade", LocalDate.class),
                        StatusProduto.valueOf(rset.getString("status")),
                        rset.getInt("lote"),
                        rset.getInt("quantidadeProduto")
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
            String comandoSql = "select p.id as idProduto, p.nome, p.descricao, p.preco, p.categoria, p.marca, p.unidadeMedida, " +
                    "        pf.id as idProdutoFrigorifico, pf.idNotificacao, pf.dtValidade, pf.lote, pf.quantidade as quantidadeProduto, " +
                    "        f.id as idFrigorifico, f.idEmpresa as cnpj, f.nome as nomeFrigorifico, f.status, f.tipo, " +
                    "         en.id as idEndereco, en.rua, en.bairro, en.cidade, en.uf, en.pais, en.cep, en.numero, en.nomeUnidade " +
                    "from produtos p " +
                    "join produtosFrigorifico pf on p.id = pf.idProdutos " +
                    "join frigorificos f on pf.idFrigorificos = f.id " +
                    "join endereco en on f.idEndereco = en.id " +
                    "where pf.lote = ?;";
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
                        StatusArmazenamento.valueOf(rset.getString("status")),
                        endereco,
                        rset.getString("tipo"),
                        rset.getString("nomeFrigorifico")
                );
                // criando o objeto Produto no frigorifico com o ID vindo do ResultSet
                ProdutosFrigorifico pf = new ProdutosFrigorifico(
                        rset.getInt("idProdutoFrigorifico"),
                        rset.getInt("idNotificacao"),
                        frigorifico,
                        produto,
                        rset.getObject("dtValidade", LocalDate.class),
                        StatusProduto.valueOf(rset.getString("status")),
                        rset.getInt("lote"),
                        rset.getInt("quantidadeProduto")
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
            String comandoSql =  "select p.id as idProduto, p.nome, p.descricao, p.preco, p.categoria, p.marca, p.unidadeMedida, " +
                    "        pf.id as idProdutoFrigorifico, pf.idNotificacao, pf.dtValidade, pf.lote, pf.quantidade as quantidadeProduto, " +
                    "        f.id as idFrigorifico, f.idEmpresa as cnpj, f.nome as nomeFrigorifico, f.status, f.tipo, " +
                    "         en.id as idEndereco, en.rua, en.bairro, en.cidade, en.uf, en.pais, en.cep, en.numero, en.nomeUnidade " +
                    "from produtos p " +
                    "join produtosFrigorifico pf on p.id = pf.idProdutos " +
                    "join frigorificos f on pf.idFrigorificos = f.id " +
                    "join endereco en on f.idEndereco = en.id " +
                    "where pf.DtValidade = ?;";
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
                        StatusArmazenamento.valueOf(rset.getString("status")),
                        endereco,
                        rset.getString("tipo"),
                        rset.getString("nomeFrigorifico")
                );
                // criando o objeto Produto no frigorifico com o ID vindo do ResultSet
                ProdutosFrigorifico pf = new ProdutosFrigorifico(
                        rset.getInt("idProdutoFrigorifico"),
                        rset.getInt("idNotificacao"),
                        frigorifico,
                        produto,
                        rset.getObject("dtValidade", LocalDate.class),
                        StatusProduto.valueOf(rset.getString("status")),
                        rset.getInt("lote"),
                        rset.getInt("quantidadeProduto")
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
    public ProdutosFrigorifico buscarProdutoFrigorificoPorId(int idProduto){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();

        ResultSet rset = null;
        ProdutosFrigorifico pf = null;

        try{
            String comandoSql = "select p.id as idProduto, p.nome, p.descricao, p.preco, p.categoria, p.marca, p.unidadeMedida, " +
                    "        pf.id as idProdutoFrigorifico, pf.idNotificacao, pf.dtValidade, pf.lote, pf.quantidade as quantidadeProduto, " +
                    "        f.id as idFrigorifico, f.idEmpresa as cnpj, f.nome as nomeFrigorifico, f.status, f.tipo, " +
                    "         en.id as idEndereco, en.rua, en.bairro, en.cidade, en.uf, en.pais, en.cep, en.numero, en.nomeUnidade " +
                    "from produtos p " +
                    "join produtosFrigorifico pf on p.id = pf.idProdutos " +
                    "join frigorificos f on pf.idFrigorificos = f.id " +
                    "join endereco en on f.idEndereco = en.id " +
                    "where p.id = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            pstmt.setInt(1, idProduto);// setando os parâmetros da instrução
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
                        StatusArmazenamento.valueOf(rset.getString("status")),
                        endereco,
                        rset.getString("tipo"),
                        rset.getString("nomeFrigorifico")
                );
                // criando o objeto Produto no frigorifico com o ID vindo do ResultSet
                pf = new ProdutosFrigorifico(
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
        return pf;
    }// BUSCAR 1 PRODUTO NA TABELA produtoFrigorificos POR ID






    // BUSCAR 1 PRODUTO NA TABELA produtoFrigorificos PELO NOME
    public ProdutosFrigorifico buscarProdutoFrigorificoPeloNome(String nomeProduto){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();

        ResultSet rset = null;
        ProdutosFrigorifico pf = null;

        try{
            String comandoSql =  "select p.id as idProduto, p.nome, p.descricao, p.preco, p.categoria, p.marca, p.unidadeMedida, " +
                    "        pf.id as idProdutoFrigorifico, pf.idNotificacao, pf.dtValidade, pf.lote, pf.quantidade as quantidadeProduto, " +
                    "        f.id as idFrigorifico, f.idEmpresa as cnpj, f.nome as nomeFrigorifico, f.status, f.tipo, " +
                    "         en.id as idEndereco, en.rua, en.bairro, en.cidade, en.uf, en.pais, en.cep, en.numero, en.nomeUnidade " +
                    "from produtos p " +
                    "join produtosFrigorifico pf on p.id = pf.idProdutos " +
                    "join frigorificos f on pf.idFrigorificos = f.id " +
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
                        StatusArmazenamento.valueOf(rset.getString("status")),
                        endereco,
                        rset.getString("tipo"),
                        rset.getString("nomeFrigorifico")
                );
                // criando o objeto Produto no frigorifico com o ID vindo do ResultSet
                pf = new ProdutosFrigorifico(
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
        return pf;
    }// BUSCAR 1 PRODUTO NA TABELA produtoFrigorificos PELO NOME


    // BUSCAR OS 5 PRIMEIROS PRODUTOS QUE ESTÃO PRESTES A VENECER OU JÁ VENCERAM
    public List<ProdutosFrigorifico> produtosPertoValidade(String cnpj){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();// abrindo a conexão do o banco

        ResultSet rset = null;
        List<ProdutosFrigorifico> lista = new ArrayList<>();

        try {
            String comandoSql = "select p.nome as produto, f.nome as frigorifico, pf.quantidade, pf.status, (pf.dtValidade - current_date) as tempoRestante " +
                    "from produtosFrigorifico pf " +
                    "join Frigorificos f on pf.idFrigorificos = f.id " +
                    "join produtos p on pf.idProdutos = p.id " +
                    "where f.idEmpresa = ? " +
                    "order by pf.dtValidade " +
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

                ProdutosFrigorifico pf = new ProdutosFrigorifico();
                pf.setProduto(p);
                pf.setFrigorificos(f);
                pf.setQuantidade(rset.getInt("quantidade"));
                pf.setStatus(StatusProduto.valueOf(rset.getString("status")));
                pf.setTempoRestante(rset.getInt("tempoRestante"));
                lista.add(pf);
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
            String comandoSql = "select count(*) " +
                    "from produtosFrigorifico pf " +
                    "join Frigorificos f on pf.idFrigorificos = f.id " +
                    "join produtos p on pf.idProdutos = p.id " +
                    "where f.idEmpresa = ? and pf.status = 'VENCIDO';";
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
            String comandoSql = "select count(*) " +
                    "from produtosFrigorifico pf " +
                    "join Frigorificos f on pf.idFrigorificos = f.id " +
                    "join produtos p on pf.idProdutos = p.id " +
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
            String comandoSql = "select count(*) " +
                    "from produtosFrigorifico pf " +
                    "join Frigorificos f on pf.idFrigorificos = f.id " +
                    "join produtos p on pf.idProdutos = p.id " +
                    "where f.idEmpresa = ? and pf.status in ('ATENCAO', 'PROXIMO_VENCIMENTO');";
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
