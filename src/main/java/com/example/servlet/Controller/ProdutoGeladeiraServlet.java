package com.example.servlet.Controller;

import com.example.servlet.Dao.AssinaturaDAO;
import com.example.servlet.Dao.GeladeiraDAO;
import com.example.servlet.Dao.ProdutosGeladeiraDAO;
import com.example.servlet.Model.Assinatura;
import com.example.servlet.Model.Geladeira;
import com.example.servlet.Model.Produto;
import com.example.servlet.Model.ProdutosGeladeira;
import com.example.servlet.Model.Usuario;
import com.example.servlet.Util.Enum.Categoria;
import com.example.servlet.Util.Enum.TipoAssinatura;
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
import java.time.format.DateTimeParseException;
import java.util.List;

@WebServlet(urlPatterns = {
        "/ver-ig-produtos",
        "/ver-ig-descricao",
        "/inserir-ig",
        "/alterar-ig",
        "/deletar-ig",
},
        name = "produtoGeladeira")
public class ProdutoGeladeiraServlet extends HttpServlet {
    private ProdutosGeladeiraDAO igDAO;
    private GeladeiraDAO geladeiraDAO;
    private AssinaturaDAO assinaturaDAO;
    private Validacao validar;

    // caminho das pastas
    private static final String jsp_ver_produto = "/assets/pages/ver-ig-produtos.jsp";
    private static final String jsp_ver_descricao = "/assets/pages/ver-ig-descricao.jsp";
    private static final String jsp_inserir = "/assets/pages/inserir-ig.jsp";
    private static final String jsp_alterar = "/assets/pages/alterar-ig.jsp";
    private static final String jsp_deletar = "/assets/pages/deletar-ig.jsp";
    private static final String jsp_login = "/assets/pages/login.jsp";

    // caminhos do servlet
    private static final String url_servlet_produto = "/ver-ig-produtos";
    private static final String url_servlet_descricao = "/ver-ig-descricao";

