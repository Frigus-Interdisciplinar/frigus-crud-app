package com.example.servlet.Controller;

import com.example.servlet.Dao.GeladeiraDAO;
import com.example.servlet.Dao.ProdutoDAO;
import com.example.servlet.Dao.ProdutosGeladeiraDAO;
import com.example.servlet.Model.Geladeira;
import com.example.servlet.Model.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {"/tela-padrao-geladeira", "/geladeira-inserir"},
        name = "/GeladeiraServlet")
public class GeladeiraServlet extends HttpServlet {
    private GeladeiraDAO gelaDao;
    private ProdutosGeladeiraDAO pgDao;
    private ProdutoDAO prodDao;
    private String action;
    private String value;
    private HttpSession session;


    @Override
    public void init(){
        gelaDao = new GeladeiraDAO();
        pgDao = new ProdutosGeladeiraDAO();
        prodDao = new ProdutoDAO();
    }



    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        session = req.getSession();
        action = req.getParameter("action");

        Usuario usuario = (Usuario) session.getAttribute("usuario");
        switch(action) {
            case "/tela-padrao-geladeira": {
                List<Geladeira> todasGeladeira = gelaDao.buscarTodasGeladeira(usuario.getCpf());
                for(Geladeira geladeira: todasGeladeira){
                    resp.getWriter().println(todasGeladeira);
                }
                value = req.getParameter("acao");
                switch (value) {
                    case "inserir":
                        resp.sendRedirect(req.getContextPath() + "/geladeira-inserir.jsp");
                        break;
                    default:
                        resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                }
            }
        }
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {
        session = req.getSession();
        action = req.getParameter("action");
        //resatando o usuario da session
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        switch(action) {
            case "/geladeira-inserir": {
                // pegando os valores do formulario
                String tipo = req.getParameter("tipo");
                Geladeira geladeira = new Geladeira(
                        usuario,
                        tipo
                );
                int funcionou = gelaDao.inserirGeladeira(geladeira);

                if (funcionou == 1){
                    // salvando a geladeira entre as requisições
                    session.setAttribute("geladeira", geladeira);
                    resp.sendRedirect(req.getContextPath() + "/tela-padrao-geladeira.jsp");
                    break;
                }else {
                    resp.sendRedirect(req.getContextPath() + "/geladeira-inserir.jsp");
                    break;
                }

            }

            default:{
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        }
    }
}