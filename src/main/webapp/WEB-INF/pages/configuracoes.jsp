<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <title>Configurações - Frigus</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/estilo.css">
    <script src="${pageContext.request.contextPath}/assets/js/admin.js"></script>
</head>
<body>
<c:set var="menuAtivo" value="configuracoes" scope="request"/>
<%@ include file="menu.jspf" %>
<main>
    <h1>Configurações</h1>

    <c:if test="${not empty erro}">
        <div class="erro"><c:out value="${erro}"/></div>
    </c:if>
    <c:if test="${not empty sucesso}">
        <div class="sucesso"><c:out value="${sucesso}"/></div>
    </c:if>

    <div class="lado-a-lado">
        <%-- credenciais da conta --%>
        <div class="cartao">
            <h2>Minha conta</h2>
            <p><strong>Nome:</strong> <c:out value="${conta.nome}"/></p>
            <p><strong>E-mail:</strong> <c:out value="${conta.email}"/></p>
            <form class="linha" method="post" action="${pageContext.request.contextPath}/admin/conta">
                <input type="hidden" name="action" value="alterar">
                <div><label>Nome</label><input name="nome" value="${conta.nome}" required></div>
                <div><label>E-mail</label><input type="email" name="email" value="${conta.email}" required></div>
                <div><label>Nova senha (vazio mantém)</label><input type="password" name="senha"></div>
                <button type="submit">Salvar alterações</button>
            </form>
        </div>

        <%-- apagar a conta --%>
        <div class="cartao">
            <h2>Deseja deletar essa conta?</h2>
            <p>A conta de <strong><c:out value="${conta.email}"/></strong> será apagada e você sairá do painel. Isso não pode ser desfeito.</p>
            <form method="post" action="${pageContext.request.contextPath}/admin/conta" onsubmit="return confirmarExclusao()">
                <input type="hidden" name="action" value="deletar">
                <button class="excluir" type="submit">Deletar minha conta</button>
            </form>
        </div>
    </div>
</main>
</body>
</html>
