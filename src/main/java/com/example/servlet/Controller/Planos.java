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
        "/ver-plano-usuario",
        "/criar-plano-usuario",
        "/alterar-plano-usuario",
        "/deletar-plano-usuario",

        // empresa
        "/ver-plano-empresa",
        "/criar-plano-empresa",
        "/alterar-plano-empresa",
        "/deletar-plano-empresa"},

        name = "/planos")
public class Planos extends HttpServlet {
    private AssinaturaDAO assinaturaDAO;
    private UsuarioDAO usuarioDAO;
    private EmpresaDAO empresaDAO;

    // caminhos das paginas
    private static final String jsp_visao_geral_usu = "/assets/pages/visao-geral-usu.jsp";
    private static final String jsp_ver_plano_usu = "/assets/pages/ver-plano-usuario.jsp";
    private static final String jsp_criar_plano_usu = "/assets/pages/criar-plano-usuario.jsp";
    private static final String jsp_alterar_plano_usu = "/assets/pages/alterar-plano-usuario.jsp";
    private static final String jsp_deletar_plano_usu = "/assets/pages/deletar-plano-usuario.jsp";

    private static final String jsp_visao_geral_emp = "/assets/pages/visao-geral-emp.jsp";
    private static final String jsp_ver_plano_emp = "/assets/pages/ver-plano-empresa.jsp";
    private static final String jsp_criar_plano_emp = "/assets/pages/criar-plano-empresa.jsp";
    private static final String jsp_alterar_plano_emp = "/assets/pages/alterar-plano-empresa.jsp";
    private static final String jsp_deletar_plano_emp = "/assets/pages/deletar-plano-empresa.jsp";


    @Override
    public void init(){
        assinaturaDAO = new AssinaturaDAO();
        usuarioDAO = new UsuarioDAO();
        empresaDAO =  new EmpresaDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException{
        HttpSession session = req.getSession(false);

        String path = req.getServletPath();
        switch (path){
            case "/ver-plano-usuario":{
                // verificando se esta logado
                if(session == null || session.getAttribute("usuarioEmail") == null){
                    resp.sendRedirect(req.getContextPath() + jsp_criar_plano_usu);
                    return;
                }
                String email = (String) session.getAttribute("usuarioEmail");
                Usuario usuario = usuarioDAO.buscarUsuario(email);
                Assinatura assinatura = assinaturaDAO.buscarAssinaturaUsuario(usuario.getCpf());
                req.setAttribute("assinatura", assinatura);
                req.getRequestDispatcher(jsp_ver_plano_usu).forward(req, resp);
                break;
            }
            case "/ver-plano-empresa": {
                // verificando se esta logado
                if(session == null || session.getAttribute("empresaEmail") == null){
                    resp.sendRedirect(req.getContextPath() + jsp_criar_plano_emp);
                    return;
                }
                String email = (String) session.getAttribute("empresaEmail");
                Empresa empresa = empresaDAO.buscarEmpresa(email);
                Assinatura assinatura = assinaturaDAO.buscarAssinaturaEmpresa(empresa.getCnpj());
                req.setAttribute("assinatura", assinatura);
                req.getRequestDispatcher(jsp_ver_plano_emp).forward(req, resp);
                break;
            }
            default:
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                break;
        }
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // pegando o caminho do usuario
        String action = req.getParameter("action");
        HttpSession session = req.getSession();

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
                    resp.sendRedirect(req.getContextPath() + jsp_visao_geral_usu);
                } else {
                    resp.sendRedirect(req.getContextPath() + jsp_criar_plano_usu);
                }
                resp.setHeader("");
                break;
            }

            case "/alterar-plano-usuario":{
                // pegando as informações do formulario
                TipoAssinatura plano = TipoAssinatura.valueOf(req.getParameter("plano"));

                // resgatando o usuario
                String usuarioEmail = (String) session.getAttribute("usuarioEmail");

                // buscando no banco
                Usuario usuario = usuarioDAO.buscarUsuario(usuarioEmail);

                // buscando a assinatura antiga no banco
                Assinatura assinaturaAntiga = assinaturaDAO.buscarAssinaturaUsuario(usuario.getCpf());

                Assinatura assinaturaNova = new Assinatura(
                        usuario,
                        plano
                );

                int funcionou = assinaturaDAO.alterarAssinUsuario(assinaturaAntiga, assinaturaNova);
                if (funcionou == 1){
                    resp.sendRedirect(req.getContextPath() + jsp_visao_geral_usu);
                    break;
                }
                else {
                    resp.sendRedirect(req.getContextPath() + jsp_alterar_plano_usu);
                }
                break;
            }


            case "/deletar-plano-usuario":{
                // resgatando o usuario
                String usuarioEmail = (String) session.getAttribute("usuarioEmail");

                Usuario usuario = usuarioDAO.buscarUsuario(usuarioEmail);

                Assinatura assinatura = assinaturaDAO.buscarAssinaturaUsuario(usuario.getCpf());

                int funcionou = assinaturaDAO.deletarAssinaturaUsuario(assinatura);
                if(funcionou == 1){
                    resp.sendRedirect(req.getContextPath() + jsp_visao_geral_usu);
                    break;
                }
                else {
                    resp.sendRedirect(req.getContextPath() + jsp_deletar_plano_usu);
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


            case "/alterar-plano-empresa":{
                // pegando as informações do formulario
                TipoAssinatura plano = TipoAssinatura.valueOf(req.getParameter("plano"));

                // resgatando a empresa
                String empresaEmail = (String) session.getAttribute("empresaEmail");

                // buscando no banco
                Empresa empresa = empresaDAO.buscarEmpresa(empresaEmail);

                // buscando a assinatura antiga no banco
                Assinatura assinaturaAntiga = assinaturaDAO.buscarAssinaturaEmpresa(empresa.getCnpj());

                Assinatura assinaturaNova = new Assinatura(
                        empresa,
                        plano
                );

                int funcionou = assinaturaDAO.alterarAssinEmpresa(assinaturaAntiga, assinaturaNova);
                if (funcionou == 1){
                    resp.sendRedirect(req.getContextPath() + jsp_visao_geral_emp);
                    break;
                }
                else {
                    resp.sendRedirect(req.getContextPath() + jsp_alterar_plano_emp);
                }
                break;
            }



            case "/deletar-plano-empresa":{
                // resgatando o usuario
                String empresaEmail = (String) session.getAttribute("empresaEmail");

                Empresa empresa = empresaDAO.buscarEmpresa(empresaEmail);

                Assinatura assinatura = assinaturaDAO.buscarAssinaturaEmpresa(empresa.getCnpj());

                int funcionou = assinaturaDAO.deletarAssinaturaEmpresa(assinatura);

                if(funcionou == 1){
                    resp.sendRedirect(req.getContextPath() + jsp_visao_geral_emp);
                    break;
                }
                else {
                    resp.sendRedirect(req.getContextPath() + jsp_deletar_plano_emp);
                }
                break;
            }
            default:{
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        }

    }
}