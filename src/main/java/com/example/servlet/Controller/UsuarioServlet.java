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

@WebServlet(urlPatterns = {
        "/tela-padrao",
        "/criar-usuario",
        "/senha-usuario",
        "/alterar-usuario",
        "/alterar-email-usuario",
        "/alterar-senha-usuario",
        "/deletar-usuario"},
        name = "/criarUsuario")
public class UsuarioServlet extends HttpServlet {
    private UsuarioDAO usuarioDAO;
    private HttpSession session;
    private Validacao validar;

    @Override
    public void init(){
        usuarioDAO = new UsuarioDAO();
        validar = new Validacao();
    }

//
//    @Override
//    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
//    throws ServletException, IOException{
//        // pegando o caminho do usuario
//        String action = req.getParameter("action");
//
//        switch (action){
//            case "/tela-padrao":{
//                String email = (String) session.getAttribute("usuarioEmail");
//                Usuario usuario = usuarioDAO.buscarUsuario(email);
//                resp.getWriter().println(usuario);
//            }
//
//        }
//    }



    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // pegando o caminho do usuario
        String action = req.getParameter("action");
        // salvando as informações entre as requisições
        session = req.getSession();

        switch (action){
            case "/criar-usuario": {
                // pegando os dados do fornmulario de cadastro
                String nomeCompleto = req.getParameter("nome");
                String email = req.getParameter("email");
                String cpf = req.getParameter("cpf");

                // formatando o cpf
                String cpfPuro = validar.formatarCpf(cpf);

                // validando
                boolean vCpf = validar.validarCpf(cpfPuro);
                boolean vnome = validar.validarNome(nomeCompleto);
                boolean vEmail = validar.validarEmail(email);

                if ( !vCpf || !vnome || !vEmail ){
                    resp.sendRedirect(req.getContextPath() + "/criar-usuario.jsp");
                    break;
                }


                // convertendo e formatando os dados
                String[] nomeUsuario = nomeCompleto.split(" ");
                String primeiroNome = nomeUsuario[0];
                String sobrenome = "";
                for (int i = 1; i < nomeUsuario.length; i++) {
                    sobrenome += nomeUsuario[i] + " ";
                }
                sobrenome = sobrenome.trim();

                // salvando as informações na session
                session.setAttribute("cpf", cpf);
                session.setAttribute("primeiroNome", primeiroNome);
                session.setAttribute("sobrenome", sobrenome);
                session.setAttribute("email", email);

                resp.sendRedirect(req.getContextPath() + "/senha-usuario.jsp");
                break;
            }



            case "/senha-usuario": {
                // pegando do formulario
                String senha = req.getParameter("senha");
                String senhaConfirmada = req.getParameter("confirmarSenha");

                // pegando as informações salvas com o session
                String cpf = (String) session.getAttribute("cpf");
                String primeiroNome = (String) session.getAttribute("primeiroNome");
                String sobrenome = (String) session.getAttribute("sobrenome");
                String email = (String) session.getAttribute("email");

                if (!senha.equals(senhaConfirmada)) {
                    resp.sendRedirect(req.getContextPath() + "/senha-usuario.jsp");
                    break;
                }

                String senhaHash = Hash.bcrypt(senha);

                // criando o objeto usuario e usuario dao
                Usuario usuario = new Usuario(
                        cpf,
                        primeiroNome,
                        sobrenome,
                        email,
                        senhaHash
                );


                int funcionou = usuarioDAO.adicionarUsuario(usuario);

                if (funcionou == 1) {
                    // salvando o usuario
                    session.setAttribute("usuarioEmail", usuario.getEmail());

                    // deletando as informações temporarias
                    session.removeAttribute("cpf");
                    session.removeAttribute("primeiroNome");
                    session.removeAttribute("sobrenome");
                    session.removeAttribute("email");

                    resp.sendRedirect(req.getContextPath() + "/criar-plano-usuario.jsp");
                } else {
                    resp.sendRedirect(req.getContextPath() + "/senha-usuario.jsp");
                }
                break;
            }


            case "/alterar-usuario":{
                // resgatando o email da ultima requisição
                String emailResgatado = (String) session.getAttribute("usuarioEmail");

                // pegando as informações do formulario
                String nomeCompleto = req.getParameter("nome");
                String email = req.getParameter("email");
                String senha = req.getParameter("senha");


                // validando
                boolean vNome = validar.validarNome(nomeCompleto);
                boolean vEmail = validar.validarEmail(email);
                boolean vSenha = validar.validarSenha(senha);

                if (!vNome || !vEmail || !vSenha){
                    resp.sendRedirect(req.getContextPath() + "/alterar-usuario.jsp");
                    break;
                }

                // convertendo e formatando os dados
                String[] nomeUsuario = nomeCompleto.split(" ");
                String primeiroNome = nomeUsuario[0];
                String sobrenome = "";
                for (int i = 1; i < nomeUsuario.length; i++) {
                    sobrenome += nomeUsuario[i] + " ";
                }
                sobrenome = sobrenome.trim();


                // hasheando a senha
                String senhaHash = Hash.bcrypt(senha);

                // criando o usuario que esta no banco
                Usuario usuarioBanco = usuarioDAO.buscarUsuario(emailResgatado);

                // alterações
                Usuario usuarioAlterado = new Usuario(
                        usuarioBanco.getCpf(),
                        primeiroNome,
                        sobrenome,
                        email,
                        senhaHash
                );

                int funcionou = usuarioDAO.alterarUsuario(usuarioBanco, usuarioAlterado);

                if (funcionou == 1){
                    session.setAttribute("usuarioEmail", usuarioAlterado.getEmail());
                    resp.sendRedirect(req.getContextPath() + "/tela-padrao.jsp");
                }
                else {
                    resp.sendRedirect(req.getContextPath() + "/alterar-usuario.jsp");
                }
                break;
            }

            case "/alterar-email-usuario":{
                // resgatando o email do session
                String email = (String) session.getAttribute("usuarioEmail");

                // pegando o email do formulario
                String emailNovo = req.getParameter("email");

                // validando
                boolean vEmail = validar.validarEmail(emailNovo);

                if( !vEmail){
                    resp.sendRedirect(req.getContextPath() + "/alterar-email-usuario.jsp");
                    break;
                }

                // resgatando o usuario do banco
                Usuario usuarioBanco = usuarioDAO.buscarUsuario(email);

                int funcionou = usuarioDAO.alterarEmailUsuario(usuarioBanco, emailNovo);

                if (funcionou == 1){
                    session.setAttribute("usuarioEmail", emailNovo);
                    resp.sendRedirect(req.getContextPath() + "tela-padrao.jsp");
                }
                else {
                    resp.sendRedirect(req.getContextPath() + "/alterar-email-usuario.jsp");
                }
                break;
            }



            case "/alterar-senha-usuario":{
                // resgatando o email do session
                String email = (String) session.getAttribute("usuarioEmail");

                // pegando a senha do formulario
                String senhaNova = req.getParameter("senha");

                // validando
                boolean vSenha = validar.validarSenha(senhaNova);

                if( !vSenha ){
                    resp.sendRedirect(req.getContextPath() + "/alterar-senha-usuario.jsp");
                    break;
                }
                // hasheando a senha
                String senhaHash = Hash.bcrypt(senhaNova);

                // resgatando o usuario do banco
                Usuario usuarioBanco = usuarioDAO.buscarUsuario(email);

                int funcionou = usuarioDAO.alterarSenhaUsuario(usuarioBanco, senhaHash);

                if (funcionou == 1){
                    resp.sendRedirect(req.getContextPath() + "tela-padrao.jsp");
                }
                else {
                    resp.sendRedirect(req.getContextPath() + "/alterar-senha-usuario.jsp");
                }
                break;
            }


            case "/deletar-usuario":{
                // pegando o email do formulario
                String email = req.getParameter("email");

                // validando
                boolean vEmail = validar.validarEmail(email);
                if ( !vEmail ){
                    resp.sendRedirect(req.getContextPath() + "/deletar-usuario.jsp");
                }

                // criando o usuario usando o email
                Usuario usuario = usuarioDAO.buscarUsuario(email);

                int funcionou = usuarioDAO.deletarUsuario(usuario);

                if (funcionou == 1){
                    resp.sendRedirect(req.getContextPath() + "/tela-padrao.jsp");
                }
                else {
                    resp.sendRedirect(req.getContextPath() + "/deletar-usuario.jsp");
                }
                break;
            }


            default:
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}