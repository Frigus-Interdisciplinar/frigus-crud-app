package com.example.servlet.Controller;

import com.example.servlet.Dao.AssinaturaDAO;
import com.example.servlet.Dao.EmpresaDAO;
import com.example.servlet.Dao.ResumoDAO;
import com.example.servlet.Model.Assinatura;
import com.example.servlet.Model.Empresa;
import com.example.servlet.Util.Enum.TipoAssinatura;
import com.example.servlet.Util.Hash;
import com.example.servlet.Util.Paginacao;
import com.example.servlet.Util.Validacao;
import com.example.servlet.Util.Voltar;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import com.example.servlet.Util.Ordenar;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = {
        "/admin/empresas"},
        name = "empresas")
public class EmpresaServlet extends HttpServlet {
    private EmpresaDAO empresaDAO;
    private AssinaturaDAO assinaturaDAO;
    private ResumoDAO resumoDAO;
    private Validacao validar;

    // caminho das paginas
    private static final String jsp_empresas = "/WEB-INF/pages/empresas.jsp";
    private static final String jsp_empresas_form = "/WEB-INF/pages/empresas-form.jsp";

    // caminho para o servlet
    private static final String url_servlet = "/admin/empresas";

    @Override
    public void init(){
        empresaDAO = new EmpresaDAO();
        assinaturaDAO = new AssinaturaDAO();
        resumoDAO = new ResumoDAO();
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

        if (action == null){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        switch (action){
            case "inserir": {
                // pegando os dados do formulario ( o CNPJ pode vir com pontos, barra e traço, fica só com os numeros )
                String cnpj = req.getParameter("cnpj");
                if (cnpj != null){
                    cnpj = cnpj.replaceAll("\\D", "");
                }
                String nome = req.getParameter("nome");
                String email = req.getParameter("email");
                String senha = req.getParameter("senha");
                String planoTxt = req.getParameter("plano");

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
                if (!"COMERCIAL".equals(planoTxt)){
                    voltaComErro(req, resp, "Escolha o plano da empresa.");
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
                        validar.normalizarEmail(email),
                        senhaHash
                );

                if (empresaDAO.cadastrarEmpresa(empresa) != 1){
                    voltaComErro(req, resp, "Erro ao cadastrar a empresa. Esse cnpj já pode estar cadastrado.");
                    return;
                }

                // toda empresa nasce com um plano
                if (assinaturaDAO.inserirAssinatura(new Assinatura(empresa, TipoAssinatura.COMERCIAL)) != 1){
                    // se o plano nao foi salvo a empresa nao fica pela metade
                    empresaDAO.excluirEmpresa(empresa);
                    voltaComErro(req, resp, "Erro ao cadastrar o plano da empresa. Por favor tente novamente.");
                    return;
                }

                session.setAttribute("sucesso", "Empresa cadastrada com sucesso.");
                resp.sendRedirect(req.getContextPath() + url_servlet);
                return;
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
                        validar.normalizarEmail(email),
                        senhaHash
                );

                if (empresaDAO.alterarEmpresa(empresaAntiga, empresaNova) == 1){
                    session.setAttribute("sucesso", "Empresa alterada com sucesso.");
                    resp.sendRedirect(req.getContextPath() + url_servlet);
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
                    resp.sendRedirect(req.getContextPath() + url_servlet);
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

    // carrega a lista ( ja filtrada ), os totais de cada empresa e mostra a tela
    private void listar(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException{
        // filtros da tela
        String busca = req.getParameter("busca");
        // o filtro de CNPJ ignora pontos, barra e traço
        String filtroCnpj = req.getParameter("cnpj");
        String cnpjBuscado = filtroCnpj == null ? "" : filtroCnpj.replaceAll("\\D", "");
        String filtroPlano = req.getParameter("plano");

        List<Empresa> todas = empresaDAO.buscarTodasEmpresa();
        Map<String, String> planos = resumoDAO.buscarPlanosDasEmpresas();

        List<Empresa> empresas = new ArrayList<>();
        for (Empresa empresa : todas){
            String texto = (empresa.getNome() + " " + empresa.getEmail() + " " + empresa.getCnpj()).toLowerCase();
            boolean achouBusca = busca == null || busca.isBlank() || texto.contains(busca.trim().toLowerCase());
            boolean achouPlano = filtroPlano == null || filtroPlano.isBlank() || filtroPlano.equals(planos.get(empresa.getCnpj()));
            boolean achouCnpj = cnpjBuscado.isEmpty() || empresa.getCnpj().contains(cnpjBuscado);
            if (achouBusca && achouPlano && achouCnpj){
                empresas.add(empresa);
            }
        }

        // ver os detalhes ou editar uma empresa ( vem no link da linha )
        String verCnpj = req.getParameter("ver");
        String editarCnpj = req.getParameter("editar");
        if (verCnpj != null && !verCnpj.isBlank()){
            req.setAttribute("registro", empresaDAO.buscarEmpresaPorCnpj(verCnpj.trim()));
            req.setAttribute("modo", "ver");
        }
        else if (editarCnpj != null && !editarCnpj.isBlank()){
            req.setAttribute("registro", empresaDAO.buscarEmpresaPorCnpj(editarCnpj.trim()));
            req.setAttribute("modo", "editar");
        }

        // ordem da lista ( vem do select "Ordenar por" )
        Map<String, Comparator<Empresa>> ordens = new LinkedHashMap<>();
        Ordenar.campo(ordens, "nome", (a, b) -> Ordenar.texto(a.getNome(), b.getNome()));
        Ordenar.campo(ordens, "novo", Comparator.comparing(Empresa::getDtCadastro, Comparator.nullsFirst(Comparator.naturalOrder())));
        req.setAttribute("filtroOrd", Ordenar.ordenar(empresas, req.getParameter("ord"), "nome:asc", ordens));

        req.setAttribute("empresas", Paginacao.aplicar(req, empresas));
        req.setAttribute("totalGeral", todas.size());
        req.setAttribute("filtroBusca", busca == null ? "" : busca);
        req.setAttribute("filtroCnpj", filtroCnpj == null ? "" : filtroCnpj);
        req.setAttribute("filtroPlano", filtroPlano == null ? "" : filtroPlano);
        req.setAttribute("planosDasEmpresas", planos);
        req.setAttribute("frigorificosPorEmpresa", resumoDAO.contarFrigorificosPorEmpresa());
        req.setAttribute("receitasPorEmpresa", resumoDAO.contarReceitasPorEmpresa());
        if (req.getParameter("novo") != null && req.getAttribute("modo") == null){
            req.setAttribute("modo", "novo");
        }
        req.getRequestDispatcher(req.getAttribute("modo") != null ? jsp_empresas_form : jsp_empresas).forward(req, resp);
    }

    // volta para a tela do formulario com uma mensagem de erro
    private void voltaComErro(HttpServletRequest req, HttpServletResponse resp, String mensagem)
            throws ServletException, IOException{
        req.getSession().setAttribute("erro", mensagem);
        resp.sendRedirect(Voltar.destino(req, url_servlet));
    }
}
