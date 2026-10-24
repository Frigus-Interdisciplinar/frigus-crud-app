package com.example.servlet.Dao;

import com.example.servlet.Model.Endereco;
import com.example.servlet.Util.Conexao;

import java.sql.*;
import java.util.List;

public class EnderecoDAO {

    // ============= BUSCAR UM ENDEREÇO PELO ID ===============
    public Endereco buscarEnderecoPorId(int idEndereco){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar(); // abre a conexao com o banco

        ResultSet rset = null;
        Endereco endereco = null;

        try{
            String comandoSql = "select * from endereco where id = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // setando o valore para a operação
            pstmt.setInt(1, idEndereco);
            rset = pstmt.executeQuery();

            if (rset.next()){
                // criando o objeto endereco
                endereco = new Endereco(
                        rset.getInt("id"),
                        rset.getString("rua"),
                        rset.getString("bairro"),
                        rset.getString("cidade"),
                        rset.getString("uf"),
                        rset.getString("pais"),
                        rset.getString("cep"),
                        rset.getString("numero"),
                        rset.getString("nomeUnidade")
                );
            }
        }catch (SQLException sqle){
            sqle.printStackTrace();
        }finally {
            conexao.desconectar(conn);// fechando a conexao com o banco
        }
        return endereco;
    }// ============= BUSCAR UM ENDEREÇO PELO ID ===============


    // ============= APAGAR ENDEREÇOS SOLTOS ===============
    // usa a conexão de quem chamou, porque faz parte da transação de apagar a empresa
    public static void apagarEnderecos(Connection conn, List<Integer> idsEnderecos) throws SQLException {
        String comandoSql = "delete from endereco where id = ?;";
        PreparedStatement pstmt = conn.prepareStatement(comandoSql);
        for (int idEndereco : idsEnderecos){
            pstmt.setInt(1, idEndereco);
            pstmt.executeUpdate();
        }
    }// ============= APAGAR ENDEREÇOS SOLTOS ===============
}
