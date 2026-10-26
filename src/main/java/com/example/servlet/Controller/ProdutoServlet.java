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
import com.example.servlet.Util.Enum.StatusProduto;
import com.example.servlet.Util.Service.ArmazenamentoService;
import com.example.servlet.Util.Paginacao;
import com.example.servlet.Util.Service.PlanoService;
import com.example.servlet.Util.Validacao;
import com.example.servlet.Util.Voltar;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import com.example.servlet.Util.Ordenar;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = {
        "/admin/produtos"},
        name = "produtos")
public class ProdutoServlet extends HttpServlet {
    private ProdutosGeladeiraDAO igDAO;
    private ProdutosFrigorificoDAO itfDAO;
    private GeladeiraDAO geladeiraDAO;
    private FrigorificosDAO frigorificoDAO;
    private AssinaturaDAO assinaturaDAO;
    private PlanoService planoService;
    private Validacao validar;

    // caminho das paginas
    private static final String jsp_produtos = "/WEB-INF/pages/produtos.jsp";
    private static final String jsp_produtos_form = "/WEB-INF/pages/produtos-form.jsp";

    // caminho para o servlet
    private static final String url_servlet = "/admin/produtos";

    // as unidades de medida do campo do formulario
    private static final String[] unidades = {"un", "kg", "g", "L", "ml", "dz"};

