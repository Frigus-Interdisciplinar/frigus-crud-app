package com.example.servlet.Controller;

import com.example.servlet.Dao.GeladeiraDAO;
import com.example.servlet.Dao.ResumoDAO;
import com.example.servlet.Dao.UsuarioDAO;
import com.example.servlet.Model.Geladeira;
import com.example.servlet.Model.Usuario;
import com.example.servlet.Util.Enum.StatusArmazenamento;
import com.example.servlet.Util.Enum.TipoAssinatura;
import com.example.servlet.Util.Paginacao;
import com.example.servlet.Util.Validacao;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

@WebServlet(urlPatterns = {
        "/admin/geladeiras"},
        name = "geladeiras")
public class GeladeiraServlet extends HttpServlet {
    private GeladeiraDAO gelaDao;
    private UsuarioDAO usuarioDAO;
    private ResumoDAO resumoDAO;
    private Validacao validar;


    // caminho das paginas
    private static final String jsp_geladeiras = "/WEB-INF/pages/geladeiras.jsp";

    // caminho para o servlet
    private static final String url_servlet = "/admin/geladeiras";



    @Override
    public void init(){
        gelaDao = new GeladeiraDAO();
        usuarioDAO = new UsuarioDAO();
        resumoDAO = new ResumoDAO();
        validar = new Validacao();
    }



    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // inserir, alterar e deletar só por POST
        if (req.getParameter("action") != null){
            resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }
        String path = req.getServletPath();

