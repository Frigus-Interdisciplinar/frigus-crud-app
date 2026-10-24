<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <title>Compras - Frigus Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/painel.css">
    <script src="${pageContext.request.contextPath}/assets/js/admin.js"></script>
</head>
<body>
<c:set var="menuAtivo" value="compras" scope="request"/>
<%@ include file="menu.jspf" %>
<main>
    <header>
        <h1>Compras</h1>
        <p>Itens das listas de compras de todos os usuários e de todas as empresas.</p>
        <p>
        <a href="${pageContext.request.contextPath}/admin/compras?novo">Nova lista de compras</a>
        <a href="${pageContext.request.contextPath}/admin/compras?novoItem">Adicionar item</a>
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
        <form method="get" action="${pageContext.request.contextPath}/admin/compras">
            <label>Item <input type="text" name="item" value="${filtroItem}" placeholder="Nome do item"></label>
            <label>Dono <input type="text" name="dono" value="${filtroDono}" placeholder="Usuário ou empresa"></label>
            <label>Categoria
                <select name="categoria">
                    <option value="">Todas</option>
                    <c:forEach items="${categorias}" var="cat">
                        <option value="${cat}" ${filtroCategoria == cat ? 'selected' : ''}>${cat}</option>
                    </c:forEach>
                </select>
            </label>
            <label>Ordenar por
                <select name="ord">
                    <option value="dono:asc" ${filtroOrd == 'dono:asc' ? 'selected' : ''}>Ordenar por nome do dono de A a Z</option>
                    <option value="dono:desc" ${filtroOrd == 'dono:desc' ? 'selected' : ''}>Ordenar por nome do dono de Z a A</option>
                    <option value="nome:asc" ${filtroOrd == 'nome:asc' ? 'selected' : ''}>Ordenar por nome do item de A a Z</option>
                    <option value="nome:desc" ${filtroOrd == 'nome:desc' ? 'selected' : ''}>Ordenar por nome do item de Z a A</option>
                    <option value="novo:asc" ${filtroOrd == 'novo:asc' ? 'selected' : ''}>Ordenar da inserção mais antiga</option>
                    <option value="novo:desc" ${filtroOrd == 'novo:desc' ? 'selected' : ''}>Ordenar da inserção mais recente</option>
                </select>
            </label>
            <button type="submit">Filtrar</button>
            <a href="${pageContext.request.contextPath}/admin/compras">Limpar filtros</a>
        </form>
    </section>

    <section id="lista">
        <p>${totalFiltrado} de ${totalGeral} itens</p>
        <c:choose>
            <c:when test="${empty itensLista}">
                <p>Nenhum registro encontrado com esses filtros.</p>
            </c:when>
            <c:otherwise>
                <table>
                    <thead>
                        <tr><th>Item</th><th>Dono</th><th>Marca</th><th>Categoria</th><th>Data da lista</th><th>Quantidade</th><th>Ações</th></tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${itensLista}" var="i">
                            <tr>
                                <td><c:out value="${i.produto.nome}"/></td>
                                <td>
                                    <c:choose>
                                        <c:when test="${not empty i.listaCompras.usuario}">Usuário: <c:out value="${i.listaCompras.usuario.nome} ${i.listaCompras.usuario.sobrenome}"/></c:when>
                                        <c:when test="${not empty i.listaCompras.empresa}">Empresa: <c:out value="${i.listaCompras.empresa.nome}"/></c:when>
                                        <c:otherwise>-</c:otherwise>
                                    </c:choose>
                                </td>
                                <td><c:out value="${i.produto.marca}"/></td>
                                <td>${i.produto.categoria}</td>
                                <td>${i.listaCompras.dtLista}</td>
                                <td>${i.quantidade} <c:out value="${i.produto.unidadeMedida}"/></td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/admin/compras?ver=${i.listaCompras.idLista}">Ver lista</a>
                                    <a href="${pageContext.request.contextPath}/admin/compras?editarItem=${i.idProdutoLista}">Editar</a>
                                    <form method="post" action="${pageContext.request.contextPath}/admin/compras" onsubmit="return confirmarExclusao()">
                                        <input type="hidden" name="action" value="deletarItem">
                                        <input type="hidden" name="idProdutoLista" value="${i.idProdutoLista}">
                                        <button type="submit">Remover</button>
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