    @Override
    public void init(){
        igDAO = new ProdutosGeladeiraDAO();
        geladeiraDAO = new GeladeiraDAO();
        assinaturaDAO = new AssinaturaDAO();
        validar = new Validacao();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession();
        if(session == null || session.getAttribute("usuario") == null){
            req.getRequestDispatcher(jsp_login).forward(req, resp);
            return;
        }
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if(usuario == null){
            voltaComErro(req, resp, "Usuário desconectado. Por favor faça login novamente.", jsp_login);
            return;
        }
        switch (req.getServletPath()){
            case "/ver-ig-produtos":{
                // pegando o id da geladeira
                String idGeladeiraTxt = req.getParameter("idGeladeira");
                int idGeladeira;
                try{
                    idGeladeira = Integer.parseInt(idGeladeiraTxt);
                } catch (NumberFormatException e) {
                    voltaComErro(req, resp, "Erro ao tentar ver essa geladeira. Por favor tente novamente.", jsp_ver_produto);
                    return;
                }
                // garantindo que a geladeira é do usuario logado
                Geladeira geladeira = geladeiraDAO.buscarUnicaGeladeira(usuario.getCpf(), idGeladeira);
                if (geladeira == null){
                    voltaComErro(req, resp, "Geladeira não encontrada.", jsp_ver_produto);
                    return;
                }
                List<ProdutosGeladeira> igLista = igDAO.buscarTodosProdutosGeladeira(idGeladeira);
                req.setAttribute("geladeira", geladeira);
                req.setAttribute("itensGeladeira", igLista);
                req.getRequestDispatcher(jsp_ver_produto).forward(req, resp);
                return;
            }
            case "/ver-ig-descricao":{
                // pegando o id do produto
                String idTxt = req.getParameter("idProdutoGeladeira");
                int id;
                try{
                    id  = Integer.parseInt(idTxt);
                } catch (NumberFormatException e) {
                    voltaComErro(req, resp, "Erro ao tentar ver esse produto. Por favor tente novamente.", jsp_ver_produto);
                    return;
                }
                ProdutosGeladeira ig = igDAO.buscarProdutoGeladeiraPorId(id);
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
        if(session == null || session.getAttribute("usuario") == null){
            req.getRequestDispatcher(jsp_login).forward(req, resp);
            return;
        }
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if(usuario == null){
            voltaComErro(req, resp, "Usuário desconectado. Por favor faça login novamente.", jsp_login);
            return;
        }
        switch (req.getServletPath()){
            case "/inserir-ig":{
                // pegando os dados do formurio
                String nome = req.getParameter("nomeProduto");
                String descricao = req.getParameter("descricao");
                String precoTxt = req.getParameter("preco");
                String categoriaTxt = req.getParameter("categoria");
                String marca = req.getParameter("marca");
                String unidadeMedida = req.getParameter("unidadeMedida");

                //geladeira
                String idGeladeiraTxt = req.getParameter("idGeladeira");

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
                    voltaComErro(req, resp, "A categoria não pode ser vazia.", jsp_inserir);
                    return;
                }
                if (marca == null){
                    voltaComErro(req, resp, "A marca não pode ser vazia.", jsp_inserir);
                    return;
                }
                if (unidadeMedida == null){
                    voltaComErro(req, resp, "A unidade de medida não pode ser vazia.", jsp_inserir);
                    return;
                }
                if (dtValidadeTxt == null){
                    voltaComErro(req, resp, "A data não pode ser vazia.", jsp_inserir);
                    return;
                }
                if (quantTxt == null){
                    voltaComErro(req, resp, "A quantidade não pode ser vazia.", jsp_inserir);
                    return;
                }
                double preco;
                try{
                    preco = Double.parseDouble(precoTxt.trim().replace(',', '.'));
                } catch (NumberFormatException e) {
                    voltaComErro(req, resp,"Erro ao adicionar produto na geladeira.", jsp_inserir);
                    return;
                }
                Categoria categoria;
                try{
                    categoria = Categoria.valueOf(categoriaTxt.trim().toUpperCase());
                } catch (IllegalArgumentException n) {
                    voltaComErro(req, resp,"Erro ao adicionar produto na geladeira.", jsp_inserir);
                    return;
                }
                LocalDate dataValidade;
                try{
                    dataValidade = LocalDate.parse(dtValidadeTxt.trim());
                } catch (DateTimeParseException e) {
                    voltaComErro(req, resp,"Erro ao adicionar produto na geladeira.", jsp_inserir);
                    return;
                }
                // lote é opcional: vazio vira 0 ("sem lote")
                int lote;
                try{
                    lote = (loteTxt == null || loteTxt.isBlank()) ? 0 : Integer.parseInt(loteTxt.trim());
                } catch (NumberFormatException e) {
                    voltaComErro(req, resp,"Erro ao adicionar produto na geladeira.", jsp_inserir);
                    return;
                }
                int quantidade;
                try{
                    quantidade = Integer.parseInt(quantTxt.trim());
                } catch (NumberFormatException e) {
                    voltaComErro(req, resp,"Erro ao adicionar produto na geladeira.", jsp_inserir);
                    return;
                }
                int idGeladeira;
                try{
                    idGeladeira = Integer.parseInt(idGeladeiraTxt);
                } catch (NumberFormatException e) {
                    voltaComErro(req, resp,"Erro ao adicionar produto na geladeira.", jsp_inserir);
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
                boolean vlote = validar.validarQuantidade(lote);
                if (!vlote){
                    voltaComErro(req, resp,"Lote inválido.", jsp_inserir);
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

                Geladeira geladeira = geladeiraDAO.buscarUnicaGeladeira(usuario.getCpf(), idGeladeira);

                if (geladeira == null){
                    voltaComErro(req, resp, "Geladeira não encontrada.", jsp_inserir);
                    return;
                }

                // regra de negocio: o plano limita a quantidade por geladeira
                Assinatura assinatura = assinaturaDAO.buscarAssinaturaUsuario(usuario.getCpf());
                if (assinatura == null){
                    voltaComErro(req, resp, "Escolha um plano antes de adicionar produtos.", jsp_inserir);
                    return;
                }
                int capacidade = TipoAssinatura.quantidadePorPlano(assinatura.getTipoAssinatura());
                int totalAtual = ArmazenamentoService.totalProdutosGeladeira(geladeira.getIdGeladeira());
                if (capacidade > 0 && totalAtual + quantidade > capacidade){
                    voltaComErro(req, resp, "Limite do seu plano: " + capacidade
                            + " unidades por geladeira (já tem " + totalAtual + ").", jsp_inserir);
                    return;
                }

                Produto produto;
                if (descricao == null || descricao.isBlank()){
                    produto = new Produto(nome, preco, categoria, marca, unidadeMedida);
                }else {
                    produto = new Produto(nome, descricao, preco, categoria, marca, unidadeMedida);
                }

                ProdutosGeladeira itemg = new ProdutosGeladeira(0, geladeira, produto, dataValidade, lote, quantidade);
                if (igDAO.inserirProdutoGeladeira(itemg) == 1){
                    // atualizando o status da geladeira
                    ArmazenamentoService.calcularStatusGeladeira(geladeira, assinatura);
                    geladeiraDAO.atualizarStatus(geladeira);

                    resp.sendRedirect(req.getContextPath() + url_servlet_produto + "?idGeladeira=" + geladeira.getIdGeladeira());
                    return;
                }
                else {
                    voltaComErro(req, resp,"Erro ao adicionar produto na geladeira.", jsp_inserir);
                    return;
                }
            }


            case "/alterar-ig":{
                // pegando os dados do formurio
                String idItemAntigoTxt = req.getParameter("idProdutoGeladeira");

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
                    voltaComErro(req, resp, "O nome não pode ser vazio.", jsp_alterar);
                    return;
                }
                if (precoTxt == null){
                    voltaComErro(req, resp, "O preço não pode ser vazio.", jsp_alterar);
                    return;
                }
                if (categoriaTxt == null){
                    voltaComErro(req, resp, "A categoria não pode ser vazia.", jsp_alterar);
                    return;
                }
                if (marca == null){
                    voltaComErro(req, resp, "A marca não pode ser vazia.", jsp_alterar);
                    return;
                }
                if (unidadeMedida == null){
                    voltaComErro(req, resp, "A unidade de medida não pode ser vazia.", jsp_alterar);
                    return;
                }
                if (dtValidadeTxt == null){
                    voltaComErro(req, resp, "A data não pode ser vazia.", jsp_alterar);
                    return;
                }
                if (quantTxt == null){
                    voltaComErro(req, resp, "A quantidade não pode ser vazia.", jsp_alterar);
                    return;
                }
                double preco;
                try{
                    preco = Double.parseDouble(precoTxt.trim().replace(',', '.'));
                } catch (NumberFormatException e) {
                    voltaComErro(req, resp,"Erro ao alterar produto da geladeira.", jsp_alterar);
                    return;
                }
                Categoria categoria;
                try{
                    categoria = Categoria.valueOf(categoriaTxt.trim().toUpperCase());
                } catch (IllegalArgumentException n) {
                    voltaComErro(req, resp,"Erro ao alterar produto da geladeira.", jsp_alterar);
                    return;
                }
                LocalDate dataValidade;
                try{
                    dataValidade = LocalDate.parse(dtValidadeTxt.trim());
                } catch (DateTimeParseException e) {
                    voltaComErro(req, resp,"Erro ao alterar produto da geladeira.", jsp_alterar);
                    return;
                }
                // lote é opcional: vazio vira 0 ("sem lote")
                int lote;
                try{
                    lote = (loteTxt == null || loteTxt.isBlank()) ? 0 : Integer.parseInt(loteTxt.trim());
                } catch (NumberFormatException e) {
                    voltaComErro(req, resp,"Erro ao alterar produto da geladeira.", jsp_alterar);
                    return;
                }
                int quantidade;
                try{
                    quantidade = Integer.parseInt(quantTxt.trim());
                } catch (NumberFormatException e) {
                    voltaComErro(req, resp,"Erro ao alterar produto da geladeira.", jsp_alterar);
                    return;
                }

                boolean vNome = validar.validarNomeProduto(nome);
                if (!vNome){
                    voltaComErro(req, resp,"Formato do nome incorreto.", jsp_alterar);
                    return;
                }

                boolean vPreco = validar.validarPreco(preco);
                if (!vPreco){
                    voltaComErro(req, resp,"Formato de preço incorreto.", jsp_alterar);
                    return;
                }

                if (lote < 0){
                    voltaComErro(req, resp,"Lote inválido.", jsp_alterar);
                    return;
                }
                boolean vQuant = validar.validarQuantidade(quantidade);
                if (!vQuant){
                    voltaComErro(req, resp,"Quantidade deve ser maior que zero", jsp_alterar);
                    return;
                }
                boolean vValidade = validar.validarValidade(dataValidade);
                if (!vValidade){
                    voltaComErro(req, resp,"A data de validade não pode ser anterior a data de amanhã.", jsp_alterar);
                    return;
                }

                int idItemAntigo;
                try{
                    idItemAntigo = Integer.parseInt(idItemAntigoTxt);
                } catch (NumberFormatException e) {
                    voltaComErro(req, resp,"Erro ao alterar produto da geladeira.", jsp_alterar);
                    return;
                }

                Produto produtoNovo;
                if (descricao == null || descricao.isBlank()){
                    produtoNovo = new Produto(nome, preco, categoria, marca, unidadeMedida);
                }else {
                    produtoNovo = new Produto(nome, descricao, preco, categoria, marca, unidadeMedida);
                }



                ProdutosGeladeira itemAntigo = igDAO.buscarProdutoGeladeiraPorId(idItemAntigo);
                if (itemAntigo == null){
                    voltaComErro(req, resp,"Erro ao alterar produto da geladeira.", jsp_alterar);
                    return;
                }

                Geladeira geladeira = geladeiraDAO.buscarUnicaGeladeira(usuario.getCpf(), itemAntigo.getGeladeira().getIdGeladeira());

                if (geladeira == null){
                    voltaComErro(req, resp,"Erro ao alterar produto da geladeira.", jsp_alterar);
                    return;
                }

                // regra de negocio: a quantidade antiga sai da conta antes de somar a nova
                Assinatura assinatura = assinaturaDAO.buscarAssinaturaUsuario(usuario.getCpf());
                if (assinatura == null){
                    voltaComErro(req, resp, "Escolha um plano antes de alterar produtos.", jsp_alterar);
                    return;
                }
                int capacidade = TipoAssinatura.quantidadePorPlano(assinatura.getTipoAssinatura());
                int totalAtual = ArmazenamentoService.totalProdutosGeladeira(geladeira.getIdGeladeira());
                if (capacidade > 0 && totalAtual - itemAntigo.getQuantidade() + quantidade > capacidade){
                    voltaComErro(req, resp, "Limite do seu plano: " + capacidade
                            + " unidades por geladeira (já tem " + totalAtual + ").", jsp_alterar);
                    return;
                }

                ProdutosGeladeira itemNovo = new ProdutosGeladeira(0, geladeira, produtoNovo, dataValidade, lote, quantidade);
                if (igDAO.alterarProdutoGeladeira(itemAntigo, itemNovo) == 1){
                    // atualizando o status da geladeira
                    ArmazenamentoService.calcularStatusGeladeira(geladeira, assinatura);
                    geladeiraDAO.atualizarStatus(geladeira);

                    resp.sendRedirect(req.getContextPath() + url_servlet_produto + "?idGeladeira=" + geladeira.getIdGeladeira());
                    return;
                }
                else {
                    voltaComErro(req, resp,"Erro ao alterar produto da geladeira.", jsp_alterar);
                    return;
                }
            }


            case "/deletar-ig":{
               //pegando o id do itemgeladeira
               String idTxt = req.getParameter("idProdutoGeladeira");
               int id;
               try {
                   id = Integer.parseInt(idTxt);
               } catch (NumberFormatException e) {
                   voltaComErro(req, resp, "Erro ao deletar esse produto. Por favor tente novamente.", jsp_deletar);
                   return;
               }
               ProdutosGeladeira itemg = igDAO.buscarProdutoGeladeiraPorId(id);
               if (itemg == null){
                   voltaComErro(req, resp, "Erro ao deletar esse produto. Por favor tente novamente.", jsp_deletar);
                   return;
               }
               Geladeira geladeira = geladeiraDAO.buscarUnicaGeladeira(usuario.getCpf(), itemg.getGeladeira().getIdGeladeira());

               if(geladeira == null){
                   voltaComErro(req, resp, "Erro ao deletar esse produto. Por favor tente novamente.", jsp_deletar);
                   return;
               }
               if (igDAO.deletarProdutoGeladeira(itemg) == 1){
                   // atualizando o status da geladeira
                   Assinatura assinatura = assinaturaDAO.buscarAssinaturaUsuario(usuario.getCpf());
                   if (assinatura != null){
                       ArmazenamentoService.calcularStatusGeladeira(geladeira, assinatura);
                       geladeiraDAO.atualizarStatus(geladeira);
                   }

                   resp.sendRedirect(req.getContextPath() + url_servlet_produto + "?idGeladeira=" + geladeira.getIdGeladeira());
                   return;
               }else {
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