    @Override
    public void init(){
        igDAO = new ProdutosGeladeiraDAO();
        itfDAO = new ProdutosFrigorificoDAO();
        geladeiraDAO = new GeladeiraDAO();
        frigorificoDAO = new FrigorificosDAO();
        assinaturaDAO = new AssinaturaDAO();
        planoService = new PlanoService();
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

                    // cabe no plano do dono da geladeira?
                    String erroPlano = planoService.validarCapacidadeGeladeira(geladeira.getIdGeladeira(), geladeira.getUsuario().getCpf(), quantidade, 0);
                    if (erroPlano != null){
                        voltaComErro(req, resp, erroPlano);
                        return;
                    }
                    ProdutosGeladeira itemg = new ProdutosGeladeira(0, geladeira, produto, dataValidade, lote, quantidade);
                    if (igDAO.inserirProdutoGeladeira(itemg) == 1){
                        atualizarStatusGeladeira(geladeira);
                        session.setAttribute("sucesso", "Item cadastrado na geladeira com sucesso.");
                        resp.sendRedirect(req.getContextPath() + url_servlet);
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

                    // cabe no plano da empresa dona do frigorifico?
                    String erroPlano = planoService.validarCapacidadeFrigorifico(frigorifico.getIdFrigorifico(), frigorifico.getEmpresa().getCnpj(), quantidade, 0);
                    if (erroPlano != null){
                        voltaComErro(req, resp, erroPlano);
                        return;
                    }
                    ProdutosFrigorifico itemf = new ProdutosFrigorifico(0, frigorifico, produto, dataValidade, lote, quantidade);
                    if (itfDAO.inserirProdutoFrigorifico(itemf) == 1){
                        atualizarStatusFrigorifico(frigorifico);
                        session.setAttribute("sucesso", "Item cadastrado no frigorífico com sucesso.");
                        resp.sendRedirect(req.getContextPath() + url_servlet);
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

                    // as unidades que o item já tinha nessa mesma geladeira saem da conta
                    int jaTinha = itemAntigo.getGeladeira().getIdGeladeira() == geladeira.getIdGeladeira() ? itemAntigo.getQuantidade() : 0;
                    String erroPlano = planoService.validarCapacidadeGeladeira(geladeira.getIdGeladeira(), geladeira.getUsuario().getCpf(), quantidade, jaTinha);
                    if (erroPlano != null){
                        voltaComErro(req, resp, erroPlano);
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
                        resp.sendRedirect(req.getContextPath() + url_servlet);
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

                    int jaTinha = itemAntigo.getFrigorificos().getIdFrigorifico() == frigorifico.getIdFrigorifico() ? itemAntigo.getQuantidade() : 0;
                    String erroPlano = planoService.validarCapacidadeFrigorifico(frigorifico.getIdFrigorifico(), frigorifico.getEmpresa().getCnpj(), quantidade, jaTinha);
                    if (erroPlano != null){
                        voltaComErro(req, resp, erroPlano);
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
                        resp.sendRedirect(req.getContextPath() + url_servlet);
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
                        resp.sendRedirect(req.getContextPath() + url_servlet);
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
                        resp.sendRedirect(req.getContextPath() + url_servlet);
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

    // carrega a lista ( ja filtrada e ordenada ) com os itens das geladeiras e dos frigorificos e mostra a tela
    private void listar(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException{
        // filtros da tela
        String fProduto = req.getParameter("produto");
        String fDono = req.getParameter("dono");
        String fArm = req.getParameter("arm");
        String fCat = req.getParameter("cat");
        String fStatus = req.getParameter("status");
        String fVal = req.getParameter("val");
        String fOrd = req.getParameter("ord");
        // vindos dos links "ver os produtos" das telas de geladeira e de frigorifico
        String fIdGeladeira = req.getParameter("idGeladeira");
        String fIdFrigorifico = req.getParameter("idFrigorifico");

        List<Geladeira> geladeiras = geladeiraDAO.buscarTodasGeladeira();
        List<Frigorificos> frigorificos = frigorificoDAO.buscarTodosFrigorifico();

        // juntando os itens das geladeiras e dos frigorificos numa lista so
        List<Map<String, Object>> todos = new ArrayList<>();
        for (ProdutosGeladeira ig : igDAO.buscarTodosProdutosGeladeira()){
            Map<String, Object> linha = new HashMap<>();
            linha.put("chave", "g-" + ig.getIdProdutosGeladeira());
            linha.put("tipoArmazenamento", "geladeira");
            linha.put("idItem", ig.getIdProdutosGeladeira());
            linha.put("idArmazenamento", ig.getGeladeira().getIdGeladeira());
            linha.put("armazenamento", ig.getGeladeira().getNome());
            linha.put("donoTipo", "usuario");
            linha.put("dono", ig.getGeladeira().getUsuario().getNome() + " " + ig.getGeladeira().getUsuario().getSobrenome());
            linha.put("produto", ig.getProduto());
            linha.put("quantidade", ig.getQuantidade());
            linha.put("lote", ig.getLote());
            linha.put("dtValidade", ig.getDtValidade());
            todos.add(linha);
        }
        for (ProdutosFrigorifico itf : itfDAO.buscarTodosProdutosFrigorifico()){
            Map<String, Object> linha = new HashMap<>();
            linha.put("chave", "f-" + itf.getIdProdutosFrigorifico());
            linha.put("tipoArmazenamento", "frigorifico");
            linha.put("idItem", itf.getIdProdutosFrigorifico());
            linha.put("idArmazenamento", itf.getFrigorificos().getIdFrigorifico());
            linha.put("armazenamento", itf.getFrigorificos().getNome());
            linha.put("donoTipo", "empresa");
            linha.put("dono", itf.getFrigorificos().getEmpresa().getNome());
            linha.put("produto", itf.getProduto());
            linha.put("quantidade", itf.getQuantidade());
            linha.put("lote", itf.getLote());
            linha.put("dtValidade", itf.getDtValidade());
            todos.add(linha);
        }

        // quantos dias faltam para vencer e a situação de cada item ( calculados na hora, pela data de validade )
        for (Map<String, Object> linha : todos){
            LocalDate validade = (LocalDate) linha.get("dtValidade");
            linha.put("dias", ChronoUnit.DAYS.between(LocalDate.now(), validade));
            linha.put("status", StatusProduto.calcularStatus(validade));
        }

        List<Map<String, Object>> linhas = new ArrayList<>();
        for (Map<String, Object> linha : todos){
            Produto produto = (Produto) linha.get("produto");
            long dias = (Long) linha.get("dias");
            String texto = (produto.getNome() + " " + produto.getMarca()).toLowerCase();

            boolean achouProduto = fProduto == null || fProduto.isBlank() || texto.contains(fProduto.trim().toLowerCase());
            boolean achouDono = fDono == null || fDono.isBlank() || fDono.equals(linha.get("donoTipo"));
            boolean achouArm = fArm == null || fArm.isBlank() || fArm.equals(linha.get("tipoArmazenamento"));
            boolean achouCat = fCat == null || fCat.isBlank() || fCat.equals(produto.getCategoria().name());
            boolean achouStatus = fStatus == null || fStatus.isBlank() || fStatus.equals(((StatusProduto) linha.get("status")).name());
            boolean achouVal = fVal == null || fVal.isBlank()
                    || (fVal.equals("vencidos") && dias < 0)
                    || (fVal.equals("7") && dias >= 0 && dias <= 7)
                    || (fVal.equals("30") && dias >= 0 && dias <= 30)
                    || (fVal.equals("longa") && dias > 30);
            boolean achouGeladeira = fIdGeladeira == null || fIdGeladeira.isBlank()
                    || ("geladeira".equals(linha.get("tipoArmazenamento")) && fIdGeladeira.trim().equals(String.valueOf(linha.get("idArmazenamento"))));
            boolean achouFrigorifico = fIdFrigorifico == null || fIdFrigorifico.isBlank()
                    || ("frigorifico".equals(linha.get("tipoArmazenamento")) && fIdFrigorifico.trim().equals(String.valueOf(linha.get("idArmazenamento"))));

            if (achouProduto && achouDono && achouArm && achouCat && achouStatus && achouVal && achouGeladeira && achouFrigorifico){
                linhas.add(linha);
            }
        }

        // ordem da lista ( vem do select "Ordenar por" ), a inserção usa o id do produto ( ele é novo a cada item )
        Map<String, Comparator<Map<String, Object>>> ordens = new LinkedHashMap<>();
        Ordenar.campo(ordens, "dias", Comparator.comparingLong(l -> (Long) l.get("dias")));
        Ordenar.campo(ordens, "dono", (x, y) -> Ordenar.texto((String) x.get("dono"), (String) y.get("dono")));
        Ordenar.campo(ordens, "nome", (x, y) -> Ordenar.texto(((Produto) x.get("produto")).getNome(), ((Produto) y.get("produto")).getNome()));
        Ordenar.campo(ordens, "novo", Comparator.comparingInt(l -> ((Produto) l.get("produto")).getIdProdutos()));
        String ordemUsada = Ordenar.ordenar(linhas, fOrd, "dias:asc", ordens);

        // ver os detalhes ou editar um item ( vem no link da linha ), ele sai da lista que ja foi carregada
        String ver = req.getParameter("ver");
        String editar = req.getParameter("editar");
        String chaveEscolhida = (ver != null && !ver.isBlank()) ? ver.trim() : (editar != null && !editar.isBlank() ? editar.trim() : null);
        if (chaveEscolhida != null){
            for (Map<String, Object> linha : todos){
                if (chaveEscolhida.equals(linha.get("chave"))){
                    req.setAttribute("registro", linha);
                    req.setAttribute("modo", ver != null && !ver.isBlank() ? "ver" : "editar");
                }
            }
        }

        req.setAttribute("linhas", Paginacao.aplicar(req, linhas));
        req.setAttribute("totalGeral", todos.size());
        req.setAttribute("geladeiras", geladeiras);
        req.setAttribute("frigorificos", frigorificos);
        req.setAttribute("categorias", Categoria.values());
        req.setAttribute("statusProduto", StatusProduto.values());
        req.setAttribute("unidades", unidades);
        req.setAttribute("filtroProduto", fProduto == null ? "" : fProduto);
        req.setAttribute("filtroDono", fDono == null ? "" : fDono);
        req.setAttribute("filtroArm", fArm == null ? "" : fArm);
        req.setAttribute("filtroCat", fCat == null ? "" : fCat);
        req.setAttribute("filtroStatus", fStatus == null ? "" : fStatus);
        req.setAttribute("filtroVal", fVal == null ? "" : fVal);
        req.setAttribute("filtroOrd", ordemUsada);
        if (req.getParameter("novo") != null && req.getAttribute("modo") == null){
            req.setAttribute("modo", "novo");
        }
        req.getRequestDispatcher(req.getAttribute("modo") != null ? jsp_produtos_form : jsp_produtos).forward(req, resp);
    }

    // volta para a tela do formulario com uma mensagem de erro
    private void voltaComErro(HttpServletRequest req, HttpServletResponse resp, String mensagem)
            throws ServletException, IOException{
        req.getSession().setAttribute("erro", mensagem);
        resp.sendRedirect(Voltar.destino(req, url_servlet));
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
