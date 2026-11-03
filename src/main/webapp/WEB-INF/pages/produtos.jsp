<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="menuAtivo" value="produtos" scope="request"/>
<c:set var="tituloPagina" value="Produtos" scope="request"/>
<c:set var="rotuloLista" value="produtos" scope="request"/>
<%@ include file="cabecalho.jspf" %>

    <div class="conteudo" id="conteudo">
        <!-- Título e botão de adicionar -->
        <div class="topo-pagina">
            <div class="page-header">
                <h1>Produtos</h1>
                <p class="subtitulo-pagina">Itens de todas as geladeiras e frigoríficos. Use o filtro Armazenamento para separar os dois.</p>
            </div>

            <button class="botao-principal" type="button">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                    <line x1="12" y1="5" x2="12" y2="19"></line>
                    <line x1="5" y1="12" x2="19" y2="12"></line>
                </svg>
                Adicionar produto
            </button>
        </div>

        <%@ include file="mensagens.jspf" %>

        <!-- Caixa da tabela -->
        <div class="caixa-tabela">
            <!-- Filtros -->
            <form class="filtros" method="get" action="${ctx}/admin/produtos">
                <div class="filtro">
                    <label for="filtro-produto">Produto</label>
                    <input id="filtro-produto" type="text" name="produto" value="<c:out value='${filtroProduto}'/>" placeholder="Nome do produto">
                </div>
                <div class="filtro">
                    <label for="filtro-dono">Dono</label>
                    <select id="filtro-dono" name="dono">
                        <option value="">Todos</option>
                        <option value="usuario" ${filtroDono == 'usuario' ? 'selected' : ''}>Usuário</option>
                        <option value="empresa" ${filtroDono == 'empresa' ? 'selected' : ''}>Empresa</option>
                    </select>
                </div>
                <div class="filtro">
                    <label for="filtro-arm">Armazenamento</label>
                    <select id="filtro-arm" name="arm">
                        <option value="">Todos</option>
                        <option value="geladeira" ${filtroArm == 'geladeira' ? 'selected' : ''}>Geladeira</option>
                        <option value="frigorifico" ${filtroArm == 'frigorifico' ? 'selected' : ''}>Frigorífico</option>
                    </select>
                </div>
                <div class="filtro">
                    <label for="filtro-cat">Categoria</label>
                    <select id="filtro-cat" name="cat">
                        <option value="">Todos</option>
                        <c:forEach items="${categorias}" var="cat">
                        <option value="${cat}" ${filtroCat == cat.name() ? 'selected' : ''}><c:out value="${cat}"/></option>
                        </c:forEach>
                    </select>
                </div>
                <div class="filtro">
                    <label for="filtro-status">Status</label>
                    <select id="filtro-status" name="status">
                        <option value="">Todos</option>
                        <c:forEach items="${statusProduto}" var="s">
                        <option value="${s}" ${filtroStatus == s.name() ? 'selected' : ''}><c:out value="${s}"/></option>
                        </c:forEach>
                    </select>
                </div>
                <div class="filtro">
                    <label for="filtro-val">Validade</label>
                    <select id="filtro-val" name="val">
                        <option value="">Todos</option>
                        <option value="vencidos" ${filtroVal == 'vencidos' ? 'selected' : ''}>Já vencidos</option>
                        <option value="7" ${filtroVal == '7' ? 'selected' : ''}>Próximos 7 dias</option>
                        <option value="30" ${filtroVal == '30' ? 'selected' : ''}>Próximos 30 dias</option>
                        <option value="longa" ${filtroVal == 'longa' ? 'selected' : ''}>Mais de 30 dias</option>
                    </select>
                </div>
                <div class="filtro filtro-grande filtro-destaque">
                    <label for="filtro-ordem">Ordenar por</label>
                    <select id="filtro-ordem" name="ord">
                        <option value="dias:asc" ${filtroOrd == 'dias:asc' ? 'selected' : ''}>Ordenar por validade mais próxima</option>
                        <option value="dias:desc" ${filtroOrd == 'dias:desc' ? 'selected' : ''}>Ordenar por validade mais distante</option>
                        <option value="dono:asc" ${filtroOrd == 'dono:asc' ? 'selected' : ''}>Ordenar por nome do dono de A a Z</option>
                        <option value="dono:desc" ${filtroOrd == 'dono:desc' ? 'selected' : ''}>Ordenar por nome do dono de Z a A</option>
                        <option value="nome:asc" ${filtroOrd == 'nome:asc' ? 'selected' : ''}>Ordenar por nome do produto de A a Z</option>
                        <option value="nome:desc" ${filtroOrd == 'nome:desc' ? 'selected' : ''}>Ordenar por nome do produto de Z a A</option>
                        <option value="novo:asc" ${filtroOrd == 'novo:asc' ? 'selected' : ''}>Ordenar da inserção mais antiga</option>
                        <option value="novo:desc" ${filtroOrd == 'novo:desc' ? 'selected' : ''}>Ordenar da inserção mais recente</option>
                    </select>
                </div>
                <button class="botao-limpar" type="button">Limpar filtros</button>
            </form>

            <!-- Tabela -->
            <div class="rolagem-tabela">
                <table class="tabela tabela-produto">
                    <thead>
                        <tr>
                            <th>Produto</th>
                            <th>Armazenamento</th>
                            <th>Dono</th>
                            <th>Qtd.</th>
                            <th>Validade</th>
                            <th>Status</th>
                            <th>Lote</th>
                            
                            <th></th>
                        </tr>
                    </thead>
    
                    <!-- Linhas da tabela (uma por registro) -->
                    <tbody>
                        <c:forEach items="${linhas}" var="l">
                            <c:set var="ehGeladeira" value="${l.tipoArmazenamento == 'geladeira'}"/>
                            <c:set var="armaz" value="${ehGeladeira ? 'Geladeira' : 'Frigorífico'}"/>
                            <tr>
                                <td><div class="titulo-cards"><c:out value="${l.produto.nome}"/></div>
                                    <div class="texto-pequeno-cards"><c:out value="${l.produto.marca} · ${l.produto.categoria}"/></div></td>
                                <td><span class="etiqueta-plano">${armaz}</span>
                                    <div class="texto-pequeno-cards"><c:out value="${l.armazenamento}"/></div></td>
                                <td><div class="titulo-cards"><c:out value="${l.dono}"/></div>
                                    <div class="texto-pequeno-cards">${l.donoTipo == 'usuario' ? 'Usuário' : 'Empresa'}</div></td>
                                <td>${l.quantidade} <c:out value="${l.produto.unidadeMedida}"/></td>
                                <td><div>${l.dtValidade}</div>
                                    <div class="texto-pequeno-cards">
                                        <c:choose>
                                            <c:when test="${l.dias < 0}">venceu há ${-l.dias} dias</c:when>
                                            <c:when test="${l.dias == 0}">vence hoje</c:when>
                                            <c:otherwise>vence em ${l.dias} dias</c:otherwise>
                                        </c:choose>
                                    </div></td>
                                <td><span class="etiqueta-plano"><c:out value="${l.status}"/></span></td>
                                <td>${l.lote}</td>
                                <td class="botoes-tabela">
                                    <button class="botao-icone" type="button" aria-label="Visualizar"
                                            data-armazenamento="${l.tipoArmazenamento}" data-nome="<c:out value='${l.produto.nome}'/>" data-categoria="<c:out value='${l.produto.categoria}'/>"
                                            data-marca="<c:out value='${l.produto.marca}'/>" data-descricao="<c:out value='${l.produto.descricao}'/>" data-unidade="<c:out value='${l.produto.unidadeMedida}'/>"
                                            data-preco="<c:out value='${l.produto.preco}'/>" data-local="<c:out value='${armaz} ${l.armazenamento} · ${l.dono}'/>"
                                            data-quantidade="${l.quantidade}" data-validade="${l.dtValidade}" data-lote="${l.lote}">
                                        <img src="${ctx}/assets/icones/icone-olho-aberto.svg" alt="">
                                    </button>
                                    <button class="botao-icone" type="button" aria-label="Editar"
                                            data-armazenamento="${l.tipoArmazenamento}" data-id-produto-geladeira="${ehGeladeira ? l.idItem : ''}" data-id-produto-frigorifico="${ehGeladeira ? '' : l.idItem}"
                                            data-nome-produto="<c:out value='${l.produto.nome}'/>" data-categoria="<c:out value='${l.produto.categoria}'/>" data-marca="<c:out value='${l.produto.marca}'/>"
                                            data-descricao="<c:out value='${l.produto.descricao}'/>" data-unidade-medida="<c:out value='${l.produto.unidadeMedida}'/>" data-preco="<c:out value='${l.produto.preco}'/>"
                                            data-id-geladeira="${ehGeladeira ? l.idArmazenamento : ''}" data-id-frigorifico="${ehGeladeira ? '' : l.idArmazenamento}"
                                            data-quantidade="${l.quantidade}" data-dt-validade="${l.dtValidade}" data-lote="${l.lote}">
                                        <img src="${ctx}/assets/icones/icone-caneta.svg" alt="">
                                    </button>
                                    <button class="botao-icone" type="button" aria-label="Excluir"
                                            data-armazenamento="${l.tipoArmazenamento}" data-id-produto-geladeira="${ehGeladeira ? l.idItem : ''}" data-id-produto-frigorifico="${ehGeladeira ? '' : l.idItem}">
                                        <img src="${ctx}/assets/icones/icone-lixera.svg" alt="">
                                    </button>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty linhas}">
                            <tr><td colspan="8">Nenhum registro encontrado com esses filtros.</td></tr>
                        </c:if>
                    </tbody>
                </table>
            </div>

            <!-- Rodapé com contagem e paginação -->
            <%@ include file="rodape-tabela.jspf" %>
        </div>
    </div>

    <!-- ========== POP-UPS ========== -->

    <!-- Adicionar produto -->
    <div class="popup-fundo" id="popup-adicionar">
        <aside class="gaveta gaveta-larga" role="dialog" aria-modal="true" aria-labelledby="popup-adicionar-titulo">
            <header class="gaveta-topo">
                <div>
                    <h2 class="popup-titulo" id="popup-adicionar-titulo">Adicionar produto</h2>
                    <p class="popup-texto">Dados do produto e do item em estoque.</p>
                </div>
                <button class="botao-fechar" type="button" aria-label="Fechar" data-fechar>
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
                </button>
            </header>
            <form class="gaveta-form" method="post" action="${ctx}/admin/produtos">
                <input type="hidden" name="action" value="inserir">
                <input type="hidden" name="voltar" value="<c:out value='${pageContext.request.queryString}'/>">
                <div class="gaveta-corpo gaveta-corpo-duplo">
                    <div class="gaveta-coluna">
                        <p class="gaveta-secao">Informações do produto</p>
                        <div class="filtro">
                            <label for="adicionar-nome">Nome <span class="obrigatorio">*</span></label>
                            <input id="adicionar-nome" name="nomeProduto" maxlength="20" type="text" placeholder="Ex.: Arroz Tipo 1" required>
                        </div>
                        <div class="gaveta-linha">
                            <div class="filtro">
                                <label for="adicionar-categoria">Categoria <span class="obrigatorio">*</span></label>
                                <select id="adicionar-categoria" name="categoria" required>
                                    <option value="">Selecione</option>
                                    <c:forEach items="${categorias}" var="cat">
                                        <option value="${cat}"><c:out value="${cat}"/></option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div class="filtro">
                                <label for="adicionar-marca">Marca <span class="obrigatorio">*</span></label>
                                <input id="adicionar-marca" name="marca" type="text" placeholder="Ex.: Camil" required maxlength="20">
                            </div>
                        </div>
                        <div class="filtro">
                            <label for="adicionar-descricao">Descrição</label>
                            <textarea id="adicionar-descricao" name="descricao" rows="3" placeholder="Detalhes do produto (opcional)"></textarea>
                        </div>
                        <div class="gaveta-linha">
                            <div class="filtro">
                                <label for="adicionar-unidade">Unidade de medida <span class="obrigatorio">*</span></label>
                                <select id="adicionar-unidade" name="unidadeMedida" required>
                                    <option value="">Selecione</option>
                                    <c:forEach items="${unidades}" var="un">
                                        <option value="${un}">${un}</option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div class="filtro">
                                <label for="adicionar-preco">Preço (R$) <span class="obrigatorio">*</span></label>
                                <input id="adicionar-preco" name="preco" type="text" placeholder="0,00" required inputmode="decimal">
                            </div>
                        </div>
                    </div>
                    <div class="gaveta-coluna">
                        <p class="gaveta-secao">Onde está armazenado</p>
                        <p class="popup-texto">Escolha se o produto está em uma geladeira ou em um frigorífico.</p>
                        <div class="alternador">
                            <label class="alternador-opcao">
                                <input type="radio" name="armazenamento" value="geladeira" checked>
                                <span>Geladeira</span>
                            </label>
                            <label class="alternador-opcao">
                                <input type="radio" name="armazenamento" value="frigorifico">
                                <span>Frigorífico</span>
                            </label>
                        </div>
                        <div class="filtro" data-opcao="geladeira">
                            <label for="adicionar-geladeira">Geladeira <span class="obrigatorio">*</span></label>
                            <select id="adicionar-geladeira" name="idGeladeira" required>
                                <option value="">Selecione</option>
                                <c:forEach items="${geladeiras}" var="g">
                                    <option value="${g.idGeladeira}"><c:out value="${g.nome} · ${g.usuario.nome} ${g.usuario.sobrenome}"/></option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="filtro" data-opcao="frigorifico" hidden>
                            <label for="adicionar-frigorifico">Frigorífico <span class="obrigatorio">*</span></label>
                            <select id="adicionar-frigorifico" name="idFrigorifico" required disabled>
                                <option value="">Selecione</option>
                                <c:forEach items="${frigorificos}" var="f">
                                    <option value="${f.idFrigorifico}"><c:out value="${f.nome} · ${f.empresa.nome}"/></option>
                                </c:forEach>
                            </select>
                        </div>
                        <hr class="gaveta-divisor">
                        <p class="gaveta-secao">Item em estoque</p>
                        <div class="gaveta-linha">
                            <div class="filtro">
                                <label for="adicionar-quantidade">Quantidade <span class="obrigatorio">*</span></label>
                                <input id="adicionar-quantidade" name="quantidade" type="number" placeholder="0" required min="1" step="1">
                            </div>
                            <div class="filtro">
                                <label for="adicionar-validade">Data de validade <span class="obrigatorio">*</span></label>
                                <input id="adicionar-validade" name="dtValidade" type="date" required>
                            </div>
                        </div>
                        <div class="filtro">
                            <label for="adicionar-lote">Lote</label>
                            <input id="adicionar-lote" name="lote" type="number" min="0" placeholder="Número do lote">
                        </div>
                    </div>
                </div>
                <footer class="gaveta-rodape">
                    <button class="botao-secundario" type="button" data-fechar>Cancelar</button>
                    <button class="botao-principal" type="submit">Adicionar produto</button>
                </footer>
            </form>
        </aside>
    </div>

    <!-- Editar produto -->
    <div class="popup-fundo" id="popup-editar">
        <aside class="gaveta gaveta-larga" role="dialog" aria-modal="true" aria-labelledby="popup-editar-titulo">
            <header class="gaveta-topo">
                <div>
                    <h2 class="popup-titulo" id="popup-editar-titulo">Editar produto</h2>
                    <p class="popup-texto">Dados do produto e do item em estoque.</p>
                </div>
                <button class="botao-fechar" type="button" aria-label="Fechar" data-fechar>
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
                </button>
            </header>
            <form class="gaveta-form" method="post" action="${ctx}/admin/produtos">
                <input type="hidden" name="action" value="alterar">
                <input type="hidden" name="voltar" value="<c:out value='${pageContext.request.queryString}'/>">
                <input type="hidden" name="armazenamento" value="">
                <input type="hidden" name="idProdutoGeladeira" value="">
                <input type="hidden" name="idProdutoFrigorifico" value="">
                <div class="gaveta-corpo gaveta-corpo-duplo">
                    <div class="gaveta-coluna">
                        <p class="gaveta-secao">Informações do produto</p>
                        <div class="filtro">
                            <label for="editar-nome">Nome <span class="obrigatorio">*</span></label>
                            <input id="editar-nome" name="nomeProduto" maxlength="20" type="text" placeholder="Ex.: Arroz Tipo 1" required>
                        </div>
                        <div class="gaveta-linha">
                            <div class="filtro">
                                <label for="editar-categoria">Categoria <span class="obrigatorio">*</span></label>
                                <select id="editar-categoria" name="categoria" required>
                                    <option value="">Selecione</option>
                                    <c:forEach items="${categorias}" var="cat">
                                        <option value="${cat}"><c:out value="${cat}"/></option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div class="filtro">
                                <label for="editar-marca">Marca <span class="obrigatorio">*</span></label>
                                <input id="editar-marca" name="marca" type="text" placeholder="Ex.: Camil" required maxlength="20">
                            </div>
                        </div>
                        <div class="filtro">
                            <label for="editar-descricao">Descrição</label>
                            <textarea id="editar-descricao" name="descricao" rows="3" placeholder="Detalhes do produto (opcional)"></textarea>
                        </div>
                        <div class="gaveta-linha">
                            <div class="filtro">
                                <label for="editar-unidade">Unidade de medida <span class="obrigatorio">*</span></label>
                                <select id="editar-unidade" name="unidadeMedida" required>
                                    <option value="">Selecione</option>
                                    <c:forEach items="${unidades}" var="un">
                                        <option value="${un}">${un}</option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div class="filtro">
                                <label for="editar-preco">Preço (R$) <span class="obrigatorio">*</span></label>
                                <input id="editar-preco" name="preco" type="text" placeholder="0,00" required inputmode="decimal">
                            </div>
                        </div>
                    </div>
                    <div class="gaveta-coluna">
                        <p class="gaveta-secao">Onde está armazenado</p>
                        <p class="popup-texto">Escolha se o produto está em uma geladeira ou em um frigorífico.</p>
                        <div class="alternador">
                            <label class="alternador-opcao">
                                <input type="radio" name="armazenamento" value="geladeira" checked disabled>
                                <span>Geladeira</span>
                            </label>
                            <label class="alternador-opcao">
                                <input type="radio" name="armazenamento" value="frigorifico" disabled>
                                <span>Frigorífico</span>
                            </label>
                        </div>
                        <div class="filtro" data-opcao="geladeira">
                            <label for="editar-geladeira">Geladeira <span class="obrigatorio">*</span></label>
                            <select id="editar-geladeira" name="idGeladeira" required>
                                <option value="">Selecione</option>
                                <c:forEach items="${geladeiras}" var="g">
                                    <option value="${g.idGeladeira}"><c:out value="${g.nome} · ${g.usuario.nome} ${g.usuario.sobrenome}"/></option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="filtro" data-opcao="frigorifico" hidden>
                            <label for="editar-frigorifico">Frigorífico <span class="obrigatorio">*</span></label>
                            <select id="editar-frigorifico" name="idFrigorifico" required disabled>
                                <option value="">Selecione</option>
                                <c:forEach items="${frigorificos}" var="f">
                                    <option value="${f.idFrigorifico}"><c:out value="${f.nome} · ${f.empresa.nome}"/></option>
                                </c:forEach>
                            </select>
                        </div>
                        <hr class="gaveta-divisor">
                        <p class="gaveta-secao">Item em estoque</p>
                        <div class="gaveta-linha">
                            <div class="filtro">
                                <label for="editar-quantidade">Quantidade <span class="obrigatorio">*</span></label>
                                <input id="editar-quantidade" name="quantidade" type="number" placeholder="0" required min="1" step="1">
                            </div>
                            <div class="filtro">
                                <label for="editar-validade">Data de validade <span class="obrigatorio">*</span></label>
                                <input id="editar-validade" name="dtValidade" type="date" required>
                            </div>
                        </div>
                        <div class="filtro">
                            <label for="editar-lote">Lote</label>
                            <input id="editar-lote" name="lote" type="number" min="0" placeholder="Número do lote">
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

    <!-- Detalhes do produto -->
    <div class="popup-fundo" id="popup-detalhes">
        <aside class="gaveta gaveta-larga" role="dialog" aria-modal="true" aria-labelledby="popup-detalhes-titulo">
            <header class="gaveta-topo">
                <div>
                    <h2 class="popup-titulo" id="popup-detalhes-titulo">Detalhes do produto</h2>
                    <p class="popup-texto">Dados do produto e do item em estoque.</p>
                </div>
                <button class="botao-fechar" type="button" aria-label="Fechar" data-fechar>
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
                </button>
            </header>
            <div class="gaveta-form">
                <div class="gaveta-corpo gaveta-corpo-duplo">
                    <div class="gaveta-coluna">
                        <p class="gaveta-secao">Informações do produto</p>
                        <div class="filtro">
                            <label for="detalhes-nome">Nome <span class="obrigatorio">*</span></label>
                            <input id="detalhes-nome" name="nome" type="text" readonly>
                        </div>
                        <div class="gaveta-linha">
                            <div class="filtro">
                                <label for="detalhes-categoria">Categoria <span class="obrigatorio">*</span></label>
                                <input id="detalhes-categoria" name="categoria" type="text" readonly>
                            </div>
                            <div class="filtro">
                                <label for="detalhes-marca">Marca</label>
                                <input id="detalhes-marca" name="marca" type="text" readonly>
                            </div>
                        </div>
                        <div class="filtro">
                            <label for="detalhes-descricao">Descrição</label>
                            <textarea id="detalhes-descricao" name="descricao" rows="3" readonly></textarea>
                        </div>
                        <div class="gaveta-linha">
                            <div class="filtro">
                                <label for="detalhes-unidade">Unidade de medida <span class="obrigatorio">*</span></label>
                                <input id="detalhes-unidade" name="unidade" type="text" readonly>
                            </div>
                            <div class="filtro">
                                <label for="detalhes-preco">Preço (R$) <span class="obrigatorio">*</span></label>
                                <input id="detalhes-preco" name="preco" type="text" readonly>
                            </div>
                        </div>
                    </div>
                    <div class="gaveta-coluna">
                        <p class="gaveta-secao">Onde está armazenado</p>
                        <p class="popup-texto">Escolha se o produto está em uma geladeira ou em um frigorífico.</p>
                        <div class="alternador">
                            <label class="alternador-opcao">
                                <input type="radio" name="armazenamento" value="geladeira" checked disabled>
                                <span>Geladeira</span>
                            </label>
                            <label class="alternador-opcao">
                                <input type="radio" name="armazenamento" value="frigorifico" disabled>
                                <span>Frigorífico</span>
                            </label>
                        </div>
                        <div class="filtro">
                            <label for="detalhes-local">Geladeira <span class="obrigatorio">*</span></label>
                            <input id="detalhes-local" name="local" type="text" readonly>
                        </div>
                        <hr class="gaveta-divisor">
                        <p class="gaveta-secao">Item em estoque</p>
                        <div class="gaveta-linha">
                            <div class="filtro">
                                <label for="detalhes-quantidade">Quantidade <span class="obrigatorio">*</span></label>
                                <input id="detalhes-quantidade" name="quantidade" type="text" readonly>
                            </div>
                            <div class="filtro">
                                <label for="detalhes-validade">Data de validade <span class="obrigatorio">*</span></label>
                                <input id="detalhes-validade" name="validade" type="text" readonly>
                            </div>
                        </div>
                        <div class="filtro">
                            <label for="detalhes-lote">Lote</label>
                            <input id="detalhes-lote" name="lote" type="text" readonly>
                        </div>
                    </div>
                </div>
                <footer class="gaveta-rodape">
                    <button class="botao-secundario" type="button" data-fechar>Fechar</button>
                </footer>
            </div>
        </aside>
    </div>

    <!-- Excluir produto -->
    <div class="popup-fundo popup-fundo-centro" id="popup-excluir">
        <form class="modal-exclusao" method="post" action="${ctx}/admin/produtos" role="alertdialog" aria-modal="true" aria-labelledby="popup-excluir-titulo">
            <input type="hidden" name="action" value="deletar">
            <input type="hidden" name="voltar" value="<c:out value='${pageContext.request.queryString}'/>">
            <input type="hidden" name="armazenamento" value="">
            <input type="hidden" name="idProdutoGeladeira" value="">
            <input type="hidden" name="idProdutoFrigorifico" value="">
            <div class="modal-icone">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="3 6 5 6 21 6"></polyline><path d="M19 6l-1 14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2L5 6"></path><path d="M10 11v6"></path><path d="M14 11v6"></path><path d="M9 6V4a1 1 0 0 1 1-1h4a1 1 0 0 1 1 1v2"></path></svg>
            </div>
            <h2 class="popup-titulo" id="popup-excluir-titulo">Excluir "<span id="nome-excluir"></span>"?</h2>
            <p class="popup-texto">O item será removido do armazenamento. Esta ação não poderá ser desfeita.</p>
            <div class="modal-botoes">
                <button class="botao-secundario" type="button" data-fechar>Cancelar</button>
                <button class="botao-principal botao-perigo" type="submit">Excluir produto</button>
            </div>
        </form>
    </div>

<%@ include file="rodape.jspf" %>
