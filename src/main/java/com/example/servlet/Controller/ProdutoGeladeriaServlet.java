package com.example.servlet.Controller;

import com.example.servlet.Dao.GeladeiraDAO;
import com.example.servlet.Dao.ProdutoDAO;
import com.example.servlet.Dao.ProdutosGeladeiraDAO;
import com.example.servlet.Model.ProdutosGeladeira;
import com.example.servlet.Util.Enum.Categoria;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@WebServlet(urlPatterns = {"/tela-padrao-pg",
        "/inserir-pg",
        "/alterar-pg",
        "/deletar-pg",
        "/busca-pg",},
        name = "/tela-padrao-pg")
public class ProdutoGeladeriaServlet extends HttpServlet {
    private ProdutosGeladeiraDAO pgDao = new ProdutosGeladeiraDAO();
    private GeladeiraDAO gelaDao;
    private ProdutoDAO prodDAO;
    private String action;
    private String metodo;
    private String filtro;
    private String value;
    private HttpSession session;

    @Override
    public void init(){
        prodDAO = new ProdutoDAO();
        pgDao = new ProdutosGeladeiraDAO();
        gelaDao = new GeladeiraDAO();
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // pegando a pagina usando o action
        action = req.getParameter("action");
        switch (action){
            case "/tela-padrao-pg":{
                List<ProdutosGeladeira> pgTodos = pgDao.buscarTodosProdutosGeladeira()


                filtro = req.getParameter("filtro");
                value = req.getParameter("value");
                switch (filtro){
                    case "lote":{
                        int lote = Integer.parseInt(value);
                        List<ProdutosGeladeira> pgBusca = pgDao.buscarProdutosGeladeiraPeloLote(lote);
                        for (ProdutosGeladeira prodGela : pgBusca){
                            resp.getWriter().println(prodGela);
                        }
                        resp.sendRedirect(req.getContextPath() + "/pg-busca-lote.jsp");
                        break;
                    }
                    case "validade":{
                        String[] data = filtro.split("/");
                        int dia, mes, ano;
                        for (int i = 0; i < data.length; i++){
                            int dataInt = Integer.parseInt(data[i]);
                            dia = dataInt;
                            mes = dataInt;
                            ano = dataInt;
                        }
                        LocalDate validade = LocalDate.of(dia, mes, ano);

                        List<ProdutosGeladeira> pgBusca = pgDao.buscarProdutosGeladeiraPorValidade(validade);
                        for(ProdutosGeladeira prodGela : pgBusca){
                            resp.getWriter().println(gelaDao);
                        }
                        resp.sendRedirect(req.getContextPath() + "pg-busca-validade.jsp");
                    }
                }
            }
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // pegando a pagina usando o action
        action = req.getParameter("action");
        switch (action){
            case "/inserir-pg":{
                // pegando as informações do formulario
                String nome = req.getParameter("nome");
                String descricao = req.getParameter("descricao");
                String preco = req.getParameter("preco");
                Categoria categoria = Categoria.valueOf(req.getParameter("categoria"));
                String marca = req.getParameter("marca");
                String unidadeMedida = req.getParameter("unidadeMedida");
                String
            }
        }

    }
}