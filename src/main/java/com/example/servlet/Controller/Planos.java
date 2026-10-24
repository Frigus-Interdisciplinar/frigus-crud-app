package com.example.servlet.Controller;

import com.example.servlet.Dao.AssinaturaDAO;
import com.example.servlet.Dao.EmpresaDAO;
import com.example.servlet.Dao.UsuarioDAO;
import com.example.servlet.Model.Assinatura;
import com.example.servlet.Model.Empresa;
import com.example.servlet.Model.Usuario;
import com.example.servlet.Util.Enum.TipoAssinatura;
import com.example.servlet.Util.Paginacao;
import com.example.servlet.Util.Validacao;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(urlPatterns = {
        "/admin/planos"},
        name = "planos")
public class Planos extends HttpServlet {
    private AssinaturaDAO assinaturaDAO;
    private UsuarioDAO usuarioDAO;
    private EmpresaDAO empresaDAO;
    private Validacao validar;

    // caminho das paginas
    private static final String jsp_planos = "/WEB-INF/pages/planos.jsp";

    // caminho para o servlet
    private static final String url_servlet = "/admin/planos";


    @Override
    public void init(){
        assinaturaDAO = new AssinaturaDAO();
        usuarioDAO = new UsuarioDAO();
        empresaDAO =  new EmpresaDAO();
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
            case "/admin/planos":{
                listar(req, resp);
                break;
            }
            default:
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                break;
        }
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        HttpSession session = req.getSession();

