<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <title>Receitas - Frigus Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/painel.css">
    <script src="${pageContext.request.contextPath}/assets/js/admin.js"></script>
</head>
<body>
<c:set var="menuAtivo" value="receitas" scope="request"/>
<%@ include file="menu.jspf" %>
<main>
    <header>
        <h1>Receitas</h1>
        <p>Receitas de todos os usuários e de todas as empresas.</p>
        <p>
        <a href="${pageContext.request.contextPath}/admin/receitas?novo">Adicionar receita</a>
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
        <form method="get" action="${pageContext.request.contextPath}/admin/receitas">
            <label>Receita <input type="text" name="receita" value="${filtroReceita}" placeholder="Nome da receita"></label>
            <label>Dono
                <select name="dono">
                    <option value="">Todos</option>
                    <option value="usuario" ${filtroDono == 'usuario' ? 'selected' : ''}>Usuário</option>
                    <option value="empresa" ${filtroDono == 'empresa' ? 'selected' : ''}>Empresa</option>
                </select>
            </label>
            <label>Ordenar por
                <select name="ord">
                    <option value="dono:asc" ${filtroOrd == 'dono:asc' ? 'selected' : ''}>Ordenar por nome do dono de A a Z</option>
                    <option value="dono:desc" ${filtroOrd == 'dono:desc' ? 'selected' : ''}>Ordenar por nome do dono de Z a A</option>
                    <option value="nome:asc" ${filtroOrd == 'nome:asc' ? 'selected' : ''}>Ordenar por nome da receita de A a Z</option>
                    <option value="nome:desc" ${filtroOrd == 'nome:desc' ? 'selected' : ''}>Ordenar por nome da receita de Z a A</option>
                    <option value="novo:asc" ${filtroOrd == 'novo:asc' ? 'selected' : ''}>Ordenar da inserção mais antiga</option>
                    <option value="novo:desc" ${filtroOrd == 'novo:desc' ? 'selected' : ''}>Ordenar da inserção mais recente</option>
                </select>
            </label>
            <button type="submit">Filtrar</button>
            <a href="${pageContext.request.contextPath}/admin/receitas">Limpar filtros</a>
        </form>
    </section>

    <section id="lista">
        <p>${totalFiltrado} de ${totalGeral} receitas</p>
        <c:choose>
            <c:when test="${empty receitas}">
                <p>Nenhum registro encontrado com esses filtros.</p>
            </c:when>
            <c:otherwise>
                <table>
                    <thead>
                        <tr><th>Receita</th><th>Usuário</th><th>Empresa</th><th>Ingredientes</th><th>Criada em</th><th>Ações</th></tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${receitas}" var="r">
                            <tr>
                                <td><c:out value="${r.nome}"/><br><c:out value="${r.descricao}"/></td>
                                <td><c:out value="${empty r.usuario ? 'NULL' : r.usuario.nome.concat(' ').concat(r.usuario.sobrenome)}"/></td>
                                <td><c:out value="${empty r.empresa ? 'NULL' : r.empresa.nome}"/></td>
                                <td>${empty ingredientesPorReceita[r.idReceita] ? 0 : ingredientesPorReceita[r.idReceita]}</td>
                                <td>${r.dtReceita}</td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/admin/receitas?ver=${r.idReceita}">Ver</a>
                                    <a href="${pageContext.request.contextPath}/admin/receitas?editar=${r.idReceita}">Editar</a>
                                    <form method="post" action="${pageContext.request.contextPath}/admin/receitas" onsubmit="return confirmarExclusao()">
                                        <input type="hidden" name="action" value="deletar">
                                        <input type="hidden" name="idReceita" value="${r.idReceita}">
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
