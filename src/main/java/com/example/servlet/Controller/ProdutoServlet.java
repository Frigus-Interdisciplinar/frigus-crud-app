package com.example.servlet.Controller;

import com.example.servlet.Dao.AssinaturaDAO;
import com.example.servlet.Dao.FrigorificosDAO;
import com.example.servlet.Dao.GeladeiraDAO;
import com.example.servlet.Dao.ProdutosFrigorificoDAO;
import com.example.servlet.Dao.ProdutosGeladeiraDAO;
import com.example.servlet.Model.Assinatura;
import com.example.servlet.Model.Frigorificos;
import com.example.servlet.Model.Geladeira;
import com.example.servlet.Model.Produto;
import com.example.servlet.Model.ProdutosFrigorifico;
import com.example.servlet.Model.ProdutosGeladeira;
import com.example.servlet.Util.Enum.Categoria;
import com.example.servlet.Util.Service.ArmazenamentoService;
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
        "/admin/produtos"},
        name = "produtos")
public class ProdutoServlet extends HttpServlet {
    private ProdutosGeladeiraDAO igDAO;
    private ProdutosFrigorificoDAO itfDAO;
    private GeladeiraDAO geladeiraDAO;
    private FrigorificosDAO frigorificoDAO;
    private AssinaturaDAO assinaturaDAO;
    private Validacao validar;

    // caminho das paginas
    private static final String jsp_produtos = "/WEB-INF/pages/produtos.jsp";

    // caminho para o servlet
    private static final String url_servlet = "/admin/produtos";

