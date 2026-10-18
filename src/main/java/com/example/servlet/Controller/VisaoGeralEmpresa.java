package com.example.servlet.Controller;

import com.example.servlet.Dao.*;
import com.example.servlet.Dto.SugestaoReceita;
import com.example.servlet.Model.Empresa;
import com.example.servlet.Model.ProdutosFrigorifico;
import com.example.servlet.Model.ProdutosGeladeira;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/visao-geral-empresa")
public class VisaoGeralEmpresa extends HttpServlet {
    private EmpresaDAO empresaDAO;
    private ProdutosFrigorificoDAO pfDAO;
    private FrigorificosDAO frigorificosDAO;
    private ProdutoListaDAO prodListaDAO;
    private ReceitaDAO receitaDAO;
    // caminho para o login
    private static final String jsp_login = "/assets/pages/login.jsp";
    private static final String jsp_dashboard = "/assets/pages/visao-geral-usuario.jsp";

    @Override
    public void init(){
        empresaDAO = new EmpresaDAO();
        pfDAO = new ProdutosFrigorificoDAO();
        frigorificosDAO = new FrigorificosDAO();
        prodListaDAO = new ProdutoListaDAO();
        receitaDAO = new ReceitaDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        // verificando se esta logado
        if (session == null || session.getAttribute("empresaEmail") == null){
            voltaComErro(req, resp, "Empresa não encontrada! Por favor Cadastre-se novamente.", jsp_login);
            return;
        }
        String email = (String) session.getAttribute("empresaEmail");
        Empresa empresa = empresaDAO.buscarEmpresa(email);
        // verificando se a empresa ainda exite
        if (empresa == null){
            voltaComErro(req, resp, "Empresa não encontrada! Por favor Cadastre-se novamente.", jsp_login);
            return;
        }

        int totalProdutosArmazenados = pfDAO.totalProdutosArmazenados(empresa.getCnpj());

        int totalProdutosProximosVencimento = pfDAO.totalProdutosProximoVencimento(empresa.getCnpj());

        int totalProdutosVencidos = pfDAO.totalProdutosVencidos(empresa.getCnpj());

        int totalFrigorificos = frigorificosDAO.totalFrigorificosEmpresa(empresa.getCnpj());

        // pega a ultima lista de compras cadastrada
        int totalItensNumaLista = prodListaDAO.totalItensNumaCompraEmpresa(empresa.getCnpj());

        int totalReceitasDisponiveis = receitaDAO.totalReceitasDisponiveisEmpresa(empresa.getCnpj());

        List<SugestaoReceita> sugestaoReceitas = receitaDAO.listarSugestoesEmpresa(empresa.getCnpj());

        List<ProdutosFrigorifico> produtosPertoValidade = pfDAO.produtosPertoValidade(empresa.getCnpj());



        req.setAttribute("totalProdutosArmazenados", totalProdutosArmazenados);

        req.setAttribute("totalProdutosProximosVencimento", totalProdutosProximosVencimento);

        req.setAttribute("totalProdutosVencidos", totalProdutosVencidos);

        req.setAttribute("totalFrigorificos", totalFrigorificos);

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