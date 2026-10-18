package com.example.servlet.Controller;

import com.example.servlet.Dao.AssinaturaDAO;
import com.example.servlet.Dao.GeladeiraDAO;
import com.example.servlet.Dao.ProdutoDAO;
import com.example.servlet.Dao.ProdutosGeladeiraDAO;
import com.example.servlet.Dao.UsuarioDAO;
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

@WebServlet(urlPatterns = {"/tela-padrao-ig",
        "/inserir-ig",
        "/alterar-ig",
        "/deletar-ig",
        "/busca-ig"},
        name = "produtoGeladeira")
public class ProdutoGeladeiraServlet extends HttpServlet {
    private ProdutosGeladeiraDAO pgDao;
    private GeladeiraDAO gelaDao;
    private ProdutoDAO prodDao;
    private UsuarioDAO usuarioDao;
    private AssinaturaDAO assinaturaDao;
    private Validacao validar;

    // Caminhos das páginas
    private static final String jsp_login = "/assets/pages/login.jsp";
    private static final String JSP_INSERIR = "/assets/pages/inserir-ig.jsp";
    private static final String JSP_ALTERAR = "/assets/pages/alterar-ig.jsp";
    private static final String JSP_DELETAR = "/assets/pages/deletar-ig.jsp";
    private static final String URL_LISTA = "/tela-padrao-ig";

