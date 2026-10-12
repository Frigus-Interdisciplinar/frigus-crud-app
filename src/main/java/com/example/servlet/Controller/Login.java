package com.example.servlet.Controller;

import com.example.servlet.Dao.EmpresaDAO;
import com.example.servlet.Dao.UsuarioDAO;
import com.example.servlet.Model.Empresa;
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

@WebServlet("/login")
public class Login extends HttpServlet {
    private UsuarioDAO usuarioDAO;
    private EmpresaDAO empresaDAO;
    private Validacao validar;

    // camihos das paginas
    private static final String jsp_login = "/assets/pages/login.jsp";
    private static final String jsp_visaoGeralUsu = "/assets/pages/visao-geral-geladeira.jsp";
    private static final String jsp_visaoGeralEmp = "/assets/pages/visao-geral-frigorifico.jsp";



    @Override
    public void init(){
        usuarioDAO = new UsuarioDAO();
        empresaDAO = new EmpresaDAO();
        validar = new Validacao();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // salvando as informações entre as requisições
        HttpSession session = req.getSession();

        // pegando os dados do formulario
        String emailSelect = req.getParameter("email");
        String senhaSelect = req.getParameter("senha");

        // validando eles
        boolean vEmail = validar.validarEmail(emailSelect);
        boolean vSenha = validar.validarSenha(senhaSelect);

        if (!vEmail || !vSenha){
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }
        // criando o objeto usuario
        Usuario usuarioSelect = usuarioDAO.buscarUsuario(emailSelect);

        if (usuarioSelect != null) {
            // vendo se a senha digitada é a mesma da armazenada no banco
            boolean senhaIgualBanco = Hash.verficarBcrypt(senhaSelect, usuarioSelect.getSenha());
            if (!senhaIgualBanco){
                resp.sendRedirect(req.getContextPath() + jsp_login);
                return;
            }
            session.setAttribute("usuarioEmail", usuarioSelect.getEmail());
            resp.sendRedirect(req.getContextPath() + jsp_visaoGeralUsu);

        } else {
            Empresa empresaSelect = empresaDAO.buscarEmpresa(emailSelect);

            if (empresaSelect != null) {
                // vendo se a senha digitada é a mesma da armazenada no banco
                boolean senhaIgualBanco = Hash.verficarBcrypt(senhaSelect, empresaSelect.getSenha());
                if (!senhaIgualBanco){
                    resp.sendRedirect(req.getContextPath() + jsp_login);
                    return;
                }
                session.setAttribute("empresaEmail", empresaSelect.getEmail());
                resp.sendRedirect(req.getContextPath() + jsp_visaoGeralEmp);
            } else {
                resp.sendRedirect(req.getContextPath() + jsp_login);
            }
        }
    }
}