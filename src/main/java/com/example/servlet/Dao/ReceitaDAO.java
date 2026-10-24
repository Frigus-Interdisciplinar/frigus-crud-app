package com.example.servlet.Dao;

import com.example.servlet.Model.Empresa;
import com.example.servlet.Model.Receita;
import com.example.servlet.Model.Usuario;
import com.example.servlet.Util.Conexao;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReceitaDAO {

    // ================== INSERIR ==================
    public int inserirReceita(Receita receita){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();

        try{
            String comandoSql = "insert into receita (idUsuario, idEmpresa, nome, descricao, modoPreparo, dtReceita, foiFeita) values (?,?,?,?,?,?,?);";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql, Statement.RETURN_GENERATED_KEYS);
            // o dono é o usuario ou a empresa, o outro vai como NULL
            if (receita.getUsuario() != null){
                pstmt.setString(1, receita.getUsuario().getCpf());
                pstmt.setNull(2, Types.VARCHAR);
            }
            else {
                pstmt.setNull(1, Types.VARCHAR);
                pstmt.setString(2, receita.getEmpresa().getCnpj());
            }
            pstmt.setString(3, receita.getNome());
            pstmt.setString(4, receita.getDescricao());
            pstmt.setString(5, receita.getModoPreparo());
            pstmt.setObject(6, receita.getDtReceita());
            pstmt.setBoolean(7, receita.isFoiFeita());

            if (pstmt.executeUpdate() > 0){
                ResultSet chaves = pstmt.getGeneratedKeys();// adicionando a chave criada pelo banco
                if (chaves.next()) receita.setIdReceita(chaves.getInt(1));
                return 1;// deu certo
            }
            else {
                return 0;// não deu certo
            }
        }catch (SQLException sqle){
            sqle.printStackTrace();
            return -1;
        }
        finally {
            conexao.desconectar(conn);
        }
    }// ================== INSERIR ==================



    // ================== ALTERAR ==================
    public int alterarReceita(Receita receitaAntiga, Receita receitaNova){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();

        try{
            String comandoSql = "update receita set idUsuario = ?, idEmpresa = ?, nome = ?, descricao = ?, modoPreparo = ?, dtReceita = ?, foiFeita = ? where id = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // o dono é o usuario ou a empresa, o outro vai como NULL
            if (receitaNova.getUsuario() != null){
                pstmt.setString(1, receitaNova.getUsuario().getCpf());
                pstmt.setNull(2, Types.VARCHAR);
            }
            else {
                pstmt.setNull(1, Types.VARCHAR);
                pstmt.setString(2, receitaNova.getEmpresa().getCnpj());
            }
            pstmt.setString(3, receitaNova.getNome());
            pstmt.setString(4, receitaNova.getDescricao());
            pstmt.setString(5, receitaNova.getModoPreparo());
            pstmt.setObject(6, receitaNova.getDtReceita());
            pstmt.setBoolean(7, receitaNova.isFoiFeita());
            pstmt.setInt(8, receitaAntiga.getIdReceita());

            if (pstmt.executeUpdate() > 0){
                return 1;// deu certo
            }
            else {
                return 0;// não deu certo
            }
        }catch (SQLException sqle){
            sqle.printStackTrace();
            return -1;
        }
        finally {
            conexao.desconectar(conn);
        }
    }// ================== ALTERAR ==================



    // ================== DELETAR ==================
    public int deletarReceita(Receita receita){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();

        try{
            // apagar a receita apaga os ingredientes pelo ON DELETE CASCADE, os produtos continuam existindo
            conn.setAutoCommit(false); // nada é salvo de verdade até o commit

            String comandoSql = "delete from receita where id = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            pstmt.setInt(1, receita.getIdReceita());

            if (pstmt.executeUpdate() <= 0){
                conn.rollback();
                return 0;// não deu certo
            }

            conn.commit();
            return 1;// deu certo
        }catch (SQLException sqle){
            sqle.printStackTrace();
            try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            return -1;
        }
        finally {
            conexao.desconectar(conn);
        }
    }// ================== DELETAR ==================



    // ================== BUSCAR TODAS AS RECEITAS ( COM O NOME DO DONO ) ==================
    public List<Receita> buscarTodasReceita(){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();

        ResultSet rset = null;
        List<Receita> lista = new ArrayList<>();

        try{
            String comandoSql = "select r.*, u.nome as nomeUsuario, u.sobrenome, e.nome as nomeEmpresa " +
                    "from receita r " +
                    "left join usuario u on u.cpf = r.idUsuario " +
                    "left join empresa e on e.cnpj = r.idEmpresa " +
                    "order by r.id;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            rset = pstmt.executeQuery();

            while (rset.next()){
                Receita receita = new Receita();
                receita.setIdReceita(rset.getInt("id"));
                receita.setNome(rset.getString("nome"));
                receita.setDescricao(rset.getString("descricao"));
                receita.setModoPreparo(rset.getString("modoPreparo"));
                receita.setDtReceita(rset.getObject("dtReceita", LocalDate.class));
                receita.setFoiFeita(rset.getBoolean("foiFeita"));

                // criando o dono: usuario ou empresa
                if (rset.getString("idUsuario") != null){
                    Usuario usuario = new Usuario();
                    usuario.setCpf(rset.getString("idUsuario"));
                    usuario.setNome(rset.getString("nomeUsuario"));
                    usuario.setSobrenome(rset.getString("sobrenome"));
                    receita.setUsuario(usuario);
                }
                else {
                    Empresa empresa = new Empresa();
                    empresa.setCnpj(rset.getString("idEmpresa"));
                    empresa.setNome(rset.getString("nomeEmpresa"));
                    receita.setEmpresa(empresa);
                }
                lista.add(receita);
            }
        }catch (SQLException sqle){
            sqle.printStackTrace();
        }
        finally {
            conexao.desconectar(conn);
        }
        return lista;
    }// ================== BUSCAR TODAS AS RECEITAS ( COM O NOME DO DONO ) ==================



    // ================== BUSCAR UMA RECEITA PELO ID ==================
    public Receita buscarReceitaPorId(int idReceita){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();

        ResultSet rset = null;
        Receita receita = null;

        try{
            String comandoSql = "select * from receita where id = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // setando o valor do id da receita
            pstmt.setInt(1, idReceita);
            rset = pstmt.executeQuery();

            if (rset.next()){
                receita = new Receita();
                receita.setIdReceita(rset.getInt("id"));
                receita.setNome(rset.getString("nome"));
                receita.setDescricao(rset.getString("descricao"));
                receita.setModoPreparo(rset.getString("modoPreparo"));
                receita.setDtReceita(rset.getObject("dtReceita", LocalDate.class));
                receita.setFoiFeita(rset.getBoolean("foiFeita"));

                // criando o dono: usuario ou empresa
                if (rset.getString("idUsuario") != null){
                    Usuario usuario = new Usuario();
                    usuario.setCpf(rset.getString("idUsuario"));
                    receita.setUsuario(usuario);
                }
                else {
                    Empresa empresa = new Empresa();
                    empresa.setCnpj(rset.getString("idEmpresa"));
                    receita.setEmpresa(empresa);
                }
            }
        }catch (SQLException sqle){
            sqle.printStackTrace();
        }
        finally {
            conexao.desconectar(conn);
        }
        return receita;
    }// ================== BUSCAR UMA RECEITA PELO ID ==================



    // ================== TOTAL DE RECEITAS ==================
    public int totalReceitas(){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();

        ResultSet rset = null;
        int total = 0;

        try{
            String comandoSql = "select count(*) as total from receita;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            rset = pstmt.executeQuery();

            if (rset.next()) total = rset.getInt("total");

        }catch (SQLException sqle){
            sqle.printStackTrace();
        }
        finally {
            conexao.desconectar(conn);
        }
        return total;
    }// ================== TOTAL DE RECEITAS ==================

}