    @Override
    public void init() {
        pgDao = new ProdutosGeladeiraDAO();
        gelaDao = new GeladeiraDAO();
        prodDao = new ProdutoDAO();
        usuarioDao = new UsuarioDAO();
        assinaturaDao = new AssinaturaDAO();
        validar = new Validacao();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // o usuario está logado?
        Usuario usuario = usuarioLogado(req);
        if (usuario == null) {
            resp.sendRedirect(req.getContextPath() + JSP_LOGIN);
            return;
        }

        // qual ação?
        switch (req.getServletPath()) {
            case "/inserir-ig":
                inserir(req, resp, usuario);
                break;
            case "alterar-ig":

            default:
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void inserir(HttpServletRequest req, HttpServletResponse resp, Usuario usuario)
            throws ServletException, IOException {

        // dados do PRODUTO
        String nome = textoLimpo(req.getParameter("nome"));
        String descricao = textoLimpo(req.getParameter("descricao"));
        String marca = textoLimpo(req.getParameter("marca"));
        String unidadeMedida = textoLimpo(req.getParameter("unidadeMedida"));
        String precoTxt = req.getParameter("preco");
        String categoriaTxt = req.getParameter("categoria");
        // dados do ITEM NA GELADEIRA
        String idGeladeiraTxt = req.getParameter("idGeladeira");
        String dtValidadeTxt = req.getParameter("dtValidade");
        String loteTxt = req.getParameter("lote");
        String quantidadeTxt = req.getParameter("quantidade");

        // convertendo os dados
        Double preco = lerDecimal(precoTxt);
        Categoria categoria = lerCategoria(categoriaTxt);
        Integer idGeladeira = lerInteiro(idGeladeiraTxt);
        Integer quantidade = lerInteiro(quantidadeTxt);
        LocalDate dtValidade = lerData(dtValidadeTxt);
        // lote é opcional: vazio vira 0 ("sem lote")
        Integer lote = (loteTxt == null || loteTxt.trim().isEmpty()) ? Integer.valueOf(0) : lerInteiro(loteTxt);

        // validando
        if (nome.isBlank() || marca.isBlank() || unidadeMedida.isBlank()
                || preco == null || categoria == null || idGeladeira == null
                || quantidade == null || dtValidade == null || lote == null) {
            voltarComErro(req, resp, "Preencha todos os campos obrigatórios corretamente.");
            return;
        }


        if (!validar.validarNomeProduto(nome)) {
            voltarComErro(req, resp, "Nome do produto inválido.");
            return;
        }
        if (!validar.validarPreco(preco)) {
            voltarComErro(req, resp, "O preço deve ser maior que zero.");
            return;
        }
        if (!validar.validarQuantidade(quantidade)) {
            voltarComErro(req, resp, "A quantidade deve ser maior que zero.");
            return;
        }
        if (!validar.validarValidade(dtValidade)) {
            voltarComErro(req, resp, "A validade deve ser uma data futura.");
            return;
        }
        if (lote < 0) {
            voltarComErro(req, resp, "Lote inválido.");
            return;
        }


        Geladeira geladeira = gelaDao.buscarUnicaGeladeira(usuario.getCpf(), idGeladeira);
        if (geladeira == null) {
            voltarComErro(req, resp, "Geladeira não encontrada.");
            return;
        }

        // aplicando a regra de negocio
        Assinatura assinatura = assinaturaDao.buscarAssinaturaUsuario(usuario.getCpf());
        if (assinatura == null) {
            voltarComErro(req, resp, "Escolha um plano antes de adicionar produtos.");
            return;
        }
        int capacidade = TipoAssinatura.quantidadePorPlano(assinatura.getTipoAssinatura());
        int totalAtual = ArmazenamentoService.totalProdutosGeladeira(geladeira.getIdGeladeira());
        if (capacidade > 0 && totalAtual + quantidade > capacidade) {
            voltarComErro(req, resp, "Limite do seu plano: " + capacidade
                    + " unidades por geladeira (já tem " + totalAtual + ").");
            return;
        }


        Produto produto = new Produto(nome, descricao, preco, categoria, marca, unidadeMedida);

        ProdutosGeladeira ig = new ProdutosGeladeira(0, geladeira, produto, dtValidade, lote, quantidade);

        if (!gravarProdutoEItem(produto, ig)) {
            voltarComErro(req, resp, "Não foi possível salvar o produto. Tente novamente.");
            return;
        }

        // atualizar o status da geladeira e gravar no banco
        ArmazenamentoService.calcularStatusGeladeira(geladeira, assinatura);
        gelaDao.atualizarStatus(geladeira);

        resp.sendRedirect(req.getContextPath() + URL_LISTA + "?idGeladeira=" + geladeira.getIdGeladeira());
    }

    // alterar
    public void alterar(HttpServletRequest req, HttpServletResponse resp, Usuario usuario){
        // pegando os dados do formulario
        String idProdutoGeladeiraTxt = req.getParameter("idProdutoGeladeira");
        int idPg = Integer.parseInt(idProdutoGeladeiraTxt);
        ProdutosGeladeira pgAntigo = pgDao.buscarProdutoGeladeiraPorId(idPg);


        // dados do PRODUTO
        String nome = textoLimpo(req.getParameter("nome"));
        String descricao = textoLimpo(req.getParameter("descricao"));
        String marca = textoLimpo(req.getParameter("marca"));
        String unidadeMedida = textoLimpo(req.getParameter("unidadeMedida"));
        String precoTxt = req.getParameter("preco");
        String categoriaTxt = req.getParameter("categoria");
        // dados do ITEM NA GELADEIRA
        String idGeladeiraTxt = req.getParameter("idGeladeira");
        String dtValidadeTxt = req.getParameter("dtValidade");
        String loteTxt = req.getParameter("lote");
        String quantidadeTxt = req.getParameter("quantidade");

        // convertendo os dados
        Double preco = lerDecimal(precoTxt);
        Categoria categoria = lerCategoria(categoriaTxt);
        Integer idGeladeira = lerInteiro(idGeladeiraTxt);
        Integer quantidade = lerInteiro(quantidadeTxt);
        LocalDate dtValidade = lerData(dtValidadeTxt);
        // lote é opcional: vazio vira 0 ("sem lote")
        Integer lote = (loteTxt == null || loteTxt.trim().isEmpty()) ? Integer.valueOf(0) : lerInteiro(loteTxt);


        Geladeira geladeira = gelaDao.buscarUnicaGeladeira(usuario.getCpf(), idGeladeira);
        if (geladeira == null) {
            voltarComErro(req, resp, "Geladeira não encontrada.");
            return;
        }


        // validando
        if (nome.isEmpty() || marca.isEmpty() || unidadeMedida.isEmpty()
                || preco == null || categoria == null || idGeladeira == null
                || quantidade == null || dtValidade == null || lote == null) {
            voltarComErro(req, resp, "Preencha todos os campos obrigatórios corretamente.", );
            return;
        }


        if (!validar.validarNomeProduto(nome)) {
            voltarComErro(req, resp, "Nome do produto inválido.");
            return;
        }
        if (!validar.validarPreco(preco)) {
            voltarComErro(req, resp, "O preço deve ser maior que zero.");
            return;
        }
        if (!validar.validarQuantidade(quantidade)) {
            voltarComErro(req, resp, "A quantidade deve ser maior que zero.");
            return;
        }
        if (!validar.validarValidade(dtValidade)) {
            voltarComErro(req, resp, "A validade deve ser uma data futura.");
            return;
        }
        if (lote < 0) {
            voltarComErro(req, resp, "Lote inválido.");
            return;
        }

        Produto produto = new Produto(nome, descricao, preco, categoria, marca, unidadeMedida);

        ProdutosGeladeira ig = new ProdutosGeladeira(0, geladeira, produto, dtValidade, lote, quantidade);

    }

    // ================= GRAVAR PRODUTO + ITEM =================
    private boolean gravarProdutoEItem(Produto produto, ProdutosGeladeira ig) {
        // grava o produto.
        if (prodDao.inserirProduto(produto) != 1) {
            return false;
        }

        // grava o item da geladeira
        if (pgDao.inserirProdutoGeladeira(ig) == 1) {
            return true;
        }

        // 2º falhou: desfaz o 1º
        if (prodDao.removerProduto(produto) != 1) {
            System.err.println("ATENÇÃO: produto órfão id=" + produto.getIdProdutos()
                    + " (o item da geladeira falhou e o produto não pôde ser removido).");
        }
        return false;
    }

    private Usuario usuarioLogado(HttpServletRequest req) {
        HttpSession session = req.getSession(false); // false = não cria sessão nova
        if (session == null) {
            return null;
        }
        String email = (String) session.getAttribute("usuarioEmail");
        if (email == null) {
            return null;
        }
        return usuarioDao.buscarUsuario(email);
    }


    private String textoLimpo(String texto) {
        return texto == null ? "" : texto.trim();
    }

    private Integer lerInteiro(String texto) {
        if (texto == null) {
            return null;
        }
        try {
            return Integer.valueOf(texto.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }


    private Double lerDecimal(String texto) {
        if (texto == null) {
            return null;
        }
        try {
            double valor = Double.parseDouble(texto.trim().replace(',', '.'));
            if (Double.isNaN(valor) || Double.isInfinite(valor)) {
                return null;
            }
            return valor;
        } catch (NumberFormatException e) {
            return null;
        }
    }


    private Categoria lerCategoria(String texto) {
        if (texto == null) {
            return null;
        }
        try {
            return Categoria.valueOf(texto.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private LocalDate lerData(String texto) {
        if (texto == null) {
            return null;
        }
        try {
            return LocalDate.parse(texto.trim());
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    // Volta para o formulário mostrando a mensagem.
    private void voltarComErro(HttpServletRequest req, HttpServletResponse resp, String mensagem, String pagina)
            throws ServletException, IOException {
        req.setAttribute("erro", mensagem);
        req.getRequestDispatcher(pagina).forward(req, resp);
    }
}