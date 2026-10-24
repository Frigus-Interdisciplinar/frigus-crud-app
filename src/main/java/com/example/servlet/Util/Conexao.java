package com.example.servlet.Util;

import io.github.cdimascio.dotenv.Dotenv;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {

    private final Dotenv dotenv;

    private final String url;
    private final String usuario;
    private final String senha;

    public Conexao() {

        // o Dotenv tambem enxerga as variaveis de ambiente do sistema
        dotenv = Dotenv.configure().directory(acharPastaEnv()).ignoreIfMalformed().ignoreIfMissing().load();

        url = dotenv.get("DB_URL");
        usuario = dotenv.get("DB_USER");
        senha = dotenv.get("DB_PASSWORD");

        validarConfiguracoes();
    }


    // acha a pasta do .env: primeiro a variavel FRIGUS_ENV_DIR, depois sobe as pastas de onde o projeto esta rodando
    private String acharPastaEnv() {

        String pastaEnv = System.getenv("FRIGUS_ENV_DIR");
        if (pastaEnv != null && !pastaEnv.isBlank()){
            return pastaEnv;
        }

        try {
            // comeca na pasta das classes ( ex: projeto/target/Servlet/WEB-INF/classes ) e sobe ate achar o .env
            Path pasta = Paths.get(Conexao.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            while (pasta != null){
                if (Files.exists(pasta.resolve(".env"))){
                    return pasta.toString();
                }
                pasta = pasta.getParent();
            }
        } catch (Exception e) {
            // se nao der para descobrir, usa a pasta atual
        }
        return ".";
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