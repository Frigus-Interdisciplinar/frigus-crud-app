<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <title>Criar conta - Frigus</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/estilo.css">
</head>
<body class="entrar">
<div class="cartao">
    <h1>Frigus</h1>
    <h2>Crie sua conta</h2>
    <c:if test="${not empty erro}">
        <div class="erro"><c:out value="${erro}"/></div>
    </c:if>
    <form method="post" action="${pageContext.request.contextPath}/cadastro">
        <label for="nome">Nome</label>
        <input type="text" id="nome" name="nome" required>
        <label for="email">E-mail</label>
        <input type="email" id="email" name="email" required>
        <label for="senha">Senha (de 8 a 15 caracteres, uma maiúscula e um especial)</label>
        <input type="password" id="senha" name="senha" required>
        <label for="confirmarSenha">Confirmar senha</label>
        <input type="password" id="confirmarSenha" name="confirmarSenha" required>
        <button type="submit">Criar conta</button>
    </form>
    <p>Já tem conta? <a href="${pageContext.request.contextPath}/login">Entrar</a></p>
</div>
</body>
</html>
