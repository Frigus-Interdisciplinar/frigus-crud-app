package com.example.servlet.Controller;

import com.example.servlet.Dao.EmpresaDAO;
import com.example.servlet.Dao.FrigorificosDAO;
import com.example.servlet.Dao.ResumoDAO;
import com.example.servlet.Model.Empresa;
import com.example.servlet.Model.Endereco;
import com.example.servlet.Model.Frigorificos;
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
import java.util.Map;
import java.util.TreeSet;

@WebServlet(urlPatterns = {
        "/admin/frigorificos"},
        name = "frigorificos")
public class FrigorificoServlet extends HttpServlet {
    private FrigorificosDAO frigorificoDAO;
    private EmpresaDAO empresaDAO;
    private ResumoDAO resumoDAO;
    private Validacao validar;

    // caminho das paginas
    private static final String jsp_frigorificos = "/WEB-INF/pages/frigorificos.jsp";

    // caminho para o servlet
    private static final String url_servlet = "/admin/frigorificos";

    // as siglas dos estados para o campo UF
    private static final String[] ufs = {"AC", "AL", "AP", "AM", "BA", "CE", "DF", "ES", "GO", "MA", "MT", "MS", "MG", "PA",
            "PB", "PR", "PE", "PI", "RJ", "RN", "RS", "RO", "RR", "SC", "SP", "SE", "TO"};

