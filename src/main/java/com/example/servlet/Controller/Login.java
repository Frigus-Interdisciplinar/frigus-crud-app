package com.example.servlet.Controller;

import com.example.servlet.Dao.SuperAdminDAO;
import com.example.servlet.Model.SuperAdmin;
import com.example.servlet.Util.Hash;
import com.example.servlet.Util.Validacao;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet(urlPatterns = {
        "/login",
        "/cadastro",
        "/cadastro/senha",
        "/logout"},
        name = "login")
public class Login extends HttpServlet {
    private SuperAdminDAO superAdminDAO;
    private Validacao validar;

    // caminho das paginas
    private static final String jsp_login = "/WEB-INF/pages/login.jsp";
    private static final String jsp_cadastro = "/WEB-INF/pages/cadastro.jsp";
    private static final String jsp_cadastro_senha = "/WEB-INF/pages/cadastro-senha.jsp";

    // caminho para o servlet
    private static final String url_visao_geral = "/admin/visao-geral";
    private static final String url_login = "/login";
    private static final String url_cadastro = "/cadastro";
    private static final String url_cadastro_senha = "/cadastro/senha";

    // o cadastro tem duas telas: o nome e o e-mail da primeira ficam na sessao ate a senha da segunda
    private static final String sessao_nome = "cadastroNome";
    private static final String sessao_email = "cadastroEmail";

    // quanto tempo o login dura quando a pessoa marca "Manter conectado" ( em segundos )
    private static final int manter_conectado = 7 * 24 * 60 * 60;

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
                // se a pessoa voltou da tela da senha, o nome e o e-mail que ela ja tinha digitado aparecem de novo
                HttpSession session = req.getSession(false);
                if (session != null){
                    req.setAttribute("nomeDigitado", session.getAttribute(sessao_nome));
                    req.setAttribute("emailDigitado", session.getAttribute(sessao_email));
                }
                req.getRequestDispatcher(jsp_cadastro).forward(req, resp);
                break;
            }
            case "/cadastro/senha": {
                // sem o nome e o e-mail da primeira tela nao da para criar a senha
                HttpSession session = req.getSession(false);
                if (session == null || session.getAttribute(sessao_nome) == null){
                    resp.sendRedirect(req.getContextPath() + url_cadastro);
                    break;
                }
                req.getRequestDispatcher(jsp_cadastro_senha).forward(req, resp);
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
                SuperAdmin superAdmin = superAdminDAO.buscarSuperAdmin(validar.normalizarEmail(email));

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
                session.setAttribute("adminNome", superAdmin.getNome());

                // "Manter conectado": o login dura mais tempo parado e o cookie da sessao sobrevive ao fechar o navegador
                if (req.getParameter("manter") != null){
                    session.setMaxInactiveInterval(manter_conectado);
                    Cookie cookie = new Cookie("JSESSIONID", session.getId());
                    cookie.setMaxAge(manter_conectado);
                    cookie.setPath(req.getContextPath().isEmpty() ? "/" : req.getContextPath());
                    cookie.setHttpOnly(true);
                    resp.addCookie(cookie);
                }
                resp.sendRedirect(req.getContextPath() + url_visao_geral);
                break;
            }
            case "/cadastro": {
                // primeira tela: nome e e-mail
                String nome = req.getParameter("nome");
                String email = req.getParameter("email");

                // se der erro, os campos voltam preenchidos
                req.setAttribute("nomeDigitado", nome);
                req.setAttribute("emailDigitado", email);

                if (nome == null || nome.isBlank()){
                    voltaComErro(req, resp, "O nome não pode ser vazio!!!", jsp_cadastro);
                    return;
                }
                if (email == null || email.isBlank()){
                    voltaComErro(req, resp, "O email não pode ser vazio!!!", jsp_cadastro);
                    return;
                }
                if (req.getParameter("termos") == null){
                    voltaComErro(req, resp, "Aceite os Termos de uso e a Política de privacidade para continuar.", jsp_cadastro);
                    return;
                }

                // validando
                if (!validar.validarNome(nome) || nome.trim().length() > 20){
                    voltaComErro(req, resp, "Nome inválido!!! Só letras e no máximo 20 caracteres.", jsp_cadastro);
                    return;
                }
                if (!validar.validarEmail(email)){
                    voltaComErro(req, resp, "E-mail inválido!!!", jsp_cadastro);
                    return;
                }
                if (superAdminDAO.buscarSuperAdmin(validar.normalizarEmail(email)) != null){
                    voltaComErro(req, resp, "Esse e-mail já está cadastrado.", jsp_cadastro);
                    return;
                }

                // guardando na sessao e indo para a tela da senha
                HttpSession session = req.getSession();
                session.setAttribute(sessao_nome, nome.trim());
                session.setAttribute(sessao_email, validar.normalizarEmail(email));
                resp.sendRedirect(req.getContextPath() + url_cadastro_senha);
                break;
            }
            case "/cadastro/senha": {
                // segunda tela: senha e confirmação ( o nome e o e-mail vêm da sessao )
                HttpSession session = req.getSession(false);
                if (session == null || session.getAttribute(sessao_nome) == null){
                    resp.sendRedirect(req.getContextPath() + url_cadastro);
                    break;
                }
                String nome = (String) session.getAttribute(sessao_nome);
                String email = (String) session.getAttribute(sessao_email);
                String senha = req.getParameter("senha");
                String senhaConfirmada = req.getParameter("confirmarSenha");

                if (senha == null || senha.isBlank()){
                    voltaComErro(req, resp, "A senha não pode ser vazia!!!", jsp_cadastro_senha);
                    return;
                }
                if (!validar.validarSenha(senha)){
                    voltaComErro(req, resp, "Senha inválida!!! Deve ter de 8 a 15 caracteres, uma letra maiúscula e um caractere especial.", jsp_cadastro_senha);
                    return;
                }
                if (!senha.equals(senhaConfirmada)){
                    voltaComErro(req, resp, "A confirmação da senha não é igual à senha!!!", jsp_cadastro_senha);
                    return;
                }
                // alguem pode ter cadastrado esse e-mail enquanto a pessoa criava a senha
                if (superAdminDAO.buscarSuperAdmin(email) != null){
                    voltaComErro(req, resp, "Esse e-mail já está cadastrado.", jsp_cadastro_senha);
                    return;
                }

                // hasheando a senha
                String senhaHash = Hash.bcrypt(senha);

                SuperAdmin superAdmin = new SuperAdmin();
                superAdmin.setNome(nome);
                superAdmin.setEmail(email);
                superAdmin.setSenha(senhaHash);

                if (superAdminDAO.adicionarSuperAdmin(superAdmin) == 1){
                    session.removeAttribute(sessao_nome);
                    session.removeAttribute(sessao_email);
                    session.setAttribute("sucesso", "Conta criada com sucesso. Entre com o seu e-mail e a sua senha.");
                    resp.sendRedirect(req.getContextPath() + url_login);
                    break;
                }
                else {
                    voltaComErro(req, resp, "Erro ao criar a conta. Por favor tente novamente.", jsp_cadastro_senha);
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
