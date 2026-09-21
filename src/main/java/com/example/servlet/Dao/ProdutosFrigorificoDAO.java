package com.example.servlet.Dao;

import com.example.servlet.Model.*;
import com.example.servlet.Util.Conexao;
import com.example.servlet.Util.Enum.Categoria;
import com.example.servlet.Util.Service.ArmazenamentoService;

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

                // 1. pega o frigorífico e a assinatura da empresa dona dele
                Frigorificos frigorifico = pf.getFrigorificos();
                AssinaturaDAO assinaturaDAO = new AssinaturaDAO();
                Assinatura assinatura = assinaturaDAO.buscarAssinaturaEmpresa();

                // 2. recalcula e aplica o novo status no objeto
                ArmazenamentoService.atualizarStatusFrigorifico(frigorifico, assinatura);

                // 3. persiste esse novo status no banco
                FrigorificosDAO frigorificosDAO = new FrigorificosDAO();
                frigorificosDAO.alterarStatusFrigorifico(frigorifico);
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
            String comandoSql = "update ProdutosFrigorifico set idNotificacao = ?, idFrigrificos = ?, idProdutos = ?, dtValidade = ?, status = ?, lote = ?, quantidade  =? where id = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);

            // setando os valores pra a instrução
            pstmt.setInt(1, pf.getIdNotificacao());
            pstmt.setInt(2, pf.getFrigorificos().getIdFrigorifico());
            pstmt.setInt(3, pf.getProduto().getIdProdutos());
            pstmt.setObject(4, pf.getDtValidade());
            pstmt.setString(5, pf.getStatus().name());
            pstmt.setInt(6, pf.getLote());
            pstmt.setInt(7, pf.getQuantidade());
            pstmt.setInt(8, pf.getIdProdutosFrigorifico());

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
                    "        pf.id as idProdutoFrigorifico, pf.idNotificacao, pf.dtValidade, pf.lote, pf.quantidade, " +
                    "        f.id as idFrigorifico, f.idEmpresa, f.status, f.tipo, " +
                    "         en.id as idEndereco, en.rua, en.bairro, en.cidade, en.uf, en.pais, en.cep, en.numero, em.nomeUnidade " +
                    "from produtos p " +
                    "join produtosFrigorificos pf on p.id = pf.idProdutos " +
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
                        rset.getInt("lote"),
                        rset.getInt("quantidade")
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
                    "        pf.id as idProdutoFrigorifico, pf.idNotificacao, pf.dtValidade, pf.lote, pf.quantidade, " +
                    "        f.id as idFrigorifico, f.idEmpresa, f.status, f.tipo, " +
                    "         en.id as idEndereco, en.rua, en.bairro, en.cidade, en.uf, en.pais, en.cep, en.numero, em.nomeUnidade " +
                    "from produtos p " +
                    "join produtosFrigorificos pf on p.id = pf.idProdutos " +
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
                        rset.getInt("lote"),
                        rset.getInt("quantidade")
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
                    "        pf.id as idProdutoFrigorifico, pf.idNotificacao, pf.dtValidade, pf.lote, pf.quantidade, " +
                    "        f.id as idFrigorifico, f.idEmpresa, f.status, f.tipo, " +
                    "         en.id as idEndereco, en.rua, en.bairro, en.cidade, en.uf, en.pais, en.cep, en.numero, em.nomeUnidade " +
                    "from produtos p " +
                    "join produtosFrigorificos pf on p.id = pf.idProdutos " +
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
                        rset.getInt("lote"),
                        rset.getInt("quantidade")
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
            String comandoSql = "select p.id as idProduto, p.nome, p.descricao, p.preco, p.categoria, p.marca, p.unidadeMedida, " +
                    "        pf.id as idProdutoFrigorifico, pf.idNotificacao, pf.dtValidade, pf.lote, pf.quantidade, " +
                    "        f.id as idFrigorifico, f.idEmpresa, f.status, f.tipo, " +
                    "         en.id as idEndereco, en.rua, en.bairro, en.cidade, en.uf, en.pais, en.cep, en.numero, em.nomeUnidade " +
                    "from produtos p " +
                    "join produtosFrigorificos pf on p.id = pf.idProdutos " +
                    "join frigorificos f on pf.idFrigorificos = f.id " +
                    "join endereco en on f.idEndereco = en.id " +
                    "where p.id = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            pstmt.setInt(1,produto.getIdProdutos());// setando os parâmetros da instrução
            rset = pstmt.executeQuery();
            if (rset.next()){
                produto = new Produto(
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
                        rset.getInt("lote"),
                        rset.getInt("quantidade")
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
            String comandoSql =  "select p.id as idProduto, p.nome, p.descricao, p.preco, p.categoria, p.marca, p.unidadeMedida, " +
                    "        pf.id as idProdutoFrigorifico, pf.idNotificacao, pf.dtValidade, pf.lote, pf.quantidade, " +
                    "        f.id as idFrigorifico, f.idEmpresa, f.status, f.tipo, " +
                    "         en.id as idEndereco, en.rua, en.bairro, en.cidade, en.uf, en.pais, en.cep, en.numero, em.nomeUnidade " +
                    "from produtos p " +
                    "join produtosFrigorificos pf on p.id = pf.idProdutos " +
                    "join frigorificos f on pf.idFrigorificos = f.id " +
                    "join endereco en on f.idEndereco = en.id " +
                    "where lower(p.nome) like ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            pstmt.setString(1, "%" + produto.getNome().toLowerCase() + "%");// setando os parâmetros da instrução
            rset = pstmt.executeQuery();
            if (rset.next()){
                produto = new Produto(
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
                        rset.getInt("lote"),
                        rset.getInt("quantidade")
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
