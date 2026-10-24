package com.example.servlet.Controller;

import com.example.servlet.Dao.SuperAdminDAO;
import com.example.servlet.Model.SuperAdmin;
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
        "/login",
        "/cadastro",
        "/logout"},
        name = "login")
public class Login extends HttpServlet {
    private SuperAdminDAO superAdminDAO;
    private Validacao validar;

    // caminho das paginas
    private static final String jsp_login = "/WEB-INF/pages/login.jsp";
    private static final String jsp_cadastro = "/WEB-INF/pages/cadastro.jsp";

    // caminho para o servlet
    private static final String url_visao_geral = "/admin/visao-geral";
    private static final String url_login = "/login";

    @Override
    public void init(){
        superAdminDAO = new SuperAdminDAO();
        validar = new Validacao();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = req.getServletPath();

        switch (path){
            case "/login": {
                // a mensagem de sucesso do cadastro fica na sessao, aqui ela vai para a request e some da sessao
                HttpSession session = req.getSession(false);
                if (session != null && session.getAttribute("sucesso") != null){
                    req.setAttribute("sucesso", session.getAttribute("sucesso"));
                    session.removeAttribute("sucesso");
                }
                req.getRequestDispatcher(jsp_login).forward(req, resp);
                break;
            }
            case "/cadastro": {
                req.getRequestDispatcher(jsp_cadastro).forward(req, resp);
                break;
            }
            case "/logout": {
                // acabando com a sessao e voltando para a pagina inicial
                HttpSession session = req.getSession(false);
                if (session != null){
                    session.invalidate();
                }
                resp.sendRedirect(req.getContextPath() + "/index.html");
                break;
            }
            default:
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // o login esta fora do /admin/*, entao o filtro nao passa por aqui
        req.setCharacterEncoding("UTF-8");

        String path = req.getServletPath();

        switch (path){
            case "/login": {
                // pegando os dados do formulario
                String email = req.getParameter("email");
                String senha = req.getParameter("senha");

                if (email == null || email.isBlank() || senha == null || senha.isBlank()){
                    voltaComErro(req, resp, "Digite o e-mail e a senha.", jsp_login);
                    return;
                }

                // buscando o super admin no banco
                SuperAdmin superAdmin = superAdminDAO.buscarSuperAdmin(email.trim());

                // a mensagem e a mesma para nao mostrar se o e-mail existe
                if (superAdmin == null){
                    voltaComErro(req, resp, "E-mail ou senha incorretos.", jsp_login);
                    return;
                }

                // vendo se a senha digitada é a mesma da armazenada no banco
                boolean senhaIgualBanco;
                try {
                    senhaIgualBanco = Hash.verficarBcrypt(senha, superAdmin.getSenha());
                } catch (IllegalArgumentException e) {
                    // a senha guardada no banco nao é um hash BCrypt
                    senhaIgualBanco = false;
                }
                if (!senhaIgualBanco){
                    voltaComErro(req, resp, "E-mail ou senha incorretos.", jsp_login);
                    return;
                }

                // salvando o admin na sessao ( com um id novo, para nao reaproveitar a sessao de antes do login )
                HttpSession session = req.getSession();
                req.changeSessionId();
                session.setAttribute("admin", superAdmin.getEmail());
                resp.sendRedirect(req.getContextPath() + url_visao_geral);
                break;
            }
            case "/cadastro": {
                // pegando os dados do formulario
                String nome = req.getParameter("nome");
                String email = req.getParameter("email");
                String senha = req.getParameter("senha");
                String senhaConfirmada = req.getParameter("confirmarSenha");

                if (nome == null || nome.isBlank()){
                    voltaComErro(req, resp, "O nome não pode ser vazio!!!", jsp_cadastro);
                    return;
                }
                if (email == null || email.isBlank()){
                    voltaComErro(req, resp, "O email não pode ser vazio!!!", jsp_cadastro);
                    return;
                }
                if (senha == null || senha.isBlank()){
                    voltaComErro(req, resp, "A senha não pode ser vazia!!!", jsp_cadastro);
                    return;
                }

                // validando
                boolean vNome = validar.validarNome(nome);
                boolean vEmail = validar.validarEmail(email);
                boolean vSenha = validar.validarSenha(senha);

                if (!vNome || nome.length() > 20){
                    voltaComErro(req, resp, "Nome inválido!!! Só letras e no máximo 20 caracteres.", jsp_cadastro);
                    return;
                }
                if (!vEmail){
                    voltaComErro(req, resp, "E-mail inválido!!!", jsp_cadastro);
                    return;
                }
                if (!vSenha){
                    voltaComErro(req, resp, "Senha inválida!!! Deve ter de 8 a 15 caracteres, uma letra maiúscula e um caractere especial.", jsp_cadastro);
                    return;
                }
                if (!senha.equals(senhaConfirmada)){
                    voltaComErro(req, resp, "A confirmação da senha não é igual à senha!!!", jsp_cadastro);
                    return;
                }
                if (superAdminDAO.buscarSuperAdmin(email.trim()) != null){
                    voltaComErro(req, resp, "Esse e-mail já está cadastrado.", jsp_cadastro);
                    return;
                }

                // hasheando a senha
                String senhaHash = Hash.bcrypt(senha);

                SuperAdmin superAdmin = new SuperAdmin();
                superAdmin.setNome(nome.trim());
                superAdmin.setEmail(email.trim());
                superAdmin.setSenha(senhaHash);

                if (superAdminDAO.adicionarSuperAdmin(superAdmin) == 1){
                    req.getSession().setAttribute("sucesso", "Conta criada com sucesso. Entre com o seu e-mail e a sua senha.");
                    resp.sendRedirect(req.getContextPath() + url_login);
                    break;
                }
                else {
                    voltaComErro(req, resp, "Erro ao criar a conta. Por favor tente novamente.", jsp_cadastro);
                    break;
                }
            }
            default:
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    // voltando para a pagina com erro
    public static void voltaComErro(HttpServletRequest req, HttpServletResponse resp, String mensagem, String pagina)
            throws ServletException, IOException{
        req.setAttribute("erro", mensagem);
        req.getRequestDispatcher(pagina).forward(req, resp);
    }
}
