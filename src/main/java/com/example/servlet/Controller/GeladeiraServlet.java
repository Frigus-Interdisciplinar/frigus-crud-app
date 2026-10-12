package com.example.servlet.Controller;

import com.example.servlet.Dao.GeladeiraDAO;
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
        "/ver-geladeira",
        "/inserir-geladeira"},
        name = "/GeladeiraServlet")
public class GeladeiraServlet extends HttpServlet {
    private GeladeiraDAO gelaDao;
    private String action;


    // caminho das paginas
    private static final String jsp_ver_geladeira = "/assets/pages/ver-geladeira.jsp";
    private static final String jsp_inserir_geladeira = "/assets/pages/inserir-geladeira.jsp";
    private static final String jsp_login = "/assets/pages/login.jsp";



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

        switch(action) {
            case "/geladeira-inserir": {
                // pegando os valores do formulario
                String tipo = req.getParameter("tipo");
                String nome = req.getParameter("nome");
                // validando o nome
                boolean vNome = validar.validarNome(nome);
                if (!vNome){
                    resp.sendRedirect(req.getContextPath() + jsp_inserir_geladeira);
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
                    resp.sendRedirect(req.getContextPath() + jsp_ver_geladeira);
                    break;
                }else {
                    resp.sendRedirect(req.getContextPath() + jsp_inserir_geladeira);
                    break;
                }
            }
            default:{
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        }
    }
}