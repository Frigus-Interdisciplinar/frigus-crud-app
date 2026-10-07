package com.example.servlet.Controller;

import com.example.servlet.Dao.AssinaturaDAO;
import com.example.servlet.Dao.EmpresaDAO;
import com.example.servlet.Dao.UsuarioDAO;
import com.example.servlet.Model.Assinatura;
import com.example.servlet.Model.Empresa;
import com.example.servlet.Model.Usuario;
import com.example.servlet.Util.Enum.TipoAssinatura;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet(urlPatterns = {
        // usuario
        "/tela-padrao-usuario",
        "/criar-plano-usuario",
        "/alterar-plano-usario",
        "deletar-plano-usuario",

        // empresa
        "/tela-padrao-empresa",
        "/criar-plano-empresa",
        "/alterar-plano-empresa",
        "/deletar-plano-empresa",},

        name = "/planos")
public class Planos extends HttpServlet {
    private AssinaturaDAO assinaturaDAO;
    private UsuarioDAO usuarioDAO;
    private EmpresaDAO empresaDAO;
    private HttpSession session;

    @Override
    public void init(){
        assinaturaDAO = new AssinaturaDAO();
        usuarioDAO = new UsuarioDAO();
        empresaDAO =  new EmpresaDAO();
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // pegando o caminho do usuario
        String action = req.getParameter("action");

        switch (action){
            case "/criar-plano-usuario": {
                // pegando as informações do formulario
                TipoAssinatura plano = TipoAssinatura.valueOf(req.getParameter("plano"));
                // resgatando o usuario
                String usuarioEmail = (String) session.getAttribute("usuarioEmail");
                // buscando no banco
                Usuario usuario = usuarioDAO.buscarUsuario(usuarioEmail);

                Assinatura assinaturaUsuario = new Assinatura(
                        usuario,
                        plano
                );
                int funcionou = assinaturaDAO.inserirAssinaturaUsuario(assinaturaUsuario);

                if (funcionou == 1) {
                    resp.sendRedirect(req.getContextPath() + "/geladeira.jsp");
                } else {
                    resp.sendRedirect(req.getContextPath() + "/criar-plano-usuario.jsp");
                }
                break;
            }

















            case "/criar-plano-empresa": {
                // pegando as informações do formulario
                TipoAssinatura plano = TipoAssinatura.valueOf(req.getParameter("plano"));

                // recuperando os dados da empresa
                String empresaEmail = (String) session.getAttribute("empresaEmail");
                // buscando no banco
                Empresa empresa = empresaDAO.buscarEmpresa(empresaEmail);

                Assinatura assinaturaEmpresa = new Assinatura(
                        empresa,
                        plano
                );
                int funcionouAssiEmpr = assinaturaDAO.inserirAssinaturaEmpresa(assinaturaEmpresa);
                if (funcionouAssiEmpr == 1) {
                    resp.sendRedirect(req.getContextPath() + "/produto.jsp");
                } else {
                    resp.sendRedirect(req.getContextPath() + "/planoEmpresa.jsp");
                }
                break;
            }
            default:{
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        }

    }
}