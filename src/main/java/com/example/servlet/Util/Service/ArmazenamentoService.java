package com.example.servlet.Util.Service;

import com.example.servlet.Model.Assinatura;
import com.example.servlet.Model.Frigorificos;
import com.example.servlet.Model.Geladeira;
import com.example.servlet.Util.Conexao;
import com.example.servlet.Util.Enum.StatusArmazenamento;
import com.example.servlet.Util.Enum.TipoAssinatura;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ArmazenamentoService {
    // CLASSE DE REGRA DE NEGÓCIO / SISTEMA ( USA UMA QUERY NO BANCO MAS NN É DAO)


    // CALCULAR A QUANTIDADE TOTAL DE PRODUTOS NA GELADEIRA
    public static int totalProdutosGeladeira(int idGeladeira) {
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();
        int total = 0;
        try {
            String sql = "SELECT COALESCE(SUM(quantidade), 0) AS total FROM ItemGeladeira WHERE idGeladeira = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, idGeladeira);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) total = rs.getInt("total");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            conexao.desconectar(conn);
        }
        return total;
    }// CALCULAR A QUANTIDADE TOTAL DE PRODUTOS NA GELADEIRA


    // ATUALIZAR O STATUS DA GELADEIRA
    public static void calcularStatusGeladeira(Geladeira geladeira, Assinatura assinatura){
        StatusArmazenamento statusAntigo = geladeira.getStatus();
        // buscar a quantidade total de produtos na geladeira
        int quantidadeProd = ArmazenamentoService.totalProdutosGeladeira(geladeira.getIdGeladeira());
        // buscar pelo tipo de plano para pegar a quantidade maxima dele
        int capacidade = TipoAssinatura.quantidadePorPlano(assinatura.getTipoAssinatura());
        // calcular o status comparando com o total de produtos que podem ser armazenados
        StatusArmazenamento novoStatus = StatusArmazenamento.DefinirStatus(quantidadeProd,capacidade);
        // aplicando no objeto geladeira
        geladeira.setStatus(novoStatus);

    }// ATUALIZAR O STATUS DA GELADEIRA






    // CALCULAR A QUANTIDADE TOTAL DE PRODUTOS NO FRIGORIFICO
    public static int totalProdutosFrigorificos(int idFrigorifico) {
        Conexao conexao = new Conexao();
        Connection conn = conexao.conectar();
        int total = 0;
        try {
            String sql = "SELECT COALESCE(SUM(quantidade), 0) AS total FROM ItemFrigorifico WHERE idFrigorifico = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, idFrigorifico);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) total = rs.getInt("total");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            conexao.desconectar(conn);
        }
        return total;
    }// CALCULAR A QUANTIDADE TOTAL DE PRODUTOS NO FRIGORIFICO


    // ATUALIZAR O STATUS DO FRIGORIFICOS
    public static void calcularStatusFrigorifico(Frigorificos frigorifico, Assinatura assinatura){
        // buscar a quantidade total de produtos na geladeira
        int quantidadeProd = ArmazenamentoService.totalProdutosFrigorificos(frigorifico.getIdFrigorifico());
        // buscar pelo tipo de plano para pegar a quantidade maxima dele
        int capacidade = TipoAssinatura.quantidadePorPlano(assinatura.getTipoAssinatura());
        // calcular o status comparando com o total de produtos que podem ser armazenados
        StatusArmazenamento novoStatus = StatusArmazenamento.DefinirStatus(quantidadeProd,capacidade);
        // aplicando no objeto geladeira
        frigorifico.setStatus(novoStatus);
    }// ATUALIZAR O STATUS DO FRIGORIFICOS
}
