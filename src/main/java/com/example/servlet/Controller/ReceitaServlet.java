package com.example.servlet.Controller;

import com.example.servlet.Dao.EmpresaDAO;
import com.example.servlet.Dao.IngredienteDAO;
import com.example.servlet.Dao.ReceitaDAO;
import com.example.servlet.Dao.UsuarioDAO;
import com.example.servlet.Model.Empresa;
import com.example.servlet.Model.Ingredientes;
import com.example.servlet.Model.Produto;
import com.example.servlet.Model.Receita;
import com.example.servlet.Model.Usuario;
import com.example.servlet.Util.Enum.Categoria;
import com.example.servlet.Util.Validacao;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.time.LocalDate;
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
        // para onde voltar depois de dar certo
        String retorno = validar.definirRetorno(req.getParameter("retorno"), url_servlet);

        if (action == null){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        switch (action){
            case "inserir":{
                // pegando as informações do formulario
                String cpf = req.getParameter("cpf");
                String cnpj = req.getParameter("cnpj");

                // o dono é o usuario ou a empresa
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

                // criando o objeto receita com o dono certo
                Receita receita = montarReceita(req, cpf, cnpj);
                if (receita == null){
                    voltaComErro(req, resp, "Dono da receita não encontrado.");
                    return;
                }

                if (receitaDAO.inserirReceita(receita) == 1){
                    session.setAttribute("sucesso", "Receita cadastrada com sucesso.");
                    resp.sendRedirect(req.getContextPath() + retorno);
                    return;
                }
                else {
                    voltaComErro(req, resp, "Erro ao adicionar essa receita. Por favor tente novamente");
                    return;
                }
            }



            case "alterar":{
                // pegando as informações do formulario
                String cpf = req.getParameter("cpf");
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

                String cnpj = req.getParameter("cnpj");
                // o dono é o usuario ou a empresa
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

                Receita receitaNova = montarReceita(req, cpf, cnpj);
                if (receitaNova == null){
                    voltaComErro(req, resp, "Dono da receita não encontrado.");
                    return;
                }

                if (receitaDAO.alterarReceita(receitaAntiga, receitaNova) == 1){
                    session.setAttribute("sucesso", "Receita alterada com sucesso.");
                    resp.sendRedirect(req.getContextPath() + retorno);
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
                    resp.sendRedirect(req.getContextPath() + retorno);
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

                // validando os campos do produto e do ingrediente
                String erroCampos = validarCamposIngrediente(req);
                if (erroCampos != null){
                    voltaComErro(req, resp, erroCampos);
                    return;
                }
                Produto produto = montarProduto(req);
                int quantidade = lerInt(req.getParameter("quantidade"));

                Ingredientes ingrediente = new Ingredientes(
                        receita,
                        produto,
                        quantidade
                );
                if (ingredienteDAO.inserirIngrediente(ingrediente) == 1){
                    session.setAttribute("sucesso", "Ingrediente cadastrado com sucesso.");
                    resp.sendRedirect(req.getContextPath() + retorno);
                    return;
                }
                else {
                    voltaComErro(req, resp, "Erro ao adicionar esse ingrediente. Por favor tente novamente");
                    return;
                }
            }


            case "alterarIngrediente":{
                Integer idIngrediente = lerInt(req.getParameter("idIngrediente"));
                Integer idReceita = lerInt(req.getParameter("idReceita"));
                if (idIngrediente == null || idReceita == null){
                    voltaComErro(req, resp, "Erro ao alterar esse ingrediente. Por favor tente novamente.");
                    return;
                }
                // o ingrediente antigo traz os ids do produto que será alterado
                Ingredientes ingredienteAntigo = ingredienteDAO.buscarIngrediente(idIngrediente);
                if (ingredienteAntigo == null){
                    voltaComErro(req, resp, "Ingrediente não encontrado.");
                    return;
                }
                // a receita nova pode ser outra
                Receita receita = receitaDAO.buscarReceitaPorId(idReceita);
                if (receita == null){
                    voltaComErro(req, resp, "Receita não encontrada.");
                    return;
                }

                // validando os campos do produto e do ingrediente
                String erroCampos = validarCamposIngrediente(req);
                if (erroCampos != null){
                    voltaComErro(req, resp, erroCampos);
                    return;
                }
                Produto produto = montarProduto(req);
                int quantidade = lerInt(req.getParameter("quantidade"));

                Ingredientes ingredienteNovo = new Ingredientes(
                        receita,
                        produto,
                        quantidade
                );
                if (ingredienteDAO.alterarIngrediente(ingredienteAntigo, ingredienteNovo) == 1){
                    session.setAttribute("sucesso", "Ingrediente alterado com sucesso.");
                    resp.sendRedirect(req.getContextPath() + retorno);
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
                    resp.sendRedirect(req.getContextPath() + retorno);
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

    // confere os campos da receita, devolve a mensagem de erro ou null se esta tudo certo
    private String validarCamposReceita(HttpServletRequest req){
        String nomeReceita = req.getParameter("nomeReceita");
        String modoPreparo = req.getParameter("modoPreparo");
        String dataReceitaTxt = req.getParameter("dataReceita");

        if (nomeReceita == null || nomeReceita.isBlank()){
            return "O nome da receita não deve ser vazio!";
        }
        if (!validar.validarNome(nomeReceita) || nomeReceita.length() > 50){
            return "Nome de receita inválido! Só letras e no máximo 50 caracteres.";
        }
        if (modoPreparo == null || modoPreparo.isBlank()){
            return "O modo de preparo da receita não deve ser vazio!";
        }
        // data vazia usa a data de hoje
        if (dataReceitaTxt != null && !dataReceitaTxt.isBlank() && lerData(dataReceitaTxt) == null){
            return "Data inválida! Por favor Digite novamente.";
        }
        return null;
    }

    // monta a receita com o dono certo ( usuario ou empresa ), devolve null se o dono nao existe
    private Receita montarReceita(HttpServletRequest req, String cpf, String cnpj){
        String nomeReceita = req.getParameter("nomeReceita").trim();
        String descricaoReceita = req.getParameter("descricaoReceita");
        String modoPreparo = req.getParameter("modoPreparo");
        String dataReceitaTxt = req.getParameter("dataReceita");
        // o checkbox so chega no formulario quando esta marcado
        boolean foiFeita = Boolean.parseBoolean(req.getParameter("foiFeita"));

        LocalDate dataReceita = LocalDate.now();
        if (dataReceitaTxt != null && !dataReceitaTxt.isBlank()){
            dataReceita = lerData(dataReceitaTxt);
        }
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

    // confere os campos do produto e da quantidade do ingrediente, devolve a mensagem de erro ou null
    private String validarCamposIngrediente(HttpServletRequest req){
        String nomeProd = req.getParameter("nomeProduto");
        String categoriaTxt = req.getParameter("categoria");
        String marca = req.getParameter("marca");
        String unidadeMedida = req.getParameter("unidadeMedida");

        if (nomeProd == null || nomeProd.isBlank()){
            return "O nome do produto não deve ser vazio!";
        }
        if (!validar.validarNomeProduto(nomeProd) || nomeProd.length() > 20){
            return "Nome do produto inválido! No máximo 20 caracteres.";
        }
        Double preco = lerDouble(req.getParameter("preco"));
        if (preco == null || !validar.validarPreco(preco)){
            return "Preço inválido! Por favor Digite novamente.";
        }
        try {
            Categoria.valueOf(categoriaTxt.trim().toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            return "Categoria inválida! Por favor Digite novamente.";
        }
        if (marca == null || marca.isBlank() || marca.length() > 20){
            return "A marca do produto não deve ser vazia e tem no máximo 20 caracteres!";
        }
        if (unidadeMedida == null || unidadeMedida.isBlank() || unidadeMedida.length() > 10){
            return "A unidade de medida do produto não deve ser vazia e tem no máximo 10 caracteres!";
        }
        Integer quantidade = lerInt(req.getParameter("quantidade"));
        if (quantidade == null || !validar.validarQuantidade(quantidade)){
            return "Quantidade inválida! Por favor Digite novamente.";
        }
        return null;
    }

    // monta o produto com os campos do formulario ( já validados )
    private Produto montarProduto(HttpServletRequest req){
        String nomeProd = req.getParameter("nomeProduto").trim();
        String descricaoProd = req.getParameter("descricaoProd");
        double preco = lerDouble(req.getParameter("preco"));
        Categoria categoria = Categoria.valueOf(req.getParameter("categoria").trim().toUpperCase());
        String marca = req.getParameter("marca").trim();
        String unidadeMedida = req.getParameter("unidadeMedida").trim();

        if (descricaoProd == null  || descricaoProd.isBlank()){
            return new Produto(nomeProd, preco, categoria, marca, unidadeMedida);
        }
        return new Produto(nomeProd, descricaoProd, preco, categoria, marca, unidadeMedida);
    }

    // carrega as listas e mostra a tela
    private void listar(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException{
        List<Receita> receitas = receitaDAO.buscarTodasReceita();
        List<Ingredientes> ingredientes = ingredienteDAO.buscarTodosIngrediente();
        List<Usuario> usuarios = usuarioDAO.buscarTodosUsuario();
        List<Empresa> empresas = empresaDAO.buscarTodasEmpresa();
        req.setAttribute("receitas", receitas);
        req.setAttribute("ingredientes", ingredientes);
        req.setAttribute("usuarios", usuarios);
        req.setAttribute("empresas", empresas);
        req.setAttribute("categorias", Categoria.values());
        req.getRequestDispatcher(jsp_receitas).forward(req, resp);
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

    // proteção contra formato errado nos tipos diferentes de string
    private static Double lerDouble(String txt) {
        try {
            Double valor = Double.parseDouble(txt.trim().replace(",", "."));
            if (valor.isInfinite() || valor.isNaN()){
                return null;
            }
            return valor;
        }
        catch (Exception e) {
            return null;
        }
    }
    private static Integer lerInt(String txt) {
        try {
            return Integer.parseInt(txt.trim());
        }
        catch (Exception e) {
            return null;
        }
    }
    private static LocalDate lerData(String txt) {
        try {
            return LocalDate.parse(txt.trim());
        }
        catch (Exception e) {
            return null;
        }
    }
}
