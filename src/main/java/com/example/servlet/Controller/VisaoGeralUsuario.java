package com.example.servlet.Controller;

import com.example.servlet.Dao.*;
import com.example.servlet.Dto.SugestaoReceita;
import com.example.servlet.Model.ProdutosGeladeira;
import com.example.servlet.Model.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/visao-geral-usuario")
public class VisaoGeralUsuario extends HttpServlet {
    private UsuarioDAO usuarioDAO;
    private ProdutosGeladeiraDAO pgDAO;
    private GeladeiraDAO geladeiraDAO;
    private ProdutoListaDAO prodListaDAO;
    private ReceitaDAO receitaDAO;
    // caminho para o login
    private static final String jsp_login = "/assets/pages/login.jsp";
    private static final String jsp_dashboard = "/assets/pages/visao-geral-usuario.jsp";


    @Override
    public void init(){
        usuarioDAO = new UsuarioDAO();
        pgDAO = new ProdutosGeladeiraDAO();
        geladeiraDAO = new GeladeiraDAO();
        prodListaDAO = new ProdutoListaDAO();
        receitaDAO = new ReceitaDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        // verificando se esta logado
        if (session == null || session.getAttribute("usuarioEmail") == null){
            voltaComErro(req, resp, "Usuário não encontrado! Por favor Cadastre-se novamente.", jsp_login);
            return;
        }
        String email = (String) session.getAttribute("usuarioEmail");
        Usuario usuario = usuarioDAO.buscarUsuario(email);
        // verificando se a empresa ainda exite
        if (usuario == null){
            voltaComErro(req, resp, "Usuário não encontrado! Por favor Cadastre-se novamente.", jsp_login);
            return;
        }

        int totalProdutosArmazenados = pgDAO.totalProdutosArmazenados(usuario.getCpf());

        int totalProdutosProximosVencimento = pgDAO.totalProdutosProximoVencimento(usuario.getCpf());

        int totalProdutosVencidos = pgDAO.totalProdutosVencidos(usuario.getCpf());

        int totalGeladeiras = geladeiraDAO.totalGeladeirasUsuario(usuario.getCpf());

        // pega a ultima lista de compras cadastrada
        int totalItensNumaLista = prodListaDAO.totalItensNumaCompraUsuario(usuario.getCpf());

        int totalReceitasDisponiveis = receitaDAO.totalReceitasDisponiveisUsuario(usuario.getCpf());

        List<SugestaoReceita> sugestaoReceitas = receitaDAO.listarSugestoesUsuario(usuario.getCpf());

        List<ProdutosGeladeira> produtosPertoValidade = pgDAO.produtosPertoValidade(usuario.getCpf());





        req.setAttribute("totalProdutosArmazenados", totalProdutosArmazenados);

        req.setAttribute("totalProdutosProximosVencimento", totalProdutosProximosVencimento);

        req.setAttribute("totalProdutosVencidos", totalProdutosVencidos);

        req.setAttribute("totalGeladeiras", totalGeladeiras);

        req.setAttribute("totalItensNumaLista", totalItensNumaLista);

        req.setAttribute("totalReceitasDisponiveis", totalReceitasDisponiveis);

        req.setAttribute("sugestaoReceitas", sugestaoReceitas);

        req.setAttribute("produtosPertoValidade", produtosPertoValidade);
        req.getRequestDispatcher(jsp_dashboard).forward(req, resp);
    }
    // voltando para a pagina com erro
    public static void voltaComErro(HttpServletRequest req, HttpServletResponse resp, String mensagem, String pagina)
            throws ServletException, IOException{
        req.setAttribute("erro", mensagem);
        req.getRequestDispatcher(pagina).forward(req, resp);
    }
}