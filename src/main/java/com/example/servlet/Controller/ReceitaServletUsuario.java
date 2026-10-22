package com.example.servlet.Controller;

import com.example.servlet.Dao.IngredienteDAO;
import com.example.servlet.Dao.ReceitaDAO;
import com.example.servlet.Dao.UsuarioDAO;
import com.example.servlet.Model.*;
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
import java.util.ArrayList;
import java.util.List;

@WebServlet(urlPatterns = {
        "/ver-receita-usuario",
        "/inserir-receita-usuario",
        "/alterar-receita-usuario",
        "/deletar-receita-usuario",


        "/inserir-ingrediente-usuario",
        "/alterar-ingrediente-usuario",
        "/deletar-ingrediente-usuario",
        },
        name = "/ver-receita-usuario")
public class ReceitaServletUsuario extends HttpServlet {
    private Validacao validar;
    private ReceitaDAO receitaDAO;
    private IngredienteDAO ingredienteDAO;
    private UsuarioDAO usuarioDAO;

    // caminho do servlet ( depois das atualizações eles passam por lá antes de aparecer na tela geral)
    private static final String url_servlet = "/ver-receita-usuario";

    // caminhos das paginas
    private static final String jsp_login = "/assets/pages/login.jsp";
    private static final String jsp_ver_receita = "/assets/pages/ver-receita-usuario.jsp";
    private static final String jsp_inserir_receita = "/assets/pages/inserir-receita-usuario.jsp";
    private static final String jsp_alterar_receita = "/assets/pages/alterar-receita-usuario.jsp";
    private static final String jsp_deletar_receita = "/assets/pages/deletar-receita-usuario.jsp";

    private static final String jsp_inserir_ingrediente = "/assets/pages/inserir-ingrediente-usuario.jsp";
    private static final String jsp_alterar_ingrediente = "/assets/pages/alterar-ingrediente-usuario.jsp";
    private static final String jsp_deletar_ingrediente = "/assets/pages/deletar-ingrediente-usuario.jsp";

    @Override
    public void init(){
        validar = new Validacao();
        receitaDAO = new ReceitaDAO();
        ingredienteDAO =  new IngredienteDAO();
        usuarioDAO = new UsuarioDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        // verifica se o usuario esta logado
        if (session == null || session.getAttribute("usuarioEmail") == null){
            resp.sendRedirect(req.getContextPath() + jsp_login);
            return;
        }
        String email = (String) session.getAttribute("usuarioEmail");
        Usuario usuario = usuarioDAO.buscarUsuario(email);
        // verificando se o usuario ainda existe
        if (usuario == null) {
            session.invalidate();
            resp.sendRedirect(req.getContextPath() + jsp_login);
            return;
        }
        // se nao for a tela geral entao é erro
        if (!req.getServletPath().equals("/ver-compra-empresa")) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        List<Ingredientes> receitas = ingredienteDAO.buscarVariasReceitas();
        List<Ingredientes> unicaReceita = new ArrayList<>();
        boolean filtrado = false;

        String nome = req.getParameter("nomeReceita");
        if (nome != null && !nome.isBlank()) {
            boolean vNome = validar.validarNomeProduto(nome);
            if (!vNome) {
                req.setAttribute("erro", "Esse nome não bate com nenhuma das opções!!!");// só avisa, a tela continua com a lista
            }
        }

        req.setAttribute();
        req.setAttribute("comprasPelaData", comprasPelaData);
        req.setAttribute("filtrando", filtrando);
        req.getRequestDispatcher(jsp_ver_compra).forward(req, resp);

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);

        // vendo se o usuario ainda está logado
        if (session == null || session.getAttribute("usuarioEmail") == null){
            voltaComErro(req, resp, "Usuário não encontrado!!! Faça login para continuar.", jsp_login);
            return;
        }

        String email = (String) session.getAttribute("usuarioEmail");
        Usuario usuario = usuarioDAO.buscarUsuario(email);
        if (usuario == null){
            voltaComErro(req, resp, "Usuário não encontrado. Por favor faça login novamente.", jsp_login);
        }

        // pegando o caminho do servlet
        String path = req.getServletPath();

