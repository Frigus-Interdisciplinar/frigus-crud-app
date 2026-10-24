package com.example.servlet.Controller;

import com.example.servlet.Dao.EmpresaDAO;
import com.example.servlet.Dao.IngredienteDAO;
import com.example.servlet.Dao.ReceitaDAO;
import com.example.servlet.Dao.ResumoDAO;
import com.example.servlet.Dao.UsuarioDAO;
import com.example.servlet.Model.Empresa;
import com.example.servlet.Model.Ingredientes;
import com.example.servlet.Model.Produto;
import com.example.servlet.Model.Receita;
import com.example.servlet.Model.Usuario;
import com.example.servlet.Util.Paginacao;
import com.example.servlet.Util.Validacao;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@WebServlet(urlPatterns = {
        "/admin/receitas"},
        name = "receitas")
public class ReceitaServlet extends HttpServlet {
    private Validacao validar;
    private ReceitaDAO receitaDAO;
    private IngredienteDAO ingredienteDAO;
    private UsuarioDAO usuarioDAO;
    private EmpresaDAO empresaDAO;
    private ResumoDAO resumoDAO;

    // caminho do servlet
    private static final String url_servlet = "/admin/receitas";

    // caminhos das paginas
    private static final String jsp_receitas = "/WEB-INF/pages/receitas.jsp";

