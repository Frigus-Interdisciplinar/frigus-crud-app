package com.example.servlet.Controller;

import com.example.servlet.Dao.EmpresaDAO;
import com.example.servlet.Dao.FrigorificosDAO;
import com.example.servlet.Model.Empresa;
import com.example.servlet.Model.Endereco;
import com.example.servlet.Model.Frigorificos;
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
        "/admin/frigorificos"},
        name = "frigorificos")
public class FrigorificoServlet extends HttpServlet {
    private FrigorificosDAO frigorificoDAO;
    private EmpresaDAO empresaDAO;
    private Validacao validar;

    // caminho das paginas
    private static final String jsp_frigorificos = "/WEB-INF/pages/frigorificos.jsp";

    // caminho para o servlet
    private static final String url_servlet = "/admin/frigorificos";

    @Override
    public void init(){
        frigorificoDAO = new FrigorificosDAO();
        empresaDAO = new EmpresaDAO();
        validar = new Validacao();
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // inserir, alterar e deletar só por POST
        if (req.getParameter("action") != null){
            resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }

        switch (req.getServletPath()){
            case "/admin/frigorificos":{
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
            case "inserir":{
                Frigorificos frigorifico;
                Endereco endereco;
                // pegando as informações do formulario

                // frigorifico
                String cnpj = req.getParameter("cnpj");
                String tipo = req.getParameter("tipo");
                String nomeFrigorifico = req.getParameter("nomeFrigorifico");
                // endereço
                String rua = req.getParameter("rua");
                String bairro = req.getParameter("bairro");
                String cidade = req.getParameter("cidade");
                String uf = req.getParameter("uf");
                String pais = req.getParameter("pais");
                String cep = req.getParameter("cep");
                String numero = req.getParameter("numero");
                String nomeUnidade = req.getParameter("nomeUnidade");


                if(cnpj == null || cnpj.isBlank()){
                    voltaComErro(req, resp, "Escolha a empresa dona do frigorífico.");
                    return;
                }
                if(tipo == null || tipo.isBlank()){
                    voltaComErro(req, resp, "O tipo de frigorífico não pode ser vazio.");
                    return;
                }
                if(nomeFrigorifico == null || nomeFrigorifico.isBlank()){
                    voltaComErro(req, resp, "O nome do frigorífico não pode ser vazio.");
                    return;
                }
                if(rua == null || rua.isBlank()){
                    voltaComErro(req, resp, "A rua não pode ser vazia.");
                    return;
                }
                if(bairro == null || bairro.isBlank()){
                    voltaComErro(req, resp, "O bairro não pode ser vazio.");
                    return;
                }
                if(cidade == null || cidade.isBlank()){
                    voltaComErro(req, resp, "A cidade não pode ser vazia.");
                    return;
                }
                if(uf == null || uf.trim().length() != 2){
                    voltaComErro(req, resp, "A UF deve ter 2 letras.");
                    return;
                }
                if(pais == null || pais.isBlank()){
                    voltaComErro(req, resp, "O país não pode ser vazio.");
                    return;
                }
                if(cep == null || cep.isBlank()){
                    voltaComErro(req, resp, "O cep não pode ser vazio.");
                    return;
                }
                if(numero == null || numero.isBlank()){
                    voltaComErro(req, resp, "O número não pode ser vazio.");
                    return;
                }
                if(nomeUnidade == null || nomeUnidade.isBlank()){
                    voltaComErro(req, resp, "O nome da unidade não pode ser vazio.");
                    return;
                }
                String cepPuro = validar.formatarCep(cep.trim());
                boolean vcep = validar.validarCep(cepPuro);
                if(!vcep){
                    voltaComErro(req, resp, "Formato do cep incorreto");
                    return;
                }
                boolean vNome = validar.validarNomeProduto(nomeFrigorifico);
                if(!vNome || nomeFrigorifico.length() > 50){
                    voltaComErro(req, resp, "Nome do frigorífico inválido. No máximo 50 caracteres.");
                    return;
                }
                // a empresa dona tem que existir
                Empresa empresa = empresaDAO.buscarEmpresaPorCnpj(validar.formatarCnpj(cnpj.trim()));
                if (empresa == null){
                    voltaComErro(req, resp, "Empresa não encontrada.");
                    return;
                }

                // o admin não tem limite de frigorificos por plano
                endereco = new Endereco(rua.trim(), bairro.trim(), cidade.trim(), uf.trim().toUpperCase(), pais.trim(), cepPuro, numero.trim(), nomeUnidade.trim());
                frigorifico = new Frigorificos(empresa, endereco, tipo.trim(), nomeFrigorifico.trim());
                if (frigorificoDAO.inserirFrigorifico(frigorifico) == 1){
                    session.setAttribute("sucesso", "Frigorífico cadastrado com sucesso.");
                    resp.sendRedirect(req.getContextPath() + retorno);
                    return;
                }
                else{
                    voltaComErro(req, resp, "Erro ao adicionar frigorifico. Por favor tente novamente.");
                    return;
                }
            }

            case "alterar":{
                // pegando as informações do formulario

                // frigorifico
                String idTxt = req.getParameter("idFrigorifico");
                String cnpj = req.getParameter("cnpj");
                String tipo = req.getParameter("tipo");
                String nomeFrigorifico = req.getParameter("nomeFrigorifico");
                // endereço
                String rua = req.getParameter("rua");
                String bairro = req.getParameter("bairro");
                String cidade = req.getParameter("cidade");
                String uf = req.getParameter("uf");
                String pais = req.getParameter("pais");
                String cep = req.getParameter("cep");
                String numero = req.getParameter("numero");
                String nomeUnidade = req.getParameter("nomeUnidade");

                // convertendo
                int id;
                try{
                    id = Integer.parseInt(idTxt.trim());
                } catch (NumberFormatException | NullPointerException e) {
                    voltaComErro(req, resp, "Erro ao alterar esse frigorífico. Por favor tente novamente.");
                    return;
                }

                if(cnpj == null || cnpj.isBlank()){
                    voltaComErro(req, resp, "Escolha a empresa dona do frigorífico.");
                    return;
                }
                if(tipo == null || tipo.isBlank()){
                    voltaComErro(req, resp, "O tipo de frigorífico não pode ser vazio.");
                    return;
                }
                if(nomeFrigorifico == null || nomeFrigorifico.isBlank()){
                    voltaComErro(req, resp, "O nome do frigorífico não pode ser vazio.");
                    return;
                }
                if(rua == null || rua.isBlank()){
                    voltaComErro(req, resp, "A rua não pode ser vazia.");
                    return;
                }
                if(bairro == null || bairro.isBlank()){
                    voltaComErro(req, resp, "O bairro não pode ser vazio.");
                    return;
                }
                if(cidade == null || cidade.isBlank()){
                    voltaComErro(req, resp, "A cidade não pode ser vazia.");
                    return;
                }
                if(uf == null || uf.trim().length() != 2){
                    voltaComErro(req, resp, "A UF deve ter 2 letras.");
                    return;
                }
                if(pais == null || pais.isBlank()){
                    voltaComErro(req, resp, "O país não pode ser vazio.");
                    return;
                }
                if(cep == null || cep.isBlank()){
                    voltaComErro(req, resp, "O cep não pode ser vazio.");
                    return;
                }
                if(numero == null || numero.isBlank()){
                    voltaComErro(req, resp, "O número não pode ser vazio.");
                    return;
                }
                if(nomeUnidade == null || nomeUnidade.isBlank()){
                    voltaComErro(req, resp, "O nome da unidade não pode ser vazio.");
                    return;
                }
                String cepPuro = validar.formatarCep(cep.trim());
                boolean vcep = validar.validarCep(cepPuro);
                if(!vcep){
                    voltaComErro(req, resp, "Formato do cep incorreto");
                    return;
                }
                boolean vNome = validar.validarNomeProduto(nomeFrigorifico);
                if(!vNome || nomeFrigorifico.length() > 50){
                    voltaComErro(req, resp, "Nome do frigorífico inválido. No máximo 50 caracteres.");
                    return;
                }

                Frigorificos frigorificoAntigo = frigorificoDAO.buscarFrigorificoPorId(id);
                if (frigorificoAntigo == null) {
                    voltaComErro(req, resp, "Frigorífico não encontrado.");
                    return;
                }
                // a nova empresa dona tem que existir
                Empresa empresa = empresaDAO.buscarEmpresaPorCnpj(validar.formatarCnpj(cnpj.trim()));
                if (empresa == null){
                    voltaComErro(req, resp, "Empresa não encontrada.");
                    return;
                }

                // o frigorifico continua com o mesmo endereço, so os dados dele mudam
                Endereco enderecoNovo = new Endereco(rua.trim(), bairro.trim(), cidade.trim(), uf.trim().toUpperCase(), pais.trim(), cepPuro, numero.trim(), nomeUnidade.trim());
                enderecoNovo.setIdEndereco(frigorificoAntigo.getEndereco().getIdEndereco());
                Frigorificos frigorificoNovo = new Frigorificos(empresa, enderecoNovo, tipo.trim(), nomeFrigorifico.trim());
                if (frigorificoDAO.alterarFrigorifico(frigorificoAntigo, frigorificoNovo) == 1){
                    session.setAttribute("sucesso", "Frigorífico alterado com sucesso.");
                    resp.sendRedirect(req.getContextPath() + retorno);
                    return;
                }
                else {
                    voltaComErro(req, resp, "Erro ao alterar esse frigorifico. Por favor tente novamente.");
                    return;
                }
            }
            case "deletar":{
                String idTxt = req.getParameter("idFrigorifico");
                int id;
                try{
                    id = Integer.parseInt(idTxt.trim());
                } catch (NumberFormatException | NullPointerException e) {
                    voltaComErro(req, resp, "Erro ao deletar esse frigorífico.");
                    return;
                }
                Frigorificos frigorifico = frigorificoDAO.buscarFrigorificoPorId(id);
                if (frigorifico == null){
                    voltaComErro(req, resp, "Frigorífico não encontrado.");
                    return;
                }
                if (frigorificoDAO.deletarFrigorifico(frigorifico) == 1){
                    session.setAttribute("sucesso", "Frigorífico apagado com sucesso, junto com o endereço e os itens dele.");
                    resp.sendRedirect(req.getContextPath() + retorno);
                    return;
                }else {
                    voltaComErro(req, resp, "Erro ao deletar esse frigorífico. Por favor tente novamente");
                    return;
                }
            }
            default:
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }

    }

    // carrega as listas e mostra a tela
    private void listar(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException{
        List<Frigorificos> frigorificos = frigorificoDAO.buscarTodosFrigorifico();
        List<Empresa> empresas = empresaDAO.buscarTodasEmpresa();
        req.setAttribute("frigorificos", frigorificos);
        req.setAttribute("empresas", empresas);
        req.getRequestDispatcher(jsp_frigorificos).forward(req, resp);
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
