<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <title>Configurações - Frigus Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/painel.css">
    <script src="${pageContext.request.contextPath}/assets/js/admin.js"></script>
</head>
<body>
<c:set var="menuAtivo" value="configuracoes" scope="request"/>
<%@ include file="menu.jspf" %>
<main>
    <header>
        <h1>Configurações</h1>
        <p>Dados da sua conta de administrador.</p>
    </header>

    <c:if test="${not empty erro}">
        <div class="erro"><c:out value="${erro}"/></div>
    </c:if>
    <c:if test="${not empty sucesso}">
        <div class="sucesso"><c:out value="${sucesso}"/></div>
    </c:if>

    <%-- perfil de quem esta logado --%>
    <section id="perfil">
        <h2>Perfil</h2>
        <p><strong>Nome:</strong> <c:out value="${conta.nome}"/></p>
        <p><strong>E-mail:</strong> <c:out value="${conta.email}"/></p>
    </section>

    <%-- editar o perfil --%>
    <section id="editar-perfil">
        <h2>Editar perfil</h2>
        <form method="post" action="${pageContext.request.contextPath}/admin/conta">
            <input type="hidden" name="action" value="alterar">
            <label>Nome <input type="text" name="nome" value="${conta.nome}" required maxlength="20"></label>
            <label>E-mail <input type="text" inputmode="email" name="email" value="${conta.email}" required></label>
            <button type="submit">Salvar perfil</button>
        </form>
    </section>

    <%-- trocar a senha --%>
    <section id="alterar-senha">
        <h2>Alterar senha</h2>
        <p>De 8 a 15 caracteres, com uma letra maiúscula e um caractere especial.</p>
        <form method="post" action="${pageContext.request.contextPath}/admin/conta">
            <input type="hidden" name="action" value="alterarSenha">
            <label>Senha atual <input type="password" name="senhaAtual" required></label>
            <label>Nova senha <input type="password" name="senhaNova" required maxlength="15"></label>
            <label>Confirmar nova senha <input type="password" name="senhaConfirma" required maxlength="15"></label>
            <button type="submit">Alterar senha</button>
        </form>
    </section>

    <%-- sessão --%>
    <section id="sessao">
        <h2>Sessão</h2>
        <p>Você está logado como <strong><c:out value="${conta.email}"/></strong>.</p>
        <a href="${pageContext.request.contextPath}/logout">Sair</a>
    </section>

    <%-- apagar a conta --%>
    <section id="apagar-conta">
        <h2>Deseja deletar essa conta?</h2>
        <p>A conta de <strong><c:out value="${conta.email}"/></strong> será apagada e você sairá do painel. Isso não pode ser desfeito.</p>
        <form method="post" action="${pageContext.request.contextPath}/admin/conta" onsubmit="return confirmarExclusao()">
            <input type="hidden" name="action" value="deletar">
            <button type="submit">Deletar minha conta</button>
        </form>
    </section>
</main>
</body>
</html>
