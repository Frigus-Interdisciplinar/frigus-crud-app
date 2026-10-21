package com.example.servlet.Controller;

import com.example.servlet.Dao.AssinaturaDAO;
import com.example.servlet.Dao.FrigorificosDAO;
import com.example.servlet.Dao.ProdutosFrigorificoDAO;
import com.example.servlet.Model.*;
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
        "/ver-if-produtos.jsp",
        "/ver-if-descricao.jsp",
        "/inserir-if.jsp",
        "/alterar-if.jsp",
        "/deletar-if.jsp",
})
public class ProdutoFrigorificoServlet extends HttpServlet {
    private ProdutosFrigorificoDAO ifDAO;
    private FrigorificosDAO frigorificosDAO;
    private Validacao validar;
    private AssinaturaDAO assinaturaDAO;

    // caminho das pastas
    private static final String jsp_ver_produto = "/assets/pages/ver-if-produtos.jsp";
    private static final String jsp_ver_descricao = "/assets/pages/ver-if-descricao.jsp";
    private static final String jsp_inserir = "/assets/pages/inserir.jsp";
    private static final String jsp_alterar = "/assets/pages/alterar.jsp";
    private static final String jsp_deletar = "/assets/pages/deletar.jsp";
    private static final String jsp_login = "/assets/pages/login.jsp";

    // caminhos do servlet
    private static final String url_servlet_produto = "/ver-if-produtos";
    private static final String url_servlet_descricao = "/ver-if-descricao";

