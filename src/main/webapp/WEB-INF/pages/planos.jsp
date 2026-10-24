<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <title>Planos - Frigus Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/painel.css">
    <script src="${pageContext.request.contextPath}/assets/js/admin.js"></script>
</head>
<body>
<c:set var="menuAtivo" value="planos" scope="request"/>
<%@ include file="menu.jspf" %>
<main>
    <header>
        <h1>Planos</h1>
        <p>Assinaturas de usuários e de empresas. O valor e a descrição vêm do plano escolhido.</p>
        <p>
        <a href="${pageContext.request.contextPath}/admin/planos?novo">Adicionar assinatura</a>
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
        <form method="get" action="${pageContext.request.contextPath}/admin/planos">
            <label>Dono <input type="text" name="dono" value="${filtroDono}" placeholder="Nome, CPF ou CNPJ"></label>
            <label>Plano
                <select name="plano">
                    <option value="">Todos</option>
                    <c:forEach items="${planos}" var="p">
                        <option value="${p}" ${filtroPlano == p ? 'selected' : ''}>${p}</option>
                    </c:forEach>
                </select>
            </label>
            <label>Ordenar por
                <select name="ord">
                    <option value="dono:asc" ${filtroOrd == 'dono:asc' ? 'selected' : ''}>Ordenar de A a Z</option>
                    <option value="dono:desc" ${filtroOrd == 'dono:desc' ? 'selected' : ''}>Ordenar de Z a A</option>
                    <option value="novo:asc" ${filtroOrd == 'novo:asc' ? 'selected' : ''}>Ordenar da inserção mais antiga</option>
                    <option value="novo:desc" ${filtroOrd == 'novo:desc' ? 'selected' : ''}>Ordenar da inserção mais recente</option>
                </select>
            </label>
            <button type="submit">Filtrar</button>
            <a href="${pageContext.request.contextPath}/admin/planos">Limpar filtros</a>
        </form>
    </section>

    <section id="lista">
        <p>${totalFiltrado} de ${totalGeral} assinaturas</p>
        <c:choose>
            <c:when test="${empty assinaturas}">
                <p>Nenhum registro encontrado com esses filtros.</p>
            </c:when>
            <c:otherwise>
                <table>
                    <thead>
                        <tr><th>Usuário</th><th>Empresa</th><th>Plano</th><th>Valor mensal</th><th>Ações</th></tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${assinaturas}" var="a">
                            <tr>
                                <td><c:out value="${empty a.usuario ? 'NULL' : a.usuario.nome.concat(' ').concat(a.usuario.sobrenome)}"/></td>
                                <td><c:out value="${empty a.empresa ? 'NULL' : a.empresa.nome}"/></td>
                                <td>${a.tipoAssinatura}</td>
                                <td>R$ ${a.valor}</td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/admin/planos?ver=${a.idAssinatura}">Ver</a>
                                    <a href="${pageContext.request.contextPath}/admin/planos?editar=${a.idAssinatura}">Editar</a>
                                    <form method="post" action="${pageContext.request.contextPath}/admin/planos" onsubmit="return confirmarExclusao()">
                                        <input type="hidden" name="action" value="deletar">
                                        <input type="hidden" name="idAssinatura" value="${a.idAssinatura}">
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
