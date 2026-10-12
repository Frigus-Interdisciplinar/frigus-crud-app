package com.example.servlet.Controller;

import com.example.servlet.Dao.EmpresaDAO;
import com.example.servlet.Model.Empresa;
import com.example.servlet.Util.Hash;
import com.example.servlet.Util.Validacao;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet(urlPatterns = {
        "/perfil-empresa",
        "/criar-empresa",
        "/senha-empresa",
        "/alterar-empresa",
        "/alterar-email-empresa",
        "/alterar-senha-empresa",
        "/deletar-empresa"},
        name = "/criar-empresa")
public class EmpresaServlet extends HttpServlet {
    private EmpresaDAO empresaDAO;
    private Validacao validar;

    private static final String jsp_tela_perfil = "/assets/pages/perfil-empresa.jsp";
    private static final String jsp_inserir = "/assets/pages/criar-empresa.jsp";
    private static final String jsp_senha = "/assets/pages/senha-empresa.jsp";
    private static final String jsp_alterar = "/assets/pages/alterar-empresa.jsp";
    private static final String jsp_alterar_email = "/assets/pages/alterar-email-empresa.jsp";
    private static final String jsp_alterar_senha = "/assets/pages/alterar-senha-empresa.jsp";
    private static final String jsp_deletar = "/assets/pages/deletar-empresa.jsp";
    private static final String jsp_visao_geral = "/assets/pages/visao-geral-emp.jsp";
    private static final String jsp_plano = "/assets/pages/criar-plano-emp.jsp";

