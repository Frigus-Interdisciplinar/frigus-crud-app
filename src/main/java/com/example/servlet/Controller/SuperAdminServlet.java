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
        "/admin/conta"},
        name = "conta")
public class SuperAdminServlet extends HttpServlet {
    private SuperAdminDAO superAdminDAO;
    private Validacao validar;

    // caminho para o servlet ( a conta aparece na tela de configuracoes )
    private static final String url_configuracoes = "/admin/configuracoes";

    @Override
    public void init(){
        superAdminDAO = new SuperAdminDAO();
        validar = new Validacao();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // alterar e deletar só por POST
        if (req.getParameter("action") != null){
            resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }
        // a conta é mostrada na tela de configuracoes
        resp.sendRedirect(req.getContextPath() + url_configuracoes);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession();
        String action = req.getParameter("action");

        if (action == null){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // a conta é sempre a de quem esta logado
        String emailLogado = (String) session.getAttribute("admin");
        SuperAdmin contaAntiga = superAdminDAO.buscarSuperAdmin(emailLogado);
        if (contaAntiga == null){
            // a conta nao existe mais, entao a sessao nao vale
            session.invalidate();
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        switch (action){
            case "alterar": {
                // pegando os dados do formulario
                String nome = req.getParameter("nome");
                String email = req.getParameter("email");

                if (nome == null || nome.isBlank()){
                    voltaComErro(req, resp, "O nome não pode ser vazio!!!");
                    return;
                }
                if (email == null || email.isBlank()){
                    voltaComErro(req, resp, "O email não pode ser vazio!!!");
                    return;
                }

                // validando
                boolean vNome = validar.validarNome(nome);
                boolean vEmail = validar.validarEmail(email);

                if (!vNome || nome.length() > 20){
                    voltaComErro(req, resp, "Nome inválido!!! Só letras e no máximo 20 caracteres.");
                    return;
                }
                if (!vEmail){
                    voltaComErro(req, resp, "E-mail inválido!!!");
                    return;
                }
                // se trocou o e-mail, o novo nao pode ser de outra conta
                SuperAdmin outraConta = superAdminDAO.buscarSuperAdmin(validar.normalizarEmail(email));
                if (outraConta != null && outraConta.getIdSuperAdmin() != contaAntiga.getIdSuperAdmin()){
                    voltaComErro(req, resp, "Esse e-mail já está cadastrado.");
                    return;
                }

                SuperAdmin contaNova = new SuperAdmin(
                        contaAntiga.getIdSuperAdmin(),
                        nome.trim(),
                        validar.normalizarEmail(email),
                        contaAntiga.getSenha()
                );

                if (superAdminDAO.alterarSuperAdmin(contaAntiga, contaNova) == 1){
                    // o e-mail da sessao acompanha o e-mail novo
                    session.setAttribute("admin", contaNova.getEmail());
                    session.setAttribute("sucesso", "Conta alterada com sucesso.");
                    resp.sendRedirect(req.getContextPath() + url_configuracoes);
                    return;
                }
                else {
                    voltaComErro(req, resp, "Erro ao alterar a conta. Por favor tente novamente.");
                    return;
                }
            }

            case "alterarSenha": {
                String senhaAtual = req.getParameter("senhaAtual");
                String senhaNova = req.getParameter("senhaNova");
                String senhaConfirma = req.getParameter("senhaConfirma");

                if (senhaAtual == null || senhaAtual.isBlank() || senhaNova == null || senhaNova.isBlank()){
                    voltaComErro(req, resp, "Preencha a senha atual e a senha nova.");
                    return;
                }
                // so troca a senha quem sabe a senha atual
                if (!Hash.verficarBcrypt(senhaAtual, contaAntiga.getSenha())){
                    voltaComErro(req, resp, "A senha atual está incorreta.");
                    return;
                }
                if (!senhaNova.equals(senhaConfirma)){
                    voltaComErro(req, resp, "A confirmação não é igual à senha nova.");
                    return;
                }
                if (!validar.validarSenha(senhaNova)){
                    voltaComErro(req, resp, "Senha inválida!!! Deve ter de 8 a 15 caracteres, uma letra maiúscula e um caractere especial.");
                    return;
                }

                SuperAdmin contaNova = new SuperAdmin(
                        contaAntiga.getIdSuperAdmin(),
                        contaAntiga.getNome(),
                        contaAntiga.getEmail(),
                        Hash.bcrypt(senhaNova)
                );
                if (superAdminDAO.alterarSuperAdmin(contaAntiga, contaNova) == 1){
                    session.setAttribute("sucesso", "Senha alterada com sucesso.");
                    resp.sendRedirect(req.getContextPath() + url_configuracoes);
                    return;
                }
                else {
                    voltaComErro(req, resp, "Erro ao alterar a senha. Por favor tente novamente.");
                    return;
                }
            }

            case "deletar": {
                if (superAdminDAO.deletarSuperAdmin(contaAntiga) == 1){
                    // sem conta nao tem sessao, volta para a pagina inicial
                    session.invalidate();
                    resp.sendRedirect(req.getContextPath() + "/index.html");
                    return;
                }
                else {
                    voltaComErro(req, resp, "Erro ao deletar a conta. Por favor tente novamente.");
                    return;
                }
            }
            default:
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    // volta para a tela de configuracoes com uma mensagem de erro ( o PainelServlet mostra a tela )
    private void voltaComErro(HttpServletRequest req, HttpServletResponse resp, String mensagem)
            throws ServletException, IOException{
        req.setAttribute("erro", mensagem);
        req.getRequestDispatcher(url_configuracoes).forward(req, resp);
    }
}