        switch(path) {
            case "/admin/geladeiras": {
                listar(req, resp);
                break;
            }
            default:
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {
        HttpSession session = req.getSession();
        String action = req.getParameter("action");

        if (action == null){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        switch(action) {
            case "inserir": {
                // pegando os valores do formulario
                String cpf = req.getParameter("cpf");
                String tipo = req.getParameter("tipo");
                String nome = req.getParameter("nome");

                if (cpf == null || cpf.isBlank()){
                    voltaComErro(req, resp, "Escolha o usuário dono da geladeira.");
                    return;
                }
                if (nome == null || nome.isBlank()){
                    voltaComErro(req, resp, "O nome da geladeira não pode ser vazio.");
                    return;
                }
                if (tipo == null || tipo.isBlank()){
                    voltaComErro(req, resp, "O tipo da geladeira não pode ser vazio.");
                    return;
                }
                // validando o nome
                boolean vNome = validar.validarNomeProduto(nome);
                if (!vNome || nome.length() > 50){
                    voltaComErro(req, resp, "Nome da geladeira inválido. No máximo 50 caracteres.");
                    return;
                }
                if (tipo.length() > 20){
                    voltaComErro(req, resp, "Tipo da geladeira inválido. No máximo 20 caracteres.");
                    return;
                }
                // o dono tem que existir
                Usuario usuario = usuarioDAO.buscarUsuarioPorCpf(cpf.trim());
                if (usuario == null){
                    voltaComErro(req, resp, "Usuário não encontrado.");
                    return;
                }

                // o admin não tem limite de geladeiras por plano
                Geladeira geladeira = new Geladeira(
                        usuario,
                        tipo.trim(),
                        nome.trim()
                );
                int funcionou = gelaDao.inserirGeladeira(geladeira);

                if (funcionou == 1){
                    session.setAttribute("sucesso", "Geladeira cadastrada com sucesso.");
                    resp.sendRedirect(req.getContextPath() + url_servlet);
                    return;
                }else {
                    voltaComErro(req, resp, "Erro ao cadastrar a geladeira. Por favor tente novamente.");
                    return;
                }
            }
            case "alterar":{
                String idTxt = req.getParameter("idGeladeira");
                String cpf = req.getParameter("cpf");
                String tipo = req.getParameter("tipo");
                String nome = req.getParameter("nome");

                int id;
                try{
                    id = Integer.parseInt(idTxt.trim());
                } catch (NumberFormatException | NullPointerException e) {
                    voltaComErro(req, resp, "Erro ao alterar essa geladeira. Por favor tente novamente.");
                    return;
                }
                Geladeira geldeiraAntiga = gelaDao.buscarUnicaGeladeira(id);
                if (geldeiraAntiga == null){
                    voltaComErro(req, resp, "Geladeira não encontrada.");
                    return;
                }

                if (cpf == null || cpf.isBlank()){
                    voltaComErro(req, resp, "Escolha o usuário dono da geladeira.");
                    return;
                }
                if (tipo == null || tipo.isBlank()){
                    voltaComErro(req, resp, "O tipo da geladeira não pode ser vazio.");
                    return;
                }
                if (nome == null || nome.isBlank()){
                    voltaComErro(req, resp, "O nome da geladeira não pode ser vazio.");
                    return;
                }
                boolean vNome = validar.validarNomeProduto(nome);
                if (!vNome || nome.length() > 50){
                    voltaComErro(req, resp, "Nome da geladeira inválido. No máximo 50 caracteres.");
                    return;
                }
                if (tipo.length() > 20){
                    voltaComErro(req, resp, "Tipo da geladeira inválido. No máximo 20 caracteres.");
                    return;
                }
                // o novo dono tem que existir
                Usuario usuario = usuarioDAO.buscarUsuarioPorCpf(cpf.trim());
                if (usuario == null){
                    voltaComErro(req, resp, "Usuário não encontrado.");
                    return;
                }

                Geladeira geladeiraNova = new Geladeira(usuario, geldeiraAntiga.getStatus(), tipo.trim(), nome.trim());

                if (gelaDao.alterarGeradeira(geldeiraAntiga, geladeiraNova) == 1){
                    session.setAttribute("sucesso", "Geladeira alterada com sucesso.");
                    resp.sendRedirect(req.getContextPath() + url_servlet);
                    return;
                }else {
                    voltaComErro(req, resp, "Erro ao alterar essa geladeira. Por favor tente novamente");
                    return;
                }
            }
            case "deletar":{
                String idTxt = req.getParameter("idGeladeira");
                int id;
                try{
                    id = Integer.parseInt(idTxt.trim());
                } catch (NumberFormatException | NullPointerException e) {
                    voltaComErro(req, resp, "Erro ao deletar essa geladeira. Por favor tente novamente.");
                    return;
                }
                if(gelaDao.deletarGeladeira(id) == 1){
                    session.setAttribute("sucesso", "Geladeira apagada com sucesso, junto com os itens dela.");
                    resp.sendRedirect(req.getContextPath() + url_servlet);
                    return;
                }
                else {
                    voltaComErro(req, resp, "Erro ao deletar essa geladeira. Por favor tente novamente.");
                    return;
                }
            }
            default:{
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        }
    }

    // carrega a lista ( ja filtrada ), a ocupação de cada geladeira e mostra a tela
    private void listar(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException{
        // filtros da tela
        String filtroNome = req.getParameter("nome");
        String filtroTipo = req.getParameter("tipo");
        String filtroUsuario = req.getParameter("usuario");
        String filtroStatus = req.getParameter("status");
        String filtroAlerta = req.getParameter("alerta");

        List<Geladeira> todas = gelaDao.buscarTodasGeladeira();
        Map<Integer, int[]> ocupacao = resumoDAO.buscarOcupacaoGeladeiras();
        Map<String, String> planos = resumoDAO.buscarPlanosDosUsuarios();

        // a capacidade de cada geladeira vem do plano do dono ( -1 = sem limite )
        Map<Integer, Integer> capacidade = new HashMap<>();
        TreeSet<String> tipos = new TreeSet<>();
        for (Geladeira geladeira : todas){
            String plano = planos.get(geladeira.getUsuario().getCpf());
            capacidade.put(geladeira.getIdGeladeira(), plano == null ? -1 : TipoAssinatura.quantidadePorPlano(TipoAssinatura.valueOf(plano)));
            tipos.add(geladeira.getTipo());
        }

        List<Geladeira> geladeiras = new ArrayList<>();
        for (Geladeira geladeira : todas){
            int[] numeros = ocupacao.getOrDefault(geladeira.getIdGeladeira(), new int[]{0, 0, 0, 0});
            String dono = (geladeira.getUsuario().getNome() + " " + geladeira.getUsuario().getSobrenome() + " " + geladeira.getUsuario().getEmail()).toLowerCase();

            boolean achouNome = filtroNome == null || filtroNome.isBlank() || geladeira.getNome().toLowerCase().contains(filtroNome.trim().toLowerCase());
            boolean achouTipo = filtroTipo == null || filtroTipo.isBlank() || filtroTipo.equals(geladeira.getTipo());
            boolean achouUsuario = filtroUsuario == null || filtroUsuario.isBlank() || dono.contains(filtroUsuario.trim().toLowerCase());
            boolean achouStatus = filtroStatus == null || filtroStatus.isBlank() || filtroStatus.equals(geladeira.getStatus().name());
            boolean achouAlerta = filtroAlerta == null || filtroAlerta.isBlank()
                    || (filtroAlerta.equals("vencidos") && numeros[2] > 0)
                    || (filtroAlerta.equals("avencer") && numeros[3] > 0)
                    || (filtroAlerta.equals("sem") && numeros[2] == 0 && numeros[3] == 0);

            if (achouNome && achouTipo && achouUsuario && achouStatus && achouAlerta){
                geladeiras.add(geladeira);
            }
        }

        // ver os detalhes ou editar uma geladeira ( vem no link da linha )
        String ver = req.getParameter("ver");
        String editar = req.getParameter("editar");
        // a geladeira escolhida sai da lista que ja foi carregada ( ela traz o nome do dono )
        String idEscolhido = (ver != null && !ver.isBlank()) ? ver.trim() : (editar != null && !editar.isBlank() ? editar.trim() : null);
        if (idEscolhido != null){
            for (Geladeira geladeira : todas){
                if (String.valueOf(geladeira.getIdGeladeira()).equals(idEscolhido)){
                    req.setAttribute("registro", geladeira);
                    req.setAttribute("modo", ver != null && !ver.isBlank() ? "ver" : "editar");
                }
            }
        }

        req.setAttribute("geladeiras", Paginacao.aplicar(req, geladeiras));
        req.setAttribute("totalGeral", todas.size());
        req.setAttribute("ocupacao", ocupacao);
        req.setAttribute("capacidade", capacidade);
        req.setAttribute("tipos", tipos);
        req.setAttribute("statusArmazenamento", StatusArmazenamento.values());
        req.setAttribute("usuarios", usuarioDAO.buscarTodosUsuario());
        req.setAttribute("filtroNome", filtroNome == null ? "" : filtroNome);
        req.setAttribute("filtroTipo", filtroTipo == null ? "" : filtroTipo);
        req.setAttribute("filtroUsuario", filtroUsuario == null ? "" : filtroUsuario);
        req.setAttribute("filtroStatus", filtroStatus == null ? "" : filtroStatus);
        req.setAttribute("filtroAlerta", filtroAlerta == null ? "" : filtroAlerta);
        req.getRequestDispatcher(jsp_geladeiras).forward(req, resp);
    }

    // volta para a tela com uma mensagem de erro
    private void voltaComErro(HttpServletRequest req, HttpServletResponse resp, String mensagem)
            throws ServletException, IOException{
        req.setAttribute("erro", mensagem);
        listar(req, resp);
    }
}