    @Override
    public void init(){
        ifDAO = new ProdutosFrigorificoDAO();
        frigorificosDAO = new FrigorificosDAO();
        validar = new Validacao();
        assinaturaDAO = new AssinaturaDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession();
        if(session == null || session.getAttribute("empresa") == null){
            req.getRequestDispatcher(jsp_login).forward(req, resp);
            return;
        }
        Empresa empresa = (Empresa) session.getAttribute("empresa");
        if(empresa == null){
            voltaComErro(req, resp, "Empresa desconectada. Por favor cadastre-se novamente.", jsp_login);
            return;
        }
        switch (req.getServletPath()){
            case "/ver-if-produtos":{
                List<ProdutosFrigorifico> igLista = ifDAO.buscarProdutoFrigorificos(empresa.getCnpj());
                req.setAttribute("itensFrigorifico", igLista);
                req.getRequestDispatcher(jsp_ver_produto).forward(req, resp);
                return;
            }
            case "/ver-if-descricao":{
                // pegando o id do produto
                String idTxt = req.getParameter("idItemFrigorifico");
                int id;
                try{
                    id  = Integer.parseInt(idTxt);
                } catch (NumberFormatException e) {
                    voltaComErro(req, resp, "Erro ao tentar ver esse produto. Por favor tente novamente.", jsp_ver_produto);
                    return;
                }
                ProdutosFrigorifico ig = ifDAO.buscarProdutoFrigorificoPorId(id);
                req.setAttribute("produto", ig);
                req.getRequestDispatcher(jsp_ver_descricao).forward(req, resp);
                return;
            }
            default:
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession();
        if(session == null || session.getAttribute("empresa") == null){
            req.getRequestDispatcher(jsp_login).forward(req, resp);
            return;
        }
        Empresa empresa = (Empresa) session.getAttribute("empresa");
        if(empresa == null){
            voltaComErro(req, resp, "Empresa desconectada. Por favor cadastre-se novamente.", jsp_login);
            return;
        }
        switch (req.getServletPath()){
            case "/inserir-if.jsp":{
                // pegando os dados do formurio
                String nome = req.getParameter("nomeProduto");
                String descricao = req.getParameter("descricao");
                String precoTxt = req.getParameter("preco");
                String categoriaTxt = req.getParameter("categoria");
                String marca = req.getParameter("marca");
                String unidadeMedida = req.getParameter("unidadeMedida");

                //frigorifico
                String idFrigorificoTxt = req.getParameter("idFrigorifico");

                String dtValidadeTxt = req.getParameter("dtValidade");
                String loteTxt = req.getParameter("lote");
                String quantTxt = req.getParameter("quantidade");

                // verificando se é nulo
                if (nome == null){
                    voltaComErro(req, resp, "O nome não pode ser vazio.", jsp_inserir);
                    return;
                }
                if (precoTxt == null){
                    voltaComErro(req, resp, "O preço não pode ser vazio.", jsp_inserir);
                    return;
                }
                if (categoriaTxt == null){
                    voltaComErro(req, resp, "O nome não pode ser vazio.", jsp_inserir);
                    return;
                }
                if (marca == null){
                    voltaComErro(req, resp, "O nome não pode ser vazio.", jsp_inserir);
                    return;
                }
                if (unidadeMedida == null){
                    voltaComErro(req, resp, "O nome não pode ser vazio.", jsp_inserir);
                    return;
                }
                if (dtValidadeTxt == null){
                    voltaComErro(req, resp, "A data não pode ser vazia.", jsp_inserir);
                    return;
                }
                if (loteTxt == null){
                    voltaComErro(req, resp, "O lote não pode ser vazio.", jsp_inserir);
                    return;
                }
                if (quantTxt == null){
                    voltaComErro(req, resp, "A quantidade não pode ser vazia.", jsp_inserir);
                    return;
                }
                double preco;
                try{
                     preco = Double.parseDouble(precoTxt);
                } catch (NumberFormatException e) {
                    voltaComErro(req, resp,"Erro ao adicionar frigorífico.", jsp_inserir);
                    return;
                }
                Categoria categoria;
                try{
                    categoria = Categoria.valueOf(categoriaTxt);
                } catch (NullPointerException n) {
                    voltaComErro(req, resp,"Erro ao adicionar frigorífico.", jsp_inserir);
                    return;
                }
                LocalDate dataValidade;
                try{
                    dataValidade = LocalDate.parse(dtValidadeTxt);
                } catch (NumberFormatException e) {
                    voltaComErro(req, resp,"Erro ao adicionar frigorífico.", jsp_inserir);
                    return;
                }
                int lote;
                try{
                    lote = Integer.parseInt(loteTxt);
                } catch (NumberFormatException e) {
                    voltaComErro(req, resp,"Erro ao adicionar frigorífico.", jsp_inserir);
                    return;
                }
                int quantidade;
                try{
                    quantidade = Integer.parseInt(quantTxt);
                } catch (NumberFormatException e) {
                    voltaComErro(req, resp,"Erro ao adicionar frigorífico.", jsp_inserir);
                    return;
                }
                int idFrigorifico;
                try{
                    idFrigorifico = Integer.parseInt(idFrigorificoTxt);
                } catch (NumberFormatException e) {
                    voltaComErro(req, resp,"Erro ao adicionar frigorífico.", jsp_inserir);
                    return;
                }

                boolean vNome = validar.validarNomeProduto(nome);
                if (!vNome){
                    voltaComErro(req, resp,"Formato do nome incorreto.", jsp_inserir);
                    return;
                }

                boolean vPreco = validar.validarPreco(preco);
                if (!vPreco){
                    voltaComErro(req, resp,"Formato de preço incorreto.", jsp_inserir);
                    return;
                }

                boolean vLote = validar.validarQuantidade(lote);
                if (!vPreco){
                    voltaComErro(req, resp,"Lote deve ser um número maior que zero", jsp_inserir);
                    return;
                }
                boolean vQuant = validar.validarQuantidade(quantidade);
                if (!vQuant){
                    voltaComErro(req, resp,"Quantidade deve ser maior que zero", jsp_inserir);
                    return;
                }
                boolean vValidade = validar.validarValidade(dataValidade);
                if (!vValidade){
                    voltaComErro(req, resp,"A data de validade não pode ser anterior a data de amanhã.", jsp_inserir);
                    return;
                }

                Frigorificos frigorifico = frigorificosDAO.buscarFrigorificoPorId(empresa.getCnpj(), idFrigorifico);

                if (frigorifico == null){
                    voltaComErro(req, resp, "Erro ao adicionar frigorífico.", jsp_inserir);
                    return;
                }
                Produto produto;
                if (descricao == null || descricao.isBlank()){
                     produto = new Produto(nome, preco, categoria, marca, unidadeMedida);
                }else {
                    produto = new Produto(nome, descricao, preco, categoria, marca, unidadeMedida);
                }

                ProdutosFrigorifico itemf = new ProdutosFrigorifico(0, frigorifico, produto, dataValidade, lote, quantidade);
                if (ifDAO.inserirProdutoFrigorifico(itemf) == 1){
                    // busca o plano da empresa
                    Assinatura assinatura = assinaturaDAO.buscarAssinaturaEmpresa(empresa.getCnpj());

                    // recalcula o status no objeto (soma o ItemFrigorifico e compara com a capacidade)
                    ArmazenamentoService.calcularStatusFrigorifico(frigorifico, assinatura);

                    // grava o novo status no banco
                    frigorificosDAO.atualizarStatus(frigorifico);
                    resp.sendRedirect(req.getContextPath() + url_servlet_produto);
                    return;
                }
                else {
                    voltaComErro(req, resp,"Erro ao adicionar frigorífico.", jsp_inserir);
                    return;
                }
            }


            case "/alterar-if.jsp":{
                // pegando os dados do formurio
                String idItemAntigoTxt = req.getParameter("idProdutoFrigorifico");

                String nome = req.getParameter("nomeProduto");
                String descricao = req.getParameter("descricao");
                String precoTxt = req.getParameter("preco");
                String categoriaTxt = req.getParameter("categoria");
                String marca = req.getParameter("marca");
                String unidadeMedida = req.getParameter("unidadeMedida");


                String dtValidadeTxt = req.getParameter("dtValidade");
                String loteTxt = req.getParameter("lote");
                String quantTxt = req.getParameter("quantidade");

                // verificando se é nulo
                if (nome == null){
                    voltaComErro(req, resp, "O nome não pode ser vazio.", jsp_inserir);
                    return;
                }
                if (precoTxt == null){
                    voltaComErro(req, resp, "O preço não pode ser vazio.", jsp_inserir);
                    return;
                }
                if (categoriaTxt == null){
                    voltaComErro(req, resp, "O nome não pode ser vazio.", jsp_inserir);
                    return;
                }
                if (marca == null){
                    voltaComErro(req, resp, "O nome não pode ser vazio.", jsp_inserir);
                    return;
                }
                if (unidadeMedida == null){
                    voltaComErro(req, resp, "O nome não pode ser vazio.", jsp_inserir);
                    return;
                }
                if (dtValidadeTxt == null){
                    voltaComErro(req, resp, "A data não pode ser vazia.", jsp_inserir);
                    return;
                }
                if (loteTxt == null){
                    voltaComErro(req, resp, "O lote não pode ser vazio.", jsp_inserir);
                    return;
                }
                if (quantTxt == null){
                    voltaComErro(req, resp, "A quantidade não pode ser vazia.", jsp_inserir);
                    return;
                }
                double preco;
                try{
                    preco = Double.parseDouble(precoTxt);
                } catch (NumberFormatException e) {
                    voltaComErro(req, resp,"Erro ao alterar frigorífico.", jsp_inserir);
                    return;
                }
                Categoria categoria;
                try{
                    categoria = Categoria.valueOf(categoriaTxt);
                } catch (NullPointerException n) {
                    voltaComErro(req, resp,"Erro ao alterar frigorífico.", jsp_inserir);
                    return;
                }
                LocalDate dataValidade;
                try{
                    dataValidade = LocalDate.parse(dtValidadeTxt);
                } catch (NumberFormatException e) {
                    voltaComErro(req, resp,"Erro ao alterar frigorífico.", jsp_inserir);
                    return;
                }
                int lote;
                try{
                    lote = Integer.parseInt(loteTxt);
                } catch (NumberFormatException e) {
                    voltaComErro(req, resp,"Erro ao alterar frigorífico.", jsp_inserir);
                    return;
                }
                int quantidade;
                try{
                    quantidade = Integer.parseInt(quantTxt);
                } catch (NumberFormatException e) {
                    voltaComErro(req, resp,"Erro ao alterar frigorífico.", jsp_inserir);
                    return;
                }

                boolean vPreco = validar.validarPreco(preco);
                if (!vPreco){
                    voltaComErro(req, resp,"Formato de preço incorreto.", jsp_inserir);
                    return;
                }

                boolean vLote = validar.validarQuantidade(lote);
                if (!vPreco){
                    voltaComErro(req, resp,"Lote deve ser um número maior que zero", jsp_inserir);
                    return;
                }
                boolean vQuant = validar.validarQuantidade(quantidade);
                if (!vQuant){
                    voltaComErro(req, resp,"Quantidade deve ser maior que zero", jsp_inserir);
                    return;
                }
                boolean vValidade = validar.validarValidade(dataValidade);
                if (!vValidade){
                    voltaComErro(req, resp,"A data de validade não pode ser anterior a data de amanhã.", jsp_inserir);
                    return;
                }

                int idItemAntigo;
                try{
                    idItemAntigo = Integer.parseInt(idItemAntigoTxt);
                } catch (NumberFormatException e) {
                    voltaComErro(req, resp,"Erro ao alterar frigorífico.", jsp_inserir);
                    return;
                }

                Produto produtoNovo;
                if (descricao == null || descricao.isBlank()){
                    produtoNovo = new Produto(nome, preco, categoria, marca, unidadeMedida);
                }else {
                    produtoNovo = new Produto(nome, descricao, preco, categoria, marca, unidadeMedida);
                }



                ProdutosFrigorifico itemAntigo = ifDAO.buscarProdutoFrigorificoPorId(idItemAntigo);
                if (itemAntigo == null){
                    voltaComErro(req, resp,"Erro ao alterar frigorífico.", jsp_inserir);
                    return;
                }

                Frigorificos frigorifico = frigorificosDAO.buscarFrigorificoPorId(empresa.getCnpj(), itemAntigo.getFrigorificos().getIdFrigorifico());

                ProdutosFrigorifico itemNovo = new ProdutosFrigorifico(0, frigorifico, produtoNovo, dataValidade, lote, quantidade);
                if (ifDAO.alterarProdutoFrigorifico(itemAntigo, itemNovo) == 1){
                    resp.sendRedirect(req.getContextPath() + url_servlet_produto);
                    return;
                }
                else {
                    voltaComErro(req, resp,"Erro ao alterar frigorífico.", jsp_inserir);
                    return;
                }
            }


            case "/deletar-if.jsp":{
               //pegando o id do itemfrigorifico
               String idTxt = req.getParameter("idItemFrigorifico");
               int id;
               try {
                   id = Integer.parseInt(idTxt);
               } catch (NumberFormatException e) {
                   voltaComErro(req, resp, "Erro ao deletar esse produto. Por favor tente novamente.", jsp_deletar);
                   return;
               }
               ProdutosFrigorifico itemf = ifDAO.buscarProdutoFrigorificoPorId(id);
               if (itemf == null){
                   voltaComErro(req, resp, "Erro ao deletar esse produto. Por favor tente novamente.", jsp_deletar);
                   return;
               }
               Frigorificos frigorifico = frigorificosDAO.buscarFrigorificoPorId(empresa.getCnpj(), itemf.getFrigorificos().getIdFrigorifico());

               if(frigorifico == null){
                   voltaComErro(req, resp, "Erro ao deletar esse produto. Por favor tente novamente.", jsp_deletar);
                   return;
               }
               if(ifDAO.deletarProdutoFrigorifico(itemf) == 1){
                   resp.sendRedirect(req.getContextPath() + url_servlet_produto);
                   return;
               }
               else {
                   voltaComErro(req, resp, "Erro ao deletar esse produto. Por favor tente novamente.", jsp_deletar);
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