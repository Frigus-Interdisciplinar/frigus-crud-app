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
    </header>

    <c:if test="${not empty erro}">
        <div class="erro"><c:out value="${erro}"/></div>
    </c:if>
    <c:if test="${not empty sucesso}">
        <div class="sucesso"><c:out value="${sucesso}"/></div>
    </c:if>

    <c:if test="${modo == 'ver' and not empty registro}">
        <section id="detalhes">
            <h2>Detalhes do frigorífico</h2>
            <dl>
                <dt>Unidade</dt><dd><c:out value="${registro.nome}"/></dd>
                <dt>Tipo</dt><dd><c:out value="${registro.tipo}"/></dd>
                <dt>Empresa dona</dt><dd><c:out value="${registro.empresa.nome} (${registro.empresa.cnpj})"/></dd>
                <dt>Status</dt><dd><c:out value="${registro.status}"/></dd>
                <dt>Endereço</dt><dd><c:out value="${registro.endereco.rua}, ${registro.endereco.numero} - ${registro.endereco.bairro}"/></dd>
                <dt>Cidade</dt><dd><c:out value="${registro.endereco.cidade}/${registro.endereco.uf} - ${registro.endereco.pais}"/></dd>
                <dt>CEP</dt><dd><c:out value="${registro.endereco.cep}"/></dd>
                <dt>Produtos</dt><dd>${empty ocupacao[registro.idFrigorifico] ? 0 : ocupacao[registro.idFrigorifico][0]}</dd>
                <dt>Itens guardados</dt><dd>${empty ocupacao[registro.idFrigorifico] ? 0 : ocupacao[registro.idFrigorifico][1]}</dd>
                <dt>Vencidos</dt><dd>${empty ocupacao[registro.idFrigorifico] ? 0 : ocupacao[registro.idFrigorifico][2]}</dd>
                <dt>A vencer em 7 dias</dt><dd>${empty ocupacao[registro.idFrigorifico] ? 0 : ocupacao[registro.idFrigorifico][3]}</dd>
            </dl>
            <p>
                <a href="${pageContext.request.contextPath}/admin/produtos?armazenamento=frigorifico&idFrigorifico=${registro.idFrigorifico}">Ver os produtos desse frigorífico</a>
                <a href="${pageContext.request.contextPath}/admin/frigorificos?editar=${registro.idFrigorifico}">Editar</a>
                <a href="${pageContext.request.contextPath}/admin/frigorificos">Fechar</a>
            </p>
        </section>
    </c:if>

    <c:if test="${modo == 'editar' and not empty registro}">
        <section id="editar">
            <h2>Editar frigorífico</h2>
            <form method="post" action="${pageContext.request.contextPath}/admin/frigorificos">
                <input type="hidden" name="voltar" value="<c:out value='${pageContext.request.queryString}'/>">
                <input type="hidden" name="action" value="alterar">
                <input type="hidden" name="idFrigorifico" value="${registro.idFrigorifico}">
                <h3>Informações do frigorífico</h3>
                <label>Empresa dona
                    <select name="cnpj" required>
                        <c:forEach items="${empresas}" var="e">
                            <option value="${e.cnpj}" ${registro.empresa.cnpj == e.cnpj ? 'selected' : ''}><c:out value="${e.nome} · ${e.cnpj}"/></option>
                        </c:forEach>
                    </select>
                </label>
                <label>Tipo <input type="text" name="tipo" value="${registro.tipo}" required maxlength="20"></label>
                <h3>Endereço da unidade</h3>
                <label>Nome da unidade <input type="text" name="nomeUnidade" value="${registro.nome}" required maxlength="50"></label>
                <label>CEP <input type="text" name="cep" value="${registro.endereco.cep}" required maxlength="9"></label>
                <label>País <input type="text" name="pais" value="${registro.endereco.pais}"></label>
                <label>Rua <input type="text" name="rua" value="${registro.endereco.rua}" required></label>
                <label>Número <input type="text" name="numero" value="${registro.endereco.numero}" required maxlength="20"></label>
                <label>Bairro <input type="text" name="bairro" value="${registro.endereco.bairro}" required></label>
                <label>Cidade <input type="text" name="cidade" value="${registro.endereco.cidade}" required></label>
                <label>UF
                    <select name="uf" required>
                        <c:forEach items="${ufs}" var="uf">
                            <option value="${uf}" ${registro.endereco.uf == uf ? 'selected' : ''}>${uf}</option>
                        </c:forEach>
                    </select>
                </label>
                <button type="submit">Salvar alterações</button>
                <a href="${pageContext.request.contextPath}/admin/frigorificos">Cancelar</a>
            </form>
        </section>
    </c:if>

    <c:if test="${modo == 'novo'}">
    <section id="adicionar">
        <h2>Adicionar frigorífico</h2>
        <p>Informe a empresa, os dados da unidade e o endereço.</p>
        <form method="post" action="${pageContext.request.contextPath}/admin/frigorificos">
            <input type="hidden" name="voltar" value="<c:out value='${pageContext.request.queryString}'/>">
            <input type="hidden" name="action" value="inserir">
            <h3>Informações do frigorífico</h3>
            <label>Empresa dona
                <select name="cnpj" required>
                    <option value="">Selecione</option>
                    <c:forEach items="${empresas}" var="e">
                        <option value="${e.cnpj}"><c:out value="${e.nome} · ${e.cnpj}"/></option>
                    </c:forEach>
                </select>
            </label>
            <label>Tipo <input type="text" name="tipo" required maxlength="20" placeholder="Ex.: Câmara refrigerada"></label>
            <h3>Endereço da unidade</h3>
            <label>Nome da unidade <input type="text" name="nomeUnidade" required maxlength="50" placeholder="Ex.: Unidade Paulista"></label>
            <label>CEP <input type="text" name="cep" required maxlength="9"></label>
            <label>País <input type="text" name="pais" placeholder="Brasil"></label>
            <label>Rua <input type="text" name="rua" required></label>
            <label>Número <input type="text" name="numero" required maxlength="20"></label>
            <label>Bairro <input type="text" name="bairro" required></label>
            <label>Cidade <input type="text" name="cidade" required></label>
            <label>UF
                <select name="uf" required>
                    <option value="">Selecione</option>
                    <c:forEach items="${ufs}" var="uf">
                        <option value="${uf}">${uf}</option>
                    </c:forEach>
                </select>
            </label>
            <button type="submit">Adicionar frigorífico</button>
        </form>
    </section>
    </c:if>

    <c:if test="${(empty modo or modo != 'novo') and (empty registro and empty registroItem)}">
        <p>Registro não encontrado.</p>
    </c:if>

    <p><a href="${pageContext.request.contextPath}/admin/frigorificos">Voltar para a listagem</a></p>
</main>
</body>
</html>
