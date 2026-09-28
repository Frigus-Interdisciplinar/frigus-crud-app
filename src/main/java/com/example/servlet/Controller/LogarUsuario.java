package com.example.servlet.Controller;

import com.example.servlet.Dao.UsuarioDAO;
import com.example.servlet.Model.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/LogarUsuario")
public class LogarUsuario extends HttpServlet {

//    @Override
//    protected void doGet(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//
//    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {


//        // pegando a ação do usuario
//        String action = req.getParameter("botao");

        Usuario usuario = null;
        UsuarioDAO usuarioDAO = new UsuarioDAO();

//        // escolhe qual metodo chamar usando botões do web
//        switch (action) {
        //case "inserir":
        // pegando os dados do fornmulario de cadastro
        String cpf = req.getParameter("cpf");
        String nomeCompleto = req.getParameter("nome");
        String email = req.getParameter("email");
        String senha = req.getParameter("senha");
        String telefone = req.getParameter("telefone");

        // convertendo, formatando e validando os dados
        String[] nome = nomeCompleto.split(" ");
        String primeiroNome = nome[0];
        String sobrenome ="";
        for(int i = 1; i < nome.length; i++){
            sobrenome += sobrenome + " ";
        }

        // criando o objeto usuario e usuario dao
        usuario = new Usuario(
                cpf,
                primeiroNome,
                sobrenome,
                email,
                senha,
                telefone
        );

        int deucerto = usuarioDAO.adicionarUsuario(usuario);
            // mensagem
               // break;
//            case "alterar":
//                Usuario alteracaoUsuario = new Usuario();
//                usuarioDAO.alterarUsuario(alteracaoUsuario);
//                // mensagem
//            case "deletar":
//                usuarioDAO.deletarUsuario(usuario);
        if (deucerto == 1){

            resp.sendRedirect("/deuCerto.jsp");
        }
        else{
            resp.sendRedirect("/deuErrado.jsp");

        }
    }
}