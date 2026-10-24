<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <title>Usuários - Frigus Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/painel.css">
    <script src="${pageContext.request.contextPath}/assets/js/admin.js"></script>
</head>
<body>
<c:set var="menuAtivo" value="usuarios" scope="request"/>
<%@ include file="menu.jspf" %>
<main>
    <header>
        <h1>Usuários</h1>
        <p>Todas as contas de pessoa física cadastradas.</p>
    </header>

    <c:if test="${not empty erro}">
        <div class="erro"><c:out value="${erro}"/></div>
    </c:if>
    <c:if test="${not empty sucesso}">
        <div class="sucesso"><c:out value="${sucesso}"/></div>
    </c:if>

    <%-- detalhes de um usuário ( link "Ver" da linha ) --%>
    <c:if test="${modo == 'ver' and not empty registro}">
        <section id="detalhes">
            <h2>Detalhes do usuário</h2>
            <dl>
                <dt>Nome</dt><dd><c:out value="${registro.nome} ${registro.sobrenome}"/></dd>
                <dt>CPF</dt><dd><c:out value="${registro.cpf}"/></dd>
                <dt>E-mail</dt><dd><c:out value="${registro.email}"/></dd>
                <dt>Plano</dt><dd><c:out value="${planosDosUsuarios[registro.cpf]}"/></dd>
                <dt>Geladeiras</dt><dd>${empty geladeirasPorUsuario[registro.cpf] ? 0 : geladeirasPorUsuario[registro.cpf]}</dd>
                <dt>Receitas</dt><dd>${empty receitasPorUsuario[registro.cpf] ? 0 : receitasPorUsuario[registro.cpf]}</dd>
            </dl>
            <p>
                <a href="${pageContext.request.contextPath}/admin/usuarios?editar=${registro.cpf}">Editar</a>
                <a href="${pageContext.request.contextPath}/admin/usuarios">Fechar</a>
            </p>
        </section>
    </c:if>

    <%-- editar um usuário ( link "Editar" da linha ) --%>
    <c:if test="${modo == 'editar' and not empty registro}">
        <section id="editar">
            <h2>Editar usuário</h2>
            <p>Dados de acesso e plano da conta. A senha em branco mantém a atual.</p>
            <form method="post" action="${pageContext.request.contextPath}/admin/usuarios">
                <input type="hidden" name="action" value="alterar">
                <input type="hidden" name="cpf" value="${registro.cpf}">
                <p>CPF: <strong><c:out value="${registro.cpf}"/></strong></p>
                <label>Nome <input type="text" name="nome" value="${registro.nome}" required maxlength="20"></label>
                <label>Sobrenome <input type="text" name="sobrenome" value="${registro.sobrenome}" required maxlength="60"></label>
                <label>E-mail <input type="email" name="email" value="${registro.email}" required></label>
                <label>Nova senha (de 8 a 15 caracteres) <input type="password" name="senha" minlength="8" maxlength="15"></label>
                <label>Plano
                    <select name="plano" required>
                        <c:forEach items="${planos}" var="p">
                            <option value="${p}" ${planosDosUsuarios[registro.cpf] == p.name() ? 'selected' : ''}><c:out value="${p}"/></option>
                        </c:forEach>
                    </select>
                </label>
                <button type="submit">Salvar alterações</button>
                <a href="${pageContext.request.contextPath}/admin/usuarios">Cancelar</a>
            </form>
        </section>
    </c:if>

    <%-- adicionar um usuário --%>
    <details class="adicionar" id="adicionar">
        <summary>Adicionar usuário</summary>
        <p>Dados de acesso e plano da conta.</p>
        <form method="post" action="${pageContext.request.contextPath}/admin/usuarios">
            <input type="hidden" name="action" value="inserir">
            <label>Nome <input type="text" name="nome" required maxlength="20"></label>
            <label>Sobrenome <input type="text" name="sobrenome" required maxlength="60"></label>
            <label>CPF <input type="text" name="cpf" required maxlength="14"></label>
            <label>E-mail <input type="email" name="email" required></label>
            <label>Senha (de 8 a 15 caracteres, uma maiúscula e um especial) <input type="password" name="senha" required minlength="8" maxlength="15"></label>
            <label>Plano
                <select name="plano" required>
                    <option value="">Selecione</option>
                    <c:forEach items="${planos}" var="p">
                        <option value="${p}"><c:out value="${p}"/></option>
                    </c:forEach>
                </select>
            </label>
            <button type="submit">Adicionar usuário</button>
        </form>
    </details>

    <%-- filtros da lista --%>
    <section id="filtros">
        <h2>Filtros</h2>
        <form method="get" action="${pageContext.request.contextPath}/admin/usuarios">
            <label>Buscar por nome, e-mail ou CPF <input type="text" name="busca" value="${filtroBusca}" placeholder="Buscar por nome…"></label>
            <label>Plano
                <select name="plano">
                    <option value="">Todos</option>
                    <c:forEach items="${planos}" var="p">
                        <option value="${p}" ${filtroPlano == p.name() ? 'selected' : ''}><c:out value="${p}"/></option>
                    </c:forEach>
                </select>
            </label>
            <button type="submit">Filtrar</button>
            <a href="${pageContext.request.contextPath}/admin/usuarios">Limpar filtros</a>
        </form>
    </section>

    <%-- lista --%>
    <section id="lista">
        <p>${totalFiltrado} de ${totalGeral} usuários</p>
        <c:choose>
            <c:when test="${empty usuarios}">
                <p>Nenhum registro encontrado com esses filtros.</p>
            </c:when>
            <c:otherwise>
                <table>
                    <thead>
                        <tr><th>Usuário</th><th>CPF</th><th>E-mail</th><th>Plano</th><th>Geladeiras</th><th>Receitas</th><th>Ações</th></tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${usuarios}" var="u">
                            <tr>
                                <td><c:out value="${u.nome} ${u.sobrenome}"/></td>
                                <td><c:out value="${u.cpf}"/></td>
                                <td><c:out value="${u.email}"/></td>
                                <td><c:out value="${planosDosUsuarios[u.cpf]}"/></td>
                                <td>${empty geladeirasPorUsuario[u.cpf] ? 0 : geladeirasPorUsuario[u.cpf]}</td>
                                <td>${empty receitasPorUsuario[u.cpf] ? 0 : receitasPorUsuario[u.cpf]}</td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/admin/usuarios?ver=${u.cpf}">Ver</a>
                                    <a href="${pageContext.request.contextPath}/admin/usuarios?editar=${u.cpf}">Editar</a>
                                    <form method="post" action="${pageContext.request.contextPath}/admin/usuarios" onsubmit="return confirmarExclusao()">
                                        <input type="hidden" name="action" value="deletar">
                                        <input type="hidden" name="cpf" value="${u.cpf}">
                                        <button type="submit">Excluir</button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:otherwise>
        </c:choose>
        <%@ include file="paginacao.jspf" %>
    </section>
</main>
</body>
</html>
