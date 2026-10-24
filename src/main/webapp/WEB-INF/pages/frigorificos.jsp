<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <title>Frigoríficos - Frigus Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/painel.css">
    <script src="${pageContext.request.contextPath}/assets/js/admin.js"></script>
</head>
<body>
<c:set var="menuAtivo" value="frigorificos" scope="request"/>
<%@ include file="menu.jspf" %>
<main>
    <header>
        <h1>Frigoríficos</h1>
        <p>Frigoríficos de todas as empresas, com endereço e validades.</p>
        <p>
        <a href="${pageContext.request.contextPath}/admin/frigorificos?novo">Adicionar frigorífico</a>
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
        <form method="get" action="${pageContext.request.contextPath}/admin/frigorificos">
            <label>Nome da unidade <input type="text" name="nome" value="${filtroNome}" placeholder="Ex.: Unidade Paulista"></label>
            <label>Cidade
                <select name="cidade">
                    <option value="">Todos</option>
                    <c:forEach items="${cidades}" var="cidade">
                        <option value="${cidade}" ${filtroCidade == cidade ? 'selected' : ''}><c:out value="${cidade}"/></option>
                    </c:forEach>
                </select>
            </label>
            <label>UF
                <select name="uf">
                    <option value="">Todos</option>
                    <c:forEach items="${ufs}" var="uf">
                        <option value="${uf}" ${filtroUf == uf ? 'selected' : ''}>${uf}</option>
                    </c:forEach>
                </select>
            </label>
            <label>Tipo
                <select name="tipo">
                    <option value="">Todos</option>
                    <c:forEach items="${tipos}" var="t">
                        <option value="${t}" ${filtroTipo == t ? 'selected' : ''}><c:out value="${t}"/></option>
                    </c:forEach>
                </select>
            </label>
            <label>Ordenar por
                <select name="ord">
                    <option value="dono:asc" ${filtroOrd == 'dono:asc' ? 'selected' : ''}>Ordenar por nome da empresa de A a Z</option>
                    <option value="dono:desc" ${filtroOrd == 'dono:desc' ? 'selected' : ''}>Ordenar por nome da empresa de Z a A</option>
                    <option value="nome:asc" ${filtroOrd == 'nome:asc' ? 'selected' : ''}>Ordenar por nome do frigorífico de A a Z</option>
                    <option value="nome:desc" ${filtroOrd == 'nome:desc' ? 'selected' : ''}>Ordenar por nome do frigorífico de Z a A</option>
                    <option value="novo:asc" ${filtroOrd == 'novo:asc' ? 'selected' : ''}>Ordenar da inserção mais antiga</option>
                    <option value="novo:desc" ${filtroOrd == 'novo:desc' ? 'selected' : ''}>Ordenar da inserção mais recente</option>
                </select>
            </label>
            <button type="submit">Filtrar</button>
            <a href="${pageContext.request.contextPath}/admin/frigorificos">Limpar filtros</a>
        </form>
    </section>

    <section id="lista">
        <p>${totalFiltrado} de ${totalGeral} frigoríficos</p>
        <c:choose>
            <c:when test="${empty frigorificos}">
                <p>Nenhum registro encontrado com esses filtros.</p>
            </c:when>
            <c:otherwise>
                <table>
                    <thead>
                        <tr><th>Frigorífico</th><th>Empresa</th><th>Endereço</th><th>CEP</th><th>Produtos</th><th>Itens</th><th>Alertas</th><th>Ações</th></tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${frigorificos}" var="f">
                            <c:set var="numeros" value="${ocupacao[f.idFrigorifico]}"/>
                            <tr>
                                <td><c:out value="${f.nome}"/> (<c:out value="${f.tipo}"/>)</td>
                                <td><c:out value="${f.empresa.nome}"/> (<c:out value="${f.empresa.cnpj}"/>)</td>
                                <td><c:out value="${f.endereco.rua}, ${f.endereco.numero} ${f.endereco.bairro} — ${f.endereco.cidade}/${f.endereco.uf}"/></td>
                                <td><c:out value="${f.endereco.cep}"/></td>
                                <td>${empty numeros ? 0 : numeros[0]}</td>
                                <td>${empty numeros ? 0 : numeros[1]} itens</td>
                                <td>${empty numeros ? 0 : numeros[2]} vencidos · ${empty numeros ? 0 : numeros[3]} a vencer</td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/admin/frigorificos?ver=${f.idFrigorifico}">Ver</a>
                                    <a href="${pageContext.request.contextPath}/admin/frigorificos?editar=${f.idFrigorifico}">Editar</a>
                                    <form method="post" action="${pageContext.request.contextPath}/admin/frigorificos" onsubmit="return confirmarExclusao()">
                                        <input type="hidden" name="action" value="deletar">
                                        <input type="hidden" name="idFrigorifico" value="${f.idFrigorifico}">
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
