package com.example.servlet.Controller;

import com.example.servlet.Dao.FrigorificosDAO;
import com.example.servlet.Model.Empresa;
import com.example.servlet.Model.Frigorificos;
import com.example.servlet.Util.Enum.Categoria;
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
        "/ver-frigorifico",
        "/inserir-frigorifico",
        "/alterar-frigorifico",
        "/deletar-frigorifico",
}, name = "/ver-frigorifico")
public class FrigorificoServlet extends HttpServlet {
    private FrigorificosDAO frigorificoDAO;

    // caminho das pastas
    private static final String jsp_login = "/assets/pages/login.jsp";
    private static final String jsp_ver = "/assets/pages/ver-frigorifico.jsp";
    private static final String jsp_inserir = "/assets/pages/inserir-frigorifico.jsp";
    private static final String jsp_alterar = "/assets/pages/alterar-frigorifico.jsp";
    private static final String jsp_deletar = "/assets/pages/deletar-frigorifico.jsp";

    // caminho para o servlet
    private static final String url_servlet = "/ver-frigorifico";

    @Override
    public void init(){
        frigorificoDAO = new FrigorificosDAO();
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession();
        if (session == null || session.getAttribute("empresa") == null){
            resp.sendRedirect(req.getContextPath() + jsp_login);
            return;
        }
        Empresa empresa = (Empresa) session.getAttribute("empresa");
        switch (req.getServletPath()){
            case "/ver-frigorifico":{
                List<Frigorificos> frigorificos = frigorificoDAO.buscarFrigorificoEmpresa(empresa.getCnpj());
                req.setAttribute("frigorificos", frigorificos);
                req.getRequestDispatcher(jsp_ver).forward(req, resp);
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
        switch (req.getServletPath()){
            case "/inserir-frigorifico":{
                // pegando as informações do formulario


                Empresa empresa = (Empresa) session.getAttribute("empresa");
                Categoria plano = (Categoria) session.getAttribute("plano");

                if(empresa == null || plano == null){
                    resp.sendRedirect(req.getContextPath() + jsp_login);
                    return;
                }
                Frigorificos frigorifico = new Frigorificos(empresa, );
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