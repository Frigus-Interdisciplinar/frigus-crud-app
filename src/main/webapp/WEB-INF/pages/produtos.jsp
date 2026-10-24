<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <title>Produtos - Frigus Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/painel.css">
    <script src="${pageContext.request.contextPath}/assets/js/admin.js"></script>
</head>
<body>
<c:set var="menuAtivo" value="produtos" scope="request"/>
<%@ include file="menu.jspf" %>
<main>
    <header>
        <h1>Produtos</h1>
        <p>Itens de todas as geladeiras e frigoríficos. Use o filtro Armazenamento para separar os dois.</p>
    </header>

    <c:if test="${not empty erro}">
        <div class="erro"><c:out value="${erro}"/></div>
    </c:if>
    <c:if test="${not empty sucesso}">
        <div class="sucesso"><c:out value="${sucesso}"/></div>
    </c:if>

    <%-- detalhes de um item ( link "Ver" da linha ) --%>
    <c:if test="${modo == 'ver' and not empty registro}">
        <section id="detalhes">
            <h2>Detalhes do produto</h2>
            <dl>
                <dt>Nome</dt><dd><c:out value="${registro.produto.nome}"/></dd>
                <dt>Marca</dt><dd><c:out value="${registro.produto.marca}"/></dd>
                <dt>Categoria</dt><dd><c:out value="${registro.produto.categoria}"/></dd>
                <dt>Descrição</dt><dd><c:out value="${registro.produto.descricao}"/></dd>
                <dt>Preço (R$)</dt><dd><c:out value="${registro.produto.preco}"/></dd>
                <dt>Unidade de medida</dt><dd><c:out value="${registro.produto.unidadeMedida}"/></dd>
                <dt>Armazenamento</dt><dd><c:out value="${registro.tipoArmazenamento == 'geladeira' ? 'Geladeira' : 'Frigorífico'} ${registro.armazenamento}"/></dd>
                <dt>Dono</dt><dd><c:out value="${registro.dono}"/> (${registro.donoTipo == 'usuario' ? 'Usuário' : 'Empresa'})</dd>
                <dt>Quantidade</dt><dd>${registro.quantidade} <c:out value="${registro.produto.unidadeMedida}"/></dd>
                <dt>Validade</dt><dd>${registro.dtValidade}</dd>
                <dt>Status</dt><dd><c:out value="${registro.status}"/></dd>
                <dt>Lote</dt><dd>${registro.lote}</dd>
            </dl>
            <p>
                <a href="${pageContext.request.contextPath}/admin/produtos?editar=${registro.chave}">Editar</a>
                <a href="${pageContext.request.contextPath}/admin/produtos">Fechar</a>
            </p>
        </section>
    </c:if>

    <%-- editar um item ( link "Editar" da linha ) --%>
    <c:if test="${modo == 'editar' and not empty registro}">
        <section id="editar">
            <h2>Editar produto</h2>
            <form method="post" action="${pageContext.request.contextPath}/admin/produtos">
                <input type="hidden" name="action" value="alterar">
                <input type="hidden" name="armazenamento" value="${registro.tipoArmazenamento}">
                <c:choose>
                    <c:when test="${registro.tipoArmazenamento == 'geladeira'}">
                        <input type="hidden" name="idProdutoGeladeira" value="${registro.idItem}">
                    </c:when>
                    <c:otherwise>
                        <input type="hidden" name="idProdutoFrigorifico" value="${registro.idItem}">
                    </c:otherwise>
                </c:choose>

                <h3>Informações do produto</h3>
                <label>Nome <input type="text" name="nomeProduto" value="${registro.produto.nome}" required maxlength="20"></label>
                <label>Categoria
                    <select name="categoria" required>
                        <c:forEach items="${categorias}" var="cat">
                            <option value="${cat}" ${registro.produto.categoria == cat ? 'selected' : ''}><c:out value="${cat}"/></option>
                        </c:forEach>
                    </select>
                </label>
                <label>Marca <input type="text" name="marca" value="${registro.produto.marca}" required maxlength="20"></label>
                <label>Descrição <textarea name="descricao"><c:out value="${registro.produto.descricao}"/></textarea></label>
                <label>Unidade de medida
                    <select name="unidadeMedida" required>
                        <c:forEach items="${unidades}" var="un">
                            <option value="${un}" ${registro.produto.unidadeMedida == un ? 'selected' : ''}>${un}</option>
                        </c:forEach>
                    </select>
                </label>
                <label>Preço (R$) <input type="text" name="preco" value="${registro.produto.preco}" required></label>

                <h3>Onde está armazenado</h3>
                <c:choose>
                    <c:when test="${registro.tipoArmazenamento == 'geladeira'}">
                        <label>Geladeira
                            <select name="idGeladeira" required>
                                <c:forEach items="${geladeiras}" var="g">
                                    <option value="${g.idGeladeira}" ${registro.idArmazenamento == g.idGeladeira ? 'selected' : ''}><c:out value="${g.nome} · ${g.usuario.nome} ${g.usuario.sobrenome}"/></option>
                                </c:forEach>
                            </select>
                        </label>
                    </c:when>
                    <c:otherwise>
                        <label>Frigorífico
                            <select name="idFrigorifico" required>
                                <c:forEach items="${frigorificos}" var="f">
                                    <option value="${f.idFrigorifico}" ${registro.idArmazenamento == f.idFrigorifico ? 'selected' : ''}><c:out value="${f.nome} · ${f.empresa.nome}"/></option>
                                </c:forEach>
                            </select>
                        </label>
                    </c:otherwise>
                </c:choose>

                <h3>Item em estoque</h3>
                <label>Quantidade <input type="number" min="1" name="quantidade" value="${registro.quantidade}" required></label>
                <label>Data de validade <input type="date" name="dtValidade" value="${registro.dtValidade}" required></label>
                <label>Lote <input type="number" min="0" name="lote" value="${registro.lote}"></label>

                <button type="submit">Salvar alterações</button>
                <a href="${pageContext.request.contextPath}/admin/produtos">Cancelar</a>
            </form>
        </section>
    </c:if>

    <%-- adicionar um produto --%>
    <details class="adicionar" id="adicionar">
        <summary>Adicionar produto</summary>
        <p>Dados do produto e do item em estoque.</p>
        <form method="post" action="${pageContext.request.contextPath}/admin/produtos">
            <input type="hidden" name="action" value="inserir">

            <h3>Informações do produto</h3>
            <label>Nome <input type="text" name="nomeProduto" required maxlength="20"></label>
            <label>Categoria
                <select name="categoria" required>
                    <option value="">Selecione</option>
                    <c:forEach items="${categorias}" var="cat">
                        <option value="${cat}"><c:out value="${cat}"/></option>
                    </c:forEach>
                </select>
            </label>
            <label>Marca <input type="text" name="marca" required maxlength="20"></label>
            <label>Descrição <textarea name="descricao"></textarea></label>
            <label>Unidade de medida
                <select name="unidadeMedida" required>
                    <option value="">Selecione</option>
                    <c:forEach items="${unidades}" var="un">
                        <option value="${un}">${un}</option>
                    </c:forEach>
                </select>
            </label>
            <label>Preço (R$) <input type="text" name="preco" required></label>

            <h3>Onde está armazenado</h3>
            <p>Escolha se o item fica numa geladeira ou num frigorífico, e selecione só o campo do tipo escolhido.</p>
            <label><input type="radio" name="armazenamento" value="geladeira" checked> Geladeira</label>
            <label><input type="radio" name="armazenamento" value="frigorifico"> Frigorífico</label>
            <label>Geladeira
                <select name="idGeladeira">
                    <option value="">Selecione</option>
                    <c:forEach items="${geladeiras}" var="g">
                        <option value="${g.idGeladeira}"><c:out value="${g.nome} · ${g.usuario.nome} ${g.usuario.sobrenome}"/></option>
                    </c:forEach>
                </select>
            </label>
            <label>Frigorífico
                <select name="idFrigorifico">
                    <option value="">Selecione</option>
                    <c:forEach items="${frigorificos}" var="f">
                        <option value="${f.idFrigorifico}"><c:out value="${f.nome} · ${f.empresa.nome}"/></option>
                    </c:forEach>
                </select>
            </label>

            <h3>Item em estoque</h3>
            <label>Quantidade <input type="number" min="1" name="quantidade" required></label>
            <label>Data de validade <input type="date" name="dtValidade" required></label>
            <label>Lote <input type="number" min="0" name="lote"></label>

            <button type="submit">Adicionar produto</button>
        </form>
    </details>

    <%-- filtros da lista --%>
    <section id="filtros">
        <h2>Filtros</h2>
        <form method="get" action="${pageContext.request.contextPath}/admin/produtos">
            <label>Produto <input type="text" name="produto" value="${filtroProduto}" placeholder="Nome do produto"></label>
            <label>Dono
                <select name="dono">
                    <option value="">Todos</option>
                    <option value="usuario" ${filtroDono == 'usuario' ? 'selected' : ''}>Usuário</option>
                    <option value="empresa" ${filtroDono == 'empresa' ? 'selected' : ''}>Empresa</option>
                </select>
            </label>
            <label>Armazenamento
                <select name="arm">
                    <option value="">Todos</option>
                    <option value="geladeira" ${filtroArm == 'geladeira' ? 'selected' : ''}>Geladeira</option>
                    <option value="frigorifico" ${filtroArm == 'frigorifico' ? 'selected' : ''}>Frigorífico</option>
                </select>
            </label>
            <label>Categoria
                <select name="cat">
                    <option value="">Todos</option>
                    <c:forEach items="${categorias}" var="cat">
                        <option value="${cat}" ${filtroCat == cat.name() ? 'selected' : ''}><c:out value="${cat}"/></option>
                    </c:forEach>
                </select>
            </label>
            <label>Status
                <select name="status">
                    <option value="">Todos</option>
                    <c:forEach items="${statusProduto}" var="s">
                        <option value="${s}" ${filtroStatus == s.name() ? 'selected' : ''}><c:out value="${s}"/></option>
                    </c:forEach>
                </select>
            </label>
            <label>Validade
                <select name="val">
                    <option value="">Todos</option>
                    <option value="vencidos" ${filtroVal == 'vencidos' ? 'selected' : ''}>Já vencidos</option>
                    <option value="7" ${filtroVal == '7' ? 'selected' : ''}>Próximos 7 dias</option>
                    <option value="30" ${filtroVal == '30' ? 'selected' : ''}>Próximos 30 dias</option>
                    <option value="longa" ${filtroVal == 'longa' ? 'selected' : ''}>Mais de 30 dias</option>
                </select>
            </label>
            <label>Ordenar por
                <select name="ord">
                    <option value="dias:asc" ${filtroOrd == 'dias:asc' ? 'selected' : ''}>Validade: mais próxima</option>
                    <option value="dias:desc" ${filtroOrd == 'dias:desc' ? 'selected' : ''}>Validade: mais distante</option>
                    <option value="nome:asc" ${filtroOrd == 'nome:asc' ? 'selected' : ''}>Nome: A a Z</option>
                    <option value="nome:desc" ${filtroOrd == 'nome:desc' ? 'selected' : ''}>Nome: Z a A</option>
                </select>
            </label>
            <button type="submit">Filtrar</button>
            <a href="${pageContext.request.contextPath}/admin/produtos">Limpar filtros</a>
        </form>
    </section>

    <%-- lista --%>
    <section id="lista">
        <p>${totalFiltrado} de ${totalGeral} produtos</p>
        <c:choose>
            <c:when test="${empty linhas}">
                <p>Nenhum registro encontrado com esses filtros.</p>
            </c:when>
            <c:otherwise>
                <table>
                    <thead>
                        <tr><th>Produto</th><th>Armazenamento</th><th>Dono</th><th>Qtd.</th><th>Validade</th><th>Status</th><th>Lote</th><th>Ações</th></tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${linhas}" var="l">
                            <tr>
                                <td><c:out value="${l.produto.nome} ${l.produto.marca} · ${l.produto.categoria}"/></td>
                                <td><c:out value="${l.tipoArmazenamento == 'geladeira' ? 'Geladeira' : 'Frigorífico'} ${l.armazenamento}"/></td>
                                <td><c:out value="${l.dono}"/> (${l.donoTipo == 'usuario' ? 'Usuário' : 'Empresa'})</td>
                                <td>${l.quantidade} <c:out value="${l.produto.unidadeMedida}"/></td>
                                <td>
                                    ${l.dtValidade}
                                    <c:choose>
                                        <c:when test="${l.dias < 0}">(venceu há ${-l.dias} dias)</c:when>
                                        <c:when test="${l.dias == 0}">(vence hoje)</c:when>
                                        <c:otherwise>(vence em ${l.dias} dias)</c:otherwise>
                                    </c:choose>
                                </td>
                                <td><c:out value="${l.status}"/></td>
                                <td>${l.lote}</td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/admin/produtos?ver=${l.chave}">Ver</a>
                                    <a href="${pageContext.request.contextPath}/admin/produtos?editar=${l.chave}">Editar</a>
                                    <form method="post" action="${pageContext.request.contextPath}/admin/produtos" onsubmit="return confirmarExclusao()">
                                        <input type="hidden" name="action" value="deletar">
                                        <input type="hidden" name="armazenamento" value="${l.tipoArmazenamento}">
                                        <c:choose>
                                            <c:when test="${l.tipoArmazenamento == 'geladeira'}">
                                                <input type="hidden" name="idProdutoGeladeira" value="${l.idItem}">
                                            </c:when>
                                            <c:otherwise>
                                                <input type="hidden" name="idProdutoFrigorifico" value="${l.idItem}">
                                            </c:otherwise>
                                        </c:choose>
                                        <button type="submit">Excluir</button>
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
