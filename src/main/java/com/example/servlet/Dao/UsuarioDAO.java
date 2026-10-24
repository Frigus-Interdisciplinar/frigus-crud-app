package com.example.servlet.Dao;

import com.example.servlet.Model.Usuario;
import com.example.servlet.Util.Conexao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    // ================ INSERIR ================
    public int adicionarUsuario(Usuario usuario){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();// abrindo a conexao com o banco

        try{
            String comandoSql = "Insert into usuario (cpf, nome, sobrenome, email, senha) values (?,?,?,?,?);";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // setando os valores para a execução
            pstmt.setString(1, usuario.getCpf());
            pstmt.setString(2, usuario.getNome());
            pstmt.setString(3, usuario.getSobrenome());
            pstmt.setString(4, usuario.getEmail());
            pstmt.setString(5, usuario.getSenha());

            if (pstmt.executeUpdate() > 0){
                return 1; // inseriu as informações no banco
            }
            else{
                return 0; // não foi possível adicionar
            }

        }catch (SQLException sqle){
            sqle.printStackTrace();
            return -1;
        }
        finally {
            conexao.desconectar(conn);// fechando a conexao com o banco
        }
    }// ================ INSERIR ================


    // ================ ALTERAR ================
    public int alterarUsuario(Usuario usuarioAntigo, Usuario usuarioNovo){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();// abrindo a conexao com o banco

        try{
            String comandoSql = "update usuario set nome = ?, sobrenome = ?, email = ?, senha = ? where cpf = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // setando os valores para a execução
            pstmt.setString(1, usuarioNovo.getNome());
            pstmt.setString(2, usuarioNovo.getSobrenome());
            pstmt.setString(3, usuarioNovo.getEmail());
            pstmt.setString(4, usuarioNovo.getSenha());
            pstmt.setString(5, usuarioAntigo.getCpf());

            if (pstmt.executeUpdate() > 0){
                return 1; // inseriu as informações no banco
            }
            else{
                return 0; // não foi possível adicionar
            }

        }catch (SQLException sqle){
            sqle.printStackTrace();
            return -1;
        }
        finally {
            conexao.desconectar(conn);// fechando a conexao com o banco
        }
    }// ================ ALTERAR ================


    // ================ DELETAR ================
    public int deletarUsuario(Usuario usuario){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();// abrindo a conexao com o banco

        try{
            // apagar o usuario apaga tudo dele pelo ON DELETE CASCADE, mas os produtos ficariam soltos
            // por isso tudo acontece na mesma transação
            conn.setAutoCommit(false); // nada é salvo de verdade até o commit

            // guarda os produtos dos itens do usuario antes de apagar ele
            String comandoSqlProdutos = "select ig.idProduto from ItemGeladeira ig join Geladeira g on g.id = ig.idGeladeira where g.idUsuario = ? " +
                    "union select il.idProduto from ItemLista il join ListaCompras lc on lc.id = il.idListaCompras where lc.idUsuario = ?;";
            List<Integer> idsProdutos = ProdutosGeladeiraDAO.buscarIds(conn, comandoSqlProdutos, usuario.getCpf());

            String comandoSql = "delete from usuario where cpf = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // setando os valores para a execução
            pstmt.setString(1, usuario.getCpf());

            if (pstmt.executeUpdate() <= 0){
                conn.rollback();
                return 0; // não foi possível apagar
            }

            // apaga os produtos que sobraram sem ninguém usando
            ProdutosGeladeiraDAO.apagarProdutosOrfaos(conn, idsProdutos);

            conn.commit();
            return 1; // apagou as informações no banco

        }catch (SQLException sqle){
            sqle.printStackTrace();
            try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            return -1;
        }
        finally {
            conexao.desconectar(conn);// fechando a conexao com o banco
        }
    }// ================ DELETAR ================


    // ================ BUSCAR UM USUARIO PELO CPF ================
    public Usuario buscarUsuarioPorCpf(String cpf){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();// abrindo a conexao com o banco

        ResultSet rset = null;
        Usuario usuario = null;

        try{
            String comandoSql = "select * from usuario where cpf = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // setando os valores para a execução
            pstmt.setString(1, cpf);
            rset = pstmt.executeQuery();

            if (rset.next()){
                usuario = new Usuario(
                        rset.getString("cpf"),
                        rset.getString("nome"),
                        rset.getString("sobrenome"),
                        rset.getString("email"),
                        rset.getString("senha")
                );
            }
        }catch (SQLException sqle){
            sqle.printStackTrace();
        }
        finally {
            conexao.desconectar(conn); // fechando a conexao com o banco
        }
        return usuario;
    }// ================ BUSCAR UM USUARIO PELO CPF ================


    // ================ BUSCAR TODOS OS USUARIOS ================
    public List<Usuario> buscarTodosUsuario(){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();// abrindo a conexao com o banco

        ResultSet rset = null;
        List<Usuario> lista = new ArrayList<>();

        try{
            String comandoSql = "select * from usuario order by nome, sobrenome;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            rset = pstmt.executeQuery();

            while (rset.next()){
                Usuario usuario = new Usuario(
                        rset.getString("cpf"),
                        rset.getString("nome"),
                        rset.getString("sobrenome"),
                        rset.getString("email"),
                        rset.getString("senha")
                );
                usuario.setDtCadastro(rset.getObject("dtCadastro", java.time.LocalDate.class));
                lista.add(usuario);
            }
        }catch (SQLException sqle){
            sqle.printStackTrace();
        }
        finally {
            conexao.desconectar(conn); // fechando a conexao com o banco
        }
        return lista;
    }// ================ BUSCAR TODOS OS USUARIOS ================


    // ================ TOTAL DE USUARIOS ================
    public int totalUsuarios(){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();// abrindo a conexao com o banco

        ResultSet rset = null;
        int total = 0;

        try{
            String comandoSql = "select count(*) as total from usuario;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            rset = pstmt.executeQuery();

            if (rset.next()) total = rset.getInt("total");

        }catch (SQLException sqle){
            sqle.printStackTrace();
        }
        finally {
            conexao.desconectar(conn); // fechando a conexao com o banco
        }
        return total;
    }// ================ TOTAL DE USUARIOS ================

}
