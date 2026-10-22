package com.example.servlet.Controller;

import com.example.servlet.Dao.GeladeiraDAO;
import com.example.servlet.Model.Empresa;
import com.example.servlet.Model.Geladeira;
import com.example.servlet.Model.Usuario;
import com.example.servlet.Util.Enum.Categoria;
import com.example.servlet.Util.Enum.TipoAssinatura;
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
        "/ver-geladeira",
        "/inserir-geladeira",
        "/alterar-geladeira",
        "/deletar-geladeira"},
        name = "/GeladeiraServlet")
public class GeladeiraServlet extends HttpServlet {
    private GeladeiraDAO gelaDao;
    private String action;


    // caminho das paginas
    private static final String jsp_ver_geladeira = "/assets/pages/ver-geladeira.jsp";
    private static final String jsp_inserir_geladeira = "/assets/pages/inserir-geladeira.jsp";
    private static final String jsp_alterar_geladeira = "/assets/pages/alterar-geladeira.jsp";
    private static final String jsp_deletar_geladeira = "/assets/pages/deletar-geladeira.jsp";
    private static final String jsp_login = "/assets/pages/login.jsp";

    // caminho para o servlet
    private static final String url_servlet = "/ver-geladeira";



    @Override
    public void init(){
        gelaDao = new GeladeiraDAO();
    }



    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession();
        String path = req.getServletPath();

        if (session == null || session.getAttribute("usuario") == null){
            resp.sendRedirect(req.getContextPath() + jsp_login);
            return;
        }

        Usuario usuario = (Usuario) session.getAttribute("usuario");
        switch(path) {
            case "/ver-geladeira": {
                List<Geladeira> todasGeladeira = gelaDao.buscarTodasGeladeira(usuario.getCpf());
                req.setAttribute("geladeiras", todasGeladeira);
                req.getRequestDispatcher(jsp_ver_geladeira).forward(req, resp);
                break;
            }
            default:
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {
        Validacao validar = new Validacao();
        HttpSession session = req.getSession();
        action = req.getParameter("action");
        //resatando o usuario da session
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        Categoria plano = (Categoria) session.getAttribute("plano");

        // se a sessao expirou tem que fazer login
        if (usuario == null || plano == null) {
            resp.sendRedirect(req.getContextPath() + jsp_login);
            return;
        }

        int limitePlano;
        if (plano.name().equals(TipoAssinatura.FREE)){
            limitePlano = 1;
        }
        else if (plano.name().equals(TipoAssinatura.PLUS) || plano.name().equals(TipoAssinatura.FAMILIA)){
            limitePlano = 3;
        }
        else {
            System.err.println("nao era para chegar aqui!!!");
            voltaComErro(req, resp, "Erro ao cadastrar a geladeira. Por favor tente novamente.", jsp_inserir_geladeira);
            return;
        }

        switch(action) {
            case "/inserir-geladeira": {

                // ver se ele ja alcançou o limite de geladeiras cadastradas
                int geladeirasExistentes = gelaDao.totalGeladeirasUsuario(usuario.getCpf());
                if (geladeirasExistentes >= limitePlano){
                    voltaComErro(req, resp, "Você atingiu o limite de geladeiras do seu plano.", jsp_ver_geladeira);
                    return;
                }

                // pegando os valores do formulario
                String tipo = req.getParameter("tipo");
                String nome = req.getParameter("nome");
                // validando o nome
                boolean vNome = validar.validarNome(nome);
                if (!vNome){
                    resp.sendRedirect(req.getContextPath() + jsp_inserir_geladeira);
                    return;
                }
                Geladeira geladeira = new Geladeira(
                        usuario,
                        tipo,
                        nome
                );
                int funcionou = gelaDao.inserirGeladeira(geladeira);

                if (funcionou == 1){
                    // salvando a geladeira entre as requisições
                    session.setAttribute("geladeira", geladeira);
                    resp.sendRedirect(req.getContextPath() + url_servlet);
                    break;
                }else {
                    resp.sendRedirect(req.getContextPath() + jsp_inserir_geladeira);
                    break;
                }
            }
            case "/alterar-geladeira":{
                String idTxt = req.getParameter("idGeladeira");
                int id = Integer.parseInt(idTxt);
                Geladeira geldeiraAntiga = gelaDao.buscarUnicaGeladeira(usuario.getCpf(), id);

                String tipo = req.getParameter("tipo");
                String nome = req.getParameter("nome");

                if (tipo == null){
                    voltaComErro(req, resp, "O tipo da geladeira não pode ser vazio.", jsp_alterar_geladeira);
                    return;
                }
                if (nome == null){
                    voltaComErro(req, resp, "O nome da geladeira não pode ser vazio.", jsp_alterar_geladeira);
                    return;
                }

                Geladeira geladeiraNova = new Geladeira(usuario, geldeiraAntiga.getStatus(), tipo, nome);

                if (gelaDao.alterarGeradeira(geldeiraAntiga, geladeiraNova) == 1){
                    resp.sendRedirect(req.getContextPath() + url_servlet);
                    return;
                }else {
                    voltaComErro(req, resp, "Erro ao alterar essa geladeira. Por favor tente novamente", jsp_alterar_geladeira);
                }
            }
            case "/deletar-geladeira":{
                String idTxt = req.getParameter("idGeladeira");
                int id = Integer.parseInt(idTxt);
                if(gelaDao.deletarGeladeira(usuario.getCpf(), id) == 1){
                    resp.sendRedirect(req.getContextPath() + url_servlet);
                    return;
                }
                else {
                    voltaComErro(req, resp, "Erro ao deletar essa geladeira. Por favor tente novamente.", jsp_deletar_geladeira);
                }
            }
            default:{
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        }
    }
    // voltando para a pagina com erro
    public static void voltaComErro(HttpServletRequest req, HttpServletResponse resp, String mensagem, String pagina)
            throws ServletException, IOException{
        req.setAttribute("erro", mensagem);
        req.getRequestDispatcher(pagina).forward(req, resp);
    }
}