    @Override
    public void init(){
        igDAO = new ProdutosGeladeiraDAO();
        itfDAO = new ProdutosFrigorificoDAO();
        geladeiraDAO = new GeladeiraDAO();
        frigorificoDAO = new FrigorificosDAO();
        assinaturaDAO = new AssinaturaDAO();
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
            case "/admin/produtos":{
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
        // o item fica na geladeira ou no frigorifico
        String armazenamento = req.getParameter("armazenamento");
        // para onde voltar depois de dar certo
        String retorno = validar.definirRetorno(req.getParameter("retorno"), url_servlet);

        if (action == null){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        if (armazenamento == null || !(armazenamento.equals("geladeira") || armazenamento.equals("frigorifico"))){
            voltaComErro(req, resp, "Escolha se o item fica numa geladeira ou num frigorífico.");
            return;
        }

        switch (action){
            case "inserir":{
                // validando os campos do produto e do item
                String erroCampos = validarCampos(req);
                if (erroCampos != null){
                    voltaComErro(req, resp, erroCampos);
                    return;
                }
                Produto produto = montarProduto(req);
                LocalDate dataValidade = lerData(req.getParameter("dtValidade"));
                int lote = lerLote(req.getParameter("lote"));
                int quantidade = lerInt(req.getParameter("quantidade"));

                if (armazenamento.equals("geladeira")){
                    Integer idGeladeira = lerInt(req.getParameter("idGeladeira"));
                    if (idGeladeira == null){
                        voltaComErro(req, resp, "Escolha a geladeira do item.");
                        return;
                    }
                    Geladeira geladeira = geladeiraDAO.buscarUnicaGeladeira(idGeladeira);
                    if (geladeira == null){
                        voltaComErro(req, resp, "Geladeira não encontrada.");
                        return;
                    }

                    // o admin não tem limite de produtos por plano
                    ProdutosGeladeira itemg = new ProdutosGeladeira(0, geladeira, produto, dataValidade, lote, quantidade);
                    if (igDAO.inserirProdutoGeladeira(itemg) == 1){
                        atualizarStatusGeladeira(geladeira);
                        session.setAttribute("sucesso", "Item cadastrado na geladeira com sucesso.");
                        resp.sendRedirect(req.getContextPath() + retorno);
                        return;
                    }
                    else {
                        voltaComErro(req, resp, "Erro ao adicionar produto na geladeira.");
                        return;
                    }
                }
                else {
                    Integer idFrigorifico = lerInt(req.getParameter("idFrigorifico"));
                    if (idFrigorifico == null){
                        voltaComErro(req, resp, "Escolha o frigorífico do item.");
                        return;
                    }
                    Frigorificos frigorifico = frigorificoDAO.buscarFrigorificoPorId(idFrigorifico);
                    if (frigorifico == null){
                        voltaComErro(req, resp, "Frigorífico não encontrado.");
                        return;
                    }

                    // o admin não tem limite de produtos por plano
                    ProdutosFrigorifico itemf = new ProdutosFrigorifico(0, frigorifico, produto, dataValidade, lote, quantidade);
                    if (itfDAO.inserirProdutoFrigorifico(itemf) == 1){
                        atualizarStatusFrigorifico(frigorifico);
                        session.setAttribute("sucesso", "Item cadastrado no frigorífico com sucesso.");
                        resp.sendRedirect(req.getContextPath() + retorno);
                        return;
                    }
                    else {
                        voltaComErro(req, resp, "Erro ao adicionar produto no frigorífico.");
                        return;
                    }
                }
            }


            case "alterar":{
                // validando os campos do produto e do item
                String erroCampos = validarCampos(req);
                if (erroCampos != null){
                    voltaComErro(req, resp, erroCampos);
                    return;
                }
                Produto produtoNovo = montarProduto(req);
                LocalDate dataValidade = lerData(req.getParameter("dtValidade"));
                int lote = lerLote(req.getParameter("lote"));
                int quantidade = lerInt(req.getParameter("quantidade"));

                if (armazenamento.equals("geladeira")){
                    Integer idItemAntigo = lerInt(req.getParameter("idProdutoGeladeira"));
                    Integer idGeladeira = lerInt(req.getParameter("idGeladeira"));
                    if (idItemAntigo == null || idGeladeira == null){
                        voltaComErro(req, resp, "Erro ao alterar produto da geladeira.");
                        return;
                    }
                    ProdutosGeladeira itemAntigo = igDAO.buscarProdutoGeladeiraPorId(idItemAntigo);
                    if (itemAntigo == null){
                        voltaComErro(req, resp, "Item da geladeira não encontrado.");
                        return;
                    }
                    // a geladeira nova pode ser outra
                    Geladeira geladeira = geladeiraDAO.buscarUnicaGeladeira(idGeladeira);
                    if (geladeira == null){
                        voltaComErro(req, resp, "Geladeira não encontrada.");
                        return;
                    }

                    ProdutosGeladeira itemNovo = new ProdutosGeladeira(0, geladeira, produtoNovo, dataValidade, lote, quantidade);
                    if (igDAO.alterarProdutoGeladeira(itemAntigo, itemNovo) == 1){
                        // atualizando o status da geladeira nova e da antiga
                        atualizarStatusGeladeira(geladeira);
                        if (itemAntigo.getGeladeira().getIdGeladeira() != geladeira.getIdGeladeira()){
                            atualizarStatusGeladeira(geladeiraDAO.buscarUnicaGeladeira(itemAntigo.getGeladeira().getIdGeladeira()));
                        }
                        session.setAttribute("sucesso", "Item da geladeira alterado com sucesso.");
                        resp.sendRedirect(req.getContextPath() + retorno);
                        return;
                    }
                    else {
                        voltaComErro(req, resp, "Erro ao alterar produto da geladeira.");
                        return;
                    }
                }
                else {
                    Integer idItemAntigo = lerInt(req.getParameter("idProdutoFrigorifico"));
                    Integer idFrigorifico = lerInt(req.getParameter("idFrigorifico"));
                    if (idItemAntigo == null || idFrigorifico == null){
                        voltaComErro(req, resp, "Erro ao alterar produto do frigorífico.");
                        return;
                    }
                    ProdutosFrigorifico itemAntigo = itfDAO.buscarProdutoFrigorificoPorId(idItemAntigo);
                    if (itemAntigo == null){
                        voltaComErro(req, resp, "Item do frigorífico não encontrado.");
                        return;
                    }
                    // o frigorifico novo pode ser outro
                    Frigorificos frigorifico = frigorificoDAO.buscarFrigorificoPorId(idFrigorifico);
                    if (frigorifico == null){
                        voltaComErro(req, resp, "Frigorífico não encontrado.");
                        return;
                    }

                    ProdutosFrigorifico itemNovo = new ProdutosFrigorifico(0, frigorifico, produtoNovo, dataValidade, lote, quantidade);
                    if (itfDAO.alterarProdutoFrigorifico(itemAntigo, itemNovo) == 1){
                        // atualizando o status do frigorifico novo e do antigo
                        atualizarStatusFrigorifico(frigorifico);
                        if (itemAntigo.getFrigorificos().getIdFrigorifico() != frigorifico.getIdFrigorifico()){
                            atualizarStatusFrigorifico(frigorificoDAO.buscarFrigorificoPorId(itemAntigo.getFrigorificos().getIdFrigorifico()));
                        }
                        session.setAttribute("sucesso", "Item do frigorífico alterado com sucesso.");
                        resp.sendRedirect(req.getContextPath() + retorno);
                        return;
                    }
                    else {
                        voltaComErro(req, resp, "Erro ao alterar produto do frigorífico.");
                        return;
                    }
                }
            }


            case "deletar":{
                if (armazenamento.equals("geladeira")){
                    //pegando o id do itemgeladeira
                    Integer id = lerInt(req.getParameter("idProdutoGeladeira"));
                    if (id == null){
                        voltaComErro(req, resp, "Erro ao deletar esse produto. Por favor tente novamente.");
                        return;
                    }
                    ProdutosGeladeira itemg = igDAO.buscarProdutoGeladeiraPorId(id);
                    if (itemg == null){
                        voltaComErro(req, resp, "Item da geladeira não encontrado.");
                        return;
                    }
                    if (igDAO.deletarProdutoGeladeira(itemg) == 1){
                        // atualizando o status da geladeira
                        atualizarStatusGeladeira(geladeiraDAO.buscarUnicaGeladeira(itemg.getGeladeira().getIdGeladeira()));
                        session.setAttribute("sucesso", "Item da geladeira apagado com sucesso.");
                        resp.sendRedirect(req.getContextPath() + retorno);
                        return;
                    }
                    else {
                        voltaComErro(req, resp, "Erro ao deletar esse produto. Por favor tente novamente.");
                        return;
                    }
                }
                else {
                    //pegando o id do itemfrigorifico
                    Integer id = lerInt(req.getParameter("idProdutoFrigorifico"));
                    if (id == null){
                        voltaComErro(req, resp, "Erro ao deletar esse produto. Por favor tente novamente.");
                        return;
                    }
                    ProdutosFrigorifico itemf = itfDAO.buscarProdutoFrigorificoPorId(id);
                    if (itemf == null){
                        voltaComErro(req, resp, "Item do frigorífico não encontrado.");
                        return;
                    }
                    if (itfDAO.deletarProdutoFrigorifico(itemf) == 1){
                        // atualizando o status do frigorifico
                        atualizarStatusFrigorifico(frigorificoDAO.buscarFrigorificoPorId(itemf.getFrigorificos().getIdFrigorifico()));
                        session.setAttribute("sucesso", "Item do frigorífico apagado com sucesso.");
                        resp.sendRedirect(req.getContextPath() + retorno);
                        return;
                    }
                    else {
                        voltaComErro(req, resp, "Erro ao deletar esse produto. Por favor tente novamente.");
                        return;
                    }
                }
            }
            default:
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    // confere os campos do produto e do item, devolve a mensagem de erro ou null se esta tudo certo
    private String validarCampos(HttpServletRequest req){
        String nome = req.getParameter("nomeProduto");
        String categoriaTxt = req.getParameter("categoria");
        String marca = req.getParameter("marca");
        String unidadeMedida = req.getParameter("unidadeMedida");
        String loteTxt = req.getParameter("lote");

        if (nome == null || nome.isBlank()){
            return "O nome não pode ser vazio.";
        }
        if (!validar.validarNomeProduto(nome) || nome.length() > 20){
            return "Formato do nome incorreto. No máximo 20 caracteres.";
        }
        Double preco = lerDouble(req.getParameter("preco"));
        if (preco == null || !validar.validarPreco(preco)){
            return "Formato de preço incorreto.";
        }
        try{
            Categoria.valueOf(categoriaTxt.trim().toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            return "Categoria inválida.";
        }
        if (marca == null || marca.isBlank() || marca.length() > 20){
            return "A marca não pode ser vazia e tem no máximo 20 caracteres.";
        }
        if (unidadeMedida == null || unidadeMedida.isBlank() || unidadeMedida.length() > 10){
            return "A unidade de medida não pode ser vazia e tem no máximo 10 caracteres.";
        }
        LocalDate dataValidade = lerData(req.getParameter("dtValidade"));
        if (dataValidade == null){
            return "A data de validade é inválida.";
        }
        // produto vencido nao pode ser cadastrado
        if (!validar.validarValidade(dataValidade)){
            return "A data de validade não pode ser anterior a data de amanhã.";
        }
        // lote é opcional: vazio vira 0 ("sem lote")
        if (loteTxt != null && !loteTxt.isBlank()){
            Integer lote = lerInt(loteTxt);
            if (lote == null || lote < 0){
                return "Lote inválido.";
            }
        }
        Integer quantidade = lerInt(req.getParameter("quantidade"));
        if (quantidade == null || !validar.validarQuantidade(quantidade)){
            return "Quantidade deve ser maior que zero";
        }
        return null;
    }

    // monta o produto com os campos do formulario ( já validados )
    private Produto montarProduto(HttpServletRequest req){
        String nome = req.getParameter("nomeProduto").trim();
        String descricao = req.getParameter("descricao");
        double preco = lerDouble(req.getParameter("preco"));
        Categoria categoria = Categoria.valueOf(req.getParameter("categoria").trim().toUpperCase());
        String marca = req.getParameter("marca").trim();
        String unidadeMedida = req.getParameter("unidadeMedida").trim();

        if (descricao == null || descricao.isBlank()){
            return new Produto(nome, preco, categoria, marca, unidadeMedida);
        }
        return new Produto(nome, descricao, preco, categoria, marca, unidadeMedida);
    }

    // se o dono tem assinatura, o status da geladeira acompanha a capacidade do plano ( sem assinatura fica como está )
    private void atualizarStatusGeladeira(Geladeira geladeira){
        if (geladeira == null) return;
        Assinatura assinatura = assinaturaDAO.buscarAssinaturaUsuario(geladeira.getUsuario().getCpf());
        if (assinatura != null){
            ArmazenamentoService.calcularStatusGeladeira(geladeira, assinatura);
            geladeiraDAO.atualizarStatus(geladeira);
        }
    }

    // se o dono tem assinatura, o status do frigorifico acompanha a capacidade do plano ( sem assinatura fica como está )
    private void atualizarStatusFrigorifico(Frigorificos frigorifico){
        if (frigorifico == null) return;
        Assinatura assinatura = assinaturaDAO.buscarAssinaturaEmpresa(frigorifico.getEmpresa().getCnpj());
        if (assinatura != null){
            ArmazenamentoService.calcularStatusFrigorifico(frigorifico, assinatura);
            frigorificoDAO.atualizarStatus(frigorifico);
        }
    }

    // carrega as listas e mostra a tela
    private void listar(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException{
        List<ProdutosGeladeira> itensGeladeira = igDAO.buscarTodosProdutosGeladeira();
        List<ProdutosFrigorifico> itensFrigorifico = itfDAO.buscarTodosProdutosFrigorifico();
        List<Geladeira> geladeiras = geladeiraDAO.buscarTodasGeladeira();
        List<Frigorificos> frigorificos = frigorificoDAO.buscarTodosFrigorifico();
        req.setAttribute("itensGeladeira", itensGeladeira);
        req.setAttribute("itensFrigorifico", itensFrigorifico);
        req.setAttribute("geladeiras", geladeiras);
        req.setAttribute("frigorificos", frigorificos);
        req.setAttribute("categorias", Categoria.values());
        req.getRequestDispatcher(jsp_produtos).forward(req, resp);
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
    // lote vazio vira 0
    private static int lerLote(String txt) {
        if (txt == null || txt.isBlank()){
            return 0;
        }
        return Integer.parseInt(txt.trim());
    }
}
