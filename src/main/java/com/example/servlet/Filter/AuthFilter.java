package com.example.servlet.Filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebFilter("/admin/*")
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        // acentos chegando certo em todos os servlets do admin
        req.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession(false);

        // se nao tem admin na sessao tem que fazer login
        if (session == null || session.getAttribute("admin") == null){
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        // a mensagem de sucesso fica na sessao entre o redirect e a tela, aqui ela vai para a request e some da sessao
        Object sucesso = session.getAttribute("sucesso");
        if (sucesso != null){
            req.setAttribute("sucesso", sucesso);
            session.removeAttribute("sucesso");
        }

        chain.doFilter(request, response);
    }
}
