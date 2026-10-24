<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <title>Compras - Frigus Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/painel.css">
    <script src="${pageContext.request.contextPath}/assets/js/admin.js"></script>
</head>
<body>
<c:set var="menuAtivo" value="compras" scope="request"/>
<%@ include file="menu.jspf" %>
<main>
    <header>
        <h1>Compras</h1>
        <p>Itens das listas de compras de todos os usuários e de todas as empresas.</p>
    </header>

    <c:if test="${not empty erro}">
        <div class="erro"><c:out value="${erro}"/></div>
    </c:if>
    <c:if test="${not empty sucesso}">
        <div class="sucesso"><c:out value="${sucesso}"/></div>
    </c:if>

    <%-- detalhes de uma lista, com os itens ( link "Ver lista" da linha ) --%>
    <c:if test="${modo == 'ver' and not empty registro}">
        <section id="detalhes">
            <h2>Lista de compras #${registro.idLista}</h2>
            <dl>
                <dt>Dono</dt>
                <dd>
                    <c:choose>
                        <c:when test="${not empty registro.usuario}">Usuário: <c:out value="${registro.usuario.nome} ${registro.usuario.sobrenome}"/></c:when>
                        <c:otherwise>Empresa: <c:out value="${registro.empresa.nome}"/></c:otherwise>
                    </c:choose>
                </dd>
                <dt>Data da lista</dt><dd>${registro.dtLista}</dd>
                <dt>Itens</dt><dd>${itensDaLista.size()}</dd>
            </dl>
            <c:choose>
                <c:when test="${empty itensDaLista}">
                    <p>Essa lista ainda não tem itens.</p>
                </c:when>
                <c:otherwise>
                    <table>
                        <thead>
                            <tr><th>Item</th><th>Marca</th><th>Categoria</th><th>Quantidade</th><th>Ações</th></tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${itensDaLista}" var="i">
                                <tr>
                                    <td><c:out value="${i.produto.nome}"/></td>
                                    <td><c:out value="${i.produto.marca}"/></td>
                                    <td>${i.produto.categoria}</td>
                                    <td>${i.quantidade} <c:out value="${i.produto.unidadeMedida}"/></td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/admin/compras?editarItem=${i.idProdutoLista}">Editar</a>
                                        <form method="post" action="${pageContext.request.contextPath}/admin/compras" onsubmit="return confirmarExclusao()">
                                            <input type="hidden" name="action" value="deletarItem">
                                            <input type="hidden" name="idProdutoLista" value="${i.idProdutoLista}">
                                            <button type="submit">Remover</button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:otherwise>
            </c:choose>
            <p>
                <a href="${pageContext.request.contextPath}/admin/compras?editar=${registro.idLista}">Editar a lista</a>
                <a href="${pageContext.request.contextPath}/admin/compras">Fechar</a>
            </p>
        </section>
    </c:if>

    <%-- editar uma lista ( dono e data ) --%>
    <c:if test="${modo == 'editar' and not empty registro}">
        <section id="editar">
            <h2>Editar lista de compras</h2>
            <form method="post" action="${pageContext.request.contextPath}/admin/compras">
                <input type="hidden" name="action" value="alterar">
                <input type="hidden" name="idCompra" value="${registro.idLista}">
                <h3>Dono da lista</h3>
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
                <label>Data da lista <input type="date" name="dtLista" value="${registro.dtLista}" required></label>
                <button type="submit">Salvar alterações</button>
                <a href="${pageContext.request.contextPath}/admin/compras">Cancelar</a>
            </form>

            <h3>Excluir a lista</h3>
            <form method="post" action="${pageContext.request.contextPath}/admin/compras" onsubmit="return confirmarExclusao()">
                <input type="hidden" name="action" value="deletar">
                <input type="hidden" name="idCompra" value="${registro.idLista}">
                <button type="submit">Excluir lista e itens</button>
            </form>
        </section>
    </c:if>

    <%-- editar um item --%>
    <c:if test="${modo == 'editarItem' and not empty registroItem}">
        <section id="editar-item">
            <h2>Editar item da lista</h2>
            <form method="post" action="${pageContext.request.contextPath}/admin/compras">
                <input type="hidden" name="action" value="alterarItem">
                <input type="hidden" name="idProdutoLista" value="${registroItem.idProdutoLista}">
                <label>Lista de compras
                    <select name="idCompra" required>
                        <c:forEach items="${compras}" var="l">
                            <option value="${l.idLista}" ${registroItem.listaCompras.idLista == l.idLista ? 'selected' : ''}>
                                #${l.idLista} · <c:out value="${empty l.usuario ? l.empresa.nome : l.usuario.nome}"/> · ${l.dtLista}
                            </option>
                        </c:forEach>
                    </select>
                </label>
                <label>Produto <input type="text" name="nome" value="${registroItem.produto.nome}" required maxlength="20"></label>
                <label>Descrição <input type="text" name="descricao" value="${registroItem.produto.descricao}"></label>
                <label>Preço <input type="text" name="preco" value="${registroItem.produto.preco}" required></label>
                <label>Categoria
                    <select name="categoria" required>
                        <c:forEach items="${categorias}" var="cat">
                            <option value="${cat}" ${registroItem.produto.categoria == cat ? 'selected' : ''}>${cat}</option>
                        </c:forEach>
                    </select>
                </label>
                <label>Marca <input type="text" name="marca" value="${registroItem.produto.marca}" required maxlength="20"></label>
                <label>Unidade <input type="text" name="unidadeMedida" value="${registroItem.produto.unidadeMedida}" required maxlength="10"></label>
                <label>Quantidade <input type="number" min="1" name="quantidade" value="${registroItem.quantidade}" required></label>
                <button type="submit">Salvar item</button>
                <a href="${pageContext.request.contextPath}/admin/compras">Cancelar</a>
            </form>
        </section>
    </c:if>

    <%-- adicionar uma lista --%>
    <details class="adicionar" id="adicionar-lista">
        <summary>Nova lista de compras</summary>
        <form method="post" action="${pageContext.request.contextPath}/admin/compras">
            <input type="hidden" name="action" value="inserir">
            <h3>Dono da lista</h3>
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
            <label>Data da lista <input type="date" name="dtLista"></label>
            <p>Sem data, vale a data de hoje.</p>
            <button type="submit">Criar lista</button>
        </form>
    </details>

    <%-- adicionar um item numa lista --%>
    <details class="adicionar" id="adicionar-item">
        <summary>Adicionar item</summary>
        <form method="post" action="${pageContext.request.contextPath}/admin/compras">
            <input type="hidden" name="action" value="inserirItem">
            <label>Lista de compras
                <select name="idCompra" required>
                    <option value="">Selecione</option>
                    <c:forEach items="${compras}" var="l">
                        <option value="${l.idLista}">
                            #${l.idLista} · <c:out value="${empty l.usuario ? l.empresa.nome : l.usuario.nome}"/> · ${l.dtLista}
                        </option>
                    </c:forEach>
                </select>
            </label>
            <label>Produto <input type="text" name="nome" required maxlength="20" placeholder="Ex.: Leite"></label>
            <label>Descrição <input type="text" name="descricao"></label>
            <label>Preço <input type="text" name="preco" required placeholder="0,00"></label>
            <label>Categoria
                <select name="categoria" required>
                    <option value="">Selecione</option>
                    <c:forEach items="${categorias}" var="cat">
                        <option value="${cat}">${cat}</option>
                    </c:forEach>
                </select>
            </label>
            <label>Marca <input type="text" name="marca" required maxlength="20"></label>
            <label>Unidade <input type="text" name="unidadeMedida" required maxlength="10" placeholder="Ex.: L, kg, un"></label>
            <label>Quantidade <input type="number" min="1" name="quantidade" required></label>
            <button type="submit">Adicionar item</button>
        </form>
    </details>

    <%-- filtros da lista --%>
    <section id="filtros">
        <h2>Filtros</h2>
        <form method="get" action="${pageContext.request.contextPath}/admin/compras">
            <label>Item <input type="text" name="item" value="${filtroItem}" placeholder="Nome do item"></label>
            <label>Dono <input type="text" name="dono" value="${filtroDono}" placeholder="Usuário ou empresa"></label>
            <label>Categoria
                <select name="categoria">
                    <option value="">Todas</option>
                    <c:forEach items="${categorias}" var="cat">
                        <option value="${cat}" ${filtroCategoria == cat ? 'selected' : ''}>${cat}</option>
                    </c:forEach>
                </select>
            </label>
            <button type="submit">Filtrar</button>
            <a href="${pageContext.request.contextPath}/admin/compras">Limpar filtros</a>
        </form>
    </section>

    <%-- lista de itens --%>
    <section id="lista">
        <p>${totalFiltrado} de ${totalGeral} itens</p>
        <c:choose>
            <c:when test="${empty itensLista}">
                <p>Nenhum registro encontrado com esses filtros.</p>
            </c:when>
            <c:otherwise>
                <table>
                    <thead>
                        <tr><th>Item</th><th>Dono</th><th>Marca</th><th>Categoria</th><th>Data da lista</th><th>Quantidade</th><th>Ações</th></tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${itensLista}" var="i">
                            <tr>
                                <td><c:out value="${i.produto.nome}"/></td>
                                <td>
                                    <c:choose>
                                        <c:when test="${not empty i.listaCompras.usuario}">Usuário: <c:out value="${i.listaCompras.usuario.nome} ${i.listaCompras.usuario.sobrenome}"/></c:when>
                                        <c:when test="${not empty i.listaCompras.empresa}">Empresa: <c:out value="${i.listaCompras.empresa.nome}"/></c:when>
                                        <c:otherwise>-</c:otherwise>
                                    </c:choose>
                                </td>
                                <td><c:out value="${i.produto.marca}"/></td>
                                <td>${i.produto.categoria}</td>
                                <td>${i.listaCompras.dtLista}</td>
                                <td>${i.quantidade} <c:out value="${i.produto.unidadeMedida}"/></td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/admin/compras?ver=${i.listaCompras.idLista}">Ver lista</a>
                                    <a href="${pageContext.request.contextPath}/admin/compras?editarItem=${i.idProdutoLista}">Editar</a>
                                    <form method="post" action="${pageContext.request.contextPath}/admin/compras" onsubmit="return confirmarExclusao()">
                                        <input type="hidden" name="action" value="deletarItem">
                                        <input type="hidden" name="idProdutoLista" value="${i.idProdutoLista}">
                                        <button type="submit">Remover</button>
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
