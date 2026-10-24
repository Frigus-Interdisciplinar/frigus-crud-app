package com.example.servlet.Controller;

import com.example.servlet.Dao.AssinaturaDAO;
import com.example.servlet.Dao.ResumoDAO;
import com.example.servlet.Dao.UsuarioDAO;
import com.example.servlet.Model.Assinatura;
import com.example.servlet.Model.Usuario;
import com.example.servlet.Util.Enum.TipoAssinatura;
import com.example.servlet.Util.Hash;
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
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import com.example.servlet.Util.Ordenar;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = {
        "/admin/usuarios"},
        name = "usuarios")
public class UsuarioServlet extends HttpServlet {
    private UsuarioDAO usuarioDAO;
    private AssinaturaDAO assinaturaDAO;
    private ResumoDAO resumoDAO;
    private Validacao validar;

    // caminho das paginas
    private static final String jsp_usuarios = "/WEB-INF/pages/usuarios.jsp";
    private static final String jsp_usuarios_form = "/WEB-INF/pages/usuarios-form.jsp";

    // caminho para o servlet
    private static final String url_servlet = "/admin/usuarios";

    @Override
    public void init(){
        usuarioDAO = new UsuarioDAO();
        assinaturaDAO = new AssinaturaDAO();
        resumoDAO = new ResumoDAO();
        validar = new Validacao();
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException{
        // inserir, alterar e deletar só por POST
        if (req.getParameter("action") != null){
            resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }
        String path = req.getServletPath();

        switch (path){
            case "/admin/usuarios": {
                listar(req, resp);
                break;
            }
            default:
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
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
            case "inserir": {
                // pegando os dados do formulario
                String cpf = req.getParameter("cpf");
                String nome = req.getParameter("nome");
                String sobrenome = req.getParameter("sobrenome");
                String email = req.getParameter("email");
                String senha = req.getParameter("senha");
                String planoTxt = req.getParameter("plano");

                if(cpf == null || cpf.isBlank()){
                    voltaComErro(req, resp, "O cpf não pode ser vazio!!!");
                    return;
                }
                if(nome == null || nome.isBlank()){
                    voltaComErro(req, resp, "O nome não pode ser vazio!!!");
                    return;
                }
                if(sobrenome == null || sobrenome.isBlank()){
                    voltaComErro(req, resp, "O sobrenome não pode ser vazio!!!");
                    return;
                }
                if(email == null || email.isBlank()){
                    voltaComErro(req, resp, "O email não pode ser vazio!!!");
                    return;
                }
                if(senha == null || senha.isBlank()){
                    voltaComErro(req, resp, "A senha não pode ser vazia!!!");
                    return;
                }
                TipoAssinatura plano = lerPlano(planoTxt);
                if (plano == null){
                    voltaComErro(req, resp, "Escolha o plano do usuário.");
                    return;
                }

                // formatando o cpf
                String cpfPuro = validar.formatarCpf(cpf.trim());

                // validando
                boolean vCpf = validar.validarCpf(cpfPuro);
                boolean vNome = validar.validarNome(nome);
                boolean vSobrenome = validar.validarNome(sobrenome);
                boolean vEmail = validar.validarEmail(email);
                boolean vSenha = validar.validarSenha(senha);

                if (!vCpf){
                    voltaComErro(req, resp, "Cpf inválido!!!");
                    return;
                }
                if (!vNome || nome.length() > 20){
                    voltaComErro(req, resp, "Nome inválido!!! Só letras e no máximo 20 caracteres.");
                    return;
                }
                if (!vSobrenome || sobrenome.length() > 60){
                    voltaComErro(req, resp, "Sobrenome inválido!!! Só letras e no máximo 60 caracteres.");
                    return;
                }
                if (!vEmail){
                    voltaComErro(req, resp, "E-mail inválido!!!");
                    return;
                }
                if (!vSenha){
                    voltaComErro(req, resp, "Senha inválida!!! Deve ter de 8 a 15 caracteres, uma letra maiúscula e um caractere especial.");
                    return;
                }

                // hasheando a senha
                String senhaHash = Hash.bcrypt(senha);

                Usuario usuario = new Usuario(
                        cpfPuro,
                        nome.trim(),
                        sobrenome.trim(),
                        email.trim(),
                        senhaHash
                );

                if (usuarioDAO.adicionarUsuario(usuario) != 1){
                    voltaComErro(req, resp, "Erro ao cadastrar o usuário. Esse cpf ou e-mail já pode estar cadastrado.");
                    return;
                }

                // todo usuario nasce com um plano
                if (assinaturaDAO.inserirAssinatura(new Assinatura(usuario, plano)) != 1){
                    // se o plano nao foi salvo o usuario nao fica pela metade
                    usuarioDAO.deletarUsuario(usuario);
                    voltaComErro(req, resp, "Erro ao cadastrar o plano do usuário. Por favor tente novamente.");
                    return;
                }

                session.setAttribute("sucesso", "Usuário cadastrado com sucesso.");
                resp.sendRedirect(req.getContextPath() + url_servlet);
                return;
            }


            case "alterar": {
                // pegando as informações do formulario
                String cpf = req.getParameter("cpf");
                String nome = req.getParameter("nome");
                String sobrenome = req.getParameter("sobrenome");
                String email = req.getParameter("email");
                // a senha é opcional: vazia mantem a senha que ja esta no banco
                String senha = req.getParameter("senha");
                String planoTxt = req.getParameter("plano");

                if(cpf == null || cpf.isBlank()){
                    voltaComErro(req, resp, "O cpf não pode ser vazio!!!");
                    return;
                }
                if(nome == null || nome.isBlank()){
                    voltaComErro(req, resp, "O nome não pode ser vazio!!!");
                    return;
                }
                if(sobrenome == null || sobrenome.isBlank()){
                    voltaComErro(req, resp, "O sobrenome não pode ser vazio!!!");
                    return;
                }
                if(email == null || email.isBlank()){
                    voltaComErro(req, resp, "O email não pode ser vazio!!!");
                    return;
                }
                TipoAssinatura plano = lerPlano(planoTxt);
                if (plano == null){
                    voltaComErro(req, resp, "Escolha o plano do usuário.");
                    return;
                }

                // validando
                boolean vNome = validar.validarNome(nome);
                boolean vSobrenome = validar.validarNome(sobrenome);
                boolean vEmail = validar.validarEmail(email);

                if (!vNome || nome.length() > 20){
                    voltaComErro(req, resp, "Nome inválido!!! Só letras e no máximo 20 caracteres.");
                    return;
                }
                if (!vSobrenome || sobrenome.length() > 60){
                    voltaComErro(req, resp, "Sobrenome inválido!!! Só letras e no máximo 60 caracteres.");
                    return;
                }
                if (!vEmail){
                    voltaComErro(req, resp, "E-mail inválido!!!");
                    return;
                }

                // criando o usuario que esta no banco
                Usuario usuarioAntigo = usuarioDAO.buscarUsuarioPorCpf(validar.formatarCpf(cpf.trim()));
                if (usuarioAntigo == null){
                    voltaComErro(req, resp, "Usuário não encontrado.");
                    return;
                }

                String senhaHash = usuarioAntigo.getSenha();
                if (senha != null && !senha.isBlank()){
                    boolean vSenha = validar.validarSenha(senha);
                    if (!vSenha){
                        voltaComErro(req, resp, "Senha inválida!!! Deve ter de 8 a 15 caracteres, uma letra maiúscula e um caractere especial.");
                        return;
                    }
                    // hasheando a senha
                    senhaHash = Hash.bcrypt(senha);
                }

                // alterações
                Usuario usuarioNovo = new Usuario(
                        usuarioAntigo.getCpf(),
                        nome.trim(),
                        sobrenome.trim(),
                        email.trim(),
                        senhaHash
                );

                if (usuarioDAO.alterarUsuario(usuarioAntigo, usuarioNovo) != 1){
                    voltaComErro(req, resp, "Erro ao alterar o usuário. Esse e-mail já pode estar cadastrado.");
                    return;
                }

                // o plano muda junto: troca o que existe ou cria se o usuario ainda nao tinha
                Assinatura assinaturaAntiga = assinaturaDAO.buscarAssinaturaUsuario(usuarioAntigo.getCpf());
                int funcionouPlano = 1;
                if (assinaturaAntiga == null){
                    funcionouPlano = assinaturaDAO.inserirAssinatura(new Assinatura(usuarioAntigo, plano));
                }
                else if (assinaturaAntiga.getTipoAssinatura() != plano){
                    funcionouPlano = assinaturaDAO.alterarAssinatura(assinaturaAntiga, new Assinatura(usuarioAntigo, plano));
                }
                if (funcionouPlano != 1){
                    voltaComErro(req, resp, "Os dados foram salvos, mas não foi possível trocar o plano.");
                    return;
                }

                session.setAttribute("sucesso", "Usuário alterado com sucesso.");
                resp.sendRedirect(req.getContextPath() + url_servlet);
                return;
            }


            case "deletar": {
                // pegando o cpf do formulario
                String cpf = req.getParameter("cpf");
                if(cpf == null || cpf.isBlank()){
                    voltaComErro(req, resp, "Erro ao deletar o usuário. Por favor tente novamente.");
                    return;
                }

                // criando o usuario usando o cpf
                Usuario usuario = usuarioDAO.buscarUsuarioPorCpf(validar.formatarCpf(cpf.trim()));
                if (usuario == null){
                    voltaComErro(req, resp, "Usuário não encontrado.");
                    return;
                }

                if (usuarioDAO.deletarUsuario(usuario) == 1){
                    session.setAttribute("sucesso", "Usuário apagado com sucesso, junto com tudo que era dele.");
                    resp.sendRedirect(req.getContextPath() + url_servlet);
                    return;
                }
                else {
                    voltaComErro(req, resp, "Erro ao deletar o usuário. Por favor tente novamente.");
                    return;
                }
            }
            default:
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    // carrega a lista ( ja filtrada ), os totais de cada usuario e mostra a tela
    private void listar(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException{
        // filtros da tela
        String busca = req.getParameter("busca");
        String filtroPlano = req.getParameter("plano");

        List<Usuario> todos = usuarioDAO.buscarTodosUsuario();
        Map<String, String> planos = resumoDAO.buscarPlanosDosUsuarios();

        List<Usuario> usuarios = new ArrayList<>();
        for (Usuario usuario : todos){
            String texto = (usuario.getNome() + " " + usuario.getSobrenome() + " " + usuario.getEmail() + " " + usuario.getCpf()).toLowerCase();
            boolean achouBusca = busca == null || busca.isBlank() || texto.contains(busca.trim().toLowerCase());
            boolean achouPlano = filtroPlano == null || filtroPlano.isBlank() || filtroPlano.equals(planos.get(usuario.getCpf()));
            if (achouBusca && achouPlano){
                usuarios.add(usuario);
            }
        }

        // ver os detalhes ou editar um usuario ( vem no link da linha )
        String verCpf = req.getParameter("ver");
        String editarCpf = req.getParameter("editar");
        if (verCpf != null && !verCpf.isBlank()){
            req.setAttribute("registro", usuarioDAO.buscarUsuarioPorCpf(verCpf.trim()));
            req.setAttribute("modo", "ver");
        }
        else if (editarCpf != null && !editarCpf.isBlank()){
            req.setAttribute("registro", usuarioDAO.buscarUsuarioPorCpf(editarCpf.trim()));
            req.setAttribute("modo", "editar");
        }

        // ordem da lista ( vem do select "Ordenar por" )
        Map<String, Comparator<Usuario>> ordens = new LinkedHashMap<>();
        Ordenar.campo(ordens, "nome", (a, b) -> Ordenar.texto(a.getNome() + " " + a.getSobrenome(), b.getNome() + " " + b.getSobrenome()));
        Ordenar.campo(ordens, "novo", Comparator.comparing(Usuario::getDtCadastro, Comparator.nullsFirst(Comparator.naturalOrder())));
        req.setAttribute("filtroOrd", Ordenar.ordenar(usuarios, req.getParameter("ord"), "nome:asc", ordens));

        req.setAttribute("usuarios", Paginacao.aplicar(req, usuarios));
        req.setAttribute("totalGeral", todos.size());
        req.setAttribute("filtroBusca", busca == null ? "" : busca);
        req.setAttribute("filtroPlano", filtroPlano == null ? "" : filtroPlano);
        req.setAttribute("planosDosUsuarios", planos);
        req.setAttribute("geladeirasPorUsuario", resumoDAO.contarGeladeirasPorUsuario());
        req.setAttribute("receitasPorUsuario", resumoDAO.contarReceitasPorUsuario());
        // o usuario so escolhe planos de pessoa fisica ( o Comercial é da empresa )
        req.setAttribute("planos", new TipoAssinatura[]{TipoAssinatura.FREE, TipoAssinatura.PLUS, TipoAssinatura.FAMILIA});
        if (req.getParameter("novo") != null && req.getAttribute("modo") == null){
            req.setAttribute("modo", "novo");
        }
        req.getRequestDispatcher(req.getAttribute("modo") != null ? jsp_usuarios_form : jsp_usuarios).forward(req, resp);
    }

    // volta para a tela do formulario com uma mensagem de erro
    private void voltaComErro(HttpServletRequest req, HttpServletResponse resp, String mensagem)
            throws ServletException, IOException{
        req.getSession().setAttribute("erro", mensagem);
        resp.sendRedirect(Voltar.destino(req, url_servlet));
    }

    // transforma o texto do formulario no plano ( null se for invalido ou de empresa )
    private TipoAssinatura lerPlano(String planoTxt){
        try {
            TipoAssinatura plano = TipoAssinatura.valueOf(planoTxt.trim().toUpperCase());
            return plano == TipoAssinatura.COMERCIAL ? null : plano;
        } catch (IllegalArgumentException | NullPointerException e) {
            return null;
        }
    }
}
