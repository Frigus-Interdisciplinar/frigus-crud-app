package com.example.servlet.Controller;

import com.example.servlet.Dao.EnderecoDAO;
import com.example.servlet.Dao.FrigorificosDAO;
import com.example.servlet.Model.Empresa;
import com.example.servlet.Model.Endereco;
import com.example.servlet.Model.Frigorificos;
import com.example.servlet.Util.Enum.Categoria;
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
        "/ver-frigorifico",
        "/inserir-frigorifico",
        "/alterar-frigorifico",
        "/deletar-frigorifico",
}, name = "/ver-frigorifico")
public class FrigorificoServlet extends HttpServlet {
    private FrigorificosDAO frigorificoDAO;
    private EnderecoDAO enderecoDAO;

    // caminho das pastas
    private static final String jsp_login = "/assets/pages/login.jsp";
    private static final String jsp_ver = "/assets/pages/ver-frigorifico.jsp";
    private static final String jsp_inserir = "/assets/pages/inserir-frigorifico.jsp";
    private static final String jsp_alterar = "/assets/pages/alterar-frigorifico.jsp";
    private static final String jsp_deletar = "/assets/pages/deletar-frigorifico.jsp";

    // caminho para o servlet
    private static final String url_servlet = "/ver-frigorifico";

    @Override
    public void init(){
        frigorificoDAO = new FrigorificosDAO();
        enderecoDAO = new EnderecoDAO();
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession();
        if (session == null || session.getAttribute("empresa") == null){
            resp.sendRedirect(req.getContextPath() + jsp_login);
            return;
        }
        Empresa empresa = (Empresa) session.getAttribute("empresa");
        switch (req.getServletPath()){
            case "/ver-frigorifico":{
                List<Frigorificos> frigorificos = frigorificoDAO.buscarTodosFrigorifico(empresa.getCnpj());
                req.setAttribute("frigorificos", frigorificos);
                req.getRequestDispatcher(jsp_ver).forward(req, resp);
                break;
            }
            default:
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Validacao validar = new Validacao();
        HttpSession session = req.getSession();
        switch (req.getServletPath()){
            case "/inserir-frigorifico":{
                Empresa empresa = (Empresa) session.getAttribute("empresa");
                Categoria plano = (Categoria) session.getAttribute("plano");

                if(empresa == null || plano == null){
                    resp.sendRedirect(req.getContextPath() + jsp_login);
                    return;
                }

                Frigorificos frigorifico;
                Endereco endereco;
                // pegando as informações do formulario

                // frigorifico
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


                if(tipo == null){
                    voltaComErro(req, resp, "O tipo de frigorífico não pode ser vazio.", jsp_inserir);
                    return;
                }
                if(nomeFrigorifico == null){
                    voltaComErro(req, resp, "O nome do frigorífico não pode ser vazio.", jsp_inserir);
                    return;
                }
                if(rua == null){
                    voltaComErro(req, resp, "A rua não pode ser vazia.", jsp_inserir);
                    return;
                }
                if(bairro == null){
                    voltaComErro(req, resp, "O bairro não pode ser vazio.", jsp_inserir);
                    return;
                }
                if(cidade == null){
                    voltaComErro(req, resp, "A cidade não pode ser vazia.", jsp_inserir);
                    return;
                }
                if(uf == null){
                    voltaComErro(req, resp, "A UF não pode ser vazia.", jsp_inserir);
                    return;
                }
                if(pais == null){
                    voltaComErro(req, resp, "O país não pode ser vazio.", jsp_inserir);
                    return;
                }
                if(cep == null){
                    voltaComErro(req, resp, "O cep não pode ser vazio.", jsp_inserir);
                    return;
                }
                if(numero == null){
                    voltaComErro(req, resp, "O número não pode ser vazio.", jsp_inserir);
                    return;
                }
                if(nomeUnidade == null){
                    voltaComErro(req, resp, "O número não pode ser vazio.", jsp_inserir);
                    return;
                }
                boolean vcep = validar.validarCep(cep);
                if(!vcep){
                    voltaComErro(req, resp, "Formato do cep incorreto", jsp_inserir);
                    return;
                }

                endereco = new Endereco(rua, bairro, cidade, uf, pais, cep, numero, nomeUnidade);
                frigorifico = new Frigorificos(empresa, endereco, tipo, nomeFrigorifico);
                if (frigorificoDAO.inserirFrigorifico(frigorifico) == 1){
                    resp.sendRedirect(req.getContextPath() + url_servlet);
                    return;
                }
                else{
                    voltaComErro(req, resp, "Erro ao adicionar frigorifico. Por favor tente novamente.", jsp_inserir);
                    return;
                }
            }

            case "/alterar-frigorifico":{
                Empresa empresa = (Empresa) session.getAttribute("empresa");
                Categoria plano = (Categoria) session.getAttribute("plano");

                if(empresa == null || plano == null){
                    resp.sendRedirect(req.getContextPath() + jsp_login);
                    return;
                }


                // pegando as informações do formulario

                // frigorifico
                String idTxt = req.getParameter("idFrigorifico");
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
                    id = Integer.parseInt(idTxt);
                } catch (NumberFormatException e) {
                    voltaComErro(req, resp, "Erro ao alterar esse frigorífico. Por favor tente novamente.", jsp_inserir);
                    return;
                }

                if(tipo == null){
                    voltaComErro(req, resp, "O tipo de frigorífico não pode ser vazio.", jsp_inserir);
                    return;
                }
                if(nomeFrigorifico == null){
                    voltaComErro(req, resp, "O nome do frigorífico não pode ser vazio.", jsp_inserir);
                    return;
                }


                if(rua == null){
                    voltaComErro(req, resp, "A rua não pode ser vazia.", jsp_inserir);
                    return;
                }
                if(bairro == null){
                    voltaComErro(req, resp, "O bairro não pode ser vazio.", jsp_inserir);
                    return;
                }
                if(cidade == null){
                    voltaComErro(req, resp, "A cidade não pode ser vazia.", jsp_inserir);
                    return;
                }
                if(uf == null){
                    voltaComErro(req, resp, "A UF não pode ser vazia.", jsp_inserir);
                    return;
                }
                if(pais == null){
                    voltaComErro(req, resp, "O país não pode ser vazio.", jsp_inserir);
                    return;
                }
                if(cep == null){
                    voltaComErro(req, resp, "O cep não pode ser vazio.", jsp_inserir);
                    return;
                }
                if(numero == null){
                    voltaComErro(req, resp, "O número não pode ser vazio.", jsp_inserir);
                    return;
                }
                if(nomeUnidade == null){
                    voltaComErro(req, resp, "O número não pode ser vazio.", jsp_inserir);
                    return;
                }
                boolean vcep = validar.validarCep(cep);
                if(!vcep){
                    voltaComErro(req, resp, "Formato do cep incorreto", jsp_inserir);
                    return;
                }

                Frigorificos frigorificoAntigo = frigorificoDAO.buscarFrigorificoPorId(empresa.getCnpj(), id);
                if (frigorificoAntigo == null) {
                    voltaComErro(req, resp, "Frigorífico não encontrado.", jsp_alterar);
                    return;
                }
                Endereco enderecoAntigo = enderecoDAO.buscarEnderecoDeUmFrigorifico(empresa.getCnpj(), frigorificoAntigo.getIdFrigorifico());
                if (enderecoAntigo == null) {
                    voltaComErro(req, resp, "Endereço do frigorífico não encontrado.", jsp_alterar);
                    return;
                }

                Endereco enderecoNovo = new Endereco(rua, bairro, cidade, uf, pais, cep, numero, nomeUnidade);

                enderecoNovo.setIdEndereco(enderecoAntigo.getIdEndereco());
                Frigorificos frigorificoNovo = new Frigorificos(empresa, enderecoNovo, tipo, nomeFrigorifico);
                if (frigorificoDAO.alterarFrigorifico(frigorificoAntigo, frigorificoNovo) == 1){
                    resp.sendRedirect(req.getContextPath() + url_servlet);
                    return;
                }
                else {
                    voltaComErro(req, resp, "Erro ao alterar esse frigorifico. Por favor tente novamente.", jsp_alterar);
                    return;
                }
            }
            case "/deletar-frigorifico":{
                Empresa empresa = (Empresa) session.getAttribute("empresa");
                Categoria plano = (Categoria) session.getAttribute("plano");

                if(empresa == null || plano == null){
                    resp.sendRedirect(req.getContextPath() + jsp_login);
                    return;
                }

                String idTxt = req.getParameter("idFrigorifico");
                int id;
                try{
                    id = Integer.parseInt(idTxt);
                } catch (NumberFormatException e) {
                    voltaComErro(req, resp, "Erro ao deletar esse frigorífico.", jsp_deletar);
                    return;
                }
                Frigorificos frigorifico = frigorificoDAO.buscarFrigorificoPorId(empresa.getCnpj(), id);
                if (frigorifico == null){
                    voltaComErro(req, resp, "Erro ao deletar esse frigorífico. Por favor tente novamente", jsp_deletar);
                    return;
                }
                if (frigorificoDAO.deletarFrigorifico(frigorifico) == 1){
                    resp.sendRedirect(req.getContextPath() + url_servlet);
                    return;
                }else {
                    voltaComErro(req, resp, "Erro ao deletar esse frigorífico. Por favor tente novamente", jsp_deletar);
                    return;
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