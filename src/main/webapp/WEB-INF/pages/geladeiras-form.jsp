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

    <c:if test="${modo == 'editar' and not empty registro}">
        <section id="editar">
            <h2>Editar geladeira</h2>
            <form method="post" action="${pageContext.request.contextPath}/admin/geladeiras">
                <input type="hidden" name="voltar" value="<c:out value='${pageContext.request.queryString}'/>">
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

    <c:if test="${modo == 'novo'}">
    <section id="adicionar">
        <h2>Adicionar geladeira</h2>
        <p>Informe o usuário dono e os dados da geladeira.</p>
        <form method="post" action="${pageContext.request.contextPath}/admin/geladeiras">
            <input type="hidden" name="voltar" value="<c:out value='${pageContext.request.queryString}'/>">
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
    </section>
    </c:if>

    <c:if test="${(empty modo or modo != 'novo') and (empty registro and empty registroItem)}">
        <p>Registro não encontrado.</p>
    </c:if>

    <p><a href="${pageContext.request.contextPath}/admin/geladeiras">Voltar para a listagem</a></p>
</main>
</body>
</html>