    @Override
    public void init(){
        validar = new Validacao();
        receitaDAO = new ReceitaDAO();
        ingredienteDAO =  new IngredienteDAO();
        usuarioDAO = new UsuarioDAO();
        empresaDAO = new EmpresaDAO();
        resumoDAO = new ResumoDAO();
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
            case "/admin/receitas": {
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
                // o dono é o usuario ou a empresa ( o que estiver marcado no formulario )
                String cpf = donoEscolhido(req, "usuario", "cpf");
                String cnpj = donoEscolhido(req, "empresa", "cnpj");
                if (!validar.validarDono(cpf, cnpj)){
                    voltaComErro(req, resp, "Escolha só o usuário ou só a empresa dona da receita.");
                    return;
                }

                // validando os campos da receita
                String erroCampos = validarCamposReceita(req);
                if (erroCampos != null){
                    voltaComErro(req, resp, erroCampos);
                    return;
                }

                // os ingredientes do formulario ( linhas sem produto são ignoradas )
                List<Ingredientes> ingredientes = new ArrayList<>();
                String erroIngredientes = lerIngredientes(req, ingredientes);
                if (erroIngredientes != null){
                    voltaComErro(req, resp, erroIngredientes);
                    return;
                }

                // criando o objeto receita com o dono certo
                Receita receita = montarReceita(req, cpf, cnpj, null);
                if (receita == null){
                    voltaComErro(req, resp, "Dono da receita não encontrado.");
                    return;
                }

                if (receitaDAO.inserirReceita(receita) != 1){
                    voltaComErro(req, resp, "Erro ao adicionar essa receita. Por favor tente novamente");
                    return;
                }

                // cada ingrediente aponta para um produto que ja existe
                for (Ingredientes ingrediente : ingredientes){
                    ingrediente.setReceita(receita);
                    if (ingredienteDAO.adicionarIngrediente(ingrediente) != 1){
                        // a receita nao fica pela metade
                        receitaDAO.deletarReceita(receita);
                        voltaComErro(req, resp, "Erro ao adicionar os ingredientes da receita. Por favor tente novamente");
                        return;
                    }
                }

                session.setAttribute("sucesso", "Receita cadastrada com sucesso.");
                resp.sendRedirect(req.getContextPath() + url_servlet);
                return;
            }



            case "alterar":{
                Integer idReceita = lerInt(req.getParameter("idReceita"));
                if (idReceita == null){
                    voltaComErro(req, resp, "Erro ao alterar essa receita. Por favor tente novamente.");
                    return;
                }
                Receita receitaAntiga = receitaDAO.buscarReceitaPorId(idReceita);
                if (receitaAntiga == null){
                    voltaComErro(req, resp, "Receita não encontrada.");
                    return;
                }

                // o dono é o usuario ou a empresa ( o que estiver marcado no formulario )
                String cpf = donoEscolhido(req, "usuario", "cpf");
                String cnpj = donoEscolhido(req, "empresa", "cnpj");
                if (!validar.validarDono(cpf, cnpj)){
                    voltaComErro(req, resp, "Escolha só o usuário ou só a empresa dona da receita.");
                    return;
                }

                // validando os campos da receita
                String erroCampos = validarCamposReceita(req);
                if (erroCampos != null){
                    voltaComErro(req, resp, erroCampos);
                    return;
                }

                Receita receitaNova = montarReceita(req, cpf, cnpj, receitaAntiga);
                if (receitaNova == null){
                    voltaComErro(req, resp, "Dono da receita não encontrado.");
                    return;
                }

                if (receitaDAO.alterarReceita(receitaAntiga, receitaNova) == 1){
                    session.setAttribute("sucesso", "Receita alterada com sucesso.");
                    resp.sendRedirect(req.getContextPath() + url_servlet);
                    return;
                }
                else {
                    voltaComErro(req, resp, "Erro ao alterar essa receita. Por favor tente novamente");
                    return;
                }
            }


            case "deletar":{
                Integer idReceita = lerInt(req.getParameter("idReceita"));
                if (idReceita == null){
                    voltaComErro(req, resp, "Erro ao deletar essa receita. Por favor tente novamente.");
                    return;
                }
                Receita receita = receitaDAO.buscarReceitaPorId(idReceita);
                if (receita == null){
                    voltaComErro(req, resp, "Receita não encontrada.");
                    return;
                }

                if (receitaDAO.deletarReceita(receita) == 1){
                    session.setAttribute("sucesso", "Receita apagada com sucesso, junto com os ingredientes dela.");
                    resp.sendRedirect(req.getContextPath() + url_servlet);
                    return;
                }
                else {
                    voltaComErro(req, resp, "Erro ao deletar essa receita. Por favor tente novamente");
                    return;
                }
            }


            case "inserirIngrediente":{
                // a receita do ingrediente
                Integer idReceita = lerInt(req.getParameter("idReceita"));
                if (idReceita == null){
                    voltaComErro(req, resp, "Escolha a receita do ingrediente.");
                    return;
                }
                Receita receita = receitaDAO.buscarReceitaPorId(idReceita);
                if (receita == null){
                    voltaComErro(req, resp, "Receita não encontrada.");
                    return;
                }

                // o produto e a quantidade
                Integer idProduto = lerInt(req.getParameter("idProduto"));
                Integer quantidade = lerInt(req.getParameter("quantidade"));
                if (idProduto == null){
                    voltaComErro(req, resp, "Escolha o produto do ingrediente.");
                    return;
                }
                if (quantidade == null || !validar.validarQuantidade(quantidade)){
                    voltaComErro(req, resp, "Quantidade inválida! Deve ser maior que zero.");
                    return;
                }

                Produto produto = new Produto();
                produto.setIdProdutos(idProduto);
                Ingredientes ingrediente = new Ingredientes(receita, produto, quantidade);
                if (ingredienteDAO.adicionarIngrediente(ingrediente) == 1){
                    session.setAttribute("sucesso", "Ingrediente adicionado com sucesso.");
                    resp.sendRedirect(req.getContextPath() + url_servlet + "?ver=" + idReceita);
                    return;
                }
                else {
                    voltaComErro(req, resp, "Erro ao adicionar esse ingrediente. Esse produto pode não existir mais.");
                    return;
                }
            }


            case "alterarIngrediente":{
                Integer idIngrediente = lerInt(req.getParameter("idIngrediente"));
                Integer idProduto = lerInt(req.getParameter("idProduto"));
                Integer quantidade = lerInt(req.getParameter("quantidade"));
                if (idIngrediente == null || idProduto == null){
                    voltaComErro(req, resp, "Erro ao alterar esse ingrediente. Por favor tente novamente.");
                    return;
                }
                if (quantidade == null || !validar.validarQuantidade(quantidade)){
                    voltaComErro(req, resp, "Quantidade inválida! Deve ser maior que zero.");
                    return;
                }
                Ingredientes ingredienteAntigo = ingredienteDAO.buscarIngrediente(idIngrediente);
                if (ingredienteAntigo == null){
                    voltaComErro(req, resp, "Ingrediente não encontrado.");
                    return;
                }

                Produto produto = new Produto();
                produto.setIdProdutos(idProduto);
                Ingredientes ingredienteNovo = new Ingredientes(ingredienteAntigo.getReceita(), produto, quantidade);
                if (ingredienteDAO.alterarIngrediente(ingredienteAntigo, ingredienteNovo) == 1){
                    session.setAttribute("sucesso", "Ingrediente alterado com sucesso.");
                    resp.sendRedirect(req.getContextPath() + url_servlet + "?ver=" + ingredienteAntigo.getReceita().getIdReceita());
                    return;
                }
                else {
                    voltaComErro(req, resp, "Erro ao alterar esse ingrediente. Por favor tente novamente");
                    return;
                }
            }


            case "deletarIngrediente":{
                Integer idIngrediente = lerInt(req.getParameter("idIngrediente"));
                if (idIngrediente == null){
                    voltaComErro(req, resp, "Erro ao deletar esse ingrediente. Por favor tente novamente.");
                    return;
                }
                Ingredientes ingrediente = ingredienteDAO.buscarIngrediente(idIngrediente);
                if (ingrediente == null){
                    voltaComErro(req, resp, "Ingrediente não encontrado.");
                    return;
                }

                if (ingredienteDAO.deletarIngrediente(ingrediente) == 1){
                    session.setAttribute("sucesso", "Ingrediente apagado com sucesso.");
                    resp.sendRedirect(req.getContextPath() + url_servlet + "?ver=" + ingrediente.getReceita().getIdReceita());
                    return;
                }
                else {
                    voltaComErro(req, resp, "Erro ao deletar esse ingrediente. Por favor tente novamente");
                    return;
                }
            }
            default:
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }

    }

    // o dono vem de um campo de usuario e de um de empresa, vale só o que estiver marcado em "donoTipo"
    private String donoEscolhido(HttpServletRequest req, String tipo, String campo){
        String donoTipo = req.getParameter("donoTipo");
        String valor = req.getParameter(campo);
        if (donoTipo != null && !donoTipo.equals(tipo)){
            return null;
        }
        return valor;
    }

    // confere os campos da receita, devolve a mensagem de erro ou null se esta tudo certo
    private String validarCamposReceita(HttpServletRequest req){
        String nomeReceita = req.getParameter("nomeReceita");
        String modoPreparo = req.getParameter("modoPreparo");

        if (nomeReceita == null || nomeReceita.isBlank()){
            return "O nome da receita não deve ser vazio!";
        }
        if (!validar.validarNomeProduto(nomeReceita) || nomeReceita.length() > 50){
            return "Nome de receita inválido! No máximo 50 caracteres.";
        }
        if (modoPreparo == null || modoPreparo.isBlank()){
            return "O modo de preparo da receita não deve ser vazio!";
        }
        return null;
    }

    // monta a receita com o dono certo ( usuario ou empresa ), devolve null se o dono nao existe
    // na alteração a data e o "foi feita" continuam os da receita antiga
    private Receita montarReceita(HttpServletRequest req, String cpf, String cnpj, Receita receitaAntiga){
        String nomeReceita = req.getParameter("nomeReceita").trim();
        String descricaoReceita = req.getParameter("descricaoReceita");
        String modoPreparo = req.getParameter("modoPreparo");

        LocalDate dataReceita = receitaAntiga == null ? LocalDate.now() : receitaAntiga.getDtReceita();
        boolean foiFeita = receitaAntiga != null && receitaAntiga.isFoiFeita();
        if (descricaoReceita == null || descricaoReceita.isBlank()){
            descricaoReceita = null;
        }

        if (cpf != null && !cpf.isBlank()){
            Usuario usuario = usuarioDAO.buscarUsuarioPorCpf(cpf.trim());
            if (usuario == null){
                return null;
            }
            return new Receita(usuario, nomeReceita, descricaoReceita, modoPreparo, dataReceita, foiFeita);
        }
        Empresa empresa = empresaDAO.buscarEmpresaPorCnpj(cnpj.trim());
        if (empresa == null){
            return null;
        }
        return new Receita(empresa, nomeReceita, descricaoReceita, modoPreparo, dataReceita, foiFeita);
    }

    // le as linhas de ingrediente do formulario ( produto e quantidade ), devolve a mensagem de erro ou null
    private String lerIngredientes(HttpServletRequest req, List<Ingredientes> ingredientes){
        String[] produtos = req.getParameterValues("idProduto");
        String[] quantidades = req.getParameterValues("quantidade");
        if (produtos == null){
            return null;
        }
        for (int i = 0; i < produtos.length; i++){
            // linha sem produto escolhido é ignorada
            if (produtos[i] == null || produtos[i].isBlank()){
                continue;
            }
            Integer idProduto = lerInt(produtos[i]);
            Integer quantidade = (quantidades != null && i < quantidades.length) ? lerInt(quantidades[i]) : null;
            if (idProduto == null){
                return "Produto do ingrediente inválido.";
            }
            if (quantidade == null || !validar.validarQuantidade(quantidade)){
                return "Quantidade inválida! Deve ser maior que zero.";
            }
            Produto produto = new Produto();
            produto.setIdProdutos(idProduto);
            ingredientes.add(new Ingredientes(null, produto, quantidade));
        }
        return null;
    }

    // carrega a lista ( ja filtrada e ordenada ), os dados dos formularios e mostra a tela
    private void listar(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException{
        // filtros da tela
        String filtroReceita = req.getParameter("receita");
        String filtroDono = req.getParameter("dono");
        String filtroOrd = req.getParameter("ord");

        List<Receita> todas = receitaDAO.buscarTodasReceita();

        List<Receita> receitas = new ArrayList<>();
        for (Receita receita : todas){
            boolean ehDeUsuario = receita.getUsuario() != null;
            boolean achouReceita = filtroReceita == null || filtroReceita.isBlank() || receita.getNome().toLowerCase().contains(filtroReceita.trim().toLowerCase());
            boolean achouDono = filtroDono == null || filtroDono.isBlank()
                    || (filtroDono.equals("usuario") && ehDeUsuario)
                    || (filtroDono.equals("empresa") && !ehDeUsuario);
            if (achouReceita && achouDono){
                receitas.add(receita);
            }
        }

        // ordem da lista ( padrão: nome de A a Z )
        Comparator<Receita> porNome = Comparator.comparing(r -> r.getNome().toLowerCase());
        Comparator<Receita> porData = Comparator.comparing(Receita::getDtReceita);
        if ("nome:desc".equals(filtroOrd)){
            receitas.sort(porNome.reversed());
        }
        else if ("criada:desc".equals(filtroOrd)){
            receitas.sort(porData.reversed());
        }
        else if ("criada:asc".equals(filtroOrd)){
            receitas.sort(porData);
        }
        else {
            receitas.sort(porNome);
        }

        // ver os detalhes ou editar uma receita ( vem no link da linha ), ela sai da lista que ja foi carregada
        String ver = req.getParameter("ver");
        String editar = req.getParameter("editar");
        String idEscolhido = (ver != null && !ver.isBlank()) ? ver.trim() : (editar != null && !editar.isBlank() ? editar.trim() : null);
        if (idEscolhido != null){
            for (Receita receita : todas){
                if (String.valueOf(receita.getIdReceita()).equals(idEscolhido)){
                    req.setAttribute("registro", receita);
                    req.setAttribute("modo", ver != null && !ver.isBlank() ? "ver" : "editar");
                    // os ingredientes da receita aparecem nos detalhes
                    req.setAttribute("ingredientes", ingredienteDAO.buscarIngredientesDaReceita(receita.getIdReceita()));
                }
            }
        }

        req.setAttribute("receitas", Paginacao.aplicar(req, receitas));
        req.setAttribute("totalGeral", todas.size());
        req.setAttribute("ingredientesPorReceita", resumoDAO.contarIngredientesPorReceita());
        req.setAttribute("usuarios", usuarioDAO.buscarTodosUsuario());
        req.setAttribute("empresas", empresaDAO.buscarTodasEmpresa());
        req.setAttribute("produtos", ingredienteDAO.buscarTodosProdutos());
        req.setAttribute("filtroReceita", filtroReceita == null ? "" : filtroReceita);
        req.setAttribute("filtroDono", filtroDono == null ? "" : filtroDono);
        req.setAttribute("filtroOrd", filtroOrd == null ? "" : filtroOrd);
        req.getRequestDispatcher(jsp_receitas).forward(req, resp);
    }

    // volta para a tela com uma mensagem de erro
    private void voltaComErro(HttpServletRequest req, HttpServletResponse resp, String mensagem)
            throws ServletException, IOException{
        req.setAttribute("erro", mensagem);
        listar(req, resp);
    }

    // proteção contra formato errado nos tipos diferentes de string
    private static Integer lerInt(String txt) {
        try {
            return Integer.parseInt(txt.trim());
        }
        catch (Exception e) {
            return null;
        }
    }
}