    @Override
    public void init(){
        frigorificoDAO = new FrigorificosDAO();
        empresaDAO = new EmpresaDAO();
        resumoDAO = new ResumoDAO();
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

        if (action == null){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        switch (action){
            case "inserir":{
                // pegando as informações do formulario

                // frigorifico
                String cnpj = req.getParameter("cnpj");
                String tipo = req.getParameter("tipo");
                // endereço
                String rua = req.getParameter("rua");
                String bairro = req.getParameter("bairro");
                String cidade = req.getParameter("cidade");
                String uf = req.getParameter("uf");
                String pais = req.getParameter("pais");
                String cep = req.getParameter("cep");
                String numero = req.getParameter("numero");
                String nomeUnidade = req.getParameter("nomeUnidade");

                // o nome do frigorifico é o nome da unidade
                String erroCampos = validarCampos(cnpj, tipo, nomeUnidade, rua, bairro, cidade, uf, cep, numero, pais);
                if (erroCampos != null){
                    voltaComErro(req, resp, erroCampos);
                    return;
                }
                // a empresa dona tem que existir
                Empresa empresa = empresaDAO.buscarEmpresaPorCnpj(validar.formatarCnpj(cnpj.trim()));
                if (empresa == null){
                    voltaComErro(req, resp, "Empresa não encontrada.");
                    return;
                }

                // o admin não tem limite de frigorificos por plano
                Endereco endereco = montarEndereco(rua, bairro, cidade, uf, pais, cep, numero, nomeUnidade);
                Frigorificos frigorifico = new Frigorificos(empresa, endereco, tipo.trim(), nomeUnidade.trim());
                if (frigorificoDAO.inserirFrigorifico(frigorifico) == 1){
                    session.setAttribute("sucesso", "Frigorífico cadastrado com sucesso.");
                    resp.sendRedirect(req.getContextPath() + url_servlet);
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

                String erroCampos = validarCampos(cnpj, tipo, nomeUnidade, rua, bairro, cidade, uf, cep, numero, pais);
                if (erroCampos != null){
                    voltaComErro(req, resp, erroCampos);
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
                Endereco enderecoNovo = montarEndereco(rua, bairro, cidade, uf, pais, cep, numero, nomeUnidade);
                enderecoNovo.setIdEndereco(frigorificoAntigo.getEndereco().getIdEndereco());
                Frigorificos frigorificoNovo = new Frigorificos(empresa, enderecoNovo, tipo.trim(), nomeUnidade.trim());
                if (frigorificoDAO.alterarFrigorifico(frigorificoAntigo, frigorificoNovo) == 1){
                    session.setAttribute("sucesso", "Frigorífico alterado com sucesso.");
                    resp.sendRedirect(req.getContextPath() + url_servlet);
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
                    resp.sendRedirect(req.getContextPath() + url_servlet);
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

    // confere os campos do frigorifico e do endereço, devolve a mensagem de erro ou null se esta tudo certo
    private String validarCampos(String cnpj, String tipo, String nomeUnidade, String rua, String bairro, String cidade, String uf, String cep, String numero, String pais){
        if(cnpj == null || cnpj.isBlank()){
            return "Escolha a empresa dona do frigorífico.";
        }
        if(tipo == null || tipo.isBlank() || tipo.length() > 20){
            return "O tipo de frigorífico não pode ser vazio e tem no máximo 20 caracteres.";
        }
        if(nomeUnidade == null || nomeUnidade.isBlank() || nomeUnidade.length() > 50){
            return "O nome da unidade não pode ser vazio e tem no máximo 50 caracteres.";
        }
        if(rua == null || rua.isBlank()){
            return "A rua não pode ser vazia.";
        }
        if(bairro == null || bairro.isBlank()){
            return "O bairro não pode ser vazio.";
        }
        if(cidade == null || cidade.isBlank()){
            return "A cidade não pode ser vazia.";
        }
        if(uf == null || uf.trim().length() != 2){
            return "A UF deve ter 2 letras.";
        }
        if(cep == null || cep.isBlank()){
            return "O cep não pode ser vazio.";
        }
        if(numero == null || numero.isBlank()){
            return "O número não pode ser vazio.";
        }
        boolean vcep = validar.validarCep(validar.formatarCep(cep.trim()));
        if(!vcep){
            return "Formato do cep incorreto";
        }
        boolean vNome = validar.validarNomeProduto(nomeUnidade);
        if(!vNome){
            return "Nome da unidade inválido.";
        }
        return null;
    }

    // monta o endereço com os campos do formulario ( já validados ), o país em branco vira Brasil
    private Endereco montarEndereco(String rua, String bairro, String cidade, String uf, String pais, String cep, String numero, String nomeUnidade){
        String paisFinal = (pais == null || pais.isBlank()) ? "Brasil" : pais.trim();
        return new Endereco(rua.trim(), bairro.trim(), cidade.trim(), uf.trim().toUpperCase(), paisFinal, validar.formatarCep(cep.trim()), numero.trim(), nomeUnidade.trim());
    }

    // carrega a lista ( ja filtrada ), a ocupação de cada frigorifico e mostra a tela
    private void listar(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException{
        // filtros da tela
        String filtroNome = req.getParameter("nome");
        String filtroCidade = req.getParameter("cidade");
        String filtroUf = req.getParameter("uf");
        String filtroTipo = req.getParameter("tipo");

        List<Frigorificos> todos = frigorificoDAO.buscarTodosFrigorifico();
        Map<Integer, int[]> ocupacao = resumoDAO.buscarOcupacaoFrigorificos();

        TreeSet<String> cidades = new TreeSet<>();
        TreeSet<String> tipos = new TreeSet<>();
        List<Frigorificos> frigorificos = new ArrayList<>();
        for (Frigorificos frigorifico : todos){
            cidades.add(frigorifico.getEndereco().getCidade());
            tipos.add(frigorifico.getTipo());

            boolean achouNome = filtroNome == null || filtroNome.isBlank() || frigorifico.getNome().toLowerCase().contains(filtroNome.trim().toLowerCase());
            boolean achouCidade = filtroCidade == null || filtroCidade.isBlank() || filtroCidade.equals(frigorifico.getEndereco().getCidade());
            boolean achouUf = filtroUf == null || filtroUf.isBlank() || filtroUf.equals(frigorifico.getEndereco().getUf());
            boolean achouTipo = filtroTipo == null || filtroTipo.isBlank() || filtroTipo.equals(frigorifico.getTipo());
            if (achouNome && achouCidade && achouUf && achouTipo){
                frigorificos.add(frigorifico);
            }
        }

        // ver os detalhes ou editar um frigorifico ( vem no link da linha ), ele sai da lista que ja foi carregada
        String ver = req.getParameter("ver");
        String editar = req.getParameter("editar");
        String idEscolhido = (ver != null && !ver.isBlank()) ? ver.trim() : (editar != null && !editar.isBlank() ? editar.trim() : null);
        if (idEscolhido != null){
            for (Frigorificos frigorifico : todos){
                if (String.valueOf(frigorifico.getIdFrigorifico()).equals(idEscolhido)){
                    req.setAttribute("registro", frigorifico);
                    req.setAttribute("modo", ver != null && !ver.isBlank() ? "ver" : "editar");
                }
            }
        }

        req.setAttribute("frigorificos", Paginacao.aplicar(req, frigorificos));
        req.setAttribute("totalGeral", todos.size());
        req.setAttribute("ocupacao", ocupacao);
        req.setAttribute("cidades", cidades);
        req.setAttribute("tipos", tipos);
        req.setAttribute("ufs", ufs);
        req.setAttribute("empresas", empresaDAO.buscarTodasEmpresa());
        req.setAttribute("filtroNome", filtroNome == null ? "" : filtroNome);
        req.setAttribute("filtroCidade", filtroCidade == null ? "" : filtroCidade);
        req.setAttribute("filtroUf", filtroUf == null ? "" : filtroUf);
        req.setAttribute("filtroTipo", filtroTipo == null ? "" : filtroTipo);
        req.getRequestDispatcher(jsp_frigorificos).forward(req, resp);
    }

    // volta para a tela com uma mensagem de erro
    private void voltaComErro(HttpServletRequest req, HttpServletResponse resp, String mensagem)
            throws ServletException, IOException{
        req.setAttribute("erro", mensagem);
        listar(req, resp);
    }
}
