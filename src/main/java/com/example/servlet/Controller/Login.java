package com.example.servlet.Controller;

import com.example.servlet.Dao.AssinaturaDAO;
import com.example.servlet.Dao.EmpresaDAO;
import com.example.servlet.Dao.UsuarioDAO;
import com.example.servlet.Model.Empresa;
import com.example.servlet.Model.Usuario;
import com.example.servlet.Model.Assinatura;
import com.example.servlet.Util.Enum.TipoAssinatura;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet(urlPatterns = {
        // caminhos do usuario
        "/LoginUsuario",
        "/CriarUsuario",
        "/SenhaUsuario",

        // caminhos da empresa
        "/loginEmpresa",
        "/CriarEmpresa",
        "/SenhaEmpresa",

        // planos
        "/Planos",
        "/PlanoUsuario",
        "/PlanoEmpresa"
        },
        name = "/Login")
public class Login extends HttpServlet {
    private UsuarioDAO usuarioDAO;
    private EmpresaDAO empresaDAO;
    private AssinaturaDAO assinaturaDAO;

    @Override
    public void init(){
        usuarioDAO = new UsuarioDAO();
        empresaDAO = new EmpresaDAO();
        assinaturaDAO = new AssinaturaDAO();
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {


        // pegando o caminho do usuario
        String action = req.getParameter("action");

        // salvando as informações entre as requisições
        HttpSession session = req.getSession();

        // escolhe qual metodo chamar usando botões do web
        switch (action) {


            case "/Login": {
                String emailSelect = req.getParameter("email");
                String senhaSelect = req.getParameter("senha");

                Usuario usuarioSelect = usuarioDAO.buscarUsuario(emailSelect);
                if (usuarioSelect != null) {
                    if (senhaSelect.equals(usuarioSelect.getSenha())) {
                        session.setAttribute("usuarioEmail", usuarioSelect.getEmail());
                        resp.sendRedirect(req.getContextPath() + "");
                    } else {
                        resp.sendRedirect(req.getContextPath() + "/Login.jsp");
                    }
                    break;

                } else {
                    Empresa empresaSelect = empresaDAO.buscarEmpresa(emailSelect);

                    if (empresaSelect != null && senhaSelect.equals(empresaSelect.getSenha())) {
                        session.setAttribute("empresaEmail", empresaSelect.getEmail());
                        resp.sendRedirect(req.getContextPath() + "");
                    } else {
                        resp.sendRedirect(req.getContextPath() + "/Login.jsp");
                    }
                    break;
                }
            }



            case "/CriarUsuario": {
                // pegando os dados do fornmulario de cadastro
                String nomeCompleto = req.getParameter("nome");
                String emailUsuario = req.getParameter("email");
                String cpf = req.getParameter("cpf");


                // convertendo, formatando e validando os dados
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
                session.setAttribute("email", emailUsuario);

                resp.sendRedirect(req.getContextPath() + "/SenhaUsuario");
                break;
            }


            case "/SenhaUsuario": {


                // pegando do formulario
                String senhaUsuario = req.getParameter("senha");
                String senhaConfirmadaUsuario = req.getParameter("confirmarSenha");

                // pegando as informações salvas com o session
                String cpf = (String) session.getAttribute("cpf");
                String primeiroNome = (String) session.getAttribute("primeiroNome");
                String sobrenome = (String) session.getAttribute("sobrenome");
                String email = (String) session.getAttribute("email");

                if (!senhaUsuario.equals(senhaConfirmadaUsuario)) {
                    resp.sendRedirect(req.getContextPath() + "/SenhaUsuario");
                    break;
                }
                // criando o objeto usuario e usuario dao
                Usuario usuario = new Usuario(
                        cpf,
                        primeiroNome,
                        sobrenome,
                        email,
                        senhaUsuario
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

                    resp.sendRedirect(req.getContextPath() + "/PlanosUsuario.jsp");
                } else {
                    resp.sendRedirect(req.getContextPath() + "/SenhaUsuario.jsp");
                }
                break;
            }


            case "/PlanoUsuario": {
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
                int funcionouAssiUsu = assinaturaDAO.inserirAssinaturaUsuario(assinaturaUsuario);

                if (funcionouAssiUsu == 1) {
                    resp.sendRedirect(req.getContextPath() + "");
                } else {
                    resp.sendRedirect(req.getContextPath() + "/PlanoUsuario.jsp");
                }
                break;
            }


            case "/CriarEmpresa": {
                // pegando os dados do fornmulario de cadastro
                String nomeEmpresa = req.getParameter("nome");
                String emailEmpresa = req.getParameter("email");
                String cnpj = req.getParameter("cnpj");

                session.setAttribute("nomeEmpresa", nomeEmpresa);
                session.setAttribute("emailEmpresa", emailEmpresa);
                session.setAttribute("cnpj", cnpj);
                resp.sendRedirect(req.getContextPath() + "/SenhaEmpresa");
                break;
            }


            case "/SenhaEmpresa": {
                // pegando do formulario
                String senhaEmpresa = req.getParameter("senha");
                String senhaConfirmadaEmpresa = req.getParameter("confirmarSenha");

                // recuperando as informações da empresa
                String cnpj = (String) session.getAttribute("cnpj");
                String nome = (String) session.getAttribute("nomeEmpresa");
                String email = (String) session.getAttribute("emailEmpresa");
                if (!senhaEmpresa.equals(senhaConfirmadaEmpresa)) {
                   resp.sendRedirect(req.getContextPath() + "/SenhaEmpresa");
                   break;
                }
                Empresa empresa = new Empresa(
                        cnpj,
                        nome,
                        email,
                        senhaEmpresa
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
                    resp.sendRedirect(req.getContextPath() + "/PlanoEmpresa.jsp");
                } else {
                    resp.sendRedirect(req.getContextPath() + "/SenhaEmpresa.jsp");
                }
                break;
            }




            case "/PlanoEmpresa": {
                // pegando as informações do formulario
                TipoAssinatura plano = TipoAssinatura.valueOf(req.getParameter("plano"));

                // recuperando os dados da empresa
                String empresaEmail= (String) session.getAttribute("empresaEmail");
                // buscando no banco
                Empresa empresa = empresaDAO.buscarEmpresa(empresaEmail);

                Assinatura assinaturaEmpresa = new Assinatura(
                        empresa,
                        plano
                );
                int funcionouAssiEmpr = assinaturaDAO.inserirAssinaturaEmpresa(assinaturaEmpresa);
                if (funcionouAssiEmpr == 1) {
                    resp.sendRedirect(req.getContextPath() + "");
                } else {
                    resp.sendRedirect(req.getContextPath() + "/PlanoEmpresa.jsp");
                }
                break;
            }

            default:
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}