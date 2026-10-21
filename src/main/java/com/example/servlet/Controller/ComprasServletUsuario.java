package com.example.servlet.Controller;

import com.example.servlet.Dao.ListaComprasDAO;
import com.example.servlet.Dao.ProdutoListaDAO;
import com.example.servlet.Dao.UsuarioDAO;
import com.example.servlet.Model.ListaCompras;
import com.example.servlet.Model.Produto;
import com.example.servlet.Model.ProdutoLista;
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
import java.util.ArrayList;
import java.util.List;

@WebServlet(urlPatterns = {
        "/ver-compra-usuario",
        "/inserir-compra-usuario",
        "/alterar-compra-usuario",
        "/deletar-compra-usuario",

        "/inserir-produto-lista-usuario",
        "/alterar-produto-lista-usuario",
        "/deletar-produto-lista-usuario"},
       name = "/ver-compra-usuario")
public class ComprasServletUsuario extends HttpServlet {
    private ListaComprasDAO listaDAO;
    private ProdutoListaDAO prodListaDAO;
    private UsuarioDAO usuarioDAO;
    private Validacao validar;

    // caminho para o servlet logo após concluir alguma tarefa
    private static final String url_ver_compra = "/ver-compra-usuario";

    // caminhos das paginas
    private static final String jsp_login = "/assets/pages/login.jsp";
    private static final String jsp_ver_compra = "/assets/pages/ver-compra-usuario.jsp";
    private static final String jsp_inserir_compra = "/assets/pages/inserir-compra-usuario.jsp";
    private static final String jsp_alterar_compra = "/assets/pages/alterar-compra-usuario.jsp";
    private static final String jsp_deletar_compra = "/assets/pages/deletar-compra-usuario.jsp";

    private static final String jsp_inserir_item_lista = "/assets/pages/inserir-produto-lista-usuario.jsp";
    private static final String jsp_alterar_item_lista = "/assets/pages/alterar-produto-lista-usuario.jsp";
    private static final String jsp_deletar_item_lista = "/assets/pages/deletar-produto-lista-usuario.jsp";

