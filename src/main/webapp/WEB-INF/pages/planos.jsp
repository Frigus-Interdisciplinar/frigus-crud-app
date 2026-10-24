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
    </header>

    <c:if test="${not empty erro}">
        <div class="erro"><c:out value="${erro}"/></div>
    </c:if>
    <c:if test="${not empty sucesso}">
        <div class="sucesso"><c:out value="${sucesso}"/></div>
    </c:if>

    <%-- detalhes de uma assinatura ( link "Ver" da linha ) --%>
    <c:if test="${modo == 'ver' and not empty registro}">
        <section id="detalhes">
            <h2>Detalhes da assinatura</h2>
            <dl>
                <dt>Dono</dt>
                <dd>
                    <c:choose>
                        <c:when test="${not empty registro.usuario}">Usuário: <c:out value="${registro.usuario.nome} ${registro.usuario.sobrenome} (${registro.usuario.cpf})"/></c:when>
                        <c:otherwise>Empresa: <c:out value="${registro.empresa.nome} (${registro.empresa.cnpj})"/></c:otherwise>
                    </c:choose>
                </dd>
                <dt>Plano</dt><dd>${registro.tipoAssinatura}</dd>
                <dt>Valor mensal</dt><dd>R$ ${registro.valor}</dd>
                <dt>Descrição</dt><dd><c:out value="${registro.descricao}"/></dd>
            </dl>
            <p>
                <a href="${pageContext.request.contextPath}/admin/planos?editar=${registro.idAssinatura}">Editar</a>
                <a href="${pageContext.request.contextPath}/admin/planos">Fechar</a>
            </p>
        </section>
    </c:if>

    <%-- editar uma assinatura ( link "Editar" da linha ) --%>
    <c:if test="${modo == 'editar' and not empty registro}">
        <section id="editar">
            <h2>Editar assinatura</h2>
            <form method="post" action="${pageContext.request.contextPath}/admin/planos">
                <input type="hidden" name="action" value="alterar">
                <input type="hidden" name="idAssinatura" value="${registro.idAssinatura}">
                <h3>Dono da assinatura</h3>
                <p>Escolha se o dono é um usuário ou uma empresa, e selecione só o campo do tipo escolhido.</p>
                <label><input type="radio" name="donoTipo" value="usuario" ${not empty registro.usuario ? 'checked' : ''}> Usuário</label>
                <label><input type="radio" name="donoTipo" value="empresa" ${empty registro.usuario ? 'checked' : ''}> Empresa</label>
                <label>Usuário
                    <select name="cpf">
                        <option value="">Selecione</option>
                        <c:forEach items="${usuarios}" var="u">
                            <option value="${u.cpf}" ${registro.usuario.cpf == u.cpf ? 'selected' : ''}><c:out value="${u.nome} ${u.sobrenome} · ${u.email}"/></option>
                        </c:forEach>
                    </select>
                </label>
                <label>Empresa
                    <select name="cnpj">
                        <option value="">Selecione</option>
                        <c:forEach items="${empresas}" var="e">
                            <option value="${e.cnpj}" ${registro.empresa.cnpj == e.cnpj ? 'selected' : ''}><c:out value="${e.nome} · ${e.cnpj}"/></option>
                        </c:forEach>
                    </select>
                </label>
                <label>Plano
                    <select name="plano" required>
                        <c:forEach items="${planos}" var="p">
                            <option value="${p}" ${registro.tipoAssinatura == p ? 'selected' : ''}>${p}</option>
                        </c:forEach>
                    </select>
                </label>
                <button type="submit">Salvar alterações</button>
                <a href="${pageContext.request.contextPath}/admin/planos">Cancelar</a>
            </form>
        </section>
    </c:if>

    <%-- adicionar uma assinatura --%>
    <details class="adicionar" id="adicionar">
        <summary>Nova assinatura</summary>
        <p>Cada usuário e cada empresa só pode ter uma assinatura.</p>
        <form method="post" action="${pageContext.request.contextPath}/admin/planos">
            <input type="hidden" name="action" value="inserir">
            <h3>Dono da assinatura</h3>
            <p>Escolha se o dono é um usuário ou uma empresa, e selecione só o campo do tipo escolhido.</p>
            <label><input type="radio" name="donoTipo" value="usuario" checked> Usuário</label>
            <label><input type="radio" name="donoTipo" value="empresa"> Empresa</label>
            <label>Usuário
                <select name="cpf">
                    <option value="">Selecione</option>
                    <c:forEach items="${usuarios}" var="u">
                        <option value="${u.cpf}"><c:out value="${u.nome} ${u.sobrenome} · ${u.email}"/></option>
                    </c:forEach>
                </select>
            </label>
            <label>Empresa
                <select name="cnpj">
                    <option value="">Selecione</option>
                    <c:forEach items="${empresas}" var="e">
                        <option value="${e.cnpj}"><c:out value="${e.nome} · ${e.cnpj}"/></option>
                    </c:forEach>
                </select>
            </label>
            <label>Plano
                <select name="plano" required>
                    <option value="">Selecione</option>
                    <c:forEach items="${planos}" var="p">
                        <option value="${p}">${p}</option>
                    </c:forEach>
                </select>
            </label>
            <button type="submit">Salvar assinatura</button>
        </form>
    </details>

    <%-- filtros da lista --%>
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
            <button type="submit">Filtrar</button>
            <a href="${pageContext.request.contextPath}/admin/planos">Limpar filtros</a>
        </form>
    </section>

    <%-- lista --%>
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
