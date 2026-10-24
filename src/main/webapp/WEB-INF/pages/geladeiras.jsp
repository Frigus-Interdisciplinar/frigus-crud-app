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
    </header>

    <c:if test="${not empty erro}">
        <div class="erro"><c:out value="${erro}"/></div>
    </c:if>
    <c:if test="${not empty sucesso}">
        <div class="sucesso"><c:out value="${sucesso}"/></div>
    </c:if>

    <%-- detalhes de uma geladeira ( link "Ver" da linha ) --%>
    <c:if test="${modo == 'ver' and not empty registro}">
        <section id="detalhes">
            <h2>Detalhes da geladeira</h2>
            <dl>
                <dt>Nome</dt><dd><c:out value="${registro.nome}"/></dd>
                <dt>Tipo</dt><dd><c:out value="${registro.tipo}"/></dd>
                <dt>Usuário dono</dt><dd><c:out value="${registro.usuario.nome} ${registro.usuario.sobrenome} (${registro.usuario.email})"/></dd>
                <dt>Status</dt><dd><c:out value="${registro.status}"/></dd>
                <dt>Produtos</dt><dd>${empty ocupacao[registro.idGeladeira] ? 0 : ocupacao[registro.idGeladeira][0]}</dd>
                <dt>Itens guardados</dt><dd>${empty ocupacao[registro.idGeladeira] ? 0 : ocupacao[registro.idGeladeira][1]}</dd>
                <dt>Vencidos</dt><dd>${empty ocupacao[registro.idGeladeira] ? 0 : ocupacao[registro.idGeladeira][2]}</dd>
                <dt>A vencer em 7 dias</dt><dd>${empty ocupacao[registro.idGeladeira] ? 0 : ocupacao[registro.idGeladeira][3]}</dd>
            </dl>
            <p>
                <a href="${pageContext.request.contextPath}/admin/produtos?armazenamento=geladeira&idGeladeira=${registro.idGeladeira}">Ver os produtos dessa geladeira</a>
                <a href="${pageContext.request.contextPath}/admin/geladeiras?editar=${registro.idGeladeira}">Editar</a>
                <a href="${pageContext.request.contextPath}/admin/geladeiras">Fechar</a>
            </p>
        </section>
    </c:if>

    <%-- editar uma geladeira ( link "Editar" da linha ) --%>
    <c:if test="${modo == 'editar' and not empty registro}">
        <section id="editar">
            <h2>Editar geladeira</h2>
            <form method="post" action="${pageContext.request.contextPath}/admin/geladeiras">
                <input type="hidden" name="action" value="alterar">
                <input type="hidden" name="idGeladeira" value="${registro.idGeladeira}">
                <label>Usuário dono
                    <select name="cpf" required>
                        <c:forEach items="${usuarios}" var="u">
                            <option value="${u.cpf}" ${registro.usuario.cpf == u.cpf ? 'selected' : ''}><c:out value="${u.nome} ${u.sobrenome} · ${u.email}"/></option>
                        </c:forEach>
                    </select>
                </label>
                <label>Nome <input type="text" name="nome" value="${registro.nome}" required maxlength="50"></label>
                <label>Tipo <input type="text" name="tipo" value="${registro.tipo}" required maxlength="20"></label>
                <button type="submit">Salvar alterações</button>
                <a href="${pageContext.request.contextPath}/admin/geladeiras">Cancelar</a>
            </form>
        </section>
    </c:if>

    <%-- adicionar uma geladeira --%>
    <details class="adicionar" id="adicionar">
        <summary>Adicionar geladeira</summary>
        <p>Informe o usuário dono e os dados da geladeira.</p>
        <form method="post" action="${pageContext.request.contextPath}/admin/geladeiras">
            <input type="hidden" name="action" value="inserir">
            <label>Usuário dono
                <select name="cpf" required>
                    <option value="">Selecione</option>
                    <c:forEach items="${usuarios}" var="u">
                        <option value="${u.cpf}"><c:out value="${u.nome} ${u.sobrenome} · ${u.email}"/></option>
                    </c:forEach>
                </select>
            </label>
            <label>Nome <input type="text" name="nome" required maxlength="50" placeholder="Ex.: Cozinha"></label>
            <label>Tipo <input type="text" name="tipo" required maxlength="20" placeholder="Ex.: Duplex frost free"></label>
            <button type="submit">Adicionar geladeira</button>
        </form>
    </details>

    <%-- filtros da lista --%>
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
            <button type="submit">Filtrar</button>
            <a href="${pageContext.request.contextPath}/admin/geladeiras">Limpar filtros</a>
        </form>
    </section>

    <%-- lista --%>
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
