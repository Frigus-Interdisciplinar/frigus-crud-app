<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <title>Usuários - Frigus Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/painel.css">
    <script src="${pageContext.request.contextPath}/assets/js/admin.js"></script>
</head>
<body>
<c:set var="menuAtivo" value="usuarios" scope="request"/>
<%@ include file="menu.jspf" %>
<main>
    <header>
        <h1>Usuários</h1>
        <p>Todas as contas de pessoa física cadastradas.</p>
    </header>

    <c:if test="${not empty erro}">
        <div class="erro"><c:out value="${erro}"/></div>
    </c:if>
    <c:if test="${not empty sucesso}">
        <div class="sucesso"><c:out value="${sucesso}"/></div>
    </c:if>

    <c:if test="${modo == 'ver' and not empty registro}">
        <section id="detalhes">
            <h2>Detalhes do usuário</h2>
            <dl>
                <dt>Nome</dt><dd><c:out value="${registro.nome} ${registro.sobrenome}"/></dd>
                <dt>CPF</dt><dd><c:out value="${registro.cpf}"/></dd>
                <dt>E-mail</dt><dd><c:out value="${registro.email}"/></dd>
                <dt>Plano</dt><dd><c:out value="${planosDosUsuarios[registro.cpf]}"/></dd>
                <dt>Geladeiras</dt><dd>${empty geladeirasPorUsuario[registro.cpf] ? 0 : geladeirasPorUsuario[registro.cpf]}</dd>
                <dt>Receitas</dt><dd>${empty receitasPorUsuario[registro.cpf] ? 0 : receitasPorUsuario[registro.cpf]}</dd>
            </dl>
            <p>
                <a href="${pageContext.request.contextPath}/admin/usuarios?editar=${registro.cpf}">Editar</a>
                <a href="${pageContext.request.contextPath}/admin/usuarios">Fechar</a>
            </p>
        </section>
    </c:if>

    <c:if test="${modo == 'editar' and not empty registro}">
        <section id="editar">
            <h2>Editar usuário</h2>
            <p>Dados de acesso e plano da conta. A senha em branco mantém a atual.</p>
            <form method="post" action="${pageContext.request.contextPath}/admin/usuarios">
                <input type="hidden" name="voltar" value="<c:out value='${pageContext.request.queryString}'/>">
                <input type="hidden" name="action" value="alterar">
                <input type="hidden" name="cpf" value="${registro.cpf}">
                <p>CPF: <strong><c:out value="${registro.cpf}"/></strong></p>
                <label>Nome <input type="text" name="nome" value="${registro.nome}" required maxlength="20"></label>
                <label>Sobrenome <input type="text" name="sobrenome" value="${registro.sobrenome}" required maxlength="60"></label>
                <label>E-mail <input type="text" inputmode="email" name="email" value="${registro.email}" required></label>
                <label>Nova senha (de 8 a 15 caracteres) <input type="password" name="senha" minlength="8" maxlength="15"></label>
                <label>Plano
                    <select name="plano" required>
                        <c:forEach items="${planos}" var="p">
                            <option value="${p}" ${planosDosUsuarios[registro.cpf] == p.name() ? 'selected' : ''}><c:out value="${p}"/></option>
                        </c:forEach>
                    </select>
                </label>
                <button type="submit">Salvar alterações</button>
                <a href="${pageContext.request.contextPath}/admin/usuarios">Cancelar</a>
            </form>
        </section>
    </c:if>

    <c:if test="${modo == 'novo'}">
    <section id="adicionar">
        <h2>Adicionar usuário</h2>
        <p>Dados de acesso e plano da conta.</p>
        <form method="post" action="${pageContext.request.contextPath}/admin/usuarios">
            <input type="hidden" name="voltar" value="<c:out value='${pageContext.request.queryString}'/>">
            <input type="hidden" name="action" value="inserir">
            <label>Nome <input type="text" name="nome" required maxlength="20"></label>
            <label>Sobrenome <input type="text" name="sobrenome" required maxlength="60"></label>
            <label>CPF <input type="text" name="cpf" required maxlength="14"></label>
            <label>E-mail <input type="text" inputmode="email" name="email" required></label>
            <label>Senha (de 8 a 15 caracteres, uma maiúscula e um especial) <input type="password" name="senha" required minlength="8" maxlength="15"></label>
            <label>Plano
                <select name="plano" required>
                    <option value="">Selecione</option>
                    <c:forEach items="${planos}" var="p">
                        <option value="${p}"><c:out value="${p}"/></option>
                    </c:forEach>
                </select>
            </label>
            <button type="submit">Adicionar usuário</button>
        </form>
    </section>
    </c:if>

    <c:if test="${(empty modo or modo != 'novo') and (empty registro and empty registroItem)}">
        <p>Registro não encontrado.</p>
    </c:if>

    <p><a href="${pageContext.request.contextPath}/admin/usuarios">Voltar para a listagem</a></p>
</main>
</body>
</html>
