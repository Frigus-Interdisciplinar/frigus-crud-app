package com.example.servlet.Dao;

import com.example.servlet.Util.Conexao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ResumoDAO {

    // ================ TOTAIS DA VISAO GERAL ( TUDO EM UMA CONSULTA SO ) ================
    public Map<String, Integer> buscarTotais(){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();// abrindo a conexao com o banco

        ResultSet rset = null;
        Map<String, Integer> totais = new LinkedHashMap<>();

        try{
            String comandoSql = "select " +
                    "(select count(*) from usuario) as usuarios, " +
                    "(select count(*) from assinatura where idUsuario is not null and tipoAssinatura <> 'FREE') as usuariosPagos, " +
                    "(select count(*) from empresa) as empresas, " +
                    "(select count(*) from assinatura where idEmpresa is not null and tipoAssinatura = 'COMERCIAL') as empresasComercial, " +
                    "(select count(*) from geladeira) as geladeiras, " +
                    "(select count(distinct idUsuario) from geladeira) as usuariosComGeladeira, " +
                    "(select count(*) from Frigorifico) as frigorificos, " +
                    "(select count(distinct idEmpresa) from Frigorifico) as empresasComFrigorifico, " +
                    "(select count(*) from ItemGeladeira) as itensGeladeira, " +
                    "(select count(*) from ItemFrigorifico) as itensFrigorifico, " +
                    "(select count(*) from ItemGeladeira where dtValidade <= current_date + 3) + " +
                    "(select count(*) from ItemFrigorifico where dtValidade <= current_date + 3) as produtosAtencao, " +
                    "(select count(*) from receita) as receitas, " +
                    "(select count(*) from receita where idUsuario is not null) as receitasUsuarios, " +
                    "(select count(*) from receita where idEmpresa is not null) as receitasEmpresas, " +
                    "(select count(*) from ItemLista) as itensLista, " +
                    "(select count(*) from listaCompras) as listas, " +
                    "(select count(*) from assinatura) as assinaturas;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            rset = pstmt.executeQuery();

            if (rset.next()){
                int colunas = rset.getMetaData().getColumnCount();
                for (int i = 1; i <= colunas; i++){
                    totais.put(rset.getMetaData().getColumnLabel(i), rset.getInt(i));
                }
            }
        }catch (SQLException sqle){
            sqle.printStackTrace();
        }
        finally {
            conexao.desconectar(conn); // fechando a conexao com o banco
        }
        return totais;
    }// ================ TOTAIS DA VISAO GERAL ( TUDO EM UMA CONSULTA SO ) ================


    // ================ ASSINATURAS E RECEITA POR PLANO ================
    // para cada plano: [quantas assinaturas, quanto elas rendem por mes]
    public Map<String, double[]> buscarReceitaPorPlano(){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();// abrindo a conexao com o banco

        ResultSet rset = null;
        Map<String, double[]> planos = new LinkedHashMap<>();

        try{
            String comandoSql = "select tipoAssinatura, count(*) as quantidade, coalesce(sum(valor), 0) as receita " +
                    "from assinatura group by tipoAssinatura order by receita, tipoAssinatura;";
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            rset = pstmt.executeQuery();

            while (rset.next()){
                planos.put(rset.getString("tipoAssinatura"), new double[]{rset.getInt("quantidade"), rset.getDouble("receita")});
            }
        }catch (SQLException sqle){
            sqle.printStackTrace();
        }
        finally {
            conexao.desconectar(conn); // fechando a conexao com o banco
        }
        return planos;
    }// ================ ASSINATURAS E RECEITA POR PLANO ================


    // ================ PLANO DE CADA USUARIO E DE CADA EMPRESA ================
    // o plano de cada usuario ( cpf -> plano )
    public Map<String, String> buscarPlanosDosUsuarios(){
        return buscarPlanos("select idUsuario as dono, tipoAssinatura from assinatura where idUsuario is not null;");
    }

    // o plano de cada empresa ( cnpj -> plano )
    public Map<String, String> buscarPlanosDasEmpresas(){
        return buscarPlanos("select idEmpresa as dono, tipoAssinatura from assinatura where idEmpresa is not null;");
    }

    private Map<String, String> buscarPlanos(String comandoSql){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();// abrindo a conexao com o banco

        ResultSet rset = null;
        Map<String, String> planos = new HashMap<>();

        try{
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            rset = pstmt.executeQuery();

            while (rset.next()){
                planos.put(rset.getString("dono"), rset.getString("tipoAssinatura"));
            }
        }catch (SQLException sqle){
            sqle.printStackTrace();
        }
        finally {
            conexao.desconectar(conn); // fechando a conexao com o banco
        }
        return planos;
    }// ================ PLANO DE CADA USUARIO E DE CADA EMPRESA ================


    // ================ QUANTOS REGISTROS CADA DONO TEM ================
    // quantas geladeiras cada usuario tem ( cpf -> total )
    public Map<String, Integer> contarGeladeirasPorUsuario(){
        return contarPorDono("select idUsuario as dono, count(*) as total from geladeira group by idUsuario;");
    }

    // quantos frigorificos cada empresa tem ( cnpj -> total )
    public Map<String, Integer> contarFrigorificosPorEmpresa(){
        return contarPorDono("select idEmpresa as dono, count(*) as total from Frigorifico group by idEmpresa;");
    }

    // quantas receitas cada usuario tem ( cpf -> total )
    public Map<String, Integer> contarReceitasPorUsuario(){
        return contarPorDono("select idUsuario as dono, count(*) as total from receita where idUsuario is not null group by idUsuario;");
    }

    // quantas receitas cada empresa tem ( cnpj -> total )
    public Map<String, Integer> contarReceitasPorEmpresa(){
        return contarPorDono("select idEmpresa as dono, count(*) as total from receita where idEmpresa is not null group by idEmpresa;");
    }

    // quantos ingredientes cada receita tem ( id da receita -> total )
    public Map<Integer, Integer> contarIngredientesPorReceita(){
        Map<Integer, Integer> totais = new HashMap<>();
        Map<String, Integer> porId = contarPorDono("select idReceita as dono, count(*) as total from Ingrediente group by idReceita;");
        for (Map.Entry<String, Integer> total : porId.entrySet()){
            totais.put(Integer.valueOf(total.getKey()), total.getValue());
        }
        return totais;
    }

    private Map<String, Integer> contarPorDono(String comandoSql){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();// abrindo a conexao com o banco

        ResultSet rset = null;
        Map<String, Integer> totais = new HashMap<>();

        try{
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            rset = pstmt.executeQuery();

            while (rset.next()){
                totais.put(rset.getString("dono"), rset.getInt("total"));
            }
        }catch (SQLException sqle){
            sqle.printStackTrace();
        }
        finally {
            conexao.desconectar(conn); // fechando a conexao com o banco
        }
        return totais;
    }// ================ QUANTOS REGISTROS CADA DONO TEM ================


    // ================ OCUPACAO E ALERTAS DE CADA GELADEIRA E FRIGORIFICO ================
    // para cada geladeira: [quantos produtos, quantos itens ( soma das quantidades ), vencidos, a vencer em 7 dias]
    public Map<Integer, int[]> buscarOcupacaoGeladeiras(){
        return buscarOcupacao("select idGeladeira as armazenamento, count(*) as produtos, coalesce(sum(quantidade), 0) as itens, " +
                "count(*) filter (where dtValidade < current_date) as vencidos, " +
                "count(*) filter (where dtValidade between current_date and current_date + 7) as aVencer " +
                "from ItemGeladeira group by idGeladeira;");
    }

    // para cada frigorifico: [quantos produtos, quantos itens ( soma das quantidades ), vencidos, a vencer em 7 dias]
    public Map<Integer, int[]> buscarOcupacaoFrigorificos(){
        return buscarOcupacao("select idFrigorifico as armazenamento, count(*) as produtos, coalesce(sum(quantidade), 0) as itens, " +
                "count(*) filter (where dtValidade < current_date) as vencidos, " +
                "count(*) filter (where dtValidade between current_date and current_date + 7) as aVencer " +
                "from ItemFrigorifico group by idFrigorifico;");
    }

    private Map<Integer, int[]> buscarOcupacao(String comandoSql){
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();// abrindo a conexao com o banco

        ResultSet rset = null;
        Map<Integer, int[]> ocupacao = new HashMap<>();

        try{
            PreparedStatement pstmt = conn.prepareStatement(comandoSql);
            rset = pstmt.executeQuery();

            while (rset.next()){
                ocupacao.put(rset.getInt("armazenamento"), new int[]{
                        rset.getInt("produtos"),
                        rset.getInt("itens"),
                        rset.getInt("vencidos"),
                        rset.getInt("aVencer")
                });
            }
        }catch (SQLException sqle){
            sqle.printStackTrace();
        }
        finally {
            conexao.desconectar(conn); // fechando a conexao com o banco
        }
        return ocupacao;
    }// ================ OCUPACAO E ALERTAS DE CADA GELADEIRA E FRIGORIFICO ================

}
