<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <title>Geladeiras - Frigus Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/painel.css">
    <script src="${pageContext.request.contextPath}/assets/js/admin.js"></script>
</head>
<body>
<c:set var="menuAtivo" value="geladeiras" scope="request"/>
<%@ include file="menu.jspf" %>
<main>
    <header>
        <h1>Geladeiras</h1>
        <p>Geladeiras de todos os usuários, com ocupação e validades.</p>
        <p>
        <a href="${pageContext.request.contextPath}/admin/geladeiras?novo">Adicionar geladeira</a>
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
        <form method="get" action="${pageContext.request.contextPath}/admin/geladeiras">
            <label>Nome <input type="text" name="nome" value="${filtroNome}" placeholder="Ex.: Cozinha"></label>
            <label>Tipo
                <select name="tipo">
                    <option value="">Todos</option>
                    <c:forEach items="${tipos}" var="t">
                        <option value="${t}" ${filtroTipo == t ? 'selected' : ''}><c:out value="${t}"/></option>
                    </c:forEach>
                </select>
            </label>
            <label>Usuário <input type="text" name="usuario" value="${filtroUsuario}" placeholder="Buscar por usuário…"></label>
            <label>Ocupação
                <select name="status">
                    <option value="">Todos</option>
                    <c:forEach items="${statusArmazenamento}" var="s">
                        <option value="${s}" ${filtroStatus == s.name() ? 'selected' : ''}><c:out value="${s}"/></option>
                    </c:forEach>
                </select>
            </label>
            <label>Alertas
                <select name="alerta">
                    <option value="">Todos</option>
                    <option value="vencidos" ${filtroAlerta == 'vencidos' ? 'selected' : ''}>Com produtos vencidos</option>
                    <option value="avencer" ${filtroAlerta == 'avencer' ? 'selected' : ''}>Com produtos a vencer</option>
                    <option value="sem" ${filtroAlerta == 'sem' ? 'selected' : ''}>Sem alertas</option>
                </select>
            </label>
            <label>Ordenar por
                <select name="ord">
                    <option value="dono:asc" ${filtroOrd == 'dono:asc' ? 'selected' : ''}>Ordenar por nome do usuário de A a Z</option>
                    <option value="dono:desc" ${filtroOrd == 'dono:desc' ? 'selected' : ''}>Ordenar por nome do usuário de Z a A</option>
                    <option value="nome:asc" ${filtroOrd == 'nome:asc' ? 'selected' : ''}>Ordenar por nome da geladeira de A a Z</option>
                    <option value="nome:desc" ${filtroOrd == 'nome:desc' ? 'selected' : ''}>Ordenar por nome da geladeira de Z a A</option>
                    <option value="novo:asc" ${filtroOrd == 'novo:asc' ? 'selected' : ''}>Ordenar da inserção mais antiga</option>
                    <option value="novo:desc" ${filtroOrd == 'novo:desc' ? 'selected' : ''}>Ordenar da inserção mais recente</option>
                </select>
            </label>
            <button type="submit">Filtrar</button>
            <a href="${pageContext.request.contextPath}/admin/geladeiras">Limpar filtros</a>
        </form>
    </section>

    <section id="lista">
        <p>${totalFiltrado} de ${totalGeral} geladeiras</p>
        <c:choose>
            <c:when test="${empty geladeiras}">
                <p>Nenhum registro encontrado com esses filtros.</p>
            </c:when>
            <c:otherwise>
                <table>
                    <thead>
                        <tr><th>Geladeira</th><th>Usuário</th><th>Ocupação</th><th>Status</th><th>Produtos</th><th>Alertas</th><th>Ações</th></tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${geladeiras}" var="g">
                            <c:set var="numeros" value="${ocupacao[g.idGeladeira]}"/>
                            <tr>
                                <td><c:out value="${g.nome}"/> (<c:out value="${g.tipo}"/>)</td>
                                <td><c:out value="${g.usuario.nome} ${g.usuario.sobrenome}"/> (<c:out value="${g.usuario.email}"/>)</td>
                                <td>
                                    ${empty numeros ? 0 : numeros[1]} de
                                    <c:choose>
                                        <c:when test="${capacidade[g.idGeladeira] < 0}">itens (sem limite)</c:when>
                                        <c:otherwise>${capacidade[g.idGeladeira]} itens</c:otherwise>
                                    </c:choose>
                                </td>
                                <td><c:out value="${g.status}"/></td>
                                <td>${empty numeros ? 0 : numeros[0]}</td>
                                <td>${empty numeros ? 0 : numeros[2]} vencidos · ${empty numeros ? 0 : numeros[3]} a vencer</td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/admin/geladeiras?ver=${g.idGeladeira}">Ver</a>
                                    <a href="${pageContext.request.contextPath}/admin/geladeiras?editar=${g.idGeladeira}">Editar</a>
                                    <form method="post" action="${pageContext.request.contextPath}/admin/geladeiras" onsubmit="return confirmarExclusao()">
                                        <input type="hidden" name="action" value="deletar">
                                        <input type="hidden" name="idGeladeira" value="${g.idGeladeira}">
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
