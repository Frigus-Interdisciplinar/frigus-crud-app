<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <title>Receitas - Frigus Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/painel.css">
    <script src="${pageContext.request.contextPath}/assets/js/admin.js"></script>
</head>
<body>
<c:set var="menuAtivo" value="receitas" scope="request"/>
<%@ include file="menu.jspf" %>
<main>
    <header>
        <h1>Receitas</h1>
        <p>Receitas de todos os usuários e de todas as empresas.</p>
    </header>

    <c:if test="${not empty erro}">
        <div class="erro"><c:out value="${erro}"/></div>
    </c:if>
    <c:if test="${not empty sucesso}">
        <div class="sucesso"><c:out value="${sucesso}"/></div>
    </c:if>

    <c:if test="${modo == 'ver' and not empty registro}">
        <section id="detalhes">
            <h2>Detalhes da receita</h2>
            <dl>
                <dt>Nome</dt><dd><c:out value="${registro.nome}"/></dd>
                <dt>Descrição</dt><dd><c:out value="${registro.descricao}"/></dd>
                <dt>Dono</dt>
                <dd>
                    <c:choose>
                        <c:when test="${not empty registro.usuario}">Usuário: <c:out value="${registro.usuario.nome} ${registro.usuario.sobrenome} (${registro.usuario.cpf})"/></c:when>
                        <c:otherwise>Empresa: <c:out value="${registro.empresa.nome} (${registro.empresa.cnpj})"/></c:otherwise>
                    </c:choose>
                </dd>
                <dt>Criada em</dt><dd>${registro.dtReceita}</dd>
                <dt>Modo de preparo</dt><dd><c:out value="${registro.modoPreparo}"/></dd>
            </dl>

            <h3>Ingredientes</h3>
            <c:choose>
                <c:when test="${empty ingredientes}">
                    <p>Essa receita ainda não tem ingredientes.</p>
                </c:when>
                <c:otherwise>
                    <table>
                        <thead>
                            <tr><th>Produto</th><th>Quantidade</th><th>Unidade</th><th>Ações</th></tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${ingredientes}" var="i">
                                <tr>
                                    <td><c:out value="${i.produto.nome} ${i.produto.marca}"/></td>
                                    <td>
                                        <form method="post" action="${pageContext.request.contextPath}/admin/receitas">
                                            <input type="hidden" name="voltar" value="<c:out value='${pageContext.request.queryString}'/>">
                                            <input type="hidden" name="action" value="alterarIngrediente">
                                            <input type="hidden" name="idIngrediente" value="${i.idIngredientes}">
                                            <input type="hidden" name="idProduto" value="${i.produto.idProdutos}">
                                            <input type="number" min="1" name="quantidade" value="${i.quantidade}" required>
                                            <button type="submit">Salvar</button>
                                        </form>
                                    </td>
                                    <td><c:out value="${i.produto.unidadeMedida}"/></td>
                                    <td>
                                        <form method="post" action="${pageContext.request.contextPath}/admin/receitas" onsubmit="return confirmarExclusao()">
                                            <input type="hidden" name="voltar" value="<c:out value='${pageContext.request.queryString}'/>">
                                            <input type="hidden" name="action" value="deletarIngrediente">
                                            <input type="hidden" name="idIngrediente" value="${i.idIngredientes}">
                                            <button type="submit">Remover</button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:otherwise>
            </c:choose>

            <h3>Adicionar ingrediente</h3>
            <form method="post" action="${pageContext.request.contextPath}/admin/receitas">
                <input type="hidden" name="voltar" value="<c:out value='${pageContext.request.queryString}'/>">
                <input type="hidden" name="action" value="inserirIngrediente">
                <input type="hidden" name="idReceita" value="${registro.idReceita}">
                <label>Produto
                    <select name="idProduto" required>
                        <option value="">Selecione um produto</option>
                        <c:forEach items="${produtos}" var="p">
                            <option value="${p.idProdutos}"><c:out value="${p.nome} ${p.marca} (${p.unidadeMedida})"/></option>
                        </c:forEach>
                    </select>
                </label>
                <label>Quantidade <input type="number" min="1" name="quantidade" required></label>
                <button type="submit">Adicionar ingrediente</button>
            </form>

            <p>
                <a href="${pageContext.request.contextPath}/admin/receitas?editar=${registro.idReceita}">Editar a receita</a>
                <a href="${pageContext.request.contextPath}/admin/receitas">Fechar</a>
            </p>
        </section>
    </c:if>

    <c:if test="${modo == 'editar' and not empty registro}">
        <section id="editar">
            <h2>Editar receita</h2>
            <form method="post" action="${pageContext.request.contextPath}/admin/receitas">
                <input type="hidden" name="voltar" value="<c:out value='${pageContext.request.queryString}'/>">
                <input type="hidden" name="action" value="alterar">
                <input type="hidden" name="idReceita" value="${registro.idReceita}">

                <h3>Dono da receita</h3>
                <p>Escolha se o dono é um usuário ou uma empresa.</p>
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

                <h3>Informações da receita</h3>
                <label>Nome <input type="text" name="nomeReceita" value="${registro.nome}" required maxlength="50"></label>
                <label>Descrição <textarea name="descricaoReceita"><c:out value="${registro.descricao}"/></textarea></label>

                <h3>Modo de preparo</h3>
                <label>Passo a passo <textarea name="modoPreparo" rows="6" required><c:out value="${registro.modoPreparo}"/></textarea></label>

                <button type="submit">Salvar alterações</button>
                <a href="${pageContext.request.contextPath}/admin/receitas">Cancelar</a>
            </form>
        </section>
    </c:if>

    <c:if test="${modo == 'novo'}">
    <section id="adicionar">
        <h2>Nova receita</h2>
        <p>Defina o dono, os ingredientes e o modo de preparo.</p>
        <form method="post" action="${pageContext.request.contextPath}/admin/receitas">
            <input type="hidden" name="voltar" value="<c:out value='${pageContext.request.queryString}'/>">
            <input type="hidden" name="action" value="inserir">

            <h3>Dono da receita</h3>
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

            <h3>Informações da receita</h3>
            <label>Nome <input type="text" name="nomeReceita" required maxlength="50" placeholder="Ex.: Macarrão ao molho branco"></label>
            <label>Descrição <textarea name="descricaoReceita" placeholder="Uma frase sobre a receita"></textarea></label>

            <h3>Ingredientes</h3>
            <p>Escolha produtos que já existem. Linhas sem produto são ignoradas; mais ingredientes podem ser adicionados depois, nos detalhes da receita.</p>
            <table>
                <thead>
                    <tr><th>Produto</th><th>Qtd.</th></tr>
                </thead>
                <tbody>
                    <c:forEach begin="1" end="5" var="linha">
                        <tr>
                            <td>
                                <select name="idProduto">
                                    <option value="">Selecione um produto</option>
                                    <c:forEach items="${produtos}" var="p">
                                        <option value="${p.idProdutos}"><c:out value="${p.nome} ${p.marca} (${p.unidadeMedida})"/></option>
                                    </c:forEach>
                                </select>
                            </td>
                            <td><input type="number" min="1" name="quantidade" placeholder="1"></td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>

            <h3>Modo de preparo</h3>
            <label>Passo a passo <textarea name="modoPreparo" rows="6" required placeholder="Escreva um passo por linha."></textarea></label>

            <button type="submit">Salvar receita</button>
        </form>
    </section>
    </c:if>

    <c:if test="${(empty modo or modo != 'novo') and (empty registro and empty registroItem)}">
        <p>Registro não encontrado.</p>
    </c:if>

    <p><a href="${pageContext.request.contextPath}/admin/receitas">Voltar para a listagem</a></p>
</main>
</body>
</html>
