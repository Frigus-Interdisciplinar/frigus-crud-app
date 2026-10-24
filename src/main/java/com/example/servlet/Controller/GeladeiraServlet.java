package com.example.servlet.Controller;

import com.example.servlet.Dao.GeladeiraDAO;
import com.example.servlet.Dao.UsuarioDAO;
import com.example.servlet.Model.Geladeira;
import com.example.servlet.Model.Usuario;
import com.example.servlet.Util.Validacao;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {
        "/admin/geladeiras"},
        name = "geladeiras")
public class GeladeiraServlet extends HttpServlet {
    private GeladeiraDAO gelaDao;
    private UsuarioDAO usuarioDAO;
    private Validacao validar;


    // caminho das paginas
    private static final String jsp_geladeiras = "/WEB-INF/pages/geladeiras.jsp";

    // caminho para o servlet
    private static final String url_servlet = "/admin/geladeiras";



    @Override
    public void init(){
        gelaDao = new GeladeiraDAO();
        usuarioDAO = new UsuarioDAO();
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
        // para onde voltar depois de dar certo
        String retorno = validar.definirRetorno(req.getParameter("retorno"), url_servlet);

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
                // validando o nome
                boolean vNome = validar.validarNomeProduto(nome);
                if (!vNome || nome.length() > 50){
                    voltaComErro(req, resp, "Nome da geladeira inválido. No máximo 50 caracteres.");
                    return;
                }
                // o dono tem que existir
                Usuario usuario = usuarioDAO.buscarUsuarioPorCpf(cpf.trim());
                if (usuario == null){
                    voltaComErro(req, resp, "Usuário não encontrado.");
                    return;
                }
                // o tipo é opcional: vazio usa o padrão do banco
                if (tipo == null || tipo.isBlank()){
                    tipo = "Geladeira";
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
                    resp.sendRedirect(req.getContextPath() + retorno);
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
                // o novo dono tem que existir
                Usuario usuario = usuarioDAO.buscarUsuarioPorCpf(cpf.trim());
                if (usuario == null){
                    voltaComErro(req, resp, "Usuário não encontrado.");
                    return;
                }

                Geladeira geladeiraNova = new Geladeira(usuario, geldeiraAntiga.getStatus(), tipo.trim(), nome.trim());

                if (gelaDao.alterarGeradeira(geldeiraAntiga, geladeiraNova) == 1){
                    session.setAttribute("sucesso", "Geladeira alterada com sucesso.");
                    resp.sendRedirect(req.getContextPath() + retorno);
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
                    resp.sendRedirect(req.getContextPath() + retorno);
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

    // carrega as listas e mostra a tela
    private void listar(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException{
        List<Geladeira> todasGeladeira = gelaDao.buscarTodasGeladeira();
        List<Usuario> usuarios = usuarioDAO.buscarTodosUsuario();
        req.setAttribute("geladeiras", todasGeladeira);
        req.setAttribute("usuarios", usuarios);
        req.getRequestDispatcher(jsp_geladeiras).forward(req, resp);
    }

    // volta para a tela de onde veio com uma mensagem de erro ( a tela de teste ou a propria tela )
    private void voltaComErro(HttpServletRequest req, HttpServletResponse resp, String mensagem)
            throws ServletException, IOException{
        req.setAttribute("erro", mensagem);
        String retorno = validar.definirRetorno(req.getParameter("retorno"), url_servlet);
        if (retorno.equals(url_servlet)){
            listar(req, resp);
        }
        else {
            req.getRequestDispatcher(retorno).forward(req, resp);
        }
    }
}
