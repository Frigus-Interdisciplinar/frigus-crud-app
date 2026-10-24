<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <title>Produtos - Frigus Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/painel.css">
    <script src="${pageContext.request.contextPath}/assets/js/admin.js"></script>
</head>
<body>
<c:set var="menuAtivo" value="produtos" scope="request"/>
<%@ include file="menu.jspf" %>
<main>
    <header>
        <h1>Produtos</h1>
        <p>Itens de todas as geladeiras e frigoríficos. Use o filtro Armazenamento para separar os dois.</p>
        <p>
        <a href="${pageContext.request.contextPath}/admin/produtos?novo">Adicionar produto</a>
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
        <form method="get" action="${pageContext.request.contextPath}/admin/produtos">
            <label>Produto <input type="text" name="produto" value="${filtroProduto}" placeholder="Nome do produto"></label>
            <label>Dono
                <select name="dono">
                    <option value="">Todos</option>
                    <option value="usuario" ${filtroDono == 'usuario' ? 'selected' : ''}>Usuário</option>
                    <option value="empresa" ${filtroDono == 'empresa' ? 'selected' : ''}>Empresa</option>
                </select>
            </label>
            <label>Armazenamento
                <select name="arm">
                    <option value="">Todos</option>
                    <option value="geladeira" ${filtroArm == 'geladeira' ? 'selected' : ''}>Geladeira</option>
                    <option value="frigorifico" ${filtroArm == 'frigorifico' ? 'selected' : ''}>Frigorífico</option>
                </select>
            </label>
            <label>Categoria
                <select name="cat">
                    <option value="">Todos</option>
                    <c:forEach items="${categorias}" var="cat">
                        <option value="${cat}" ${filtroCat == cat.name() ? 'selected' : ''}><c:out value="${cat}"/></option>
                    </c:forEach>
                </select>
            </label>
            <label>Status
                <select name="status">
                    <option value="">Todos</option>
                    <c:forEach items="${statusProduto}" var="s">
                        <option value="${s}" ${filtroStatus == s.name() ? 'selected' : ''}><c:out value="${s}"/></option>
                    </c:forEach>
                </select>
            </label>
            <label>Validade
                <select name="val">
                    <option value="">Todos</option>
                    <option value="vencidos" ${filtroVal == 'vencidos' ? 'selected' : ''}>Já vencidos</option>
                    <option value="7" ${filtroVal == '7' ? 'selected' : ''}>Próximos 7 dias</option>
                    <option value="30" ${filtroVal == '30' ? 'selected' : ''}>Próximos 30 dias</option>
                    <option value="longa" ${filtroVal == 'longa' ? 'selected' : ''}>Mais de 30 dias</option>
                </select>
            </label>
            <label>Ordenar por
                <select name="ord">
                    <option value="dias:asc" ${filtroOrd == 'dias:asc' ? 'selected' : ''}>Ordenar por validade mais próxima</option>
                    <option value="dias:desc" ${filtroOrd == 'dias:desc' ? 'selected' : ''}>Ordenar por validade mais distante</option>
                    <option value="dono:asc" ${filtroOrd == 'dono:asc' ? 'selected' : ''}>Ordenar por nome do dono de A a Z</option>
                    <option value="dono:desc" ${filtroOrd == 'dono:desc' ? 'selected' : ''}>Ordenar por nome do dono de Z a A</option>
                    <option value="nome:asc" ${filtroOrd == 'nome:asc' ? 'selected' : ''}>Ordenar por nome do produto de A a Z</option>
                    <option value="nome:desc" ${filtroOrd == 'nome:desc' ? 'selected' : ''}>Ordenar por nome do produto de Z a A</option>
                    <option value="novo:asc" ${filtroOrd == 'novo:asc' ? 'selected' : ''}>Ordenar da inserção mais antiga</option>
                    <option value="novo:desc" ${filtroOrd == 'novo:desc' ? 'selected' : ''}>Ordenar da inserção mais recente</option>
                </select>
            </label>
            <button type="submit">Filtrar</button>
            <a href="${pageContext.request.contextPath}/admin/produtos">Limpar filtros</a>
        </form>
    </section>

    <section id="lista">
        <p>${totalFiltrado} de ${totalGeral} produtos</p>
        <c:choose>
            <c:when test="${empty linhas}">
                <p>Nenhum registro encontrado com esses filtros.</p>
            </c:when>
            <c:otherwise>
                <table>
                    <thead>
                        <tr><th>Produto</th><th>Armazenamento</th><th>Dono</th><th>Qtd.</th><th>Validade</th><th>Status</th><th>Lote</th><th>Ações</th></tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${linhas}" var="l">
                            <tr>
                                <td><c:out value="${l.produto.nome} ${l.produto.marca} · ${l.produto.categoria}"/></td>
                                <td><c:out value="${l.tipoArmazenamento == 'geladeira' ? 'Geladeira' : 'Frigorífico'} ${l.armazenamento}"/></td>
                                <td><c:out value="${l.dono}"/> (${l.donoTipo == 'usuario' ? 'Usuário' : 'Empresa'})</td>
                                <td>${l.quantidade} <c:out value="${l.produto.unidadeMedida}"/></td>
                                <td>
                                    ${l.dtValidade}
                                    <c:choose>
                                        <c:when test="${l.dias < 0}">(venceu há ${-l.dias} dias)</c:when>
                                        <c:when test="${l.dias == 0}">(vence hoje)</c:when>
                                        <c:otherwise>(vence em ${l.dias} dias)</c:otherwise>
                                    </c:choose>
                                </td>
                                <td><c:out value="${l.status}"/></td>
                                <td>${l.lote}</td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/admin/produtos?ver=${l.chave}">Ver</a>
                                    <a href="${pageContext.request.contextPath}/admin/produtos?editar=${l.chave}">Editar</a>
                                    <form method="post" action="${pageContext.request.contextPath}/admin/produtos" onsubmit="return confirmarExclusao()">
                                        <input type="hidden" name="action" value="deletar">
                                        <input type="hidden" name="armazenamento" value="${l.tipoArmazenamento}">
                                        <c:choose>
                                            <c:when test="${l.tipoArmazenamento == 'geladeira'}">
                                                <input type="hidden" name="idProdutoGeladeira" value="${l.idItem}">
                                            </c:when>
                                            <c:otherwise>
                                                <input type="hidden" name="idProdutoFrigorifico" value="${l.idItem}">
                                            </c:otherwise>
                                        </c:choose>
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