    @Override
    public void init(){
        empresaDAO = new EmpresaDAO();
         validar = new Validacao();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException{
        String path = req.getServletPath();
        HttpSession session = req.getSession(false);

        // verifica se esta logado
        if (session == null || session.getAttribute("empresaEmail") == null){
            resp.sendRedirect(req.getContextPath() + jsp_inserir);
            return;
        }

        String email = (String) session.getAttribute("empresaEmail");

        switch (path){
            case "/perfil-empresa":{
                Empresa empresa = empresaDAO.buscarEmpresa(email);
                req.setAttribute("empresa", empresa);
                req.getRequestDispatcher(jsp_tela_perfil).forward(req, resp);
                break;
            }
            default:
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // pegando o caminho do usuario
        String action = req.getParameter("action");

        // salvando as informações entre as requisições
        HttpSession session = req.getSession();

        switch (action){



            case "/criar-empresa": {
                // pegando os dados do fornmulario de cadastro
                String nome = req.getParameter("nome");
                String email = req.getParameter("email");
                String cnpj = req.getParameter("cnpj");

                // fromatar
                String cnpjPuro = validar.formatarCnpj(cnpj);

                // validação
                boolean vNome = validar.validarNome(nome);
                boolean vEmail = validar.validarEmail(email);
                boolean vCnpj = validar.validarCnpj(cnpjPuro);

                if ( (vNome == false) || (vCnpj == false) || (vEmail == false) ){
                    resp.sendRedirect(req.getContextPath() + jsp_inserir);
                    break;
                }

                session.setAttribute("nomeEmpresa", nome);
                session.setAttribute("emailEmpresa", email);
                session.setAttribute("cnpj", cnpj);
                resp.sendRedirect(req.getContextPath() + jsp_senha);
                break;
            }


            case "/senha-empresa": {
                // pegando do formulario
                String senha = req.getParameter("senha");
                String senhaConfirmada = req.getParameter("confirmarSenha");

                // recuperando as informações da empresa
                String cnpj = (String) session.getAttribute("cnpj");
                String nome = (String) session.getAttribute("nomeEmpresa");
                String email = (String) session.getAttribute("emailEmpresa");
                if (!senha.equals(senhaConfirmada)) {
                    resp.sendRedirect(req.getContextPath() + jsp_senha);
                    break;
                }

                // hasheando a senha para jogar no banco
                String senhaHash = Hash.bcrypt(senha);
                // criando o objeto empresa
                Empresa empresa = new Empresa(
                        cnpj,
                        nome,
                        email,
                        senhaHash
                );

                int funcionou = empresaDAO.cadastrarEmpresa(empresa);


                if (funcionou == 1) {
                    // salvando o objeto empresa
                    session.setAttribute("empresaEmail", empresa.getEmail());

                    // deletando as informações temporarias
                    session.removeAttribute("cnpj");
                    session.removeAttribute("nomeEmpresa");
                    session.removeAttribute("email");

                    // redirecionando para a proxima pagina
                    resp.sendRedirect(req.getContextPath() + jsp_plano);
                    break;
                } else {
                    resp.sendRedirect(req.getContextPath() + jsp_senha);
                }
                break;
            }

            case "/alterar-empresa":{
                // resgatando o email da ultima requisição
                String emailResgatado = (String) session.getAttribute("empresaEmail");

                // pegando as informações do formulario
                String nomeAlterado = req.getParameter("nome");
                String emailAlterado = req.getParameter("email");
                String senhaAlterado = req.getParameter("senha");

                // validandos os dados de entrada
                boolean vNomeAlterado = validar.validarNome(nomeAlterado);
                boolean vEmailAlterado = validar.validarEmail(emailAlterado);
                boolean vSenhaAlterado = validar.validarSenha(senhaAlterado);

                if ((vNomeAlterado == false) || (vEmailAlterado == false) || (vSenhaAlterado == false)){
                    resp.sendRedirect(req.getContextPath() + jsp_alterar);
                    break;
                }
                // hasheando a senha
                String senhaHash = Hash.bcrypt(senhaAlterado);

                // criando o objeto da empresa antiga
                Empresa empresaAntiga = empresaDAO.buscarEmpresa(emailResgatado);

                // criando a nova empresa
                Empresa empresaNova = new Empresa(
                        empresaAntiga.getCnpj(),
                        nomeAlterado,
                        emailAlterado,
                        senhaHash
                );

                int funcionou = empresaDAO.alterarEmpresa(empresaAntiga, empresaNova);

                if (funcionou == 1) {
                    // salvando o objeto empresa
                    session.setAttribute("empresaEmail", empresaNova.getEmail());

                    // deletando as informações temporarias
                    session.removeAttribute("cnpj");
                    session.removeAttribute("nomeEmpresa");
                    session.removeAttribute("email");

                    // redirecionando para a proxima pagina
                    resp.sendRedirect(req.getContextPath() + jsp_visao_geral);
                    break;
                } else {
                    resp.sendRedirect(req.getContextPath() + jsp_alterar);
                }
                break;
            }



            case "/alterar-email-empresa":{
                // resgatando o email da ultima requisição
                String emailResgatado = (String) session.getAttribute("empresaEmail");

                // pegando as informações do formulario
                String emailAlterado = req.getParameter("email");

                // validandos os dados de entrada
                boolean vEmailAlterado = validar.validarEmail(emailAlterado);

                if (vEmailAlterado == false){
                    resp.sendRedirect(req.getContextPath() + jsp_alterar_email);
                    break;
                }

                // criando o objeto da empresa antiga
                Empresa empresa = empresaDAO.buscarEmpresa(emailResgatado);

                int funcionou = empresaDAO.alterarEmailEmpresa(empresa, emailAlterado);

                if (funcionou == 1) {
                    // salvando o objeto empresa
                    session.setAttribute("empresaEmail", emailAlterado);
                    // redirecionando para a proxima pagina
                    resp.sendRedirect(req.getContextPath() + jsp_visao_geral);
                    break;
                } else {
                    resp.sendRedirect(req.getContextPath() + jsp_alterar_email);
                }
                break;
            }


            case "/alterar-senha-empresa":{
                // resgatando o email
                String emailResgatado = (String) session.getAttribute("empresaEmail");

                // pegando a senha do formulario
                String senha = req.getParameter("senha");

                // validando a senha
                boolean vSenha = validar.validarSenha(senha);

                if (vSenha == false){
                    resp.sendRedirect(req.getContextPath() + jsp_alterar_senha);
                    break;
                }
                // hasheando a senha
                String senhaHash = Hash.bcrypt(senha);

                // criando o usuario com o email resgatado
                Empresa empresa = empresaDAO.buscarEmpresa(emailResgatado);

                int funcionou = empresaDAO.alterarSenhaEmpresa(empresa, senhaHash);
                if (funcionou == 1){
                    resp.sendRedirect(req.getContextPath() + jsp_visao_geral);
                }
                else {
                    resp.sendRedirect(req.getContextPath() + jsp_alterar_senha);

                }
            }


            case "/deletar-empresa":{
                String email = req.getParameter("email");
                // validando
                boolean vEmail = validar.validarEmail(email);
                if ( vEmail == false){
                    resp.sendRedirect(req.getContextPath() + jsp_deletar);
                }

                int funcionou = empresaDAO.excluirEmpresa(email);
                if (funcionou == 1) {
                    // redirecionando para a proxima pagina
                    resp.sendRedirect(req.getContextPath() + jsp_visao_geral);
                    break;
                } else {
                    resp.sendRedirect(req.getContextPath() + jsp_deletar);
                }
                break;
            }
            default:
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}