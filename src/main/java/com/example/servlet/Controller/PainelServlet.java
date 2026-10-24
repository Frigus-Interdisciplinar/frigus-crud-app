package com.example.servlet.Controller;

import com.example.servlet.Dao.*;
import com.example.servlet.Util.Enum.Categoria;
import com.example.servlet.Util.Enum.TipoAssinatura;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(urlPatterns = {
        "/admin/visao-geral",
        "/admin/configuracoes",
        "/admin/teste-banco"},
        name = "painel")
public class PainelServlet extends HttpServlet {
    private UsuarioDAO usuarioDAO;
    private EmpresaDAO empresaDAO;
    private GeladeiraDAO geladeiraDAO;
    private FrigorificosDAO frigorificoDAO;
    private ProdutosGeladeiraDAO igDAO;
    private ProdutosFrigorificoDAO itfDAO;
    private ReceitaDAO receitaDAO;
    private IngredienteDAO ingredienteDAO;
    private ListaComprasDAO listaDAO;
    private ProdutoListaDAO prodListaDAO;
    private AssinaturaDAO assinaturaDAO;
    private SuperAdminDAO superAdminDAO;

    // caminho das paginas
    private static final String jsp_visao_geral = "/WEB-INF/pages/visao-geral.jsp";
    private static final String jsp_configuracoes = "/WEB-INF/pages/configuracoes.jsp";
    private static final String jsp_teste_banco = "/WEB-INF/pages/teste-banco.jsp";

    @Override
    public void init(){
        usuarioDAO = new UsuarioDAO();
        empresaDAO = new EmpresaDAO();
        geladeiraDAO = new GeladeiraDAO();
        frigorificoDAO = new FrigorificosDAO();
        igDAO = new ProdutosGeladeiraDAO();
        itfDAO = new ProdutosFrigorificoDAO();
        receitaDAO = new ReceitaDAO();
        ingredienteDAO = new IngredienteDAO();
        listaDAO = new ListaComprasDAO();
        prodListaDAO = new ProdutoListaDAO();
        assinaturaDAO = new AssinaturaDAO();
        superAdminDAO = new SuperAdminDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = req.getServletPath();

        switch (path){
            case "/admin/visao-geral": {
                carregarContagens(req);
                req.getRequestDispatcher(jsp_visao_geral).forward(req, resp);
                break;
            }
            case "/admin/configuracoes": {
                // os dados da conta de quem esta logado
                req.setAttribute("conta", superAdminDAO.buscarSuperAdmin((String) req.getSession().getAttribute("admin")));
                req.getRequestDispatcher(jsp_configuracoes).forward(req, resp);
                break;
            }
            case "/admin/teste-banco": {
                carregarContagens(req);
                carregarListas(req);
                req.getRequestDispatcher(jsp_teste_banco).forward(req, resp);
                break;
            }
            default:
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    // os outros servlets mandam o POST de volta para cá quando dá erro na tela de teste ( a mensagem de erro já vem na request )
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doGet(req, resp);
    }

    // contagens da visão geral
    private void carregarContagens(HttpServletRequest req){
        req.setAttribute("totalUsuarios", usuarioDAO.totalUsuarios());
        req.setAttribute("totalEmpresas", empresaDAO.totalEmpresas());
        req.setAttribute("totalGeladeiras", geladeiraDAO.totalGeladeiras());
        req.setAttribute("totalFrigorificos", frigorificoDAO.totalFrigorificos());
        req.setAttribute("totalItensGeladeira", igDAO.totalProdutosArmazenados());
        req.setAttribute("totalItensFrigorifico", itfDAO.totalProdutosArmazenados());
        req.setAttribute("totalReceitas", receitaDAO.totalReceitas());
        req.setAttribute("totalListasCompras", listaDAO.totalListasCompras());
        req.setAttribute("totalItensListas", prodListaDAO.totalItensListas());
        req.setAttribute("totalAssinaturas", assinaturaDAO.totalAssinaturas());
    }

    // todas as listas da tela de teste do banco
    private void carregarListas(HttpServletRequest req){
        req.setAttribute("usuarios", usuarioDAO.buscarTodosUsuario());
        req.setAttribute("empresas", empresaDAO.buscarTodasEmpresa());
        req.setAttribute("geladeiras", geladeiraDAO.buscarTodasGeladeira());
        req.setAttribute("frigorificos", frigorificoDAO.buscarTodosFrigorifico());
        req.setAttribute("itensGeladeira", igDAO.buscarTodosProdutosGeladeira());
        req.setAttribute("itensFrigorifico", itfDAO.buscarTodosProdutosFrigorifico());
        req.setAttribute("receitas", receitaDAO.buscarTodasReceita());
        req.setAttribute("ingredientes", ingredienteDAO.buscarTodosIngrediente());
        req.setAttribute("compras", listaDAO.buscarTodasListaCompras());
        req.setAttribute("itensLista", prodListaDAO.buscarTodosProdutoLista());
        req.setAttribute("assinaturas", assinaturaDAO.buscarTodasAssinatura());
        req.setAttribute("categorias", Categoria.values());
        req.setAttribute("planos", TipoAssinatura.values());
    }
}
