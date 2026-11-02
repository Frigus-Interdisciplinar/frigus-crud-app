<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="menuAtivo" value="geladeiras" scope="request"/>
<c:set var="tituloPagina" value="Geladeiras" scope="request"/>
<c:set var="rotuloLista" value="geladeiras" scope="request"/>
<%@ include file="cabecalho.jspf" %>

    <div class="conteudo" id="conteudo">
        <!-- Título e botão de adicionar -->
        <div class="topo-pagina">
            <div class="page-header">
                <h1>Geladeira</h1>
                <p class="subtitulo-pagina">Geladeiras de todos os usuários, com ocupação e validades.</p>
            </div>

            <button class="botao-principal" type="button">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                    <line x1="12" y1="5" x2="12" y2="19"></line>
                    <line x1="5" y1="12" x2="19" y2="12"></line>
                </svg>
                Adicionar geladeira
            </button>
        </div>

        <%@ include file="mensagens.jspf" %>

        <!-- Caixa da tabela -->
        <div class="caixa-tabela">
            <!-- Filtros -->
            <form class="filtros" method="get" action="${ctx}/admin/geladeiras">
                <div class="filtro">
                    <label for="filtro-nome">Nome</label>
                    <input id="filtro-nome" type="text" name="nome" value="<c:out value='${filtroNome}'/>" placeholder="Ex.: Cozinha">
                </div>
                <div class="filtro">
                    <label for="filtro-tipo">Tipo</label>
                    <select id="filtro-tipo" name="tipo">
                        <option value="">Todos</option>
                        <c:forEach items="${tipos}" var="t">
                        <option value="${t}" ${filtroTipo == t ? 'selected' : ''}><c:out value="${t}"/></option>
                        </c:forEach>
                    </select>
                </div>
                <div class="filtro">
                    <label for="filtro-usuario">Usuário (nome ou e-mail)</label>
                    <input id="filtro-usuario" type="text" name="usuario" value="<c:out value='${filtroUsuario}'/>" placeholder="Buscar por usuário…">
                </div>
                <div class="filtro">
                    <label for="filtro-status">Ocupação</label>
                    <select id="filtro-status" name="status">
                        <option value="">Todos</option>
                        <c:forEach items="${statusArmazenamento}" var="s">
                        <option value="${s}" ${filtroStatus == s.name() ? 'selected' : ''}><c:out value="${s}"/></option>
                        </c:forEach>
                    </select>
                </div>
                <div class="filtro">
                    <label for="filtro-alerta">Alertas</label>
                    <select id="filtro-alerta" name="alerta">
                        <option value="">Todos</option>
                        <option value="vencidos" ${filtroAlerta == 'vencidos' ? 'selected' : ''}>Com produtos vencidos</option>
                        <option value="avencer" ${filtroAlerta == 'avencer' ? 'selected' : ''}>Com produtos a vencer</option>
                        <option value="sem" ${filtroAlerta == 'sem' ? 'selected' : ''}>Sem alertas</option>
                    </select>
                </div>
                <div class="filtro filtro-grande filtro-destaque">
                    <label for="filtro-ordem">Ordenar por</label>
                    <select id="filtro-ordem" name="ord">
                        <option value="dono:asc" ${filtroOrd == 'dono:asc' ? 'selected' : ''}>Ordenar por nome do usuário de A a Z</option>
                        <option value="dono:desc" ${filtroOrd == 'dono:desc' ? 'selected' : ''}>Ordenar por nome do usuário de Z a A</option>
                        <option value="nome:asc" ${filtroOrd == 'nome:asc' ? 'selected' : ''}>Ordenar por nome da geladeira de A a Z</option>
                        <option value="nome:desc" ${filtroOrd == 'nome:desc' ? 'selected' : ''}>Ordenar por nome da geladeira de Z a A</option>
                        <option value="novo:asc" ${filtroOrd == 'novo:asc' ? 'selected' : ''}>Ordenar da inserção mais antiga</option>
                        <option value="novo:desc" ${filtroOrd == 'novo:desc' ? 'selected' : ''}>Ordenar da inserção mais recente</option>
                    </select>
                </div>
                <button class="botao-limpar" type="button">Limpar filtros</button>
            </form>

            <!-- Tabela -->
            <table class="tabela tabela-geladeira">
                <thead>
                    <tr>
                        <th>Geladeira</th>
                        <th>Usuário</th>
                        <th>Ocupação</th>
                        <th>Status</th>
                        <th>Produtos</th>
                        <th>Alertas</th>
                        <th></th>
                    </tr>
                </thead>

                <!-- Linhas da tabela (uma por registro) -->
                <tbody>
                    <c:forEach items="${geladeiras}" var="g">
                        <c:set var="numeros" value="${ocupacao[g.idGeladeira]}"/>
                        <c:set var="vencidos" value="${empty numeros ? 0 : numeros[2]}"/>
                        <c:set var="aVencer" value="${empty numeros ? 0 : numeros[3]}"/>
                        <c:set var="dono" value="${g.usuario.nome} ${g.usuario.sobrenome} · ${g.usuario.email}"/>
                        <tr>
                            <td>
                                <div class="titulo-cards"><c:out value="${g.nome}"/></div>
                                <div class="texto-pequeno-cards"><c:out value="${g.tipo}"/></div>
                            </td>
                            <td>
                                <div class="titulo-cards"><c:out value="${g.usuario.nome} ${g.usuario.sobrenome}"/></div>
                                <div class="texto-pequeno-cards"><c:out value="${g.usuario.email}"/></div>
                            </td>
                            <td>
                                <div>${empty numeros ? 0 : numeros[1]} de ${capacidade[g.idGeladeira] < 0 ? '∞' : capacidade[g.idGeladeira]}</div>
                                <div class="texto-pequeno-cards">itens</div>
                            </td>
                            <td><span class="etiqueta-plano"><c:out value="${g.status}"/></span></td>
                            <td>${empty numeros ? 0 : numeros[0]}</td>
                            <td>
                                <c:choose>
                                    <c:when test="${vencidos + aVencer > 0}"><span class="alerta">${vencidos} vencidos · ${aVencer} a vencer</span></c:when>
                                    <c:otherwise><span class="sem-alerta">Sem alertas</span></c:otherwise>
                                </c:choose>
                            </td>
                            <td class="botoes-tabela">
                                <button class="botao-icone" type="button" aria-label="Visualizar"
                                        data-usuario="<c:out value='${dono}'/>" data-nome="<c:out value='${g.nome}'/>" data-tipo="<c:out value='${g.tipo}'/>">
                                    <img src="${ctx}/assets/icones/icone-olho-aberto.svg" alt="">
                                </button>
                                <button class="botao-icone" type="button" aria-label="Editar"
                                        data-id-geladeira="${g.idGeladeira}" data-cpf="${g.usuario.cpf}" data-nome="<c:out value='${g.nome}'/>" data-tipo="<c:out value='${g.tipo}'/>">
                                    <img src="${ctx}/assets/icones/icone-caneta.svg" alt="">
                                </button>
                                <button class="botao-icone" type="button" aria-label="Excluir" data-id-geladeira="${g.idGeladeira}">
                                    <img src="${ctx}/assets/icones/icone-lixera.svg" alt="">
                                </button>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty geladeiras}">
                        <tr><td colspan="7">Nenhum registro encontrado com esses filtros.</td></tr>
                    </c:if>
                </tbody>
            </table>

            <!-- Rodapé com contagem e paginação -->
            <%@ include file="rodape-tabela.jspf" %>
        </div>
    </div>

    <!-- ========== POP-UPS ========== -->

    <!-- Adicionar geladeira -->
    <div class="popup-fundo" id="popup-adicionar">
        <aside class="gaveta" role="dialog" aria-modal="true" aria-labelledby="popup-adicionar-titulo">
            <header class="gaveta-topo">
                <div>
                    <h2 class="popup-titulo" id="popup-adicionar-titulo">Adicionar geladeira</h2>
                    <p class="popup-texto">Informe o usuário dono e os dados da geladeira.</p>
                </div>
                <button class="botao-fechar" type="button" aria-label="Fechar" data-fechar>
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
                </button>
            </header>
            <form class="gaveta-form" method="post" action="${ctx}/admin/geladeiras">
                <input type="hidden" name="action" value="inserir">
                <input type="hidden" name="voltar" value="<c:out value='${pageContext.request.queryString}'/>">
                <div class="gaveta-corpo">
                    <p class="gaveta-secao">Informações da geladeira</p>
                    <div class="filtro">
                        <label for="adicionar-usuario">Usuário dono <span class="obrigatorio">*</span></label>
                        <select id="adicionar-usuario" name="cpf" required>
                            <option value="">Selecione</option>
                            <c:forEach items="${usuarios}" var="u">
                                <option value="${u.cpf}"><c:out value="${u.nome} ${u.sobrenome} · ${u.email}"/></option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="filtro">
                        <label for="adicionar-nome">Nome <span class="obrigatorio">*</span></label>
                        <input id="adicionar-nome" name="nome" type="text" placeholder="Ex.: Cozinha, Garagem" required maxlength="50">
                    </div>
                    <div class="filtro">
                        <label for="adicionar-tipo">Tipo <span class="obrigatorio">*</span></label>
                        <input id="adicionar-tipo" name="tipo" type="text" placeholder="Ex.: Duplex frost free" required maxlength="20">
                    </div>
                </div>
                <footer class="gaveta-rodape">
                    <button class="botao-secundario" type="button" data-fechar>Cancelar</button>
                    <button class="botao-principal" type="submit">Adicionar geladeira</button>
                </footer>
            </form>
        </aside>
    </div>

    <!-- Editar geladeira -->
    <div class="popup-fundo" id="popup-editar">
        <aside class="gaveta" role="dialog" aria-modal="true" aria-labelledby="popup-editar-titulo">
            <header class="gaveta-topo">
                <div>
                    <h2 class="popup-titulo" id="popup-editar-titulo">Editar geladeira</h2>
                    <p class="popup-texto">Informe o usuário dono e os dados da geladeira.</p>
                </div>
                <button class="botao-fechar" type="button" aria-label="Fechar" data-fechar>
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
                </button>
            </header>
            <form class="gaveta-form" method="post" action="${ctx}/admin/geladeiras">
                <input type="hidden" name="action" value="alterar">
                <input type="hidden" name="voltar" value="<c:out value='${pageContext.request.queryString}'/>">
                <input type="hidden" name="idGeladeira" value="">
                <div class="gaveta-corpo">
                    <p class="gaveta-secao">Informações da geladeira</p>
                    <div class="filtro">
                        <label for="editar-usuario">Usuário dono <span class="obrigatorio">*</span></label>
                        <select id="editar-usuario" name="cpf" required>
                            <option value="">Selecione</option>
                            <c:forEach items="${usuarios}" var="u">
                                <option value="${u.cpf}"><c:out value="${u.nome} ${u.sobrenome} · ${u.email}"/></option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="filtro">
                        <label for="editar-nome">Nome <span class="obrigatorio">*</span></label>
                        <input id="editar-nome" name="nome" type="text" required>
                    </div>
                    <div class="filtro">
                        <label for="editar-tipo">Tipo <span class="obrigatorio">*</span></label>
                        <input id="editar-tipo" name="tipo" type="text" required>
                    </div>
                </div>
                <footer class="gaveta-rodape">
                    <button class="botao-secundario" type="button" data-fechar>Cancelar</button>
                    <button class="botao-principal" type="submit">Salvar alterações</button>
                </footer>
            </form>
        </aside>
    </div>

    <!-- Detalhes da geladeira -->
    <div class="popup-fundo" id="popup-detalhes">
        <aside class="gaveta" role="dialog" aria-modal="true" aria-labelledby="popup-detalhes-titulo">
            <header class="gaveta-topo">
                <div>
                    <h2 class="popup-titulo" id="popup-detalhes-titulo">Detalhes da geladeira</h2>
                    <p class="popup-texto">Informe o usuário dono e os dados da geladeira.</p>
                </div>
                <button class="botao-fechar" type="button" aria-label="Fechar" data-fechar>
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
                </button>
            </header>
            <div class="gaveta-form">
                <div class="gaveta-corpo">
                    <p class="gaveta-secao">Informações da geladeira</p>
                    <div class="filtro">
                        <label for="detalhes-usuario">Usuário dono <span class="obrigatorio">*</span></label>
                        <input id="detalhes-usuario" name="usuario" type="text" readonly>
                    </div>
                    <div class="filtro">
                        <label for="detalhes-nome">Nome <span class="obrigatorio">*</span></label>
                        <input id="detalhes-nome" name="nome" type="text" readonly>
                    </div>
                    <div class="filtro">
                        <label for="detalhes-tipo">Tipo <span class="obrigatorio">*</span></label>
                        <input id="detalhes-tipo" name="tipo" type="text" readonly>
                    </div>
                </div>
                <footer class="gaveta-rodape">
                    <button class="botao-secundario" type="button" data-fechar>Fechar</button>
                </footer>
            </div>
        </aside>
    </div>

    <!-- Excluir geladeira -->
    <div class="popup-fundo popup-fundo-centro" id="popup-excluir">
        <form class="modal-exclusao" method="post" action="${ctx}/admin/geladeiras" role="alertdialog" aria-modal="true" aria-labelledby="popup-excluir-titulo">
            <input type="hidden" name="action" value="deletar">
            <input type="hidden" name="voltar" value="<c:out value='${pageContext.request.queryString}'/>">
            <input type="hidden" name="idGeladeira" value="">
            <div class="modal-icone">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="3 6 5 6 21 6"></polyline><path d="M19 6l-1 14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2L5 6"></path><path d="M10 11v6"></path><path d="M14 11v6"></path><path d="M9 6V4a1 1 0 0 1 1-1h4a1 1 0 0 1 1 1v2"></path></svg>
            </div>
            <h2 class="popup-titulo" id="popup-excluir-titulo">Excluir "<span id="nome-excluir"></span>"?</h2>
            <p class="popup-texto">Os produtos guardados nesta geladeira também serão removidos. Esta ação não poderá ser desfeita.</p>
            <div class="modal-botoes">
                <button class="botao-secundario" type="button" data-fechar>Cancelar</button>
                <button class="botao-principal botao-perigo" type="submit">Excluir geladeira</button>
            </div>
        </form>
    </div>

<%@ include file="rodape.jspf" %>
