package com.example.servlet.Teste;

import java.io.*;

import com.example.servlet.Dao.ProdutoDAO;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet(name = "helloServlet", value = "/hello-servlet")
public class HelloServlet extends HttpServlet {
    private String message;
    ProdutoDAO produtoDAO = new ProdutoDAO();

    public void init() {
        message = "Hello World!";
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String action = "criarProduto";

        switch (action) {
            case "criarPedido":
                criarPedido();
        }

        response.setContentType("text/html");

        // Hello
        PrintWriter out = response.getWriter();
        out.println("<html><body>");
        out.println("<h1>" + message + "</h1>");
        out.println("</body></html>");
    }

    public static void criarPedido() {
        produtoDAO.inserirProduto();
    }

    public void destroy() {

    }
}