    @Override
    public void init(){
        listaDAO = new ListaComprasDAO();
        prodListaDAO = new ProdutoListaDAO();
        usuarioDAO = new UsuarioDAO();
        validar = new Validacao();
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
        if (!req.getServletPath().equals("/ver-compra-usuario")) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        List<ListaCompras> todasCompras = listaDAO.buscarTodasComprasUsuario(usuario.getCpf());
        List<ProdutoLista> comprasPelaData = new ArrayList<>();
        boolean filtrando = false;

        String dtTxt = req.getParameter("dtListaCompra");
        if (dtTxt != null && !dtTxt.isBlank()) {
            LocalDate dtCompra = lerData(dtTxt);
            if (dtCompra == null) {
                req.setAttribute("erro", "Data inválida!!!");// só avisa, a tela continua com a lista
            } else {
                if (!validar.validarDatas1Ano(dtCompra)){
                    req.setAttribute("erro", "A data tem um limite de 6 meses para frente e 6 meses para trás!!!");// só avisa, a tela continua com a lista
                }
                else {
                    filtrando = true;
                    comprasPelaData = listaDAO.buscarListaPelaDataUsuario(usuario.getCpf(), dtCompra);
                }
            }
        }

        req.setAttribute("compras", todasCompras);
        req.setAttribute("comprasPelaData", comprasPelaData);
        req.setAttribute("filtrando", filtrando);
        req.getRequestDispatcher(jsp_ver_compra).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        // vericando se o usuario esta logado
        if (session == null || session.getAttribute("usuarioEmail") == null){
            resp.sendRedirect(req.getContextPath() + jsp_login);
            return;
        }



        // resgatando o email da ultima requisição
        String email = (String) session.getAttribute("usuarioEmail");
        Usuario usuario = usuarioDAO.buscarUsuario(email);
        // verificando se o usuario ainda existe
        if (usuario == null) {
            session.invalidate();
            resp.sendRedirect(req.getContextPath() + jsp_login);
            return;
        }

        // pegando o caminho do servlet
        String path = req.getServletPath();

        switch (path){
            case "/inserir-compra-usuario":{
                // pegando os valores do formulario
                // produto
                String nome = req.getParameter("nome");
                String descricao = req.getParameter("descricao");
                String precoTxt = req.getParameter("preco");
                String categoriaTxt = req.getParameter("categoria");
                Categoria categoria;
                try {
                    categoria = Categoria.valueOf(categoriaTxt);
                }
                catch (IllegalArgumentException | NullPointerException e){
                    voltaComErro(req, resp, "A categoria não pode ser vazia!!!", jsp_inserir_compra);
                    return;
                }
                String marca = req.getParameter("marca");
                String unidadeMedida = req.getParameter("unidadeMedida");

                // produto lista
                String quantidadeTxt = req.getParameter("quantidade");

                // lista de compras
                String dtListaTxt = req.getParameter("dtLista");

                // convertendo os valores
                Double preco = lerDouble(precoTxt);
                Integer quantidade = lerInt(quantidadeTxt);
                LocalDate dtLista = lerData(dtListaTxt);

                if (preco == null){
                    voltaComErro(req, resp, "Preço inválido!!!", jsp_inserir_compra);
                    return;
                }
                if (quantidade == null){
                    voltaComErro(req, resp, "Quantidade inválida!!!", jsp_inserir_compra);
                    return;
                }
                if (dtLista == null){
                    voltaComErro(req, resp, "Data inválida!!!", jsp_inserir_compra);
                    return;
                }




                // validando os dados
                boolean vNome = validar.validarNomeProduto(nome);
                boolean vPreco = validar.validarPreco(preco);
                boolean vQuantidade = validar.validarQuantidade(quantidade);
                boolean vData = validar.validarDatas1Ano(dtLista);


                if (!vNome){
                    voltaComErro(req, resp, "Nome inválido!!!", jsp_inserir_compra);
                    return;
                }
                if (!vPreco){
                    voltaComErro(req, resp, "Preço inválido!!! Deve ser maior zero.", jsp_inserir_compra);
                    return;
                }

                if (!vQuantidade){
                    voltaComErro(req, resp, "Quantidade inválida!!! Deve ser maior que zero.", jsp_inserir_compra);
                    return;
                }
                if (!vData){
                    voltaComErro(req, resp, "Data inválida!!! Deve estar entre o intervalo de 1 ano, 6 meses para frente e 6 meses para trás", jsp_inserir_compra);
                    return;
                }
                if (marca == null || marca.isBlank()){
                    voltaComErro(req, resp, "a Marca não pode ser vazia!!!", jsp_inserir_compra);
                    return;
                }

                if (unidadeMedida == null || unidadeMedida.isBlank()){
                    voltaComErro(req, resp, "A unidade de medida não pode ser vazia!!!", jsp_inserir_compra);
                    return;
                }


                Produto prod;
                ProdutoLista prodLista;
                ListaCompras compras;
                if (descricao == null || descricao.isBlank()){
                    prod = new Produto(nome, preco, categoria, marca, unidadeMedida);
                }
                else {
                    prod = new Produto(nome, descricao, preco, categoria, marca, unidadeMedida);
                }

                compras = new ListaCompras(usuario, dtLista);
                prodLista = new ProdutoLista(compras, prod, quantidade);
                if (prodListaDAO.adicionarProdutoListas(prodLista) == 1){
                    resp.sendRedirect(req.getContextPath() + url_ver_compra);
                    break;
                }else {
                    voltaComErro(req, resp, "Não foi possível adicionar essa compra.", jsp_inserir_compra);
                    break;
                }

            }


            case "/alterar-compra-usuario":{
                // pegando o id da copra que será alterada
                String idCompraTxt = req.getParameter("idCompra");
                Integer idCompra = lerInt(idCompraTxt);
                if (idCompra == null){
                    System.err.println("o id da compra nunca pode ser nulo!!!");
                    voltaComErro(req, resp, "Erro ao alterar essa compra. Por favor tente novamente.", jsp_alterar_compra);
                    return;
                }
                ListaCompras compraAntiga = listaDAO.buscarCompraUsuarioPorId(usuario.getCpf(), idCompra);
                if (compraAntiga == null) {
                    System.err.println("Compra não encontrada!!!");
                    voltaComErro(req, resp, "Erro ao alterar essa compra. Por favor tente novamente.", jsp_alterar_compra);
                    return;
                }

                // pegando a nova data do formulario
                String dtListaNovaTxt = req.getParameter("dtLista");
                LocalDate dtListaNova = lerData(dtListaNovaTxt);

                if (dtListaNova == null){
                    voltaComErro(req, resp, "Data inválida!!!", jsp_alterar_compra);
                    return;
                }

                // validando a data
                boolean vData = validar.validarDatas1Ano(dtListaNova);
                if (!vData){
                    voltaComErro(req, resp, "Data inválida!!! Deve estar entre o intervalo de 1 ano, 6 meses para frente e 6 meses para trás", jsp_alterar_compra);
                    return;
                }

                ListaCompras compraNova = new ListaCompras(usuario, dtListaNova);

                if (listaDAO.alterarListaComprasUsuario(compraAntiga, compraNova) == 1){
                    resp.sendRedirect(req.getContextPath() + url_ver_compra);
                    break;
                }
                else {
                    voltaComErro(req, resp, "Não foi possível alterar essa compra.", jsp_alterar_compra);
                    break;
                }
            }

            case "/deletar-compra-usuario":{
                // pegando o id da compra no formulario
                String idCompraTxt = req.getParameter("idCompra");
                Integer idCompra = lerInt(idCompraTxt);
                if (idCompra == null){
                    System.err.println("o id não pode ser null nunca!!!");
                    voltaComErro(req, resp, "Erro ao deletar essa compra. Por favor tente novamente.", jsp_deletar_compra);
                    return;
                }
                ListaCompras compra = listaDAO.buscarCompraUsuarioPorId(usuario.getCpf(), idCompra);
                if (compra == null){
                    System.err.println("Compra não encontrada.");
                    voltaComErro(req, resp, "Erro ao deletar essa compra. Por favor tente novamente.", jsp_deletar_compra);
                    return;
                }

                if (listaDAO.deletarListaCompras(compra) == 1){
                    resp.sendRedirect(req.getContextPath() + url_ver_compra);
                    break;
                }
                else {
                    voltaComErro(req, resp, "Não foi possível excluir essa compra.", jsp_deletar_compra);
                    break;
                }
            }


            case "/inserir-produto-lista-usuario":{
                // objeto do produto
                Produto produto;

                // pegando as informações do produto
                String nome = req.getParameter("nome");
                String descricao = req.getParameter("descricao");
                String precoTxt = req.getParameter("preco");
                String categoriaTxt = req.getParameter("categoria");
                Categoria categoria;
                try {
                    categoria = Categoria.valueOf(categoriaTxt);
                }catch (IllegalArgumentException | NullPointerException e){
                    voltaComErro(req, resp, "A categoria não pode ser vazia!!!", jsp_inserir_item_lista);
                    return;
                }
                String marca = req.getParameter("marca");
                String unidadeMedida = req.getParameter("unidadeMedida");

                // pegando a quantidade
                String quantTxt = req.getParameter("quantidade");

                // pegando o id da compra no formulario
                String idCompraTxt = req.getParameter("idCompra");

                // convertendo para os tipos certos
                Double preco = lerDouble(precoTxt);
                Integer idCompra = lerInt(idCompraTxt);
                Integer quantidade = lerInt(quantTxt);

                if (preco == null){
                    voltaComErro(req, resp, "Preço inválido!!!", jsp_inserir_item_lista);
                    return;
                }
                if (quantidade == null){
                    voltaComErro(req, resp, "Quantidade inválida!!!", jsp_inserir_item_lista);
                    return;
                }
                if (idCompra == null){
                    voltaComErro(req, resp, "Erro ao adicionar esse produto nessa lista. Por favor tente novamente.", jsp_inserir_item_lista);
                    return;
                }


                // pegando a compra ja existente do banco
                ListaCompras compra = listaDAO.buscarCompraUsuarioPorId(usuario.getCpf(), idCompra);

                if (compra == null){
                    System.err.println("a compra nunca deve ser nula!!!");
                    voltaComErro(req, resp, "Erro ao tentar adicionar esse produto nessa lista. Por favor tente novamente.", jsp_inserir_item_lista);
                    return;
                }

                // validando os campos do produto
                boolean vNome = validar.validarNomeProduto(nome);
                boolean vPreco = validar.validarPreco(preco);
                boolean vQuantidade = validar.validarQuantidade(quantidade);

                if (!vNome) {
                    voltaComErro(req, resp, "Nome inválido!!!", jsp_inserir_item_lista);
                    return;
                }
                if (!vPreco) {
                    voltaComErro(req, resp, "Preço inválido!!! Deve ser maior que zero.", jsp_inserir_item_lista);
                    return;
                }
                if (!vQuantidade) {
                    voltaComErro(req, resp, "Quantidade inválida!!! Deve ser maior que zero", jsp_inserir_item_lista);
                    return;
                }
                if (marca == null || marca.isBlank()) {
                    voltaComErro(req, resp, "A marca não pode ser vazia!!!", jsp_inserir_item_lista);
                    return;
                }
                if (unidadeMedida == null || unidadeMedida.isBlank()) {
                    voltaComErro(req, resp, "A unidade de medida não pode ser vazia!!!", jsp_inserir_item_lista);
                    return;
                }


                if (descricao == null || descricao.isBlank()){
                    produto = new Produto(nome, preco, categoria, marca, unidadeMedida);
                }
                else {
                    produto = new Produto(nome, descricao, preco, categoria, marca, unidadeMedida);
                }

                ProdutoLista prodLista = new ProdutoLista(compra, produto, quantidade);
                if (prodListaDAO.adicionarProdutoListas(prodLista) == 1){
                    resp.sendRedirect(req.getContextPath() + url_ver_compra);
                    return;
                }
                else {
                    voltaComErro(req, resp, "Não foi possível adicionar esse produto a essa lista de compras. Por favor tente novamente", jsp_inserir_item_lista);
                    return;
                }
            }


            case "/alterar-produto-lista-usuario":{
                // objeto do novo produto e o novo produto na lista de compras
                Produto produtoNovo;
                ProdutoLista prodListaNovo;



                // pegando o id da compra no formulario
                String idCompraTxt = req.getParameter("idCompra");
                Integer idCompra = lerInt(idCompraTxt);
                if (idCompra == null){
                    System.err.println("O id da compra nunca pode ser nulo!!!");
                    voltaComErro(req, resp, "Erro ao alterar esse produto dessa compra. Por favor tente novamente.", jsp_alterar_item_lista);
                    return;
                }
                // pegando a compra ja existente do banco
                ListaCompras compra = listaDAO.buscarCompraUsuarioPorId(usuario.getCpf(), idCompra);
                if (compra == null){
                    System.err.println("compra não encontrada!!!");
                    voltaComErro(req, resp, "Erro ao alterar essa compra. Por favor tente novamente.", jsp_alterar_item_lista);
                    return;
                }

                // pegando o id do produto na lista para mais tarde ser usado para localizar o objeto que sera alterado
                String idProdListaTxt = req.getParameter("idProdutoLista");
                Integer idProdLista = lerInt(idProdListaTxt);
                if (idProdLista == null){
                    System.err.println("O id do produto na lista de compras nunca pode ser nulo!!!");
                    voltaComErro(req, resp, "Erro ao alterar esse produto dessa compra. Por favor tente novamente.", jsp_alterar_item_lista);
                    return;
                }

                // resgatando o item na lista que esta no banco
                ProdutoLista prodListaAntigo = prodListaDAO.buscarProdutoNumaLista(idProdLista, idCompra);
                if (prodListaAntigo == null){
                    System.err.println("!!!");
                    voltaComErro(req, resp, "Erro ao alterar esse produto dessa compra. Por favor tente novamente.", jsp_alterar_item_lista);
                    return;
                }


                // pegando os dados do novo produto do formulario
                String nome = req.getParameter("nome");
                String descricao = req.getParameter("descricao");
                String precoTxt = req.getParameter("preco");
                String categoriaTxt = req.getParameter("categoria");
                Categoria categoria;
                try {
                    categoria = Categoria.valueOf(categoriaTxt);
                } catch (IllegalArgumentException | NullPointerException e){
                    voltaComErro(req, resp, "A categoria não pode ser vazia!!!", jsp_alterar_item_lista);
                    return;
                }
                String marca = req.getParameter("marca");
                String unidadeMedida = req.getParameter("unidadeMedida");

                // convertendo para double
                Double preco = lerDouble(precoTxt);

                if (preco == null){
                    voltaComErro(req, resp, "Preço inválido!!!", jsp_alterar_item_lista);
                    return;
                }


                // pegando a quantidade de produtos na lista
                String quantidadeTxt = req.getParameter("quantidade");
                // convertendo para int
                Integer quantidade = lerInt(quantidadeTxt);

                if (quantidade == null){
                    voltaComErro(req, resp, "Quantidade inválida!!!", jsp_alterar_item_lista);
                    return;
                }

                // validando os campos do produto
                boolean vNome = validar.validarNomeProduto(nome);
                boolean vPreco = validar.validarPreco(preco);
                boolean vQuantidade = validar.validarQuantidade(quantidade);


                if (!vNome) {
                    voltaComErro(req, resp, "Nome inválido!!!", jsp_alterar_item_lista);
                    return;
                }
                if (!vPreco) {
                    voltaComErro(req, resp, "Preço inválido!!! Deve ser maior que zero.", jsp_alterar_item_lista);
                    return;
                }
                if (!vQuantidade) {
                    voltaComErro(req, resp, "Quantidade inválida!!! Deve ser maior que zero", jsp_alterar_item_lista);
                    return;
                }
                if (marca == null || marca.isBlank()) {
                    voltaComErro(req, resp, "A marca não pode ser vazia!!!", jsp_alterar_item_lista);
                    return;
                }
                if (unidadeMedida == null || unidadeMedida.isBlank()) {
                    voltaComErro(req, resp, "A unidade de medida não pode ser vazia!!!", jsp_alterar_item_lista);
                    return;
                }



                if (descricao == null || descricao.isBlank()){
                    produtoNovo = new Produto(nome, preco, categoria, marca, unidadeMedida);
                }
                else {
                    produtoNovo = new Produto(nome, descricao, preco, categoria, marca, unidadeMedida);
                }

                prodListaNovo = new ProdutoLista(compra, produtoNovo, quantidade);
                if(prodListaDAO.alterarProdutoListas(prodListaAntigo, prodListaNovo) == 1){
                    resp.sendRedirect(req.getContextPath() + url_ver_compra);
                    break;
                }
                else {
                    voltaComErro(req, resp, "Não foi possível alterar esse produto dessa lista!!!", jsp_alterar_item_lista);
                    break;
                }
            }

            case "/deletar-produto-lista-usuario":{
                // pegando o id do item na compra do formulario
                String idProdutoListaTxt = req.getParameter("idProdutoLista");
                String idCompraTxt = req.getParameter("idCompra");
                // convertendo os dados
                Integer idProdLista = lerInt(idProdutoListaTxt);
                Integer idCompra = lerInt(idCompraTxt);
                if (idProdLista == null){
                    System.err.println("o id do produto na lista nunca pode ser nulo!!!");
                    voltaComErro(req, resp, "Erro ao deletar esse produto nessa compra. Por favor tente novamente.", jsp_deletar_item_lista);
                    return;
                }
                if (idCompra == null){
                    System.err.println("o id da compra nunca pode ser nulo!!!");
                    voltaComErro(req, resp, "Erro ao deletar esse produto nessa compra. Por favor tente novamente.", jsp_deletar_item_lista);
                    return;
                }

                // verificando se essa compra é do usuario certo
                ListaCompras compraValidacao = listaDAO.buscarCompraUsuarioPorId(usuario.getCpf(), idCompra);
                if (compraValidacao == null){
                    voltaComErro(req, resp, "Erro ao deletar esse produto dessa lista. Por favor tente novamente.", jsp_deletar_item_lista);
                    return;
                }

                ProdutoLista prodLista = prodListaDAO.buscarProdutoNumaLista(idProdLista, idCompra);
                if (prodLista == null){
                    voltaComErro(req, resp, "Erro ao deletar esse produto dessa lista. Por favor tente novamente.", jsp_deletar_item_lista);
                    return;
                }
                if(prodListaDAO.deletarProdutoListas(prodLista) == 1){
                    resp.sendRedirect(req.getContextPath() + url_ver_compra);
                    break;
                }
                else{
                    voltaComErro(req, resp, "Não foi possível excluir esse produto dessa lista!!!", jsp_deletar_item_lista);
                    break;
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