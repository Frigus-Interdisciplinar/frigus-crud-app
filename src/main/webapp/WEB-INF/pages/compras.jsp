<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="menuAtivo" value="compras" scope="request"/>
<c:set var="tituloPagina" value="Lista de compras" scope="request"/>
<c:set var="rotuloLista" value="itens" scope="request"/>
<%@ include file="cabecalho.jspf" %>

    <div class="conteudo" id="conteudo">
        <!-- Título e botão de adicionar -->
        <div class="topo-pagina">
            <div class="page-header">
                <h1>Lista de compras</h1>
                <p class="subtitulo-pagina">Itens das listas de compras de todos os usuários e empresas.</p>
            </div>

            <button class="botao-principal" type="button">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                    <line x1="12" y1="5" x2="12" y2="19"></line>
                    <line x1="5" y1="12" x2="19" y2="12"></line>
                </svg>
                Adicionar item
            </button>
        </div>

        <%@ include file="mensagens.jspf" %>

        <!-- Caixa da tabela -->
        <div class="caixa-tabela">
            <!-- Filtros -->
            <form class="filtros" method="get" action="${ctx}/admin/compras">
                <div class="filtro">
                    <label for="filtro-item">Item</label>
                    <input id="filtro-item" type="text" name="item" value="<c:out value='${filtroItem}'/>" placeholder="Nome do item">
                </div>
                <div class="filtro">
                    <label for="filtro-dono">Dono (usuário ou empresa)</label>
                    <input id="filtro-dono" type="text" name="dono" value="<c:out value='${filtroDono}'/>" placeholder="Usuário ou empresa">
                </div>
                <div class="filtro">
                    <label for="filtro-categoria">Categoria</label>
                    <select id="filtro-categoria" name="categoria">
                        <option value="">Todas</option>
                        <c:forEach items="${categorias}" var="cat">
                        <option value="${cat}" ${filtroCategoria == cat ? 'selected' : ''}>${cat}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="filtro filtro-grande filtro-destaque">
                    <label for="filtro-ordem">Ordenar por</label>
                    <select id="filtro-ordem" name="ord">
                        <option value="dono:asc" ${filtroOrd == 'dono:asc' ? 'selected' : ''}>Ordenar por nome do dono de A a Z</option>
                        <option value="dono:desc" ${filtroOrd == 'dono:desc' ? 'selected' : ''}>Ordenar por nome do dono de Z a A</option>
                        <option value="nome:asc" ${filtroOrd == 'nome:asc' ? 'selected' : ''}>Ordenar por nome do item de A a Z</option>
                        <option value="nome:desc" ${filtroOrd == 'nome:desc' ? 'selected' : ''}>Ordenar por nome do item de Z a A</option>
                        <option value="novo:asc" ${filtroOrd == 'novo:asc' ? 'selected' : ''}>Ordenar da inserção mais antiga</option>
                        <option value="novo:desc" ${filtroOrd == 'novo:desc' ? 'selected' : ''}>Ordenar da inserção mais recente</option>
                    </select>
                </div>
                <button class="botao-limpar" type="button">Limpar filtros</button>
            </form>

            <!-- Tabela -->
            <table class="tabela tabela-compra">
                <thead>
                    <tr>
                        <th>Item</th>
                        <th>Categoria</th>
                        <th>Quantidade</th>
                        <th>Lista de</th>
                        <th>Usuário</th>
                        <th>Empresa</th>
                        
                        <th></th>
                    </tr>
                </thead>

                <!-- Linhas da tabela (uma por registro) -->
                <tbody>
                    <c:forEach items="${itensLista}" var="i">
                        <c:set var="lista" value="${i.listaCompras}"/>
                        <c:set var="ehUsuario" value="${not empty lista.usuario}"/>
                        <c:set var="dono" value="${ehUsuario ? lista.usuario.nome.concat(' ').concat(lista.usuario.sobrenome) : lista.empresa.nome}"/>
                        <tr>
                            <td><div class="titulo-cards"><c:out value="${i.produto.nome}"/></div>
                                <div class="texto-pequeno-cards"><c:out value="${i.produto.marca}"/></div></td>
                            <td>${i.produto.categoria}</td>
                            <td>${i.quantidade} <c:out value="${i.produto.unidadeMedida}"/></td>
                            <td>${lista.dtLista}</td>
                            <td><c:if test="${ehUsuario}"><div class="titulo-cards"><c:out value="${lista.usuario.nome} ${lista.usuario.sobrenome}"/></div>
                                <div class="texto-pequeno-cards"><c:out value="${lista.usuario.email}"/></div></c:if><c:if test="${not ehUsuario}">—</c:if></td>
                            <td><c:if test="${not ehUsuario}"><div class="titulo-cards"><c:out value="${lista.empresa.nome}"/></div>
                                <div class="texto-pequeno-cards"><c:out value="${lista.empresa.email}"/></div></c:if><c:if test="${ehUsuario}">—</c:if></td>
                            <td class="botoes-tabela">
                                <button class="botao-icone" type="button" aria-label="Visualizar"
                                        data-dono-tipo="${ehUsuario ? 'usuario' : 'empresa'}" data-dono="<c:out value='${dono}'/>" data-produto="<c:out value='${i.produto.nome}'/>"
                                        data-marca="<c:out value='${i.produto.marca}'/>" data-data="${lista.dtLista}" data-quantidade="${i.quantidade}" data-unidade="<c:out value='${i.produto.unidadeMedida}'/>">
                                    <img src="${ctx}/assets/icones/icone-olho-aberto.svg" alt="">
                                </button>
                                <button class="botao-icone" type="button" aria-label="Editar"
                                        data-id-produto-lista="${i.idProdutoLista}" data-id-compra="${lista.idLista}" data-nome="<c:out value='${i.produto.nome}'/>"
                                        data-categoria="${i.produto.categoria}" data-preco="<c:out value='${i.produto.preco}'/>" data-marca="<c:out value='${i.produto.marca}'/>"
                                        data-descricao="<c:out value='${i.produto.descricao}'/>" data-unidade-medida="<c:out value='${i.produto.unidadeMedida}'/>" data-quantidade="${i.quantidade}">
                                    <img src="${ctx}/assets/icones/icone-caneta.svg" alt="">
                                </button>
                                <button class="botao-icone" type="button" aria-label="Excluir" data-id-produto-lista="${i.idProdutoLista}">
                                    <img src="${ctx}/assets/icones/icone-lixera.svg" alt="">
                                </button>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty itensLista}">
                        <tr><td colspan="7">Nenhum registro encontrado com esses filtros.</td></tr>
                    </c:if>
                </tbody>
            </table>

            <!-- Rodapé com contagem e paginação -->
            <%@ include file="rodape-tabela.jspf" %>
        </div>
    </div>

    <!-- ========== POP-UPS ========== -->

    <!-- Adicionar item -->
    <div class="popup-fundo" id="popup-adicionar">
        <aside class="gaveta" role="dialog" aria-modal="true" aria-labelledby="popup-adicionar-titulo">
            <header class="gaveta-topo">
                <div>
                    <h2 class="popup-titulo" id="popup-adicionar-titulo">Adicionar item</h2>
                    <p class="popup-texto">Item de uma lista de compras.</p>
                </div>
                <button class="botao-fechar" type="button" aria-label="Fechar" data-fechar>
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
                </button>
            </header>
            <form class="gaveta-form" method="post" action="${ctx}/admin/compras">
                <input type="hidden" name="action" value="inserirItem">
                <input type="hidden" name="voltar" value="<c:out value='${pageContext.request.queryString}'/>">
                <div class="gaveta-corpo">
                    <p class="gaveta-secao">Lista de compras</p>
                    <div class="filtro">
                        <label for="adicionar-lista">Lista <span class="obrigatorio">*</span></label>
                        <select id="adicionar-lista" name="idCompra" required>
                            <option value="">Selecione</option>
                            <c:forEach items="${compras}" var="c">
                                <option value="${c.idLista}"><c:out value="${empty c.usuario ? c.empresa.nome : c.usuario.nome.concat(' ').concat(c.usuario.sobrenome)}"/> · ${c.dtLista}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <hr class="gaveta-divisor">
                    <p class="gaveta-secao">Item</p>
                    <div class="filtro">
                        <label for="adicionar-produto">Produto <span class="obrigatorio">*</span></label>
                        <input id="adicionar-produto" name="nome" maxlength="20" type="text" placeholder="Ex.: Arroz Tipo 1" required>
                    </div>
                    <div class="filtro">
                        <label for="adicionar-preco">Preço (R$) <span class="obrigatorio">*</span></label>
                        <input id="adicionar-preco" name="preco" type="text" placeholder="0,00" required inputmode="decimal">
                    </div>
                    <div class="gaveta-linha">
                        <div class="filtro">
                            <label for="adicionar-marca">Marca <span class="obrigatorio">*</span></label>
                            <input id="adicionar-marca" name="marca" required maxlength="20" type="text" placeholder="Ex.: Tio João">
                        </div>
                        <div class="filtro">
                            <label for="adicionar-categoria">Categoria <span class="obrigatorio">*</span></label>
                            <select id="adicionar-categoria" name="categoria" required>
                                <option value="">Selecione</option>
                                <c:forEach items="${categorias}" var="cat">
                                    <option value="${cat}"><c:out value="${cat}"/></option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>
                    <div class="gaveta-linha">
                        <div class="filtro">
                            <label for="adicionar-quantidade">Quantidade <span class="obrigatorio">*</span></label>
                            <input id="adicionar-quantidade" name="quantidade" type="number" placeholder="0" required min="1" step="1">
                        </div>
                        <div class="filtro">
                            <label for="adicionar-unidade">Unidade <span class="obrigatorio">*</span></label>
                            <select id="adicionar-unidade" name="unidadeMedida" required>
                                <option value="">Selecione</option>
                                <option>un</option>
                                <option>kg</option>
                                <option>g</option>
                                <option>L</option>
                                <option>ml</option>
                            </select>
                        </div>
                    </div>
                </div>
                <footer class="gaveta-rodape">
                    <button class="botao-secundario" type="button" data-fechar>Cancelar</button>
                    <button class="botao-principal" type="submit">Adicionar item</button>
                </footer>
            </form>
        </aside>
    </div>

    <!-- Editar item -->
    <div class="popup-fundo" id="popup-editar">
        <aside class="gaveta" role="dialog" aria-modal="true" aria-labelledby="popup-editar-titulo">
            <header class="gaveta-topo">
                <div>
                    <h2 class="popup-titulo" id="popup-editar-titulo">Editar item</h2>
                    <p class="popup-texto">Item de uma lista de compras.</p>
                </div>
                <button class="botao-fechar" type="button" aria-label="Fechar" data-fechar>
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
                </button>
            </header>
            <form class="gaveta-form" method="post" action="${ctx}/admin/compras">
                <input type="hidden" name="action" value="alterarItem">
                <input type="hidden" name="voltar" value="<c:out value='${pageContext.request.queryString}'/>">
                <input type="hidden" name="idProdutoLista" value="">
                <input type="hidden" name="descricao" value="">
                <div class="gaveta-corpo">
                    <p class="gaveta-secao">Lista de compras</p>
                    <div class="filtro">
                        <label for="editar-lista">Lista <span class="obrigatorio">*</span></label>
                        <select id="editar-lista" name="idCompra" required>
                            <option value="">Selecione</option>
                            <c:forEach items="${compras}" var="c">
                                <option value="${c.idLista}"><c:out value="${empty c.usuario ? c.empresa.nome : c.usuario.nome.concat(' ').concat(c.usuario.sobrenome)}"/> · ${c.dtLista}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <hr class="gaveta-divisor">
                    <p class="gaveta-secao">Item</p>
                    <div class="filtro">
                        <label for="editar-produto">Produto <span class="obrigatorio">*</span></label>
                        <input id="editar-produto" name="nome" maxlength="20" type="text" placeholder="Ex.: Arroz Tipo 1" required>
                    </div>
                    <div class="filtro">
                        <label for="editar-preco">Preço (R$) <span class="obrigatorio">*</span></label>
                        <input id="editar-preco" name="preco" type="text" placeholder="0,00" required inputmode="decimal">
                    </div>
                    <div class="gaveta-linha">
                        <div class="filtro">
                            <label for="editar-marca">Marca <span class="obrigatorio">*</span></label>
                            <input id="editar-marca" name="marca" required maxlength="20" type="text" placeholder="Ex.: Tio João">
                        </div>
                        <div class="filtro">
                            <label for="editar-categoria">Categoria <span class="obrigatorio">*</span></label>
                            <select id="editar-categoria" name="categoria" required>
                                <option value="">Selecione</option>
                                <c:forEach items="${categorias}" var="cat">
                                    <option value="${cat}"><c:out value="${cat}"/></option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>
                    <div class="gaveta-linha">
                        <div class="filtro">
                            <label for="editar-quantidade">Quantidade <span class="obrigatorio">*</span></label>
                            <input id="editar-quantidade" name="quantidade" type="number" placeholder="0" required min="1" step="1">
                        </div>
                        <div class="filtro">
                            <label for="editar-unidade">Unidade <span class="obrigatorio">*</span></label>
                            <select id="editar-unidade" name="unidadeMedida" required>
                                <option value="">Selecione</option>
                                <option>un</option>
                                <option>kg</option>
                                <option>g</option>
                                <option>L</option>
                                <option>ml</option>
                            </select>
                        </div>
                    </div>
                </div>
                <footer class="gaveta-rodape">
                    <button class="botao-secundario" type="button" data-fechar>Cancelar</button>
                    <button class="botao-principal" type="submit">Salvar alterações</button>
                </footer>
            </form>
        </aside>
    </div>

    <!-- Detalhes do item -->
    <div class="popup-fundo" id="popup-detalhes">
        <aside class="gaveta" role="dialog" aria-modal="true" aria-labelledby="popup-detalhes-titulo">
            <header class="gaveta-topo">
                <div>
                    <h2 class="popup-titulo" id="popup-detalhes-titulo">Detalhes do item</h2>
                    <p class="popup-texto">Item de uma lista de compras.</p>
                </div>
                <button class="botao-fechar" type="button" aria-label="Fechar" data-fechar>
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
                </button>
            </header>
            <div class="gaveta-form">
                <div class="gaveta-corpo">
                    <p class="gaveta-secao">Dono da lista</p>
                    <p class="popup-texto">Escolha se o dono é um usuário ou uma empresa.</p>
                    <div class="alternador">
                        <label class="alternador-opcao">
                            <input type="radio" name="donoTipo" value="usuario" checked disabled>
                            <span>Usuário</span>
                        </label>
                        <label class="alternador-opcao">
                            <input type="radio" name="donoTipo" value="empresa" disabled>
                            <span>Empresa</span>
                        </label>
                    </div>
                    <div class="filtro">
                        <label for="detalhes-dono">Usuário <span class="obrigatorio">*</span></label>
                        <input id="detalhes-dono" name="dono" type="text" readonly>
                    </div>
                    <hr class="gaveta-divisor">
                    <p class="gaveta-secao">Item</p>
                    <div class="filtro">
                        <label for="detalhes-produto">Produto <span class="obrigatorio">*</span></label>
                        <input id="detalhes-produto" name="produto" type="text" readonly>
                    </div>
                    <div class="gaveta-linha">
                        <div class="filtro">
                            <label for="detalhes-marca">Marca</label>
                            <input id="detalhes-marca" name="marca" type="text" readonly>
                        </div>
                        <div class="filtro">
                            <label for="detalhes-data">Lista de (data) <span class="obrigatorio">*</span></label>
                            <input id="detalhes-data" name="data" type="text" readonly>
                        </div>
                    </div>
                    <div class="gaveta-linha">
                        <div class="filtro">
                            <label for="detalhes-quantidade">Quantidade <span class="obrigatorio">*</span></label>
                            <input id="detalhes-quantidade" name="quantidade" type="text" readonly>
                        </div>
                        <div class="filtro">
                            <label for="detalhes-unidade">Unidade <span class="obrigatorio">*</span></label>
                            <input id="detalhes-unidade" name="unidade" type="text" readonly>
                        </div>
                    </div>
                </div>
                <footer class="gaveta-rodape">
                    <button class="botao-secundario" type="button" data-fechar>Fechar</button>
                </footer>
            </div>
        </aside>
    </div>

    <!-- Excluir item -->
    <div class="popup-fundo popup-fundo-centro" id="popup-excluir">
        <form class="modal-exclusao" method="post" action="${ctx}/admin/compras" role="alertdialog" aria-modal="true" aria-labelledby="popup-excluir-titulo">
            <input type="hidden" name="action" value="deletarItem">
            <input type="hidden" name="voltar" value="<c:out value='${pageContext.request.queryString}'/>">
            <input type="hidden" name="idProdutoLista" value="">
            <div class="modal-icone">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="3 6 5 6 21 6"></polyline><path d="M19 6l-1 14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2L5 6"></path><path d="M10 11v6"></path><path d="M14 11v6"></path><path d="M9 6V4a1 1 0 0 1 1-1h4a1 1 0 0 1 1 1v2"></path></svg>
            </div>
            <h2 class="popup-titulo" id="popup-excluir-titulo">Excluir "<span id="nome-excluir"></span>"?</h2>
            <p class="popup-texto">O item será removido da lista de compras. Esta ação não poderá ser desfeita.</p>
            <div class="modal-botoes">
                <button class="botao-secundario" type="button" data-fechar>Cancelar</button>
                <button class="botao-principal botao-perigo" type="submit">Excluir item</button>
            </div>
        </form>
    </div>

<%@ include file="rodape.jspf" %>
