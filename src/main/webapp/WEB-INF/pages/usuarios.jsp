<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <title>Usuários - Frigus Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/painel.css">
    <script src="${pageContext.request.contextPath}/assets/js/admin.js"></script>
    <script src="${pageContext.request.contextPath}/assets/js/mascaras.js" defer></script>
</head>
<body>
<c:set var="menuAtivo" value="usuarios" scope="request"/>
<%@ include file="menu.jspf" %>
<main>
    <header>
        <h1>Usuários</h1>
        <p>Todas as contas de pessoa física cadastradas.</p>
        <p>
        <a href="${pageContext.request.contextPath}/admin/usuarios?novo">Adicionar usuário</a>
        </p>
    </header>

    <c:if test="${not empty erro}">
        <div class="erro"><c:out value="${erro}"/></div>
    </c:if>
    <c:if test="${not empty sucesso}">
        <div class="sucesso"><c:out value="${sucesso}"/></div>
    </c:if>

    <section id="filtros">
        <h2>Filtros</h2>
        <form method="get" action="${pageContext.request.contextPath}/admin/usuarios">
            <label>Buscar por nome, e-mail ou CPF <input type="text" name="busca" value="${filtroBusca}" placeholder="Buscar por nome…"></label>
            <label>CPF <input type="text" id="f-cpf" name="cpf" value="${filtroCpf}" maxlength="14" placeholder="000.000.000-00" inputmode="numeric"></label>
            <label>Plano
                <select name="plano">
                    <option value="">Todos</option>
                    <c:forEach items="${planos}" var="p">
                        <option value="${p}" ${filtroPlano == p.name() ? 'selected' : ''}><c:out value="${p}"/></option>
                    </c:forEach>
                </select>
            </label>
            <label>Ordenar por
                <select name="ord">
                    <option value="nome:asc" ${filtroOrd == 'nome:asc' ? 'selected' : ''}>Ordenar de A a Z</option>
                    <option value="nome:desc" ${filtroOrd == 'nome:desc' ? 'selected' : ''}>Ordenar de Z a A</option>
                    <option value="novo:asc" ${filtroOrd == 'novo:asc' ? 'selected' : ''}>Ordenar da inserção mais antiga</option>
                    <option value="novo:desc" ${filtroOrd == 'novo:desc' ? 'selected' : ''}>Ordenar da inserção mais recente</option>
                </select>
            </label>
            <button type="submit">Filtrar</button>
            <a href="${pageContext.request.contextPath}/admin/usuarios">Limpar filtros</a>
        </form>
    </section>

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
