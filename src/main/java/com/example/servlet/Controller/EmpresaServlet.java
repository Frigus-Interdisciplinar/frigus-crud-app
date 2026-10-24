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
import java.util.List;

@WebServlet(urlPatterns = {
        "/admin/empresas"},
        name = "empresas")
public class EmpresaServlet extends HttpServlet {
    private EmpresaDAO empresaDAO;
    private Validacao validar;

    // caminho das paginas
    private static final String jsp_empresas = "/WEB-INF/pages/empresas.jsp";

    // caminho para o servlet
    private static final String url_servlet = "/admin/empresas";

    @Override
    public void init(){
        empresaDAO = new EmpresaDAO();
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

        switch (req.getServletPath()){
            case "/admin/empresas": {
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
                String cnpj = req.getParameter("cnpj");
                String nome = req.getParameter("nome");
                String email = req.getParameter("email");
                String senha = req.getParameter("senha");

                if(cnpj == null || cnpj.isBlank()){
                    voltaComErro(req, resp, "O cnpj não pode ser vazio!!!");
                    return;
                }
                if(nome == null || nome.isBlank()){
                    voltaComErro(req, resp, "O nome não pode ser vazio!!!");
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

                // formatando o cnpj
                String cnpjPuro = validar.formatarCnpj(cnpj.trim());

                // validando
                boolean vCnpj = validar.validarCnpj(cnpjPuro);
                boolean vNome = validar.validarNomeProduto(nome);
                boolean vEmail = validar.validarEmail(email);
                boolean vSenha = validar.validarSenha(senha);

                if (!vCnpj){
                    voltaComErro(req, resp, "Cnpj inválido!!!");
                    return;
                }
                if (!vNome || nome.length() > 80){
                    voltaComErro(req, resp, "Nome inválido!!! No máximo 80 caracteres.");
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

                Empresa empresa = new Empresa(
                        cnpjPuro,
                        nome.trim(),
                        email.trim(),
                        senhaHash
                );

                if (empresaDAO.cadastrarEmpresa(empresa) == 1){
                    session.setAttribute("sucesso", "Empresa cadastrada com sucesso.");
                    resp.sendRedirect(req.getContextPath() + retorno);
                    return;
                }
                else {
                    voltaComErro(req, resp, "Erro ao cadastrar a empresa. Esse cnpj já pode estar cadastrado.");
                    return;
                }
            }


            case "alterar": {
                // pegando as informações do formulario
                String cnpj = req.getParameter("cnpj");
                String nome = req.getParameter("nome");
                String email = req.getParameter("email");
                // a senha é opcional: vazia mantem a senha que ja esta no banco
                String senha = req.getParameter("senha");

                if(cnpj == null || cnpj.isBlank()){
                    voltaComErro(req, resp, "O cnpj não pode ser vazio!!!");
                    return;
                }
                if(nome == null || nome.isBlank()){
                    voltaComErro(req, resp, "O nome não pode ser vazio!!!");
                    return;
                }
                if(email == null || email.isBlank()){
                    voltaComErro(req, resp, "O email não pode ser vazio!!!");
                    return;
                }

                // validando
                boolean vNome = validar.validarNomeProduto(nome);
                boolean vEmail = validar.validarEmail(email);

                if (!vNome || nome.length() > 80){
                    voltaComErro(req, resp, "Nome inválido!!! No máximo 80 caracteres.");
                    return;
                }
                if (!vEmail){
                    voltaComErro(req, resp, "E-mail inválido!!!");
                    return;
                }

                // criando a empresa que esta no banco
                Empresa empresaAntiga = empresaDAO.buscarEmpresaPorCnpj(validar.formatarCnpj(cnpj.trim()));
                if (empresaAntiga == null){
                    voltaComErro(req, resp, "Empresa não encontrada.");
                    return;
                }

                String senhaHash = empresaAntiga.getSenha();
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
                Empresa empresaNova = new Empresa(
                        empresaAntiga.getCnpj(),
                        nome.trim(),
                        email.trim(),
                        senhaHash
                );

                if (empresaDAO.alterarEmpresa(empresaAntiga, empresaNova) == 1){
                    session.setAttribute("sucesso", "Empresa alterada com sucesso.");
                    resp.sendRedirect(req.getContextPath() + retorno);
                    return;
                }
                else {
                    voltaComErro(req, resp, "Erro ao alterar a empresa. Por favor tente novamente.");
                    return;
                }
            }


            case "deletar": {
                // pegando o cnpj do formulario
                String cnpj = req.getParameter("cnpj");
                if(cnpj == null || cnpj.isBlank()){
                    voltaComErro(req, resp, "Erro ao deletar a empresa. Por favor tente novamente.");
                    return;
                }

                // criando a empresa usando o cnpj
                Empresa empresa = empresaDAO.buscarEmpresaPorCnpj(validar.formatarCnpj(cnpj.trim()));
                if (empresa == null){
                    voltaComErro(req, resp, "Empresa não encontrada.");
                    return;
                }

                if (empresaDAO.excluirEmpresa(empresa) == 1){
                    session.setAttribute("sucesso", "Empresa apagada com sucesso, junto com tudo que era dela.");
                    resp.sendRedirect(req.getContextPath() + retorno);
                    return;
                }
                else {
                    voltaComErro(req, resp, "Erro ao deletar a empresa. Por favor tente novamente.");
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
        List<Empresa> empresas = empresaDAO.buscarTodasEmpresa();
        req.setAttribute("empresas", empresas);
        req.getRequestDispatcher(jsp_empresas).forward(req, resp);
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
