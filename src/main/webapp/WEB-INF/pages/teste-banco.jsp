<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <title>Teste do banco - Frigus</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/estilo.css">
    <script src="${pageContext.request.contextPath}/assets/js/admin.js"></script>
</head>
<body>
<c:set var="menuAtivo" value="teste-banco" scope="request"/>
<%@ include file="menu.jspf" %>
<main>
    <h1>Teste do banco</h1>

    <c:if test="${not empty erro}">
        <div class="erro"><c:out value="${erro}"/></div>
    </c:if>
    <c:if test="${not empty sucesso}">
        <div class="sucesso"><c:out value="${sucesso}"/></div>
    </c:if>

    <%-- contagens --%>
    <div class="contagens">
        <div><strong>${totalUsuarios}</strong>usuários</div>
        <div><strong>${totalEmpresas}</strong>empresas</div>
        <div><strong>${totalGeladeiras}</strong>geladeiras</div>
        <div><strong>${totalFrigorificos}</strong>frigoríficos</div>
        <div><strong>${totalItensGeladeira}</strong>itens de geladeira</div>
        <div><strong>${totalItensFrigorifico}</strong>itens de frigorífico</div>
        <div><strong>${totalReceitas}</strong>receitas</div>
        <div><strong>${totalListasCompras}</strong>listas de compras</div>
        <div><strong>${totalItensListas}</strong>itens de lista</div>
        <div><strong>${totalAssinaturas}</strong>assinaturas</div>
    </div>
    <br>

    <%-- ===================== USUARIOS ===================== --%>
    <div class="cartao">
        <h2>Usuários</h2>
        <table>
            <tr><th>CPF</th><th>Nome</th><th>Sobrenome</th><th>E-mail</th><th></th></tr>
            <c:forEach items="${usuarios}" var="u">
                <tr>
                    <td><c:out value="${u.cpf}"/></td>
                    <td><c:out value="${u.nome}"/></td>
                    <td><c:out value="${u.sobrenome}"/></td>
                    <td><c:out value="${u.email}"/></td>
                    <td>
                        <form class="inline" method="post" action="${pageContext.request.contextPath}/admin/usuarios" onsubmit="return confirmarExclusao()">
                            <input type="hidden" name="action" value="deletar">
                            <input type="hidden" name="retorno" value="/admin/teste-banco">
                            <input type="hidden" name="cpf" value="${u.cpf}">
                            <button class="excluir" type="submit">Excluir</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
        </table>
        <form class="linha" method="post" action="${pageContext.request.contextPath}/admin/usuarios">
            <input type="hidden" name="action" value="inserir">
            <input type="hidden" name="retorno" value="/admin/teste-banco">
            <div><label>CPF</label><input name="cpf" size="12" required></div>
            <div><label>Nome</label><input name="nome" required></div>
            <div><label>Sobrenome</label><input name="sobrenome" required></div>
            <div><label>E-mail</label><input name="email" required></div>
            <div><label>Senha (8 a 15, maiúscula e especial)</label><input type="password" name="senha" required></div>
            <button type="submit">Inserir usuário</button>
        </form>
        <form class="linha" method="post" action="${pageContext.request.contextPath}/admin/usuarios">
            <input type="hidden" name="action" value="alterar">
            <input type="hidden" name="retorno" value="/admin/teste-banco">
            <div><label>Usuário</label>
                <select name="cpf">
                    <c:forEach items="${usuarios}" var="u"><option value="${u.cpf}"><c:out value="${u.nome} ${u.sobrenome} - ${u.cpf}"/></option></c:forEach>
                </select>
            </div>
            <div><label>Nome</label><input name="nome" required></div>
            <div><label>Sobrenome</label><input name="sobrenome" required></div>
            <div><label>E-mail</label><input name="email" required></div>
            <div><label>Nova senha (vazio mantém)</label><input type="password" name="senha"></div>
            <button type="submit">Alterar usuário</button>
        </form>
    </div>

    <%-- ===================== EMPRESAS ===================== --%>
    <div class="cartao">
        <h2>Empresas</h2>
        <table>
            <tr><th>CNPJ</th><th>Nome</th><th>E-mail</th><th></th></tr>
            <c:forEach items="${empresas}" var="e">
                <tr>
                    <td><c:out value="${e.cnpj}"/></td>
                    <td><c:out value="${e.nome}"/></td>
                    <td><c:out value="${e.email}"/></td>
                    <td>
                        <form class="inline" method="post" action="${pageContext.request.contextPath}/admin/empresas" onsubmit="return confirmarExclusao()">
                            <input type="hidden" name="action" value="deletar">
                            <input type="hidden" name="retorno" value="/admin/teste-banco">
                            <input type="hidden" name="cnpj" value="${e.cnpj}">
                            <button class="excluir" type="submit">Excluir</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
        </table>
        <form class="linha" method="post" action="${pageContext.request.contextPath}/admin/empresas">
            <input type="hidden" name="action" value="inserir">
            <input type="hidden" name="retorno" value="/admin/teste-banco">
            <div><label>CNPJ</label><input name="cnpj" size="16" required></div>
            <div><label>Nome</label><input name="nome" required></div>
            <div><label>E-mail</label><input name="email" required></div>
            <div><label>Senha (8 a 15, maiúscula e especial)</label><input type="password" name="senha" required></div>
            <button type="submit">Inserir empresa</button>
        </form>
        <form class="linha" method="post" action="${pageContext.request.contextPath}/admin/empresas">
            <input type="hidden" name="action" value="alterar">
            <input type="hidden" name="retorno" value="/admin/teste-banco">
            <div><label>Empresa</label>
                <select name="cnpj">
                    <c:forEach items="${empresas}" var="e"><option value="${e.cnpj}"><c:out value="${e.nome} - ${e.cnpj}"/></option></c:forEach>
                </select>
            </div>
            <div><label>Nome</label><input name="nome" required></div>
            <div><label>E-mail</label><input name="email" required></div>
            <div><label>Nova senha (vazio mantém)</label><input type="password" name="senha"></div>
            <button type="submit">Alterar empresa</button>
        </form>
    </div>

    <%-- ===================== GELADEIRAS ===================== --%>
    <div class="cartao">
        <h2>Geladeiras</h2>
        <table>
            <tr><th>ID</th><th>Nome</th><th>Tipo</th><th>Status</th><th>Dono (usuário)</th><th></th></tr>
            <c:forEach items="${geladeiras}" var="g">
                <tr>
                    <td>${g.idGeladeira}</td>
                    <td><c:out value="${g.nome}"/></td>
                    <td><c:out value="${g.tipo}"/></td>
                    <td><c:out value="${g.status}"/></td>
                    <td><c:out value="${g.usuario.nome} ${g.usuario.sobrenome} (${g.usuario.cpf})"/></td>
                    <td>
                        <form class="inline" method="post" action="${pageContext.request.contextPath}/admin/geladeiras" onsubmit="return confirmarExclusao()">
                            <input type="hidden" name="action" value="deletar">
                            <input type="hidden" name="retorno" value="/admin/teste-banco">
                            <input type="hidden" name="idGeladeira" value="${g.idGeladeira}">
                            <button class="excluir" type="submit">Excluir</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
        </table>
        <form class="linha" method="post" action="${pageContext.request.contextPath}/admin/geladeiras">
            <input type="hidden" name="action" value="inserir">
            <input type="hidden" name="retorno" value="/admin/teste-banco">
            <div><label>Usuário dono</label>
                <select name="cpf">
                    <option value="">-- escolha --</option>
                    <c:forEach items="${usuarios}" var="u"><option value="${u.cpf}"><c:out value="${u.nome} ${u.sobrenome} - ${u.cpf}"/></option></c:forEach>
                </select>
            </div>
            <div><label>Nome</label><input name="nome" required></div>
            <div><label>Tipo</label><input name="tipo" placeholder="Geladeira"></div>
            <button type="submit">Inserir geladeira</button>
        </form>
        <form class="linha" method="post" action="${pageContext.request.contextPath}/admin/geladeiras">
            <input type="hidden" name="action" value="alterar">
            <input type="hidden" name="retorno" value="/admin/teste-banco">
            <div><label>Geladeira</label>
                <select name="idGeladeira">
                    <c:forEach items="${geladeiras}" var="g"><option value="${g.idGeladeira}"><c:out value="${g.idGeladeira} - ${g.nome}"/></option></c:forEach>
                </select>
            </div>
            <div><label>Usuário dono</label>
                <select name="cpf">
                    <option value="">-- escolha --</option>
                    <c:forEach items="${usuarios}" var="u"><option value="${u.cpf}"><c:out value="${u.nome} ${u.sobrenome} - ${u.cpf}"/></option></c:forEach>
                </select>
            </div>
            <div><label>Nome</label><input name="nome" required></div>
            <div><label>Tipo</label><input name="tipo" required></div>
            <button type="submit">Alterar geladeira</button>
        </form>
    </div>

    <%-- ===================== FRIGORIFICOS ( COM ENDERECO ) ===================== --%>
    <div class="cartao">
        <h2>Frigoríficos e endereços</h2>
        <table>
            <tr><th>ID</th><th>Nome</th><th>Tipo</th><th>Status</th><th>Dona (empresa)</th><th>Endereço</th><th></th></tr>
            <c:forEach items="${frigorificos}" var="f">
                <tr>
                    <td>${f.idFrigorifico}</td>
                    <td><c:out value="${f.nome}"/></td>
                    <td><c:out value="${f.tipo}"/></td>
                    <td><c:out value="${f.status}"/></td>
                    <td><c:out value="${f.empresa.nome} (${f.empresa.cnpj})"/></td>
                    <td><c:out value="${f.endereco.nomeUnidade}: ${f.endereco.rua}, ${f.endereco.numero} - ${f.endereco.bairro}, ${f.endereco.cidade}/${f.endereco.uf} - ${f.endereco.cep}"/></td>
                    <td>
                        <form class="inline" method="post" action="${pageContext.request.contextPath}/admin/frigorificos" onsubmit="return confirmarExclusao()">
                            <input type="hidden" name="action" value="deletar">
                            <input type="hidden" name="retorno" value="/admin/teste-banco">
                            <input type="hidden" name="idFrigorifico" value="${f.idFrigorifico}">
                            <button class="excluir" type="submit">Excluir</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
        </table>
        <form class="linha" method="post" action="${pageContext.request.contextPath}/admin/frigorificos">
            <input type="hidden" name="action" value="inserir">
            <input type="hidden" name="retorno" value="/admin/teste-banco">
            <div><label>Empresa dona</label>
                <select name="cnpj">
                    <option value="">-- escolha --</option>
                    <c:forEach items="${empresas}" var="e"><option value="${e.cnpj}"><c:out value="${e.nome} - ${e.cnpj}"/></option></c:forEach>
                </select>
            </div>
            <div><label>Nome</label><input name="nomeFrigorifico" required></div>
            <div><label>Tipo</label><input name="tipo" value="Freezer" required></div>
            <div><label>Nome da unidade</label><input name="nomeUnidade" required></div>
            <div><label>Rua</label><input name="rua" required></div>
            <div><label>Número</label><input name="numero" size="6" required></div>
            <div><label>Bairro</label><input name="bairro" required></div>
            <div><label>Cidade</label><input name="cidade" value="São Paulo" required></div>
            <div><label>UF</label><input name="uf" size="2" value="SP" required></div>
            <div><label>País</label><input name="pais" value="Brasil" required></div>
            <div><label>CEP</label><input name="cep" size="9" required></div>
            <button type="submit">Inserir frigorífico</button>
        </form>
        <form class="linha" method="post" action="${pageContext.request.contextPath}/admin/frigorificos">
            <input type="hidden" name="action" value="alterar">
            <input type="hidden" name="retorno" value="/admin/teste-banco">
            <div><label>Frigorífico</label>
                <select name="idFrigorifico">
                    <c:forEach items="${frigorificos}" var="f"><option value="${f.idFrigorifico}"><c:out value="${f.idFrigorifico} - ${f.nome}"/></option></c:forEach>
                </select>
            </div>
            <div><label>Empresa dona</label>
                <select name="cnpj">
                    <option value="">-- escolha --</option>
                    <c:forEach items="${empresas}" var="e"><option value="${e.cnpj}"><c:out value="${e.nome} - ${e.cnpj}"/></option></c:forEach>
                </select>
            </div>
            <div><label>Nome</label><input name="nomeFrigorifico" required></div>
            <div><label>Tipo</label><input name="tipo" required></div>
            <div><label>Nome da unidade</label><input name="nomeUnidade" required></div>
            <div><label>Rua</label><input name="rua" required></div>
            <div><label>Número</label><input name="numero" size="6" required></div>
            <div><label>Bairro</label><input name="bairro" required></div>
            <div><label>Cidade</label><input name="cidade" required></div>
            <div><label>UF</label><input name="uf" size="2" required></div>
            <div><label>País</label><input name="pais" required></div>
            <div><label>CEP</label><input name="cep" size="9" required></div>
            <button type="submit">Alterar frigorífico</button>
        </form>
    </div>

    <%-- ===================== ITENS DE GELADEIRA ( COM PRODUTO ) ===================== --%>
    <div class="cartao">
        <h2>Itens de geladeira (produto)</h2>
        <table>
            <tr><th>ID</th><th>Produto</th><th>Categoria</th><th>Preço</th><th>Geladeira</th><th>Dono</th><th>Validade</th><th>Status</th><th>Lote</th><th>Qtd</th><th></th></tr>
            <c:forEach items="${itensGeladeira}" var="ig">
                <tr>
                    <td>${ig.idProdutosGeladeira}</td>
                    <td><c:out value="${ig.produto.nome} (${ig.produto.marca})"/></td>
                    <td><c:out value="${ig.produto.categoria}"/></td>
                    <td>${ig.produto.preco}</td>
                    <td><c:out value="${ig.geladeira.nome}"/></td>
                    <td><c:out value="${ig.geladeira.usuario.nome}"/></td>
                    <td>${ig.dtValidade}</td>
                    <td><c:out value="${ig.status}"/></td>
                    <td>${ig.lote}</td>
                    <td>${ig.quantidade}</td>
                    <td>
                        <form class="inline" method="post" action="${pageContext.request.contextPath}/admin/produtos" onsubmit="return confirmarExclusao()">
                            <input type="hidden" name="action" value="deletar">
                            <input type="hidden" name="armazenamento" value="geladeira">
                            <input type="hidden" name="retorno" value="/admin/teste-banco">
                            <input type="hidden" name="idProdutoGeladeira" value="${ig.idProdutosGeladeira}">
                            <button class="excluir" type="submit">Excluir</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
        </table>
        <form class="linha" method="post" action="${pageContext.request.contextPath}/admin/produtos">
            <input type="hidden" name="action" value="inserir">
            <input type="hidden" name="armazenamento" value="geladeira">
            <input type="hidden" name="retorno" value="/admin/teste-banco">
            <div><label>Geladeira</label>
                <select name="idGeladeira">
                    <c:forEach items="${geladeiras}" var="g"><option value="${g.idGeladeira}"><c:out value="${g.idGeladeira} - ${g.nome}"/></option></c:forEach>
                </select>
            </div>
            <div><label>Produto</label><input name="nomeProduto" required></div>
            <div><label>Descrição</label><input name="descricao"></div>
            <div><label>Preço</label><input name="preco" size="6" required></div>
            <div><label>Categoria</label>
                <select name="categoria">
                    <c:forEach items="${categorias}" var="cat"><option value="${cat}"><c:out value="${cat}"/></option></c:forEach>
                </select>
            </div>
            <div><label>Marca</label><input name="marca" required></div>
            <div><label>Unidade</label><input name="unidadeMedida" size="6" required></div>
            <div><label>Validade</label><input type="date" name="dtValidade" required></div>
            <div><label>Lote</label><input name="lote" size="5"></div>
            <div><label>Quantidade</label><input name="quantidade" size="5" required></div>
            <button type="submit">Inserir item na geladeira</button>
        </form>
        <form class="linha" method="post" action="${pageContext.request.contextPath}/admin/produtos">
            <input type="hidden" name="action" value="alterar">
            <input type="hidden" name="armazenamento" value="geladeira">
            <input type="hidden" name="retorno" value="/admin/teste-banco">
            <div><label>Item</label>
                <select name="idProdutoGeladeira">
                    <c:forEach items="${itensGeladeira}" var="ig"><option value="${ig.idProdutosGeladeira}"><c:out value="${ig.idProdutosGeladeira} - ${ig.produto.nome}"/></option></c:forEach>
                </select>
            </div>
            <div><label>Geladeira</label>
                <select name="idGeladeira">
                    <c:forEach items="${geladeiras}" var="g"><option value="${g.idGeladeira}"><c:out value="${g.idGeladeira} - ${g.nome}"/></option></c:forEach>
                </select>
            </div>
            <div><label>Produto</label><input name="nomeProduto" required></div>
            <div><label>Descrição</label><input name="descricao"></div>
            <div><label>Preço</label><input name="preco" size="6" required></div>
            <div><label>Categoria</label>
                <select name="categoria">
                    <c:forEach items="${categorias}" var="cat"><option value="${cat}"><c:out value="${cat}"/></option></c:forEach>
                </select>
            </div>
            <div><label>Marca</label><input name="marca" required></div>
            <div><label>Unidade</label><input name="unidadeMedida" size="6" required></div>
            <div><label>Validade</label><input type="date" name="dtValidade" required></div>
            <div><label>Lote</label><input name="lote" size="5"></div>
            <div><label>Quantidade</label><input name="quantidade" size="5" required></div>
            <button type="submit">Alterar item da geladeira</button>
        </form>
    </div>

    <%-- ===================== ITENS DE FRIGORIFICO ( COM PRODUTO ) ===================== --%>
    <div class="cartao">
        <h2>Itens de frigorífico (produto)</h2>
        <table>
            <tr><th>ID</th><th>Produto</th><th>Categoria</th><th>Preço</th><th>Frigorífico</th><th>Empresa</th><th>Validade</th><th>Status</th><th>Lote</th><th>Qtd</th><th></th></tr>
            <c:forEach items="${itensFrigorifico}" var="itf">
                <tr>
                    <td>${itf.idProdutosFrigorifico}</td>
                    <td><c:out value="${itf.produto.nome} (${itf.produto.marca})"/></td>
                    <td><c:out value="${itf.produto.categoria}"/></td>
                    <td>${itf.produto.preco}</td>
                    <td><c:out value="${itf.frigorificos.nome}"/></td>
                    <td><c:out value="${itf.frigorificos.empresa.nome}"/></td>
                    <td>${itf.dtValidade}</td>
                    <td><c:out value="${itf.status}"/></td>
                    <td>${itf.lote}</td>
                    <td>${itf.quantidade}</td>
                    <td>
                        <form class="inline" method="post" action="${pageContext.request.contextPath}/admin/produtos" onsubmit="return confirmarExclusao()">
                            <input type="hidden" name="action" value="deletar">
                            <input type="hidden" name="armazenamento" value="frigorifico">
                            <input type="hidden" name="retorno" value="/admin/teste-banco">
                            <input type="hidden" name="idProdutoFrigorifico" value="${itf.idProdutosFrigorifico}">
                            <button class="excluir" type="submit">Excluir</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
        </table>
        <form class="linha" method="post" action="${pageContext.request.contextPath}/admin/produtos">
            <input type="hidden" name="action" value="inserir">
            <input type="hidden" name="armazenamento" value="frigorifico">
            <input type="hidden" name="retorno" value="/admin/teste-banco">
            <div><label>Frigorífico</label>
                <select name="idFrigorifico">
                    <c:forEach items="${frigorificos}" var="f"><option value="${f.idFrigorifico}"><c:out value="${f.idFrigorifico} - ${f.nome}"/></option></c:forEach>
                </select>
            </div>
            <div><label>Produto</label><input name="nomeProduto" required></div>
            <div><label>Descrição</label><input name="descricao"></div>
            <div><label>Preço</label><input name="preco" size="6" required></div>
            <div><label>Categoria</label>
                <select name="categoria">
                    <c:forEach items="${categorias}" var="cat"><option value="${cat}"><c:out value="${cat}"/></option></c:forEach>
                </select>
            </div>
            <div><label>Marca</label><input name="marca" required></div>
            <div><label>Unidade</label><input name="unidadeMedida" size="6" required></div>
            <div><label>Validade</label><input type="date" name="dtValidade" required></div>
            <div><label>Lote</label><input name="lote" size="5"></div>
            <div><label>Quantidade</label><input name="quantidade" size="5" required></div>
            <button type="submit">Inserir item no frigorífico</button>
        </form>
        <form class="linha" method="post" action="${pageContext.request.contextPath}/admin/produtos">
            <input type="hidden" name="action" value="alterar">
            <input type="hidden" name="armazenamento" value="frigorifico">
            <input type="hidden" name="retorno" value="/admin/teste-banco">
            <div><label>Item</label>
                <select name="idProdutoFrigorifico">
                    <c:forEach items="${itensFrigorifico}" var="itf"><option value="${itf.idProdutosFrigorifico}"><c:out value="${itf.idProdutosFrigorifico} - ${itf.produto.nome}"/></option></c:forEach>
                </select>
            </div>
            <div><label>Frigorífico</label>
                <select name="idFrigorifico">
                    <c:forEach items="${frigorificos}" var="f"><option value="${f.idFrigorifico}"><c:out value="${f.idFrigorifico} - ${f.nome}"/></option></c:forEach>
                </select>
            </div>
            <div><label>Produto</label><input name="nomeProduto" required></div>
            <div><label>Descrição</label><input name="descricao"></div>
            <div><label>Preço</label><input name="preco" size="6" required></div>
            <div><label>Categoria</label>
                <select name="categoria">
                    <c:forEach items="${categorias}" var="cat"><option value="${cat}"><c:out value="${cat}"/></option></c:forEach>
                </select>
            </div>
            <div><label>Marca</label><input name="marca" required></div>
            <div><label>Unidade</label><input name="unidadeMedida" size="6" required></div>
            <div><label>Validade</label><input type="date" name="dtValidade" required></div>
            <div><label>Lote</label><input name="lote" size="5"></div>
            <div><label>Quantidade</label><input name="quantidade" size="5" required></div>
            <button type="submit">Alterar item do frigorífico</button>
        </form>
    </div>

    <%-- ===================== RECEITAS ===================== --%>
    <div class="cartao">
        <h2>Receitas</h2>
        <table>
            <tr><th>ID</th><th>Nome</th><th>Dono</th><th>Data</th><th>Foi feita?</th><th>Modo de preparo</th><th></th></tr>
            <c:forEach items="${receitas}" var="r">
                <tr>
                    <td>${r.idReceita}</td>
                    <td><c:out value="${r.nome}"/></td>
                    <td>
                        <c:choose>
                            <c:when test="${not empty r.usuario}">Usuário: <c:out value="${r.usuario.nome} ${r.usuario.sobrenome} (${r.usuario.cpf})"/></c:when>
                            <c:otherwise>Empresa: <c:out value="${r.empresa.nome} (${r.empresa.cnpj})"/></c:otherwise>
                        </c:choose>
                    </td>
                    <td>${r.dtReceita}</td>
                    <td>${r.foiFeita}</td>
                    <td><c:out value="${r.modoPreparo}"/></td>
                    <td>
                        <form class="inline" method="post" action="${pageContext.request.contextPath}/admin/receitas" onsubmit="return confirmarExclusao()">
                            <input type="hidden" name="action" value="deletar">
                            <input type="hidden" name="retorno" value="/admin/teste-banco">
                            <input type="hidden" name="idReceita" value="${r.idReceita}">
                            <button class="excluir" type="submit">Excluir</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
        </table>
        <form class="linha" method="post" action="${pageContext.request.contextPath}/admin/receitas">
            <input type="hidden" name="action" value="inserir">
            <input type="hidden" name="retorno" value="/admin/teste-banco">
            <div><label>Dono: usuário</label>
                <select name="cpf">
                    <option value="">-- nenhum --</option>
                    <c:forEach items="${usuarios}" var="u"><option value="${u.cpf}"><c:out value="${u.nome} ${u.sobrenome} - ${u.cpf}"/></option></c:forEach>
                </select>
            </div>
            <div><label>Dono: empresa</label>
                <select name="cnpj">
                    <option value="">-- nenhuma --</option>
                    <c:forEach items="${empresas}" var="e"><option value="${e.cnpj}"><c:out value="${e.nome} - ${e.cnpj}"/></option></c:forEach>
                </select>
            </div>
            <div><label>Nome</label><input name="nomeReceita" required></div>
            <div><label>Descrição</label><input name="descricaoReceita"></div>
            <div><label>Modo de preparo</label><input name="modoPreparo" required></div>
            <div><label>Data</label><input type="date" name="dataReceita"></div>
            <div><label>Foi feita?</label><input type="checkbox" name="foiFeita" value="true"></div>
            <button type="submit">Inserir receita</button>
        </form>
        <form class="linha" method="post" action="${pageContext.request.contextPath}/admin/receitas">
            <input type="hidden" name="action" value="alterar">
            <input type="hidden" name="retorno" value="/admin/teste-banco">
            <div><label>Receita</label>
                <select name="idReceita">
                    <c:forEach items="${receitas}" var="r"><option value="${r.idReceita}"><c:out value="${r.idReceita} - ${r.nome}"/></option></c:forEach>
                </select>
            </div>
            <div><label>Dono: usuário</label>
                <select name="cpf">
                    <option value="">-- nenhum --</option>
                    <c:forEach items="${usuarios}" var="u"><option value="${u.cpf}"><c:out value="${u.nome} ${u.sobrenome} - ${u.cpf}"/></option></c:forEach>
                </select>
            </div>
            <div><label>Dono: empresa</label>
                <select name="cnpj">
                    <option value="">-- nenhuma --</option>
                    <c:forEach items="${empresas}" var="e"><option value="${e.cnpj}"><c:out value="${e.nome} - ${e.cnpj}"/></option></c:forEach>
                </select>
            </div>
            <div><label>Nome</label><input name="nomeReceita" required></div>
            <div><label>Descrição</label><input name="descricaoReceita"></div>
            <div><label>Modo de preparo</label><input name="modoPreparo" required></div>
            <div><label>Data</label><input type="date" name="dataReceita"></div>
            <div><label>Foi feita?</label><input type="checkbox" name="foiFeita" value="true"></div>
            <button type="submit">Alterar receita</button>
        </form>
    </div>

    <%-- ===================== INGREDIENTES ( COM PRODUTO ) ===================== --%>
    <div class="cartao">
        <h2>Ingredientes (produto)</h2>
        <table>
            <tr><th>ID</th><th>Receita</th><th>Produto</th><th>Categoria</th><th>Preço</th><th>Qtd</th><th></th></tr>
            <c:forEach items="${ingredientes}" var="i">
                <tr>
                    <td>${i.idIngredientes}</td>
                    <td><c:out value="${i.receita.idReceita} - ${i.receita.nome}"/></td>
                    <td><c:out value="${i.produto.nome} (${i.produto.marca})"/></td>
                    <td><c:out value="${i.produto.categoria}"/></td>
                    <td>${i.produto.preco}</td>
                    <td>${i.quantidade}</td>
                    <td>
                        <form class="inline" method="post" action="${pageContext.request.contextPath}/admin/receitas" onsubmit="return confirmarExclusao()">
                            <input type="hidden" name="action" value="deletarIngrediente">
                            <input type="hidden" name="retorno" value="/admin/teste-banco">
                            <input type="hidden" name="idIngrediente" value="${i.idIngredientes}">
                            <button class="excluir" type="submit">Excluir</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
        </table>
        <form class="linha" method="post" action="${pageContext.request.contextPath}/admin/receitas">
            <input type="hidden" name="action" value="inserirIngrediente">
            <input type="hidden" name="retorno" value="/admin/teste-banco">
            <div><label>Receita</label>
                <select name="idReceita">
                    <c:forEach items="${receitas}" var="r"><option value="${r.idReceita}"><c:out value="${r.idReceita} - ${r.nome}"/></option></c:forEach>
                </select>
            </div>
            <div><label>Produto</label><input name="nomeProduto" required></div>
            <div><label>Descrição</label><input name="descricaoProd"></div>
            <div><label>Preço</label><input name="preco" size="6" required></div>
            <div><label>Categoria</label>
                <select name="categoria">
                    <c:forEach items="${categorias}" var="cat"><option value="${cat}"><c:out value="${cat}"/></option></c:forEach>
                </select>
            </div>
            <div><label>Marca</label><input name="marca" required></div>
            <div><label>Unidade</label><input name="unidadeMedida" size="6" required></div>
            <div><label>Quantidade</label><input name="quantidade" size="5" required></div>
            <button type="submit">Inserir ingrediente</button>
        </form>
        <form class="linha" method="post" action="${pageContext.request.contextPath}/admin/receitas">
            <input type="hidden" name="action" value="alterarIngrediente">
            <input type="hidden" name="retorno" value="/admin/teste-banco">
            <div><label>Ingrediente</label>
                <select name="idIngrediente">
                    <c:forEach items="${ingredientes}" var="i"><option value="${i.idIngredientes}"><c:out value="${i.idIngredientes} - ${i.produto.nome}"/></option></c:forEach>
                </select>
            </div>
            <div><label>Receita</label>
                <select name="idReceita">
                    <c:forEach items="${receitas}" var="r"><option value="${r.idReceita}"><c:out value="${r.idReceita} - ${r.nome}"/></option></c:forEach>
                </select>
            </div>
            <div><label>Produto</label><input name="nomeProduto" required></div>
            <div><label>Descrição</label><input name="descricaoProd"></div>
            <div><label>Preço</label><input name="preco" size="6" required></div>
            <div><label>Categoria</label>
                <select name="categoria">
                    <c:forEach items="${categorias}" var="cat"><option value="${cat}"><c:out value="${cat}"/></option></c:forEach>
                </select>
            </div>
            <div><label>Marca</label><input name="marca" required></div>
            <div><label>Unidade</label><input name="unidadeMedida" size="6" required></div>
            <div><label>Quantidade</label><input name="quantidade" size="5" required></div>
            <button type="submit">Alterar ingrediente</button>
        </form>
    </div>

    <%-- ===================== LISTAS DE COMPRAS ===================== --%>
    <div class="cartao">
        <h2>Listas de compras</h2>
        <table>
            <tr><th>ID</th><th>Dono</th><th>Data</th><th></th></tr>
            <c:forEach items="${compras}" var="lc">
                <tr>
                    <td>${lc.idLista}</td>
                    <td>
                        <c:choose>
                            <c:when test="${not empty lc.usuario}">Usuário: <c:out value="${lc.usuario.nome} ${lc.usuario.sobrenome} (${lc.usuario.cpf})"/></c:when>
                            <c:otherwise>Empresa: <c:out value="${lc.empresa.nome} (${lc.empresa.cnpj})"/></c:otherwise>
                        </c:choose>
                    </td>
                    <td>${lc.dtLista}</td>
                    <td>
                        <form class="inline" method="post" action="${pageContext.request.contextPath}/admin/compras" onsubmit="return confirmarExclusao()">
                            <input type="hidden" name="action" value="deletar">
                            <input type="hidden" name="retorno" value="/admin/teste-banco">
                            <input type="hidden" name="idCompra" value="${lc.idLista}">
                            <button class="excluir" type="submit">Excluir</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
        </table>
        <form class="linha" method="post" action="${pageContext.request.contextPath}/admin/compras">
            <input type="hidden" name="action" value="inserir">
            <input type="hidden" name="retorno" value="/admin/teste-banco">
            <div><label>Dono: usuário</label>
                <select name="cpf">
                    <option value="">-- nenhum --</option>
                    <c:forEach items="${usuarios}" var="u"><option value="${u.cpf}"><c:out value="${u.nome} ${u.sobrenome} - ${u.cpf}"/></option></c:forEach>
                </select>
            </div>
            <div><label>Dono: empresa</label>
                <select name="cnpj">
                    <option value="">-- nenhuma --</option>
                    <c:forEach items="${empresas}" var="e"><option value="${e.cnpj}"><c:out value="${e.nome} - ${e.cnpj}"/></option></c:forEach>
                </select>
            </div>
            <div><label>Data</label><input type="date" name="dtLista"></div>
            <button type="submit">Inserir lista</button>
        </form>
        <form class="linha" method="post" action="${pageContext.request.contextPath}/admin/compras">
            <input type="hidden" name="action" value="alterar">
            <input type="hidden" name="retorno" value="/admin/teste-banco">
            <div><label>Lista</label>
                <select name="idCompra">
                    <c:forEach items="${compras}" var="lc"><option value="${lc.idLista}"><c:out value="${lc.idLista} - ${lc.dtLista}"/></option></c:forEach>
                </select>
            </div>
            <div><label>Dono: usuário</label>
                <select name="cpf">
                    <option value="">-- nenhum --</option>
                    <c:forEach items="${usuarios}" var="u"><option value="${u.cpf}"><c:out value="${u.nome} ${u.sobrenome} - ${u.cpf}"/></option></c:forEach>
                </select>
            </div>
            <div><label>Dono: empresa</label>
                <select name="cnpj">
                    <option value="">-- nenhuma --</option>
                    <c:forEach items="${empresas}" var="e"><option value="${e.cnpj}"><c:out value="${e.nome} - ${e.cnpj}"/></option></c:forEach>
                </select>
            </div>
            <div><label>Data</label><input type="date" name="dtLista" required></div>
            <button type="submit">Alterar lista</button>
        </form>
    </div>

    <%-- ===================== ITENS DE LISTA ( COM PRODUTO ) ===================== --%>
    <div class="cartao">
        <h2>Itens de lista (produto)</h2>
        <table>
            <tr><th>ID</th><th>Lista</th><th>Produto</th><th>Categoria</th><th>Preço</th><th>Qtd</th><th></th></tr>
            <c:forEach items="${itensLista}" var="il">
                <tr>
                    <td>${il.idProdutoLista}</td>
                    <td><c:out value="${il.listaCompras.idLista} - ${il.listaCompras.dtLista}"/></td>
                    <td><c:out value="${il.produto.nome} (${il.produto.marca})"/></td>
                    <td><c:out value="${il.produto.categoria}"/></td>
                    <td>${il.produto.preco}</td>
                    <td>${il.quantidade}</td>
                    <td>
                        <form class="inline" method="post" action="${pageContext.request.contextPath}/admin/compras" onsubmit="return confirmarExclusao()">
                            <input type="hidden" name="action" value="deletarItem">
                            <input type="hidden" name="retorno" value="/admin/teste-banco">
                            <input type="hidden" name="idProdutoLista" value="${il.idProdutoLista}">
                            <button class="excluir" type="submit">Excluir</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
        </table>
        <form class="linha" method="post" action="${pageContext.request.contextPath}/admin/compras">
            <input type="hidden" name="action" value="inserirItem">
            <input type="hidden" name="retorno" value="/admin/teste-banco">
            <div><label>Lista</label>
                <select name="idCompra">
                    <c:forEach items="${compras}" var="lc"><option value="${lc.idLista}"><c:out value="${lc.idLista} - ${lc.dtLista}"/></option></c:forEach>
                </select>
            </div>
            <div><label>Produto</label><input name="nome" required></div>
            <div><label>Descrição</label><input name="descricao"></div>
            <div><label>Preço</label><input name="preco" size="6" required></div>
            <div><label>Categoria</label>
                <select name="categoria">
                    <c:forEach items="${categorias}" var="cat"><option value="${cat}"><c:out value="${cat}"/></option></c:forEach>
                </select>
            </div>
            <div><label>Marca</label><input name="marca" required></div>
            <div><label>Unidade</label><input name="unidadeMedida" size="6" required></div>
            <div><label>Quantidade</label><input name="quantidade" size="5" required></div>
            <button type="submit">Inserir item na lista</button>
        </form>
        <form class="linha" method="post" action="${pageContext.request.contextPath}/admin/compras">
            <input type="hidden" name="action" value="alterarItem">
            <input type="hidden" name="retorno" value="/admin/teste-banco">
            <div><label>Item</label>
                <select name="idProdutoLista">
                    <c:forEach items="${itensLista}" var="il"><option value="${il.idProdutoLista}"><c:out value="${il.idProdutoLista} - ${il.produto.nome}"/></option></c:forEach>
                </select>
            </div>
            <div><label>Lista</label>
                <select name="idCompra">
                    <c:forEach items="${compras}" var="lc"><option value="${lc.idLista}"><c:out value="${lc.idLista} - ${lc.dtLista}"/></option></c:forEach>
                </select>
            </div>
            <div><label>Produto</label><input name="nome" required></div>
            <div><label>Descrição</label><input name="descricao"></div>
            <div><label>Preço</label><input name="preco" size="6" required></div>
            <div><label>Categoria</label>
                <select name="categoria">
                    <c:forEach items="${categorias}" var="cat"><option value="${cat}"><c:out value="${cat}"/></option></c:forEach>
                </select>
            </div>
            <div><label>Marca</label><input name="marca" required></div>
            <div><label>Unidade</label><input name="unidadeMedida" size="6" required></div>
            <div><label>Quantidade</label><input name="quantidade" size="5" required></div>
            <button type="submit">Alterar item da lista</button>
        </form>
    </div>

    <%-- ===================== ASSINATURAS ===================== --%>
    <div class="cartao">
        <h2>Assinaturas</h2>
        <table>
            <tr><th>ID</th><th>Dono</th><th>Plano</th><th>Valor</th><th></th></tr>
            <c:forEach items="${assinaturas}" var="a">
                <tr>
                    <td>${a.idAssinatura}</td>
                    <td>
                        <c:choose>
                            <c:when test="${not empty a.usuario}">Usuário: <c:out value="${a.usuario.nome} ${a.usuario.sobrenome} (${a.usuario.cpf})"/></c:when>
                            <c:otherwise>Empresa: <c:out value="${a.empresa.nome} (${a.empresa.cnpj})"/></c:otherwise>
                        </c:choose>
                    </td>
                    <td><c:out value="${a.tipoAssinatura}"/></td>
                    <td>${a.valor}</td>
                    <td>
                        <form class="inline" method="post" action="${pageContext.request.contextPath}/admin/planos" onsubmit="return confirmarExclusao()">
                            <input type="hidden" name="action" value="deletar">
                            <input type="hidden" name="retorno" value="/admin/teste-banco">
                            <input type="hidden" name="idAssinatura" value="${a.idAssinatura}">
                            <button class="excluir" type="submit">Excluir</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
        </table>
        <form class="linha" method="post" action="${pageContext.request.contextPath}/admin/planos">
            <input type="hidden" name="action" value="inserir">
            <input type="hidden" name="retorno" value="/admin/teste-banco">
            <div><label>Dono: usuário</label>
                <select name="cpf">
                    <option value="">-- nenhum --</option>
                    <c:forEach items="${usuarios}" var="u"><option value="${u.cpf}"><c:out value="${u.nome} ${u.sobrenome} - ${u.cpf}"/></option></c:forEach>
                </select>
            </div>
            <div><label>Dono: empresa</label>
                <select name="cnpj">
                    <option value="">-- nenhuma --</option>
                    <c:forEach items="${empresas}" var="e"><option value="${e.cnpj}"><c:out value="${e.nome} - ${e.cnpj}"/></option></c:forEach>
                </select>
            </div>
            <div><label>Plano</label>
                <select name="plano">
                    <c:forEach items="${planos}" var="p"><option value="${p}"><c:out value="${p}"/></option></c:forEach>
                </select>
            </div>
            <button type="submit">Inserir assinatura</button>
        </form>
        <form class="linha" method="post" action="${pageContext.request.contextPath}/admin/planos">
            <input type="hidden" name="action" value="alterar">
            <input type="hidden" name="retorno" value="/admin/teste-banco">
            <div><label>Assinatura</label>
                <select name="idAssinatura">
                    <c:forEach items="${assinaturas}" var="a"><option value="${a.idAssinatura}"><c:out value="${a.idAssinatura} - ${a.tipoAssinatura}"/></option></c:forEach>
                </select>
            </div>
            <div><label>Dono: usuário</label>
                <select name="cpf">
                    <option value="">-- nenhum --</option>
                    <c:forEach items="${usuarios}" var="u"><option value="${u.cpf}"><c:out value="${u.nome} ${u.sobrenome} - ${u.cpf}"/></option></c:forEach>
                </select>
            </div>
            <div><label>Dono: empresa</label>
                <select name="cnpj">
                    <option value="">-- nenhuma --</option>
                    <c:forEach items="${empresas}" var="e"><option value="${e.cnpj}"><c:out value="${e.nome} - ${e.cnpj}"/></option></c:forEach>
                </select>
            </div>
            <div><label>Plano</label>
                <select name="plano">
                    <c:forEach items="${planos}" var="p"><option value="${p}"><c:out value="${p}"/></option></c:forEach>
                </select>
            </div>
            <button type="submit">Alterar assinatura</button>
        </form>
    </div>
</main>
</body>
</html>
