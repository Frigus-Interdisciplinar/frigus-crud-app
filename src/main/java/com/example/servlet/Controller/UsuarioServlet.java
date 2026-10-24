package com.example.servlet.Controller;

import com.example.servlet.Dao.UsuarioDAO;
import com.example.servlet.Model.Usuario;
import com.example.servlet.Util.Hash;
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
        "/admin/usuarios"},
        name = "usuarios")
public class UsuarioServlet extends HttpServlet {
    private UsuarioDAO usuarioDAO;
    private Validacao validar;

    // caminho das paginas
    private static final String jsp_usuarios = "/WEB-INF/pages/usuarios.jsp";

    // caminho para o servlet
    private static final String url_servlet = "/admin/usuarios";

    @Override
    public void init(){
        usuarioDAO = new UsuarioDAO();
        validar = new Validacao();
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException{
        // inserir, alterar e deletar só por POST
        if (req.getParameter("action") != null){
            resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }
        String path = req.getServletPath();

        switch (path){
            case "/admin/usuarios": {
                listar(req, resp);
                break;
            }
            default:
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }



    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession();
        String action = req.getParameter("action");
        // para onde voltar depois de dar certo
        String retorno = validar.definirRetorno(req.getParameter("retorno"), url_servlet);

        if (action == null){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        switch (action){
            case "inserir": {
                // pegando os dados do formulario
                String cpf = req.getParameter("cpf");
                String nome = req.getParameter("nome");
                String sobrenome = req.getParameter("sobrenome");
                String email = req.getParameter("email");
                String senha = req.getParameter("senha");

                if(cpf == null || cpf.isBlank()){
                    voltaComErro(req, resp, "O cpf não pode ser vazio!!!");
                    return;
                }
                if(nome == null || nome.isBlank()){
                    voltaComErro(req, resp, "O nome não pode ser vazio!!!");
                    return;
                }
                if(sobrenome == null || sobrenome.isBlank()){
                    voltaComErro(req, resp, "O sobrenome não pode ser vazio!!!");
                    return;
                }
                if(email == null || email.isBlank()){
                    voltaComErro(req, resp, "O email não pode ser vazio!!!");
                    return;
                }
                if(senha == null || senha.isBlank()){
                    voltaComErro(req, resp, "A senha não pode ser vazia!!!");
                    return;
                }

                // formatando o cpf
                String cpfPuro = validar.formatarCpf(cpf.trim());

                // validando
                boolean vCpf = validar.validarCpf(cpfPuro);
                boolean vNome = validar.validarNome(nome);
                boolean vSobrenome = validar.validarNome(sobrenome);
                boolean vEmail = validar.validarEmail(email);
                boolean vSenha = validar.validarSenha(senha);

                if (!vCpf){
                    voltaComErro(req, resp, "Cpf inválido!!!");
                    return;
                }
                if (!vNome || nome.length() > 20){
                    voltaComErro(req, resp, "Nome inválido!!! Só letras e no máximo 20 caracteres.");
                    return;
                }
                if (!vSobrenome || sobrenome.length() > 60){
                    voltaComErro(req, resp, "Sobrenome inválido!!! Só letras e no máximo 60 caracteres.");
                    return;
                }
                if (!vEmail){
                    voltaComErro(req, resp, "E-mail inválido!!!");
                    return;
                }
                if (!vSenha){
                    voltaComErro(req, resp, "Senha inválida!!! Deve ter de 8 a 15 caracteres, uma letra maiúscula e um caractere especial.");
                    return;
                }

                // hasheando a senha
                String senhaHash = Hash.bcrypt(senha);

                Usuario usuario = new Usuario(
                        cpfPuro,
                        nome.trim(),
                        sobrenome.trim(),
                        email.trim(),
                        senhaHash
                );

                if (usuarioDAO.adicionarUsuario(usuario) == 1){
                    session.setAttribute("sucesso", "Usuário cadastrado com sucesso.");
                    resp.sendRedirect(req.getContextPath() + retorno);
                    return;
                }
                else {
                    voltaComErro(req, resp, "Erro ao cadastrar o usuário. Esse cpf ou e-mail já pode estar cadastrado.");
                    return;
                }
            }


            case "alterar": {
                // pegando as informações do formulario
                String cpf = req.getParameter("cpf");
                String nome = req.getParameter("nome");
                String sobrenome = req.getParameter("sobrenome");
                String email = req.getParameter("email");
                // a senha é opcional: vazia mantem a senha que ja esta no banco
                String senha = req.getParameter("senha");

                if(cpf == null || cpf.isBlank()){
                    voltaComErro(req, resp, "O cpf não pode ser vazio!!!");
                    return;
                }
                if(nome == null || nome.isBlank()){
                    voltaComErro(req, resp, "O nome não pode ser vazio!!!");
                    return;
                }
                if(sobrenome == null || sobrenome.isBlank()){
                    voltaComErro(req, resp, "O sobrenome não pode ser vazio!!!");
                    return;
                }
                if(email == null || email.isBlank()){
                    voltaComErro(req, resp, "O email não pode ser vazio!!!");
                    return;
                }

                // validando
                boolean vNome = validar.validarNome(nome);
                boolean vSobrenome = validar.validarNome(sobrenome);
                boolean vEmail = validar.validarEmail(email);

                if (!vNome || nome.length() > 20){
                    voltaComErro(req, resp, "Nome inválido!!! Só letras e no máximo 20 caracteres.");
                    return;
                }
                if (!vSobrenome || sobrenome.length() > 60){
                    voltaComErro(req, resp, "Sobrenome inválido!!! Só letras e no máximo 60 caracteres.");
                    return;
                }
                if (!vEmail){
                    voltaComErro(req, resp, "E-mail inválido!!!");
                    return;
                }

                // criando o usuario que esta no banco
                Usuario usuarioAntigo = usuarioDAO.buscarUsuarioPorCpf(validar.formatarCpf(cpf.trim()));
                if (usuarioAntigo == null){
                    voltaComErro(req, resp, "Usuário não encontrado.");
                    return;
                }

                String senhaHash = usuarioAntigo.getSenha();
                if (senha != null && !senha.isBlank()){
                    boolean vSenha = validar.validarSenha(senha);
                    if (!vSenha){
                        voltaComErro(req, resp, "Senha inválida!!! Deve ter de 8 a 15 caracteres, uma letra maiúscula e um caractere especial.");
                        return;
                    }
                    // hasheando a senha
                    senhaHash = Hash.bcrypt(senha);
                }

                // alterações
                Usuario usuarioNovo = new Usuario(
                        usuarioAntigo.getCpf(),
                        nome.trim(),
                        sobrenome.trim(),
                        email.trim(),
                        senhaHash
                );

                if (usuarioDAO.alterarUsuario(usuarioAntigo, usuarioNovo) == 1){
                    session.setAttribute("sucesso", "Usuário alterado com sucesso.");
                    resp.sendRedirect(req.getContextPath() + retorno);
                    return;
                }
                else {
                    voltaComErro(req, resp, "Erro ao alterar o usuário. Esse e-mail já pode estar cadastrado.");
                    return;
                }
            }


            case "deletar": {
                // pegando o cpf do formulario
                String cpf = req.getParameter("cpf");
                if(cpf == null || cpf.isBlank()){
                    voltaComErro(req, resp, "Erro ao deletar o usuário. Por favor tente novamente.");
                    return;
                }

                // criando o usuario usando o cpf
                Usuario usuario = usuarioDAO.buscarUsuarioPorCpf(validar.formatarCpf(cpf.trim()));
                if (usuario == null){
                    voltaComErro(req, resp, "Usuário não encontrado.");
                    return;
                }

                if (usuarioDAO.deletarUsuario(usuario) == 1){
                    session.setAttribute("sucesso", "Usuário apagado com sucesso, junto com tudo que era dele.");
                    resp.sendRedirect(req.getContextPath() + retorno);
                    return;
                }
                else {
                    voltaComErro(req, resp, "Erro ao deletar o usuário. Por favor tente novamente.");
                    return;
                }
            }
            default:
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    // carrega a lista e mostra a tela
    private void listar(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException{
        List<Usuario> usuarios = usuarioDAO.buscarTodosUsuario();
        req.setAttribute("usuarios", usuarios);
        req.getRequestDispatcher(jsp_usuarios).forward(req, resp);
    }

    // volta para a tela de onde veio com uma mensagem de erro ( a tela de teste ou a propria tela )
    private void voltaComErro(HttpServletRequest req, HttpServletResponse resp, String mensagem)
            throws ServletException, IOException{
        req.setAttribute("erro", mensagem);
        String retorno = validar.definirRetorno(req.getParameter("retorno"), url_servlet);
        if (retorno.equals(url_servlet)){
            listar(req, resp);
        }
        else {
            req.getRequestDispatcher(retorno).forward(req, resp);
        }
    }
}
