package com.example.servlet.Controller;

import com.example.servlet.Dao.ResumoDAO;
import com.example.servlet.Dao.SuperAdminDAO;
import com.example.servlet.Util.Enum.TipoAssinatura;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

@WebServlet(urlPatterns = {
        "/admin/visao-geral",
        "/admin/configuracoes"},
        name = "painel")
public class PainelServlet extends HttpServlet {
    private ResumoDAO resumoDAO;
    private SuperAdminDAO superAdminDAO;

    // caminho das paginas
    private static final String jsp_visao_geral = "/WEB-INF/pages/visao-geral.jsp";
    private static final String jsp_configuracoes = "/WEB-INF/pages/configuracoes.jsp";

    @Override
    public void init(){
        resumoDAO = new ResumoDAO();
        superAdminDAO = new SuperAdminDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = req.getServletPath();

        switch (path){
            case "/admin/visao-geral": {
                // os totais vêm de uma consulta só ( cada ida ao banco demora )
                Map<String, Integer> totais = resumoDAO.buscarTotais();
                Map<String, double[]> receitaPorPlano = resumoDAO.buscarReceitaPorPlano();

                // receita total por mes = soma da receita de cada plano
                double receitaTotal = 0;
                for (double[] plano : receitaPorPlano.values()){
                    receitaTotal += plano[1];
                }

                req.setAttribute("totais", totais);
                req.setAttribute("receitaPorPlano", receitaPorPlano);
                req.setAttribute("receitaTotal", receitaTotal);
                req.setAttribute("totalPlanos", TipoAssinatura.values().length);
                req.getRequestDispatcher(jsp_visao_geral).forward(req, resp);
                break;
            }
            case "/admin/configuracoes": {
                // os dados da conta de quem esta logado
                req.setAttribute("conta", superAdminDAO.buscarSuperAdmin((String) req.getSession().getAttribute("admin")));
                req.getRequestDispatcher(jsp_configuracoes).forward(req, resp);
                break;
            }
            default:
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    // o SuperAdminServlet manda o POST de volta para cá quando dá erro ( a mensagem de erro já vem na request )
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doGet(req, resp);
    }
}
