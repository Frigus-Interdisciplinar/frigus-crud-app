//package com.example.servlet.Teste;
//
//import java.io.*;
//
//import com.example.servlet.Dao.ProdutoDAO;
//import com.example.servlet.Model.Produto;
//import com.example.servlet.Util.Enum.Categoria;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.*;
//import jakarta.servlet.annotation.*;
//
//@WebServlet(urlPatterns = {"/ServletCargo", "/criarProduto"})
//public class HelloServlet extends HttpServlet {
//    private String message;
//    ProdutoDAO produtoDAO = new ProdutoDAO();
//
//    public void init() {
//        message = "Hello World!";
//    }
//
//    @Override
//    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        String action = req.getParameter("action");
//
//        switch (action) {
//            case "listarProdutos":
//                buscarProduto(req, resp);
//                break;
//        }
//    }
//
//    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        String action = request.getParameter("action");
//
//        switch (action) {
//            case "criarPedido":
//                criarPedido(request, response);
//                break;
//        }
//
//        response.setContentType("text/html");
//
//        // Hello
//        PrintWriter out = response.getWriter();
//        out.println("<html><body>");
//        out.println("<h1>" + message + "</h1>");
//        out.println("</body></html>");
//    }
//
//    public static void buscarProduto() {
//
//    }
//
//    protected void criarPedido(HttpServletRequest request, HttpServletResponse response) {
//        // Inserindo produto
//        Produto produto = new Produto();
//        produto.setNome(request.getParameter("nome"));
//        produto.setDescricao(request.getParameter("descricao"));
//        produto.setPreco(Double.parseDouble(request.getParameter("preco")));
//        produto.setCategoria(Categoria.valueOf(request.getParameter("nome")));
//        produto.setMarca(request.getParameter("marca"));
//        produto.setUnidadeMedida(request.getParameter("unidadeMedida"));
//
//        Integer resposta = produtoDAO.inserirProduto(produto);
//
//        if (resposta == 1) {
//            // Redirecionar para o caminho que lista novamente
//        }
//    }
//
//    public void destroy() {
//
//    }
//}