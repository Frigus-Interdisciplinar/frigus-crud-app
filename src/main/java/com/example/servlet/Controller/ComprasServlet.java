package com.example.servlet.Controller;

import com.example.servlet.Dao.EmpresaDAO;
import com.example.servlet.Dao.ListaComprasDAO;
import com.example.servlet.Dao.ProdutoListaDAO;
import com.example.servlet.Dao.UsuarioDAO;
import com.example.servlet.Model.Empresa;
import com.example.servlet.Model.ListaCompras;
import com.example.servlet.Model.Produto;
import com.example.servlet.Model.ProdutoLista;
import com.example.servlet.Model.Usuario;
import com.example.servlet.Util.Enum.Categoria;
import com.example.servlet.Util.Paginacao;
import com.example.servlet.Util.Validacao;
import com.example.servlet.Util.Voltar;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import com.example.servlet.Util.Ordenar;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = {
        "/admin/compras"},
       name = "compras")
public class ComprasServlet extends HttpServlet {
    private ListaComprasDAO listaDAO;
    private ProdutoListaDAO prodListaDAO;
    private UsuarioDAO usuarioDAO;
    private EmpresaDAO empresaDAO;
    private Validacao validar;

    // caminho para o servlet logo após concluir alguma tarefa
    private static final String url_ver_compra = "/admin/compras";

    // caminhos das paginas
    private static final String jsp_compras = "/WEB-INF/pages/compras.jsp";
    private static final String jsp_compras_form = "/WEB-INF/pages/compras-form.jsp";

