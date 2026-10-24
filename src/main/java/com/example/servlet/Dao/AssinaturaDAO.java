package com.example.servlet.Dao;

import com.example.servlet.Model.Assinatura;
import com.example.servlet.Model.Empresa;
import com.example.servlet.Model.Usuario;
import com.example.servlet.Util.Conexao;
import com.example.servlet.Util.Enum.TipoAssinatura;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class AssinaturaDAO {

    // ============ INSERIR ASSINATURA ( DO USUARIO OU DA EMPRESA ) ============
    public int inserirAssinatura(Assinatura assinatura){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();

        try{
            String comandoSql = "insert into assinatura (idUsuario, idEmpresa, tipoAssinatura, valor, descricao) values(?,?,?,?,?);";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql, Statement.RETURN_GENERATED_KEYS);

            // setando os valores para a operação
            // o dono é o usuario ou a empresa, o outro vai como NULL
            if (assinatura.getUsuario() != null){
                pstmt.setString(1, assinatura.getUsuario().getCpf());
                pstmt.setNull(2, Types.VARCHAR);
            }
            else {
                pstmt.setNull(1, Types.VARCHAR);
                pstmt.setString(2, assinatura.getEmpresa().getCnpj());
            }
            pstmt.setString(3, assinatura.getTipoAssinatura().name());
            pstmt.setDouble(4, assinatura.getValor());
            pstmt.setString(5, assinatura.getDescricao());

            if(pstmt.executeUpdate() > 0){
                ResultSet chaves = pstmt.getGeneratedKeys();
                if (chaves.next()) assinatura.setIdAssinatura(chaves.getInt(1));
                return 1; // verifica se deu certo
            }else{
                return 0; // deu errado!!
            }
        }catch (SQLException sqle){
            sqle.printStackTrace();
            return -1;
        }
        finally {
            conexao.desconectar(conn);
        }
    }// ============ INSERIR ASSINATURA ( DO USUARIO OU DA EMPRESA ) ============



    // ============ ALTERAR ASSINATURA ( DO USUARIO OU DA EMPRESA ) ============
    public int alterarAssinatura(Assinatura assinaturaAntiga, Assinatura assinaturaNova){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();

        try{
            String comandoSql = "update assinatura set idUsuario = ?, idEmpresa = ?, tipoAssinatura = ?, valor = ?, descricao = ? where id = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // setando os valor para a operação
            // o dono é o usuario ou a empresa, o outro vai como NULL
            if (assinaturaNova.getUsuario() != null){
                pstmt.setString(1, assinaturaNova.getUsuario().getCpf());
                pstmt.setNull(2, Types.VARCHAR);
            }
            else {
                pstmt.setNull(1, Types.VARCHAR);
                pstmt.setString(2, assinaturaNova.getEmpresa().getCnpj());
            }
            pstmt.setString(3, assinaturaNova.getTipoAssinatura().name());
            pstmt.setDouble(4, assinaturaNova.getValor());
            pstmt.setString(5, assinaturaNova.getDescricao());
            pstmt.setInt(6, assinaturaAntiga.getIdAssinatura());

            if (pstmt.executeUpdate() > 0){
                return 1;// deu certo
            }else{
                return 0;// nao foi possivel realizar o comando
            }

        }catch (SQLException sqle){
            sqle.printStackTrace();
            return -1;// erro!!
        }finally {
            conexao.desconectar(conn);// fechando a conexao com o banco
        }
    }// ============ ALTERAR ASSINATURA ( DO USUARIO OU DA EMPRESA ) ============


    // ============ DELETAR ASSINATURA ============
    public int deletarAssinatura(Assinatura assinatura){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();

        try{
            String comandoSql = "delete from assinatura where id = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);

            // setando os valores para a operação
            pstmt.setInt(1, assinatura.getIdAssinatura());

            if(pstmt.executeUpdate() > 0){
                return 1; // verifica se deu certo
            }else{
                return 0; // deu errado!!
            }
        }catch (SQLException sqle){
            sqle.printStackTrace();
            return -1;
        }
        finally {
            conexao.desconectar(conn);
        }
    }// ============ DELETAR ASSINATURA ============



    // ============ BUSCANDO A ASSINATURA DO USUARIO POR CPF ============
    public Assinatura buscarAssinaturaUsuario(String cpf){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();

        Assinatura assinatura = null;
        ResultSet rset = null;

        try{
            String comandoSql = "select * from assinatura where idUsuario = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // sentado o valor do cpf
            pstmt.setString(1, cpf);
            rset = pstmt.executeQuery();

            if (rset.next()){
                // Criando o objeto usuario com o ID vindo do ResultSet
                Usuario usuario = new Usuario();
                usuario.setCpf(rset.getString("idUsuario"));

                // criando o objeto Assinatura
                assinatura = new Assinatura(
                        rset.getInt("id"),
                        usuario,
                        TipoAssinatura.valueOf(rset.getString("tipoAssinatura")),
                        rset.getDouble("valor"),
                        rset.getString("descricao")
                );
            }

        }catch (SQLException sqle){
            sqle.printStackTrace();
        }
        finally {
            conexao.desconectar(conn); // fechando a conexão
        }
        return assinatura;
    }// ============ BUSCANDO A ASSINATURA DO USUARIO POR CPF ============



    // ============ BUSCANDO A ASSINATURA DA EMPRESA POR CNPJ ============
    public Assinatura buscarAssinaturaEmpresa(String cnpj){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();

        Assinatura assinatura = null;
        ResultSet rset = null;

        try{
            String comandoSql = "select * from assinatura where idEmpresa = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // sentado o valor do cnpj
            pstmt.setString(1, cnpj);
            rset = pstmt.executeQuery();

            if (rset.next()){
                // Criando o objeto usuario com o ID vindo do ResultSet
                Empresa empresa = new Empresa();
                empresa.setCnpj(rset.getString("idEmpresa"));

                // criando o objeto Assinatura
                 assinatura = new Assinatura(
                        rset.getInt("id"),
                        empresa,
                        TipoAssinatura.valueOf(rset.getString("tipoAssinatura")),
                        rset.getDouble("valor"),
                        rset.getString("descricao")
                );
            }

        }catch (SQLException sqle){
            sqle.printStackTrace();
        }
        finally {
            conexao.desconectar(conn); // fechando a conexão
        }
        return assinatura;
    }// ============ BUSCANDO A ASSINATURA DA EMPRESA POR CNPJ ============



    // ============ BUSCANDO A ASSINATURA PELO ID ============
    public Assinatura buscarAssinaturaPorId(int idAssinatura){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();

        Assinatura assinatura = null;
        ResultSet rset = null;

        try{
            String comandoSql = "select * from assinatura where id = ?;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            // sentado o valor do id
            pstmt.setInt(1, idAssinatura);
            rset = pstmt.executeQuery();

            if (rset.next()){
                assinatura = new Assinatura();
                assinatura.setIdAssinatura(rset.getInt("id"));
                assinatura.setTipoAssinatura(TipoAssinatura.valueOf(rset.getString("tipoAssinatura")));
                assinatura.setValor(rset.getDouble("valor"));
                assinatura.setDescricao(rset.getString("descricao"));

                // criando o dono: usuario ou empresa
                if (rset.getString("idUsuario") != null){
                    Usuario usuario = new Usuario();
                    usuario.setCpf(rset.getString("idUsuario"));
                    assinatura.setUsuario(usuario);
                }
                else {
                    Empresa empresa = new Empresa();
                    empresa.setCnpj(rset.getString("idEmpresa"));
                    assinatura.setEmpresa(empresa);
                }
            }

        }catch (SQLException sqle){
            sqle.printStackTrace();
        }
        finally {
            conexao.desconectar(conn); // fechando a conexão
        }
        return assinatura;
    }// ============ BUSCANDO A ASSINATURA PELO ID ============



    // ============ BUSCANDO TODAS AS ASSINATURAS ( COM O NOME DO DONO ) ============
    public List<Assinatura> buscarTodasAssinatura(){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();

        List<Assinatura> lista = new ArrayList<>();
        ResultSet rset = null;

        try{
            String comandoSql = "select a.*, u.nome as nomeUsuario, u.sobrenome, e.nome as nomeEmpresa " +
                    "from assinatura a " +
                    "left join usuario u on u.cpf = a.idUsuario " +
                    "left join empresa e on e.cnpj = a.idEmpresa " +
                    "order by a.id;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            rset = pstmt.executeQuery();

            while (rset.next()){
                Assinatura assinatura = new Assinatura();
                assinatura.setIdAssinatura(rset.getInt("id"));
                assinatura.setTipoAssinatura(TipoAssinatura.valueOf(rset.getString("tipoAssinatura")));
                assinatura.setValor(rset.getDouble("valor"));
                assinatura.setDescricao(rset.getString("descricao"));

                // criando o dono: usuario ou empresa
                if (rset.getString("idUsuario") != null){
                    Usuario usuario = new Usuario();
                    usuario.setCpf(rset.getString("idUsuario"));
                    usuario.setNome(rset.getString("nomeUsuario"));
                    usuario.setSobrenome(rset.getString("sobrenome"));
                    assinatura.setUsuario(usuario);
                }
                else {
                    Empresa empresa = new Empresa();
                    empresa.setCnpj(rset.getString("idEmpresa"));
                    empresa.setNome(rset.getString("nomeEmpresa"));
                    assinatura.setEmpresa(empresa);
                }
                lista.add(assinatura);
            }

        }catch (SQLException sqle){
            sqle.printStackTrace();
        }
        finally {
            conexao.desconectar(conn); // fechando a conexão
        }
        return lista;
    }// ============ BUSCANDO TODAS AS ASSINATURAS ( COM O NOME DO DONO ) ============



    // ============ TOTAL DE ASSINATURAS ============
    public int totalAssinaturas(){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();

        ResultSet rset = null;
        int total = 0;

        try{
            String comandoSql = "select count(*) as total from assinatura;";
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
    }// ============ TOTAL DE ASSINATURAS ============
}
