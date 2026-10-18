package com.example.servlet.Controller;

import com.example.servlet.Dao.EmpresaDAO;
import com.example.servlet.Model.Empresa;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/visaoGeralEMpresa")
public class VisaoGeralEmpresa extends HttpServlet {
    private EmpresaDAO empresaDAO;
    // caminho para o login
    private static final String jsp_login = "/assets/pages/login.jsp";

    @Override
    public void init(){
        empresaDAO = new EmpresaDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        // verificando se esta logado
        if (session == null || session.getAttribute("empresaEmail") == null){
            voltaComErro(req, resp, "Empresa não encontrada! Por favor Cadastre-se novamente.", jsp_login);
            return;
        }
        String email = (String) session.getAttribute("empresaEmail");
        Empresa empresa = empresaDAO.buscarEmpresa(email);
        // verificando se a empresa ainda exite
        if (empresa == null){
            voltaComErro(req, resp, "Empresa não encontrada! Por favor Cadastre-se novamente.", jsp_login);
            return;
        }




    }
    // voltando para a pagina com erro
    public static void voltaComErro(HttpServletRequest req, HttpServletResponse resp, String mensagem, String pagina)
            throws ServletException, IOException{
        req.setAttribute("erro", mensagem);
        req.getRequestDispatcher(pagina).forward(req, resp);
    }
}