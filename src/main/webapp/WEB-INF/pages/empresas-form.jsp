<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <title>Empresas - Frigus Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/painel.css">
    <script src="${pageContext.request.contextPath}/assets/js/admin.js"></script>
</head>
<body>
<c:set var="menuAtivo" value="empresas" scope="request"/>
<%@ include file="menu.jspf" %>
<main>
    <header>
        <h1>Empresas</h1>
        <p>Todas as contas empresariais cadastradas.</p>
    </header>

    <c:if test="${not empty erro}">
        <div class="erro"><c:out value="${erro}"/></div>
    </c:if>
    <c:if test="${not empty sucesso}">
        <div class="sucesso"><c:out value="${sucesso}"/></div>
    </c:if>

    <c:if test="${modo == 'ver' and not empty registro}">
        <section id="detalhes">
            <h2>Detalhes da empresa</h2>
            <dl>
                <dt>Nome</dt><dd><c:out value="${registro.nome}"/></dd>
                <dt>CNPJ</dt><dd><c:out value="${registro.cnpj}"/></dd>
                <dt>E-mail de contato</dt><dd><c:out value="${registro.email}"/></dd>
                <dt>Plano</dt><dd><c:out value="${planosDasEmpresas[registro.cnpj]}"/></dd>
                <dt>Frigoríficos</dt><dd>${empty frigorificosPorEmpresa[registro.cnpj] ? 0 : frigorificosPorEmpresa[registro.cnpj]}</dd>
                <dt>Receitas</dt><dd>${empty receitasPorEmpresa[registro.cnpj] ? 0 : receitasPorEmpresa[registro.cnpj]}</dd>
            </dl>
            <p>
                <a href="${pageContext.request.contextPath}/admin/empresas?editar=${registro.cnpj}">Editar</a>
                <a href="${pageContext.request.contextPath}/admin/empresas">Fechar</a>
            </p>
        </section>
    </c:if>

    <c:if test="${modo == 'editar' and not empty registro}">
        <section id="editar">
            <h2>Editar empresa</h2>
            <p>Dados de acesso da conta empresarial. A senha em branco mantém a atual.</p>
            <form method="post" action="${pageContext.request.contextPath}/admin/empresas">
                <input type="hidden" name="voltar" value="<c:out value='${pageContext.request.queryString}'/>">
                <input type="hidden" name="action" value="alterar">
                <input type="hidden" name="cnpj" value="${registro.cnpj}">
                <p>CNPJ: <strong><c:out value="${registro.cnpj}"/></strong></p>
                <label>Nome da empresa <input type="text" name="nome" value="${registro.nome}" required maxlength="80"></label>
                <label>E-mail de contato <input type="email" name="email" value="${registro.email}" required></label>
                <label>Nova senha (de 8 a 15 caracteres) <input type="password" name="senha" minlength="8" maxlength="15"></label>
                <p>Plano: <c:out value="${planosDasEmpresas[registro.cnpj]}"/> (o plano é trocado na tela Planos)</p>
                <button type="submit">Salvar alterações</button>
                <a href="${pageContext.request.contextPath}/admin/empresas">Cancelar</a>
            </form>
        </section>
    </c:if>

    <c:if test="${modo == 'novo'}">
    <section id="adicionar">
        <h2>Adicionar empresa</h2>
        <p>Dados de acesso e plano da conta empresarial.</p>
        <form method="post" action="${pageContext.request.contextPath}/admin/empresas">
            <input type="hidden" name="voltar" value="<c:out value='${pageContext.request.queryString}'/>">
            <input type="hidden" name="action" value="inserir">
            <label>Nome da empresa <input type="text" name="nome" required maxlength="80"></label>
            <label>CNPJ <input type="text" name="cnpj" required maxlength="18"></label>
            <label>Plano
                <select name="plano" required>
                    <option value="">Selecione</option>
                    <option value="COMERCIAL">Comercial</option>
                </select>
            </label>
            <label>E-mail de contato <input type="email" name="email" required></label>
            <label>Senha (de 8 a 15 caracteres, uma maiúscula e um especial) <input type="password" name="senha" required minlength="8" maxlength="15"></label>
            <button type="submit">Adicionar empresa</button>
        </form>
    </section>
    </c:if>

    <c:if test="${(empty modo or modo != 'novo') and (empty registro and empty registroItem)}">
        <p>Registro não encontrado.</p>
    </c:if>

    <p><a href="${pageContext.request.contextPath}/admin/empresas">Voltar para a listagem</a></p>
</main>
</body>
</html>