        if (action == null){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        switch (action){
            case "inserir": {
                // pegando as informações do formulario
                String planoTxt = req.getParameter("plano");
                String cpf = req.getParameter("cpf");
                String cnpj = req.getParameter("cnpj");

                // o dono é o usuario ou a empresa
                if (!validar.validarDono(cpf, cnpj)){
                    voltaComErro(req, resp, "Escolha só o usuário ou só a empresa dona da assinatura.");
                    return;
                }
                TipoAssinatura plano;
                try {
                    plano = TipoAssinatura.valueOf(planoTxt.trim().toUpperCase());
                } catch (IllegalArgumentException | NullPointerException e) {
                    voltaComErro(req, resp, "Plano inválido.");
                    return;
                }

                Assinatura assinatura;
                if (cpf != null && !cpf.isBlank()){
                    Usuario usuario = usuarioDAO.buscarUsuarioPorCpf(cpf.trim());
                    if (usuario == null){
                        voltaComErro(req, resp, "Usuário não encontrado.");
                        return;
                    }
                    // cada usuario so pode ter uma assinatura
                    if (assinaturaDAO.buscarAssinaturaUsuario(usuario.getCpf()) != null){
                        voltaComErro(req, resp, "Esse usuário já tem uma assinatura.");
                        return;
                    }
                    assinatura = new Assinatura(usuario, plano);
                }
                else {
                    Empresa empresa = empresaDAO.buscarEmpresaPorCnpj(cnpj.trim());
                    if (empresa == null){
                        voltaComErro(req, resp, "Empresa não encontrada.");
                        return;
                    }
                    // cada empresa so pode ter uma assinatura
                    if (assinaturaDAO.buscarAssinaturaEmpresa(empresa.getCnpj()) != null){
                        voltaComErro(req, resp, "Essa empresa já tem uma assinatura.");
                        return;
                    }
                    assinatura = new Assinatura(empresa, plano);
                }

                if (assinaturaDAO.inserirAssinatura(assinatura) == 1) {
                    session.setAttribute("sucesso", "Assinatura cadastrada com sucesso.");
                    resp.sendRedirect(req.getContextPath() + url_servlet);
                    return;
                } else {
                    voltaComErro(req, resp, "Erro ao cadastrar a assinatura. Por favor tente novamente.");
                    return;
                }
            }

            case "alterar":{
                // pegando as informações do formulario
                String idTxt = req.getParameter("idAssinatura");
                String planoTxt = req.getParameter("plano");
                String cpf = req.getParameter("cpf");
                String cnpj = req.getParameter("cnpj");

                int id;
                try{
                    id = Integer.parseInt(idTxt.trim());
                } catch (NumberFormatException | NullPointerException e) {
                    voltaComErro(req, resp, "Erro ao alterar essa assinatura. Por favor tente novamente.");
                    return;
                }
                // buscando a assinatura antiga no banco
                Assinatura assinaturaAntiga = assinaturaDAO.buscarAssinaturaPorId(id);
                if (assinaturaAntiga == null){
                    voltaComErro(req, resp, "Assinatura não encontrada.");
                    return;
                }

                // o dono é o usuario ou a empresa
                if (!validar.validarDono(cpf, cnpj)){
                    voltaComErro(req, resp, "Escolha só o usuário ou só a empresa dona da assinatura.");
                    return;
                }
                TipoAssinatura plano;
                try {
                    plano = TipoAssinatura.valueOf(planoTxt.trim().toUpperCase());
                } catch (IllegalArgumentException | NullPointerException e) {
                    voltaComErro(req, resp, "Plano inválido.");
                    return;
                }

                // o valor e a descrição vêm do plano escolhido
                Assinatura assinaturaNova;
                if (cpf != null && !cpf.isBlank()){
                    Usuario usuario = usuarioDAO.buscarUsuarioPorCpf(cpf.trim());
                    if (usuario == null){
                        voltaComErro(req, resp, "Usuário não encontrado.");
                        return;
                    }
                    // se trocou o dono, o novo dono não pode ter assinatura
                    Assinatura jaTem = assinaturaDAO.buscarAssinaturaUsuario(usuario.getCpf());
                    if (jaTem != null && jaTem.getIdAssinatura() != assinaturaAntiga.getIdAssinatura()){
                        voltaComErro(req, resp, "Esse usuário já tem uma assinatura.");
                        return;
                    }
                    assinaturaNova = new Assinatura(usuario, plano);
                }
                else {
                    Empresa empresa = empresaDAO.buscarEmpresaPorCnpj(cnpj.trim());
                    if (empresa == null){
                        voltaComErro(req, resp, "Empresa não encontrada.");
                        return;
                    }
                    // se trocou o dono, o novo dono não pode ter assinatura
                    Assinatura jaTem = assinaturaDAO.buscarAssinaturaEmpresa(empresa.getCnpj());
                    if (jaTem != null && jaTem.getIdAssinatura() != assinaturaAntiga.getIdAssinatura()){
                        voltaComErro(req, resp, "Essa empresa já tem uma assinatura.");
                        return;
                    }
                    assinaturaNova = new Assinatura(empresa, plano);
                }

                int funcionou = assinaturaDAO.alterarAssinatura(assinaturaAntiga, assinaturaNova);
                if (funcionou == 1){
                    session.setAttribute("sucesso", "Assinatura alterada com sucesso.");
                    resp.sendRedirect(req.getContextPath() + url_servlet);
                    return;
                }
                else {
                    voltaComErro(req, resp, "Erro ao alterar a assinatura. Por favor tente novamente.");
                    return;
                }
            }


            case "deletar":{
                String idTxt = req.getParameter("idAssinatura");
                int id;
                try{
                    id = Integer.parseInt(idTxt.trim());
                } catch (NumberFormatException | NullPointerException e) {
                    voltaComErro(req, resp, "Erro ao deletar essa assinatura. Por favor tente novamente.");
                    return;
                }

                Assinatura assinatura = assinaturaDAO.buscarAssinaturaPorId(id);
                if (assinatura == null){
                    voltaComErro(req, resp, "Assinatura não encontrada.");
                    return;
                }

                int funcionou = assinaturaDAO.deletarAssinatura(assinatura);
                if(funcionou == 1){
                    session.setAttribute("sucesso", "Assinatura apagada com sucesso.");
                    resp.sendRedirect(req.getContextPath() + url_servlet);
                    return;
                }
                else {
                    voltaComErro(req, resp, "Erro ao deletar a assinatura. Por favor tente novamente.");
                    return;
                }
            }
            default:{
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        }

    }

    // carrega as assinaturas ( ja filtradas ) e mostra a tela, os planos vêm do enum
    private void listar(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException{
        // filtros da tela
        String filtroDono = req.getParameter("dono");
        String filtroPlano = req.getParameter("plano");

        List<Assinatura> todas = assinaturaDAO.buscarTodasAssinatura();
        List<Assinatura> assinaturas = new ArrayList<>();
        for (Assinatura assinatura : todas){
            String dono = assinatura.getUsuario() != null
                    ? assinatura.getUsuario().getNome() + " " + assinatura.getUsuario().getSobrenome() + " " + assinatura.getUsuario().getCpf()
                    : assinatura.getEmpresa().getNome() + " " + assinatura.getEmpresa().getCnpj();
            boolean achouDono = filtroDono == null || filtroDono.isBlank() || dono.toLowerCase().contains(filtroDono.trim().toLowerCase());
            boolean achouPlano = filtroPlano == null || filtroPlano.isBlank() || filtroPlano.equals(assinatura.getTipoAssinatura().name());
            if (achouDono && achouPlano){
                assinaturas.add(assinatura);
            }
        }

        // ver os detalhes ou editar uma assinatura ( vem no link da linha )
        String ver = req.getParameter("ver");
        String editar = req.getParameter("editar");
        String idEscolhido = (ver != null && !ver.isBlank()) ? ver.trim() : (editar != null && !editar.isBlank() ? editar.trim() : null);
        if (idEscolhido != null){
            for (Assinatura assinatura : todas){
                if (String.valueOf(assinatura.getIdAssinatura()).equals(idEscolhido)){
                    req.setAttribute("registro", assinatura);
                    req.setAttribute("modo", ver != null && !ver.isBlank() ? "ver" : "editar");
                }
            }
        }

        req.setAttribute("assinaturas", Paginacao.aplicar(req, assinaturas));
        req.setAttribute("totalGeral", todas.size());
        req.setAttribute("usuarios", usuarioDAO.buscarTodosUsuario());
        req.setAttribute("empresas", empresaDAO.buscarTodasEmpresa());
        req.setAttribute("planos", TipoAssinatura.values());
        req.setAttribute("filtroDono", filtroDono == null ? "" : filtroDono);
        req.setAttribute("filtroPlano", filtroPlano == null ? "" : filtroPlano);
        req.getRequestDispatcher(jsp_planos).forward(req, resp);
    }

    // volta para a tela de onde veio com uma mensagem de erro 
    private void voltaComErro(HttpServletRequest req, HttpServletResponse resp, String mensagem)
            throws ServletException, IOException{
        req.setAttribute("erro", mensagem);
        listar(req, resp);
    }
}
