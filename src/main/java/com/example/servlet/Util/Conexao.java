package com.example.servlet.Util;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {

    private final Dotenv dotenv;

    private final String url;
    private final String usuario;
    private final String senha;

    public Conexao() {

        dotenv = Dotenv.configure().directory("C:\\Users\\20260224-ieg\\OneDrive - Instituto J&F\\Desktop\\Frigus\\Frigus_BackEnd\\.env").ignoreIfMalformed().ignoreIfMissing().load();

        url = dotenv.get("DB_URL");
        usuario = dotenv.get("DB_USER");
        senha = dotenv.get("DB_PASSWORD");

        validarConfiguracoes();
    }

    /**
     * Abre e retorna uma conexão com o banco de dados.
     *
     * O SQLException é tratado aqui dentro para que
     * os DAOs não precisem tratar a conexão antes do try.
     */
    public Connection conectar() {

        try {
            Class.forName("org.postgresql.Driver");
            System.out.println(
                    java.sql.DriverManager.getDrivers().hasMoreElements()
            );

            return DriverManager.getConnection(
                    url,
                    usuario,
                    senha
            );



        } catch (SQLException e) {

            throw new RuntimeException(
                    "Erro ao conectar com o banco de dados.",
                    e
            );
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Valida se as configurações necessárias foram carregadas.
     */
    private void validarConfiguracoes() {

        if (url == null || url.trim().isEmpty()) {
            throw new IllegalStateException(
                    "A variável DB_URL não foi configurada no arquivo .env."
            );
        }

        if (usuario == null || usuario.trim().isEmpty()) {
            throw new IllegalStateException(
                    "A variável DB_USER não foi configurada no arquivo .env."
            );
        }

        if (senha == null || senha.trim().isEmpty()) {
            throw new IllegalStateException(
                    "A variável DB_PASSWORD não foi configurada no arquivo .env."
            );
        }
    }

    /**
     * Fecha uma conexão com o banco de dados.
     *
     * @param conn conexão que será fechada
     */
    public void desconectar(Connection conn) {

        try {

            if (conn != null && !conn.isClosed()) {
                conn.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}