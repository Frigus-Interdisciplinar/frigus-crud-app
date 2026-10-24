package com.example.servlet.Util;

import io.github.cdimascio.dotenv.Dotenv;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Conexao {

    // conexoes abertas que estao livres para usar de novo ( abrir uma nova no banco da nuvem demora cerca de 1,5 segundo )
    private static final ConcurrentLinkedQueue<Connection> livres = new ConcurrentLinkedQueue<>();
    // quando cada conexao foi aberta e quando foi usada pela ultima vez
    private static final Map<Connection, Long> abertaEm = new ConcurrentHashMap<>();
    private static final Map<Connection, Long> usadaEm = new ConcurrentHashMap<>();
    // quantas conexoes ficam guardadas e por quanto tempo ( em milissegundos )
    private static final int maximo_livres = 5;
    private static final long tempo_ocioso = 30_000;
    private static final long tempo_de_vida = 600_000;

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
     *
     * Antes de abrir uma nova, tenta reaproveitar uma que ja estava aberta.
     */
    public Connection conectar() {

        // reaproveitando uma conexao livre
        Connection livre;
        while ((livre = livres.poll()) != null) {
            try {
                long agora = System.currentTimeMillis();
                boolean velha = agora - abertaEm.getOrDefault(livre, 0L) > tempo_de_vida;
                boolean parada = agora - usadaEm.getOrDefault(livre, 0L) > tempo_ocioso;
                // so pergunta ao banco se ela ainda funciona quando ficou parada por um tempo
                if (!livre.isClosed() && !velha && (!parada || livre.isValid(2))) {
                    return livre;
                }
            } catch (SQLException e) {
                // essa nao serve mais, tenta a proxima
            }
            fechar(livre);
        }

        try {
            Class.forName("org.postgresql.Driver");

            Connection nova = DriverManager.getConnection(
                    url,
                    usuario,
                    senha
            );
            abertaEm.put(nova, System.currentTimeMillis());
            return nova;



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
     * Devolve uma conexão com o banco de dados.
     *
     * Ela volta para a fila de conexões livres, para ser usada de novo.
     * So é fechada de verdade se a fila ja estiver cheia ou se der erro.
     *
     * @param conn conexão que será devolvida
     */
    public void desconectar(Connection conn) {

        try {

            if (conn == null || conn.isClosed()) {
                return;
            }

            // volta ao normal ( desfaz o que ficou sem commit e liga o commit automatico de novo )
            if (!conn.getAutoCommit()) {
                conn.rollback();
                conn.setAutoCommit(true);
            }

            if (livres.size() < maximo_livres) {
                usadaEm.put(conn, System.currentTimeMillis());
                livres.offer(conn);
            } else {
                fechar(conn);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            fechar(conn);
        }
    }

    // fecha de verdade uma conexão
    private void fechar(Connection conn) {
        abertaEm.remove(conn);
        usadaEm.remove(conn);
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