    @Override
    public void init(){
        listaDAO = new ListaComprasDAO();
        prodListaDAO = new ProdutoListaDAO();
        usuarioDAO = new UsuarioDAO();
        empresaDAO = new EmpresaDAO();
        validar = new Validacao();
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // inserir, alterar e deletar só por POST
        if (req.getParameter("action") != null){
            resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }
        // se nao for a tela geral entao é erro
        if (!req.getServletPath().equals("/admin/compras")) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        listar(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession();
        String action = req.getParameter("action");

        if (action == null){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        switch (action){
            case "inserir":{
                // pegando os valores do formulario
                String cpf = req.getParameter("cpf");
                String cnpj = req.getParameter("cnpj");
                String dtListaTxt = req.getParameter("dtLista");

                // o dono é o usuario ou a empresa
                if (!validar.validarDono(cpf, cnpj)){
                    voltaComErro(req, resp, "Escolha só o usuário ou só a empresa dona da lista de compras.");
                    return;
                }

                // convertendo os valores ( data vazia usa a data de hoje )
                LocalDate dtLista = LocalDate.now();
                if (dtListaTxt != null && !dtListaTxt.isBlank()){
                    dtLista = lerData(dtListaTxt);
                    if (dtLista == null){
                        voltaComErro(req, resp, "Data inválida!!!");
                        return;
                    }
                }

                ListaCompras compras = montarLista(cpf, cnpj, dtLista);
                if (compras == null){
                    voltaComErro(req, resp, "Dono da lista de compras não encontrado.");
                    return;
                }

                if (listaDAO.inserirListaCompras(compras) == 1){
                    session.setAttribute("sucesso", "Lista de compras cadastrada com sucesso.");
                    resp.sendRedirect(req.getContextPath() + url_ver_compra);
                    break;
                }else {
                    voltaComErro(req, resp, "Não foi possível adicionar essa compra.");
                    break;
                }

            }


            case "alterar":{
                // pegando o id da compra que será alterada
                String cpf = req.getParameter("cpf");
                String cnpj = req.getParameter("cnpj");
                Integer idCompra = lerInt(req.getParameter("idCompra"));
                if (idCompra == null){
                    System.err.println("o id da compra nunca pode ser nulo!!!");
                    voltaComErro(req, resp, "Erro ao alterar essa compra. Por favor tente novamente.");
                    return;
                }
                ListaCompras compraAntiga = listaDAO.buscarListaComprasPorId(idCompra);
                if (compraAntiga == null) {
                    System.err.println("Compra não encontrada!!!");
                    voltaComErro(req, resp, "Compra não encontrada.");
                    return;
                }

                // o dono é o usuario ou a empresa
                if (!validar.validarDono(cpf, cnpj)){
                    voltaComErro(req, resp, "Escolha só o usuário ou só a empresa dona da lista de compras.");
                    return;
                }

                // pegando a nova data do formulario
                LocalDate dtListaNova = lerData(req.getParameter("dtLista"));
                if (dtListaNova == null){
                    voltaComErro(req, resp, "Data inválida!!!");
                    return;
                }

                ListaCompras compraNova = montarLista(cpf, cnpj, dtListaNova);
                if (compraNova == null){
                    voltaComErro(req, resp, "Dono da lista de compras não encontrado.");
                    return;
                }

                if (listaDAO.alterarListaCompras(compraAntiga, compraNova) == 1){
                    session.setAttribute("sucesso", "Lista de compras alterada com sucesso.");
                    resp.sendRedirect(req.getContextPath() + url_ver_compra);
                    break;
                }
                else {
                    voltaComErro(req, resp, "Não foi possível alterar essa compra.");
                    break;
                }
            }

            case "deletar":{
                // pegando o id da compra no formulario
                Integer idCompra = lerInt(req.getParameter("idCompra"));
                if (idCompra == null){
                    System.err.println("o id não pode ser null nunca!!!");
                    voltaComErro(req, resp, "Erro ao deletar essa compra. Por favor tente novamente.");
                    return;
                }
                ListaCompras compra = listaDAO.buscarListaComprasPorId(idCompra);
                if (compra == null){
                    System.err.println("Compra não encontrada.");
                    voltaComErro(req, resp, "Compra não encontrada.");
                    return;
                }

                if (listaDAO.deletarListaCompras(compra) == 1){
                    session.setAttribute("sucesso", "Lista de compras apagada com sucesso, junto com os itens dela.");
                    resp.sendRedirect(req.getContextPath() + url_ver_compra);
                    break;
                }
                else {
                    voltaComErro(req, resp, "Não foi possível excluir essa compra.");
                    break;
                }
            }


            case "inserirItem":{
                // pegando o id da compra no formulario
                Integer idCompra = lerInt(req.getParameter("idCompra"));
                if (idCompra == null){
                    voltaComErro(req, resp, "Escolha a lista de compras do item.");
                    return;
                }

                // pegando a compra ja existente do banco
                ListaCompras compra = listaDAO.buscarListaComprasPorId(idCompra);

                if (compra == null){
                    System.err.println("a compra nunca deve ser nula!!!");
                    voltaComErro(req, resp, "Compra não encontrada.");
                    return;
                }

                // validando os campos do produto e da quantidade
                String erroCampos = validarCampos(req);
                if (erroCampos != null){
                    voltaComErro(req, resp, erroCampos);
                    return;
                }
                Produto produto = montarProduto(req);
                int quantidade = lerInt(req.getParameter("quantidade"));

                ProdutoLista prodLista = new ProdutoLista(compra, produto, quantidade);
                if (prodListaDAO.adicionarProdutoListas(prodLista) == 1){
                    session.setAttribute("sucesso", "Item cadastrado na lista de compras com sucesso.");
                    resp.sendRedirect(req.getContextPath() + url_ver_compra);
                    return;
                }
                else {
                    voltaComErro(req, resp, "Não foi possível adicionar esse produto a essa lista de compras. Por favor tente novamente");
                    return;
                }
            }


            case "alterarItem":{
                // pegando o id da compra no formulario ( pode ser outra lista )
                Integer idCompra = lerInt(req.getParameter("idCompra"));
                if (idCompra == null){
                    System.err.println("O id da compra nunca pode ser nulo!!!");
                    voltaComErro(req, resp, "Erro ao alterar esse produto dessa compra. Por favor tente novamente.");
                    return;
                }
                // pegando a compra ja existente do banco
                ListaCompras compra = listaDAO.buscarListaComprasPorId(idCompra);
                if (compra == null){
                    System.err.println("compra não encontrada!!!");
                    voltaComErro(req, resp, "Compra não encontrada.");
                    return;
                }

                // pegando o id do produto na lista para mais tarde ser usado para localizar o objeto que sera alterado
                Integer idProdLista = lerInt(req.getParameter("idProdutoLista"));
                if (idProdLista == null){
                    System.err.println("O id do produto na lista de compras nunca pode ser nulo!!!");
                    voltaComErro(req, resp, "Erro ao alterar esse produto dessa compra. Por favor tente novamente.");
                    return;
                }

                // resgatando o item na lista que esta no banco
                ProdutoLista prodListaAntigo = prodListaDAO.buscarProdutoNumaLista(idProdLista);
                if (prodListaAntigo == null){
                    voltaComErro(req, resp, "Item da lista não encontrado.");
                    return;
                }

                // validando os campos do produto e da quantidade
                String erroCampos = validarCampos(req);
                if (erroCampos != null){
                    voltaComErro(req, resp, erroCampos);
                    return;
                }
                Produto produtoNovo = montarProduto(req);
                int quantidade = lerInt(req.getParameter("quantidade"));

                ProdutoLista prodListaNovo = new ProdutoLista(compra, produtoNovo, quantidade);
                if(prodListaDAO.alterarProdutoListas(prodListaAntigo, prodListaNovo) == 1){
                    session.setAttribute("sucesso", "Item da lista de compras alterado com sucesso.");
                    resp.sendRedirect(req.getContextPath() + url_ver_compra);
                    break;
                }
                else {
                    voltaComErro(req, resp, "Não foi possível alterar esse produto dessa lista!!!");
                    break;
                }
            }

            case "deletarItem":{
                // pegando o id do item na compra do formulario
                Integer idProdLista = lerInt(req.getParameter("idProdutoLista"));
                if (idProdLista == null){
                    System.err.println("o id do produto na lista nunca pode ser nulo!!!");
                    voltaComErro(req, resp, "Erro ao deletar esse produto nessa compra. Por favor tente novamente.");
                    return;
                }

                ProdutoLista prodLista = prodListaDAO.buscarProdutoNumaLista(idProdLista);
                if (prodLista == null){
                    voltaComErro(req, resp, "Item da lista não encontrado.");
                    return;
                }
                if(prodListaDAO.deletarProdutoListas(prodLista) == 1){
                    session.setAttribute("sucesso", "Item da lista de compras apagado com sucesso.");
                    resp.sendRedirect(req.getContextPath() + url_ver_compra);
                    break;
                }
                else{
                    voltaComErro(req, resp, "Não foi possível excluir esse produto dessa lista!!!");
                    break;
                }
            }

            default:
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    // monta a lista com o dono certo ( usuario ou empresa ), devolve null se o dono nao existe
    private ListaCompras montarLista(String cpf, String cnpj, LocalDate dtLista){
        if (cpf != null && !cpf.isBlank()){
            Usuario usuario = usuarioDAO.buscarUsuarioPorCpf(cpf.trim());
            if (usuario == null){
                return null;
            }
            return new ListaCompras(usuario, dtLista);
        }
        Empresa empresa = empresaDAO.buscarEmpresaPorCnpj(cnpj.trim());
        if (empresa == null){
            return null;
        }
        return new ListaCompras(empresa, dtLista);
    }

    // confere os campos do produto e a quantidade, devolve a mensagem de erro ou null se esta tudo certo
    private String validarCampos(HttpServletRequest req){
        String nome = req.getParameter("nome");
        String categoriaTxt = req.getParameter("categoria");
        String marca = req.getParameter("marca");
        String unidadeMedida = req.getParameter("unidadeMedida");

        if (nome == null || nome.isBlank()){
            return "O nome não pode ser vazio!!!";
        }
        if (!validar.validarNomeProduto(nome) || nome.length() > 20){
            return "Nome inválido!!! No máximo 20 caracteres.";
        }
        Double preco = lerDouble(req.getParameter("preco"));
        if (preco == null){
            return "Preço inválido!!!";
        }
        if (!validar.validarPreco(preco)){
            return "Preço inválido!!! Deve ser maior zero.";
        }
        try {
            Categoria.valueOf(categoriaTxt.trim().toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e){
            return "A categoria não pode ser vazia!!!";
        }
        if (marca == null || marca.isBlank() || marca.length() > 20) {
            return "A marca não pode ser vazia e tem no máximo 20 caracteres!!!";
        }
        if (unidadeMedida == null || unidadeMedida.isBlank() || unidadeMedida.length() > 10) {
            return "A unidade de medida não pode ser vazia e tem no máximo 10 caracteres!!!";
        }
        Integer quantidade = lerInt(req.getParameter("quantidade"));
        if (quantidade == null){
            return "Quantidade inválida!!!";
        }
        if (!validar.validarQuantidade(quantidade)){
            return "Quantidade inválida!!! Deve ser maior que zero.";
        }
        return null;
    }

    // monta o produto com os campos do formulario ( já validados )
    private Produto montarProduto(HttpServletRequest req){
        String nome = req.getParameter("nome").trim();
        String descricao = req.getParameter("descricao");
        double preco = lerDouble(req.getParameter("preco"));
        Categoria categoria = Categoria.valueOf(req.getParameter("categoria").trim().toUpperCase());
        String marca = req.getParameter("marca").trim();
        String unidadeMedida = req.getParameter("unidadeMedida").trim();

        if (descricao == null || descricao.isBlank()){
            return new Produto(nome, preco, categoria, marca, unidadeMedida);
        }
        return new Produto(nome, descricao, preco, categoria, marca, unidadeMedida);
    }

    // carrega as listas ( ja filtradas ) e mostra a tela
    private void listar(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException{
        // filtros da tela
        String filtroItem = req.getParameter("item");
        String filtroDono = req.getParameter("dono");
        String filtroCategoria = req.getParameter("categoria");

        List<ListaCompras> todasCompras = listaDAO.buscarTodasListaCompras();
        List<ProdutoLista> todosItens = prodListaDAO.buscarTodosProdutoLista();

        // o item so traz o id da lista, o dono vem da lista de compras
        Map<Integer, ListaCompras> comprasPorId = new HashMap<>();
        for (ListaCompras compra : todasCompras){
            comprasPorId.put(compra.getIdLista(), compra);
        }

        List<ProdutoLista> itensLista = new ArrayList<>();
        for (ProdutoLista item : todosItens){
            ListaCompras compra = comprasPorId.get(item.getListaCompras().getIdLista());
            String dono = "";
            if (compra != null){
                dono = compra.getUsuario() != null
                        ? compra.getUsuario().getNome() + " " + compra.getUsuario().getSobrenome()
                        : compra.getEmpresa().getNome();
                // o item passa a mostrar o dono da lista
                item.setListaCompras(compra);
            }
            boolean achouItem = filtroItem == null || filtroItem.isBlank() || item.getProduto().getNome().toLowerCase().contains(filtroItem.trim().toLowerCase());
            boolean achouDono = filtroDono == null || filtroDono.isBlank() || dono.toLowerCase().contains(filtroDono.trim().toLowerCase());
            boolean achouCategoria = filtroCategoria == null || filtroCategoria.isBlank() || filtroCategoria.equals(item.getProduto().getCategoria().name());
            if (achouItem && achouDono && achouCategoria){
                itensLista.add(item);
            }
        }

        // ver os detalhes ou editar uma lista, ou editar um item ( vem no link da linha )
        String ver = req.getParameter("ver");
        String editar = req.getParameter("editar");
        String editarItem = req.getParameter("editarItem");
        String idEscolhido = (ver != null && !ver.isBlank()) ? ver.trim() : (editar != null && !editar.isBlank() ? editar.trim() : null);
        if (idEscolhido != null){
            for (ListaCompras compra : todasCompras){
                if (String.valueOf(compra.getIdLista()).equals(idEscolhido)){
                    req.setAttribute("registro", compra);
                    req.setAttribute("modo", ver != null && !ver.isBlank() ? "ver" : "editar");
                    List<ProdutoLista> itensDaLista = new ArrayList<>();
                    for (ProdutoLista item : todosItens){
                        if (item.getListaCompras().getIdLista() == compra.getIdLista()){
                            itensDaLista.add(item);
                        }
                    }
                    req.setAttribute("itensDaLista", itensDaLista);
                }
            }
        }
        else if (editarItem != null && !editarItem.isBlank()){
            for (ProdutoLista item : todosItens){
                if (String.valueOf(item.getIdProdutoLista()).equals(editarItem.trim())){
                    req.setAttribute("registroItem", item);
                    req.setAttribute("modo", "editarItem");
                }
            }
        }

        req.setAttribute("compras", todasCompras);
        // ordem da lista ( vem do select "Ordenar por" ), o dono de cada item é o dono da lista de compras
        Map<String, Comparator<ProdutoLista>> ordens = new LinkedHashMap<>();
        Ordenar.campo(ordens, "dono", (a, b) -> Ordenar.texto(donoDaLista(a.getListaCompras()), donoDaLista(b.getListaCompras())));
        Ordenar.campo(ordens, "nome", (a, b) -> Ordenar.texto(a.getProduto().getNome(), b.getProduto().getNome()));
        Ordenar.campo(ordens, "novo", Comparator.comparingInt(ProdutoLista::getIdProdutoLista));
        req.setAttribute("filtroOrd", Ordenar.ordenar(itensLista, req.getParameter("ord"), "dono:asc", ordens));

        req.setAttribute("itensLista", Paginacao.aplicar(req, itensLista));
        req.setAttribute("totalGeral", todosItens.size());
        req.setAttribute("usuarios", usuarioDAO.buscarTodosUsuario());
        req.setAttribute("empresas", empresaDAO.buscarTodasEmpresa());
        req.setAttribute("categorias", Categoria.values());
        req.setAttribute("filtroItem", filtroItem == null ? "" : filtroItem);
        req.setAttribute("filtroDono", filtroDono == null ? "" : filtroDono);
        req.setAttribute("filtroCategoria", filtroCategoria == null ? "" : filtroCategoria);
        if (req.getParameter("novo") != null && req.getAttribute("modo") == null){
            req.setAttribute("modo", "novo");
        }
        if (req.getParameter("novoItem") != null && req.getAttribute("modo") == null){
            req.setAttribute("modo", "novoItem");
        }
        req.getRequestDispatcher(req.getAttribute("modo") != null ? jsp_compras_form : jsp_compras).forward(req, resp);
    }

    // nome do dono da lista de compras ( usuario ou empresa )
    private static String donoDaLista(ListaCompras compra){
        if (compra.getUsuario() != null){
            return compra.getUsuario().getNome() + " " + compra.getUsuario().getSobrenome();
        }
        return compra.getEmpresa() != null ? compra.getEmpresa().getNome() : "";
    }

    // volta para a tela do formulario com uma mensagem de erro
    private void voltaComErro(HttpServletRequest req, HttpServletResponse resp, String mensagem)
    throws ServletException, IOException{
        req.getSession().setAttribute("erro", mensagem);
        resp.sendRedirect(Voltar.destino(req, url_ver_compra));
    }
    // proteção contra formato errado nos tipos diferentes de string
    private static Double lerDouble(String txt) {
        try {
            Double valor = Double.parseDouble(txt.trim().replace(",", "."));
            if (valor.isInfinite() || valor.isNaN()){
                return null;
            }
            return valor;
        }
        catch (Exception e) {
            return null;
        }
    }
    private static Integer lerInt(String txt) {
        try {
            return Integer.parseInt(txt.trim());
        }
        catch (Exception e) {
            return null;
        }
    }
    private static LocalDate lerData(String txt) {
        try {
            return LocalDate.parse(txt.trim());
        }
        catch (Exception e) {
            return null;
        }
    }
}