        switch (path){
            case "/inserir-receita-usuario":{
                // pegando as informações do formulario
                // produto
                String nomeProd = req.getParameter("nomeProduto");
                String descricaoProd = req.getParameter("descricaoProd");
                String precoTxt = req.getParameter("preco");
                String categoriaTxt = req.getParameter("categoria");
                String marca = req.getParameter("marca");
                String unidadeMedida = req.getParameter("unidadeMedida");

                // receita
                String nomeReceita = req.getParameter("nomeReceita");
                String descricaoReceita = req.getParameter("descricaoReceita");
                String modoPreparo = req.getParameter("modoPreparo");
                String dataReceitaTxt = req.getParameter("dataReceita");
                String foiFeitaTxt = req.getParameter("foiFeita");

                // ingrediente
                String quantidadeTxt = req.getParameter("quantidade");

                // verificando se tem campos nulos
                if (nomeProd == null || nomeProd.isBlank()){
                    voltaComErro(req, resp, "O nome do produto não deve ser vazio!", jsp_inserir_receita);
                    return;
                }
                if (precoTxt == null || precoTxt.isBlank()){
                    voltaComErro(req, resp, "O preço do produto não deve ser vazio!", jsp_inserir_receita);
                    return;
                }
                if (categoriaTxt == null || categoriaTxt.isBlank()){
                    voltaComErro(req, resp, "A categoria do produto não deve ser vazia!", jsp_inserir_receita);
                    return;
                }
                if (marca == null || marca.isBlank()){
                    voltaComErro(req, resp, "A marca do produto não deve ser vazia!", jsp_inserir_receita);
                    return;
                }
                if (unidadeMedida == null || unidadeMedida.isBlank()){
                    voltaComErro(req, resp, "A unidade de medida do produto não deve ser vazia!", jsp_inserir_receita);
                    return;
                }
                if (nomeReceita == null || nomeReceita.isBlank()){
                    voltaComErro(req, resp, "O nome da receita não deve ser vazia!", jsp_inserir_receita);
                    return;
                }
                if (modoPreparo == null || modoPreparo.isBlank()){
                    voltaComErro(req, resp, "O modo de preparo da receita não deve ser vazio!", jsp_inserir_receita);
                    return;
                }
                if (dataReceitaTxt == null || dataReceitaTxt.isBlank()){
                    voltaComErro(req, resp, "A data de preparo da receita não deve ser vazia!", jsp_inserir_receita);
                    return;
                }
                if (foiFeitaTxt == null || foiFeitaTxt.isBlank()){
                    voltaComErro(req, resp, "A pergunta Foi feita? deve ser respondida!!", jsp_inserir_receita);
                    return;
                }

                // convertendo e validadando os dados

                boolean vNomeProd = validar.validarNomeProduto(nomeProd);
                if (!vNomeProd){
                    voltaComErro(req, resp, "Nome do produto inválido! Por favor digite novamente.", jsp_inserir_receita);
                    return;
                }

                Double preco = Double.parseDouble(precoTxt.replace(",", "."));
                boolean vPreco = validar.validarPreco(preco);
                if (preco == null || !vPreco){
                    voltaComErro(req, resp, "Preço inválido! Por favor Digite novamente.", jsp_inserir_receita);
                    return;
                }

                Categoria categoria;
                try {
                    categoria = Categoria.valueOf(categoriaTxt);
                } catch (IllegalArgumentException e) {
                    voltaComErro(req, resp, "Categoria inválida! Por favor Digite novamente.", jsp_inserir_receita);
                    return;
                }


                boolean vNomeReceita = validar.validarNome(nomeReceita);
                if(!vNomeReceita){
                    voltaComErro(req, resp, "Nome de receita inválido! Por favor Digite novamente.", jsp_inserir_receita);
                    return;
                }

                Integer quantidade = Integer.parseInt(quantidadeTxt);
                boolean vQuant = validar.validarQuantidade(quantidade);
                if (quantidade == null || !vQuant){
                    voltaComErro(req, resp, "Qunatidade inválida! Por favor Digite novamente.", jsp_inserir_receita);
                    return;
                }

                LocalDate dataReceita = LocalDate.parse(dataReceitaTxt);
                if (dataReceita == null){
                    voltaComErro(req, resp, "Data inválida! Por favor Digite novamente.", jsp_inserir_receita);
                    return;
                }
                boolean foiFeita = Boolean.parseBoolean(foiFeitaTxt);

                // criando o objeto produto, receita e ingrediente
                Produto produto;
                Receita receita;
                Ingredientes ingrediente;


                if (descricaoProd == null  || descricaoProd.isBlank()){
                    produto = new Produto(nomeProd, preco, categoria, marca, unidadeMedida);
                }
                else {
                    produto = new Produto(nomeProd, descricaoProd, preco, categoria, marca, unidadeMedida);
                }

                if (descricaoReceita == null  || descricaoReceita.isBlank()){
                    receita = new Receita(usuario, nomeReceita, modoPreparo, dataReceita, foiFeita);
                }
                else {
                    receita = new Receita(usuario, nomeReceita, descricaoReceita, modoPreparo, dataReceita, foiFeita);
                }

                // inserindo no banco
                ingrediente = new Ingredientes(
                        receita,
                        produto,
                        quantidade
                );
                if (ingredienteDAO.inserirIngrediente(ingrediente) == 1){
                    resp.sendRedirect(req.getContextPath() + url_servlet);
                    return;
                }
                else {
                    voltaComErro(req, resp, "Erro ao adicionar essa receita. Por favor tente novamente", jsp_inserir_receita);
                    return;
                }
            }



            case "/alterar-receita-usuario":{
                // pegando as informações do formulario
                String idIngredienteTxt = req.getParameter("idIngrediente");
                // produto
                String nomeProd = req.getParameter("nomeProduto");
                String descricaoProd = req.getParameter("descricaoProd");
                String precoTxt = req.getParameter("preco");
                String categoriaTxt = req.getParameter("categoria");
                String marca = req.getParameter("marca");
                String unidadeMedida = req.getParameter("unidadeMedida");

                // receita
                String nomeReceita = req.getParameter("nomeReceita");
                String descricaoReceita = req.getParameter("descricaoReceita");
                String modoPreparo = req.getParameter("modoPreparo");
                String dataReceitaTxt = req.getParameter("dataReceita");
                String foiFeitaTxt = req.getParameter("foiFeita");

                // ingrediente
                String quantidadeTxt = req.getParameter("quantidade");

                // verificando se tem campos nulos
                if (nomeProd == null || nomeProd.isBlank()){
                    voltaComErro(req, resp, "O nome do produto não deve ser vazio!", jsp_inserir_receita);
                    return;
                }
                if (precoTxt == null || precoTxt.isBlank()){
                    voltaComErro(req, resp, "O preço do produto não deve ser vazio!", jsp_inserir_receita);
                    return;
                }
                if (categoriaTxt == null || categoriaTxt.isBlank()){
                    voltaComErro(req, resp, "A categoria do produto não deve ser vazia!", jsp_inserir_receita);
                    return;
                }
                if (marca == null || marca.isBlank()){
                    voltaComErro(req, resp, "A marca do produto não deve ser vazia!", jsp_inserir_receita);
                    return;
                }
                if (unidadeMedida == null || unidadeMedida.isBlank()){
                    voltaComErro(req, resp, "A unidade de medida do produto não deve ser vazia!", jsp_inserir_receita);
                    return;
                }
                if (nomeReceita == null || nomeReceita.isBlank()){
                    voltaComErro(req, resp, "O nome da receita não deve ser vazia!", jsp_inserir_receita);
                    return;
                }
                if (modoPreparo == null || modoPreparo.isBlank()){
                    voltaComErro(req, resp, "O modo de preparo da receita não deve ser vazio!", jsp_inserir_receita);
                    return;
                }
                if (dataReceitaTxt == null || dataReceitaTxt.isBlank()){
                    voltaComErro(req, resp, "A data de preparo da receita não deve ser vazia!", jsp_inserir_receita);
                    return;
                }
                if (foiFeitaTxt == null || foiFeitaTxt.isBlank()){
                    voltaComErro(req, resp, "A pergunta Foi feita? deve ser respondida!!", jsp_inserir_receita);
                    return;
                }

                // convertendo e validadando os dados

                boolean vNomeProd = validar.validarNomeProduto(nomeProd);
                if (!vNomeProd){
                    voltaComErro(req, resp, "Nome do produto inválido! Por favor digite novamente.", jsp_inserir_receita);
                    return;
                }

                Double preco = Double.parseDouble(precoTxt.replace(",", "."));
                boolean vPreco = validar.validarPreco(preco);
                if (preco == null || !vPreco){
                    voltaComErro(req, resp, "Preço inválido! Por favor Digite novamente.", jsp_inserir_receita);
                    return;
                }

                Categoria categoria;
                try {
                    categoria = Categoria.valueOf(categoriaTxt);
                } catch (IllegalArgumentException e) {
                    voltaComErro(req, resp, "Categoria inválida! Por favor Digite novamente.", jsp_inserir_receita);
                    return;
                }


                boolean vNomeReceita = validar.validarNome(nomeReceita);
                if(!vNomeReceita){
                    voltaComErro(req, resp, "Nome de receita inválido! Por favor Digite novamente.", jsp_inserir_receita);
                    return;
                }

                Integer quantidade = Integer.parseInt(quantidadeTxt);
                boolean vQuant = validar.validarQuantidade(quantidade);
                if (quantidade == null || !vQuant){
                    voltaComErro(req, resp, "Qunatidade inválida! Por favor Digite novamente.", jsp_inserir_receita);
                    return;
                }

                LocalDate dataReceita = LocalDate.parse(dataReceitaTxt);
                if (dataReceita == null){
                    voltaComErro(req, resp, "Data inválida! Por favor Digite novamente.", jsp_inserir_receita);
                    return;
                }
                boolean foiFeita = Boolean.parseBoolean(foiFeitaTxt);


                // o ingrediente antigo traz os ids do produto e da receita que serão alterados
                int idIngrediente;
                try {
                    idIngrediente = Integer.parseInt(idIngredienteTxt);
                } catch (NumberFormatException e) {
                    voltaComErro(req, resp, "Erro ao alterar essa receita. Por favor tente novamente.", jsp_alterar_receita);
                    return;
                }
                Ingredientes ingredienteAntigo = ingredienteDAO.buscarIngrediente(idIngrediente);
                if (ingredienteAntigo == null){
                    voltaComErro(req, resp, "Erro ao alterar essa receita. Por favor tente novamente.", jsp_alterar_receita);
                    return;
                }

                // criando o objeto produto, receita e ingrediente
                Produto produto;
                Receita receita;
                Ingredientes ingrediente;


                if (descricaoProd == null  || descricaoProd.isBlank()){
                    produto = new Produto(nomeProd, preco, categoria, marca, unidadeMedida);
                }
                else {
                    produto = new Produto(nomeProd, descricaoProd, preco, categoria, marca, unidadeMedida);
                }

                if (descricaoReceita == null  || descricaoReceita.isBlank()){
                    receita = new Receita(usuario, nomeReceita, modoPreparo, dataReceita, foiFeita);
                }
                else {
                    receita = new Receita(usuario, nomeReceita, descricaoReceita, modoPreparo, dataReceita, foiFeita);
                }

                // alterando no banco
                ingrediente = new Ingredientes(
                        receita,
                        produto,
                        quantidade
                );
                if (ingredienteDAO.alterarIngrediente(ingredienteAntigo, ingrediente) == 1){
                    resp.sendRedirect(req.getContextPath() + url_servlet);
                    return;
                }
                else {
                    voltaComErro(req, resp, "Erro ao alterar essa receita. Por favor tente novamente", jsp_alterar_receita);
                    return;
                }
            }
        }

    }
    // volta para a pagina com uma mensagem de erro
    public static void voltaComErro(HttpServletRequest req, HttpServletResponse resp, String erro, String pagina)
            throws ServletException, IOException{
        req.setAttribute("erro", erro);
        req.getRequestDispatcher(pagina).forward(req, resp);
    }
}