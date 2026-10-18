package com.example.servlet.Dao;

import com.example.servlet.Dto.SugestaoReceita;
import com.example.servlet.Model.Receita;
import com.example.servlet.Util.Conexao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReceitaDAO {

    // ================== USUARIO ==================

    // ========== INSERIR RECEITA DO USUARIO ==========
    public int inserirReceitaUsuario(Receita receita){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();

        try{
            String comandoSql = "insert into receita (idUsuario, nome, descricao, modoPreparo, dtReceita, foiFeita) values (?,?,?,?,?,?);";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql, Statement.RETURN_GENERATED_KEYS);

            // setando os valores para a operação
            pstmt.setString(1, receita.getUsuario().getCpf());
            pstmt.setString(2, receita.getNome());
            pstmt.setString(3, receita.getDescricao());
            pstmt.setString(4, receita.getModoPreparo());
            pstmt.setObject(5, receita.getDtReceita());
            pstmt.setBoolean(6, receita.isFoiFeita());

            if (pstmt.executeUpdate() > 0){
                ResultSet chaves = pstmt.getGeneratedKeys();// adicionando a chave criada pelo banco
                if(chaves.next()) receita.setIdReceita(chaves.getInt(1));
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
            conexao.desconectar(conn);
        }

    }// ========== INSERIR RECEITA DO USUARIO ==========



    // ========== ALTERAR RECEITA DO USUARIO ==========
    public int alterarReceitaUsuario(Receita receitaAntiga, Receita receitaNova){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();

        try{
            String comandoSql = "update receita set idUsuario = ?, nome = ?, descricao = ?, modoPreparo = ?, dtReceita = ?, foiFeita = ? where id = ? and idUsuario is not null;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);

            // setando os valores para a operação
            pstmt.setString(1, receitaNova.getUsuario().getCpf());
            pstmt.setString(2, receitaNova.getNome());
            pstmt.setString(3, receitaNova.getDescricao());
            pstmt.setString(4, receitaNova.getModoPreparo());
            pstmt.setObject(5, receitaNova.getDtReceita());
            pstmt.setBoolean(6, receitaNova.isFoiFeita());
            pstmt.setInt(7, receitaAntiga.getIdReceita());

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
            conexao.desconectar(conn);
        }

    }// ========== ALTERAR RECEITA DO USUARIO ==========





    // RECEITAS DISPONIVEIS - USUARIO
    // A QUANTIDADE DE PRODUTOS ARMAZENADOS É MAIOR QUE A QUANTIDADE DE PRODUTOS NECESSARIOS PARA A RECEITA
    public int totalReceitasDisponiveisUsuario(String cpf){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();
        ResultSet rset = null;

        try {
            String comandoSql = "SELECT count(*) " +
                    "FROM Receita r " +
                    "WHERE r.idUsuario = ? " +
                    "  AND EXISTS (SELECT 1 FROM Ingrediente i0 WHERE i0.idReceita = r.id) " +
                    "  AND NOT EXISTS ( " +
                    "        SELECT 1 " +
                    "        FROM Ingrediente i " +
                    "        WHERE i.idReceita = r.id " +
                    "          AND i.quantidade > COALESCE(( " +
                    "                SELECT SUM(ig.quantidade) " +
                    "                FROM ItemGeladeira ig " +
                    "                JOIN Geladeira g ON g.id = ig.idGeladeira " +
                    "                WHERE g.idUsuario   = r.idUsuario " +
                    "                  AND ig.idProduto = i.idProduto " +
                    "                  AND ig.dtValidade >= CURRENT_DATE " +
                    "          ), 0) " +
                    "  );";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            pstmt.setString(1, cpf);
            rset = pstmt.executeQuery();
            if(rset.next()) return rset.getInt(1);
        }catch (SQLException sqle){
            sqle.printStackTrace();
        }
        finally {
            conexao.desconectar(conn);
        }

        return 0;
    }
    // RECEITAS DISPONIVEIS - USUARIO
    // A QUANTIDADE DE PRODUTOS ARMAZENADOS É MAIOR QUE A QUANTIDADE DE PRODUTOS NECESSARIOS PARA A RECEITA


    // SUGESTOES DE RECEITA
    public List<SugestaoReceita> listarSugestoesUsuario(String cpf){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();
        List<SugestaoReceita> lista = new ArrayList<>();
        ResultSet rs = null;
        try{
            String comandoSql = """
        WITH estoque AS (
            SELECT ig.idProduto, SUM(ig.quantidade) AS qtd
            FROM ItemGeladeira ig
            JOIN Geladeira g ON g.id = ig.idGeladeira
            WHERE g.idUsuario = ?
              AND ig.dtValidade >= CURRENT_DATE
            GROUP BY ig.idProduto
        )
        SELECT r.id,
               r.nome,
               COUNT(*) AS total_ingredientes,
               COUNT(*) FILTER (WHERE COALESCE(e.qtd, 0) >= i.quantidade) AS ingredientes_disponiveis
        FROM Receita r
        JOIN Ingrediente i ON i.idReceita  = r.id
        LEFT JOIN estoque e ON e.idProduto = i.idProduto
        WHERE r.idUsuario = ?
        GROUP BY r.id, r.nome
        ORDER BY COUNT(*) FILTER (WHERE COALESCE(e.qtd, 0) >= i.quantidade)::float / COUNT(*) DESC,
                 r.nome
        LIMIT 3
        """;
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);

            pstmt.setString(1, cpf);
            pstmt.setString(2, cpf);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                lista.add(new SugestaoReceita(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getInt("total_ingredientes"),
                        rs.getInt("ingredientes_disponiveis")
                ));
            }


        }catch (SQLException sqle){
            sqle.printStackTrace();
        }
        finally {
            conexao.desconectar(conn);
        }
        return lista;
    }
    // SUGESTOES DE RECEITA



    // ================== USUARIO ==================



    // ================== EMPRESA ==================


    // ========== INSERIR RECEITA DA EMPRESA ==========
    public int inserirReceitaEmpresa(Receita receita){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();

        try{
            String comandoSql = "insert into receita (idEmpresa, nome, descricao, modoPreparo, dtReceita, foiFeita) values (?,?,?,?,?,?);";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql, Statement.RETURN_GENERATED_KEYS);

            // setando os valores para a operação
            pstmt.setString(1, receita.getEmpresa().getCnpj());
            pstmt.setString(2, receita.getNome());
            pstmt.setString(3, receita.getDescricao());
            pstmt.setString(4, receita.getModoPreparo());
            pstmt.setObject(5, receita.getDtReceita());
            pstmt.setBoolean(6, receita.isFoiFeita());

            if (pstmt.executeUpdate() > 0){
                ResultSet chaves = pstmt.getGeneratedKeys();// adicionando a chave criada pelo banco
                if(chaves.next()) receita.setIdReceita(chaves.getInt(1));
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
            conexao.desconectar(conn);
        }

    }// ========== INSERIR RECEITA DA EMPRESA ==========



    // ========== ALTERAR RECEITA DA EMPRESA ==========
    public int alterarReceitaEmpresa(Receita receitaAntiga, Receita receitaNova){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();

        try{
            String comandoSql = "update receita set idEmpresa = ?, nome = ?, descricao = ?, modoPreparo = ?, dtReceita = ?, foiFeita = ? where id = ? and idEmpresa is not null;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);

            // setando os valores para a operação
            pstmt.setString(1, receitaNova.getEmpresa().getCnpj());
            pstmt.setString(2, receitaNova.getNome());
            pstmt.setString(3, receitaNova.getDescricao());
            pstmt.setString(4, receitaNova.getModoPreparo());
            pstmt.setObject(5, receitaNova.getDtReceita());
            pstmt.setBoolean(6, receitaNova.isFoiFeita());
            pstmt.setInt(7, receitaAntiga.getIdReceita());

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
            conexao.desconectar(conn);
        }

    }// ========== ALTERAR RECEITA DA EMPRESA ==========


    // RECEITAS DISPONIVEIS - EMPRESA
    // A QUANTIDADE DE PRODUTOS ARMAZENADOS É MAIOR QUE A QUANTIDADE DE PRODUTOS NECESSARIOS PARA A RECEITA
    public int totalReceitasDisponiveisEmpresa(String cnpj){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();
        ResultSet rset = null;

        try {
            String comandoSql = """
            SELECT COUNT(*)
            FROM Receita r
            WHERE r.idEmpresa = ?
              AND EXISTS (SELECT 1 FROM Ingrediente i0 WHERE i0.idReceita = r.id)
              AND NOT EXISTS (
                    SELECT 1
                    FROM Ingrediente i
                    WHERE i.idReceita = r.id
                      AND i.quantidade > COALESCE((
                            SELECT SUM(if.quantidade)
                            FROM ItemFrigorifico if
                            JOIN Frigorifico f ON f.id = if.idFrigorifico
                            WHERE f.idEmpresa   = r.idEmpresa
                              AND if.idProduto = i.idProduto
                              AND if.dtValidade >= CURRENT_DATE
                      ), 0)
              )
        """;
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            pstmt.setString(1, cnpj);
            rset = pstmt.executeQuery();
            if(rset.next()) return rset.getInt(1);
        }catch (SQLException sqle){
            sqle.printStackTrace();
        }
        finally {
            conexao.desconectar(conn);
        }

        return 0;
    }
    // RECEITAS DISPONIVEIS - EMPRESA
    // A QUANTIDADE DE PRODUTOS ARMAZENADOS É MAIOR QUE A QUANTIDADE DE PRODUTOS NECESSARIOS PARA A RECEITA



    // SUGESTOES DE RECEITA
    public List<SugestaoReceita> listarSugestoesEmpresa(String cnpj){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();
        List<SugestaoReceita> lista = new ArrayList<>();
        ResultSet rs = null;
        try{
            String comandoSql = """
        WITH estoque AS (
            SELECT if.idProduto, SUM(if.quantidade) AS qtd
            FROM ItemFrigorifico if
            JOIN Frigorifico f ON f.id = if.idFrigorifico
            WHERE f.idEmpresa = ?
              AND if.dtValidade >= CURRENT_DATE
            GROUP BY if.idProduto
        )
        SELECT r.id,
               r.nome,
               COUNT(*) AS total_ingredientes,
               COUNT(*) FILTER (WHERE COALESCE(e.qtd, 0) >= i.quantidade) AS ingredientes_disponiveis
        FROM Receita r
        JOIN Ingrediente i ON i.idReceita  = r.id
        LEFT JOIN estoque e ON e.idProduto = i.idProduto
        WHERE r.idEmpresa = ?
        GROUP BY r.id, r.nome
        ORDER BY COUNT(*) FILTER (WHERE COALESCE(e.qtd, 0) >= i.quantidade)::float / COUNT(*) DESC,
                 r.nome
        LIMIT 3
        """;
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);

            pstmt.setString(1, cnpj);
            pstmt.setString(2, cnpj);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                lista.add(new SugestaoReceita(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getInt("total_ingredientes"),
                        rs.getInt("ingredientes_disponiveis")
                ));
            }


        }catch (SQLException sqle){
            sqle.printStackTrace();
        }
        finally {
            conexao.desconectar(conn);
        }
        return lista;
    }
    // SUGESTOES DE RECEITA



    // ================== EMPRESA ==================


    // ========== DELETAR RECEITA UNIVERSAL ==========
    public int deletarReceita(Receita receita){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();

        try{
            String comandoSql = "delete from receita where id = ?";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);

            // setando os valores para a operação
            pstmt.setInt(1, receita.getIdReceita());

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
            conexao.desconectar(conn);
        }

    }// ========== DELETAR RECEITA UNIVERSAL ==========

}
