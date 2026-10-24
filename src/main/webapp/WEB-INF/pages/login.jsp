<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <title>Entrar - Frigus</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/estilo.css">
</head>
<body class="entrar">
<div class="cartao">
    <h1>Frigus</h1>
    <h2>Super Administrador</h2>
    <c:if test="${not empty erro}">
        <div class="erro"><c:out value="${erro}"/></div>
    </c:if>
    <c:if test="${not empty sucesso}">
        <div class="sucesso"><c:out value="${sucesso}"/></div>
    </c:if>
    <form method="post" action="${pageContext.request.contextPath}/login">
        <label for="email">E-mail</label>
        <input type="email" id="email" name="email" required>
        <label for="senha">Senha</label>
        <input type="password" id="senha" name="senha" required>
        <button type="submit">Entrar</button>
    </form>
    <p>Não tem conta? <a href="${pageContext.request.contextPath}/cadastro">Crie sua conta</a></p>
</div>
</body>
</html>
