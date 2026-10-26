<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <title>Empresas - Frigus Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/painel.css">
    <script src="${pageContext.request.contextPath}/assets/js/admin.js"></script>
    <script src="${pageContext.request.contextPath}/assets/js/mascaras.js" defer></script>
</head>
<body>
<c:set var="menuAtivo" value="empresas" scope="request"/>
<%@ include file="menu.jspf" %>
<main>
    <header>
        <h1>Empresas</h1>
        <p>Todas as contas empresariais cadastradas.</p>
        <p>
        <a href="${pageContext.request.contextPath}/admin/empresas?novo">Adicionar empresa</a>
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
        <form method="get" action="${pageContext.request.contextPath}/admin/empresas">
            <label>Buscar por nome, e-mail ou CNPJ <input type="text" name="busca" value="${filtroBusca}" placeholder="Buscar por nome…"></label>
            <label>CNPJ <input type="text" id="f-cnpj" name="cnpj" value="${filtroCnpj}" maxlength="18" placeholder="00.000.000/0000-00" inputmode="numeric"></label>
            <label>Plano
                <select name="plano">
                    <option value="">Todos</option>
                    <option value="COMERCIAL" ${filtroPlano == 'COMERCIAL' ? 'selected' : ''}>Comercial</option>
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
            <a href="${pageContext.request.contextPath}/admin/empresas">Limpar filtros</a>
        </form>
    </section>

    <section id="lista">
        <p>${totalFiltrado} de ${totalGeral} empresas</p>
        <c:choose>
            <c:when test="${empty empresas}">
                <p>Nenhum registro encontrado com esses filtros.</p>
            </c:when>
            <c:otherwise>
                <table>
                    <thead>
                        <tr><th>Empresa</th><th>E-mail</th><th>Plano</th><th>Frigoríficos</th><th>Receitas</th><th>Ações</th></tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${empresas}" var="e">
                            <tr>
                                <td><c:out value="${e.nome}"/></td>
                                <td><c:out value="${e.email}"/></td>
                                <td><c:out value="${planosDasEmpresas[e.cnpj]}"/></td>
                                <td>${empty frigorificosPorEmpresa[e.cnpj] ? 0 : frigorificosPorEmpresa[e.cnpj]}</td>
                                <td>${empty receitasPorEmpresa[e.cnpj] ? 0 : receitasPorEmpresa[e.cnpj]}</td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/admin/empresas?ver=${e.cnpj}">Ver</a>
                                    <a href="${pageContext.request.contextPath}/admin/empresas?editar=${e.cnpj}">Editar</a>
                                    <form method="post" action="${pageContext.request.contextPath}/admin/empresas" onsubmit="return confirmarExclusao()">
                                        <input type="hidden" name="action" value="deletar">
                                        <input type="hidden" name="cnpj" value="${e.cnpj}">
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
