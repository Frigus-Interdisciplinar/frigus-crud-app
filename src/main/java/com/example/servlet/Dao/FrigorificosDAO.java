package com.example.servlet.Dao;

import com.example.servlet.Model.Empresa;
import com.example.servlet.Model.Endereco;
import com.example.servlet.Model.Frigorificos;
import com.example.servlet.Util.Conexao;
import com.example.servlet.Util.Enum.StatusArmazenamento;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FrigorificosDAO {
    // INSERIR
    public int inserirFrigorifico(Frigorificos frigorifico){
        Conexao conexao = new Conexao(); // abrindo a conexão
        Connection conn = conexao.conectar();
        Endereco endereco = frigorifico.getEndereco();

        try{
            conn.setAutoCommit(false); // nada é salvo de verdade até o commit

            // insere o endereço
            String comandoSqlEndereco = "insert into endereco (rua, bairro, cidade, uf, pais, cep, numero, nomeUnidade) values (?,?,?,?,?,?,?,?);";
            PreparedStatement pstmtEndereco = conn.prepareStatement(comandoSqlEndereco, Statement.RETURN_GENERATED_KEYS);
            pstmtEndereco.setString(1, endereco.getRua());
            pstmtEndereco.setString(2, endereco.getBairro());
            pstmtEndereco.setString(3, endereco.getCidade());
            pstmtEndereco.setString(4, endereco.getUf());
            pstmtEndereco.setString(5, endereco.getPais());
            pstmtEndereco.setString(6, endereco.getCep());
            pstmtEndereco.setString(7, endereco.getNumero());
            pstmtEndereco.setString(8, endereco.getNomeUnidade());

            if (pstmtEndereco.executeUpdate() <= 0){
                conn.rollback();
                return 0;
            }
            ResultSet chavesEndereco = pstmtEndereco.getGeneratedKeys();// adicionando a chave criada pelo banco
            if (chavesEndereco.next()) endereco.setIdEndereco(chavesEndereco.getInt(1));

            // insere o frigorífico usando o id do endereço recém criado
            String comandoSql = "insert into Frigorifico (idEmpresa, status, idEndereco, tipo, nome) values (?,?,?,?,?);";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql, Statement.RETURN_GENERATED_KEYS);
            // setando os valores para o comando
            pstmt.setString(1, frigorifico.getEmpresa().getCnpj());
            pstmt.setString(2, frigorifico.getStatus().name());
            pstmt.setInt(3, endereco.getIdEndereco());
            pstmt.setString(4, frigorifico.getTipo());
            pstmt.setString(5, frigorifico.getNome());

            if (pstmt.executeUpdate() <= 0){
                conn.rollback();
                endereco.setIdEndereco(0);
                return 0;
            }
            ResultSet chaves = pstmt.getGeneratedKeys();// adicionando a chave criada pelo banco
            if(chaves.next()) frigorifico.setIdFrigorifico(chaves.getInt(1));

            conn.commit();
            return 1;// verifica se deu certo
        }
        catch (SQLException sqle){
            sqle.printStackTrace();
            try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            // o rollback torna esses ids falsos
            endereco.setIdEndereco(0);
            frigorifico.setIdFrigorifico(0);
            return -1;
        }
        finally {
            conexao.desconectar(conn);// fechando a conexão
        }
    }// INSERIR




    //==============ALTERAR=============
    public int alterarFrigorifico(Frigorificos frigorificoAntigo, Frigorificos frigorificoNovo){
        Conexao conexao = new Conexao(); // abrindo a conexão
        Connection conn = conexao.conectar();

        // o frigorífico continua com o mesmo endereço, então o id vem do frigorífico antigo
        int idEndereco = frigorificoAntigo.getEndereco().getIdEndereco();
        Endereco enderecoNovo = frigorificoNovo.getEndereco();

        try{
            conn.setAutoCommit(false); // nada é salvo de verdade até o commit

            // altera o endereço
            String comandoSqlEndereco = "update endereco set rua = ?, bairro = ?, cidade = ?, uf = ?, pais = ?, cep = ?, numero = ?, nomeUnidade = ? where id = ?;";
            PreparedStatement pstmtEndereco = conn.prepareStatement(comandoSqlEndereco);
            pstmtEndereco.setString(1, enderecoNovo.getRua());
            pstmtEndereco.setString(2, enderecoNovo.getBairro());
            pstmtEndereco.setString(3, enderecoNovo.getCidade());
            pstmtEndereco.setString(4, enderecoNovo.getUf());
            pstmtEndereco.setString(5, enderecoNovo.getPais());
            pstmtEndereco.setString(6, enderecoNovo.getCep());
            pstmtEndereco.setString(7, enderecoNovo.getNumero());
            pstmtEndereco.setString(8, enderecoNovo.getNomeUnidade());
            pstmtEndereco.setInt(9, idEndereco);

            if (pstmtEndereco.executeUpdate() <= 0){
                conn.rollback();
                return 0;
            }

            String comandoSql = "update Frigorifico set idEmpresa = ?, idEndereco = ?, tipo = ?, nome = ?  where id = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // setando os valores para o comando
            pstmt.setString(1, frigorificoNovo.getEmpresa().getCnpj());
            pstmt.setInt(2, idEndereco);
            pstmt.setString(3, frigorificoNovo.getTipo());
            pstmt.setString(4, frigorificoNovo.getNome());
            pstmt.setInt(5, frigorificoAntigo.getIdFrigorifico());

            if (pstmt.executeUpdate() <= 0){
                conn.rollback();
                return 0;// não deu certo
            }

            conn.commit();
            return 1;// verifica se deu certo
        }
        catch (SQLException sqle){
            sqle.printStackTrace();
            try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            return -1;
        }
        finally {
            conexao.desconectar(conn);// fechando a conexão
        }
    }// ==============ALTERAR===============




    // ================ ALTERAR STATUS ================
    public int atualizarStatus(Frigorificos frigorificos){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();// abrindo a conexão com o banco

        try{
            String comandoSlq = "update Frigorifico set status = ? where id = ?";
            PreparedStatement pstmt = conn.prepareStatement(comandoSlq);
            // setando os valores para a operação
            pstmt.setString(1, frigorificos.getStatus().name());
            pstmt.setInt(2, frigorificos.getIdFrigorifico());
            // verifica se foi possivel alterar o banco
            if (pstmt.executeUpdate() > 0){
                return 1;// deu certo
            }else {
                return 0;// não deu certo
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            return -1;// deu erro!!!
        }
        finally {
            conexao.desconectar(conn);// fechando a conexão com o banco
        }
    }// ================ ALTERAR STATUS ================



    // ================== DELETAR ==================
    public int deletarFrigorifico(Frigorificos frigorifico){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();// abrindo a conexao
        try{
            conn.setAutoCommit(false); // nada é salvo de verdade até o commit

            // guarda os produtos dos itens antes de apagar o frigorífico, porque eles ficariam soltos
            String comandoSqlProdutos = "select idProduto from ItemFrigorifico where idFrigorifico = ?;";
            List<Integer> idsProdutos = ProdutosGeladeiraDAO.buscarIds(conn, comandoSqlProdutos, frigorifico.getIdFrigorifico());

            // apagar o endereço já apaga o frigorífico, pelo ON DELETE CASCADE da fk_frigorifico_endereco
            String comandoSqlEndereco = "delete from endereco where id = ?;";
            PreparedStatement pstmtEndereco = conn.prepareStatement(comandoSqlEndereco);
            pstmtEndereco.setInt(1, frigorifico.getEndereco().getIdEndereco());
            if (pstmtEndereco.executeUpdate() <= 0){
                conn.rollback();
                return 0;
            }

            // apaga os produtos que sobraram sem ninguém usando
            ProdutosGeladeiraDAO.apagarProdutosOrfaos(conn, idsProdutos);

            conn.commit();
            return 1;

        }catch (SQLException sqle){
            sqle.printStackTrace();
            try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            return -1;
        }
        finally {
            conexao.desconectar(conn);
        }
    }
    // ================== DELETAR ==================




    // ==============BUSCAR TODOS OS FRIGORIFICOS ( COM O ENDEREÇO E O NOME DA EMPRESA ) ===============
    public List<Frigorificos> buscarTodosFrigorifico(){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();// abrindo a conexao

        ResultSet rset = null;

        List<Frigorificos> listaFrigorificos = new ArrayList<>();

        try{
            String comandoSql = "select f.id, f.idEmpresa, f.status, f.tipo, f.nome, e.nome as nomeEmpresa, " +
                    "en.id as idEndereco, en.rua, en.bairro, en.cidade, en.uf, en.pais, en.cep, en.numero, en.nomeUnidade " +
                    "from Frigorifico f " +
                    "join empresa e on e.cnpj = f.idEmpresa " +
                    "join endereco en on en.id = f.idEndereco " +
                    "order by f.id;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            rset = pstmt.executeQuery();
            while (rset.next()){
                // criando o objeto endereço
                Endereco endereco = new Endereco(
                        rset.getInt("idEndereco"),
                        rset.getString("rua"),
                        rset.getString("bairro"),
                        rset.getString("cidade"),
                        rset.getString("uf"),
                        rset.getString("pais"),
                        rset.getString("cep"),
                        rset.getString("numero"),
                        rset.getString("nomeUnidade")
                );

                // criando o objeto empresa
                Empresa empresa = new Empresa();
                empresa.setCnpj(rset.getString("idEmpresa"));
                empresa.setNome(rset.getString("nomeEmpresa"));
                // criando o objeto frigorifico
                Frigorificos frigorifico = new Frigorificos(
                        rset.getInt("id"),
                        empresa,
                        StatusArmazenamento.valueOf(rset.getString("status")),
                        endereco,
                        rset.getString("tipo"),
                        rset.getString("nome")
                );
                listaFrigorificos.add(frigorifico);
            }
        }catch (SQLException sqle){
            sqle.printStackTrace();
        }finally {
            conexao.desconectar(conn);// fechando a conexao
        }
        return listaFrigorificos;
    }// ==============BUSCAR TODOS OS FRIGORIFICOS ( COM O ENDEREÇO E O NOME DA EMPRESA ) ===============



    // ==============BUSCAR UM FRIGORIFICO PELO ID ===============
    public Frigorificos buscarFrigorificoPorId(int id){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();// abrindo a conexao

        ResultSet rset = null;

        Frigorificos frigorifico = null;

        try{
            String comandoSql = "select * from Frigorifico where id = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // setando o valor do id do frigorifico
            pstmt.setInt(1, id);
            rset = pstmt.executeQuery();
            while (rset.next()){
                // criando o objeto endereço
                Endereco endereco = new Endereco();
                endereco.setIdEndereco(rset.getInt("idEndereco"));

                // criando o objeto empresa
                Empresa empresa = new Empresa();
                empresa.setCnpj(rset.getString("idEmpresa"));
                // criando o objeto frigorifico
                 frigorifico = new Frigorificos(
                        rset.getInt("id"),
                        empresa,
                        StatusArmazenamento.valueOf(rset.getString("status")),
                        endereco,
                        rset.getString("tipo"),
                        rset.getString("nome")
                );

            }
        }catch (SQLException sqle){
            sqle.printStackTrace();
        }finally {
            conexao.desconectar(conn);// fechando a conexao
        }
        return frigorifico;
    }// ==============BUSCAR UM FRIGORIFICO PELO ID ===============


    // TOTAL DE FRIGORIFICOS
    public int totalFrigorificos(){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();
        int total = 0;
        ResultSet rset = null;
        try {
            String comandoSql = "select count(*) as total from Frigorifico;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            rset = pstmt.executeQuery();

            if(rset.next()) total = rset.getInt("total");

        }catch (SQLException sqle){
            sqle.printStackTrace();
        }
        finally {
            conexao.desconectar(conn);
        }
        return total;
    }
    // TOTAL DE FRIGORIFICOS
}
