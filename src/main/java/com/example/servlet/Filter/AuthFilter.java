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

        // as telas do painel nao ficam guardadas no navegador ( depois de sair, o botao Voltar nao mostra os dados )
        resp.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        resp.setHeader("Pragma", "no-cache");
        resp.setDateHeader("Expires", 0);

        HttpSession session = req.getSession(false);

        // se nao tem admin na sessao tem que fazer login
        if (session == null || session.getAttribute("admin") == null){
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        // protecao contra formulario enviado por outro site: um POST so vale se vier desta mesma pagina
        if ("POST".equalsIgnoreCase(req.getMethod()) && !vemDoMesmoSite(req)){
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        // a mensagem de sucesso fica na sessao entre o redirect e a tela, aqui ela vai para a request e some da sessao
        Object sucesso = session.getAttribute("sucesso");
        if (sucesso != null){
            req.setAttribute("sucesso", sucesso);
            session.removeAttribute("sucesso");
        }

        // o erro do formulario tambem vem da sessao ( o servlet redireciona de volta para a tela )
        Object erro = session.getAttribute("erro");
        if (erro != null){
            req.setAttribute("erro", erro);
            session.removeAttribute("erro");
        }

        chain.doFilter(request, response);
    }

    // o navegador manda Origin ( ou Referer ) nos POST: o endereço tem que ser o mesmo do site
    private boolean vemDoMesmoSite(HttpServletRequest req){
        String origem = req.getHeader("Origin");
        if (origem == null || origem.isBlank()){
            origem = req.getHeader("Referer");
        }
        // sem nenhum dos dois ( ex.: ferramentas de teste ) nao da para saber, deixa passar
        if (origem == null || origem.isBlank()){
            return true;
        }
        try {
            String host = java.net.URI.create(origem).getAuthority();
            return host != null && host.equalsIgnoreCase(req.getHeader("Host"));
        } catch (IllegalArgumentException e){
            return false;
        }
    }
}
