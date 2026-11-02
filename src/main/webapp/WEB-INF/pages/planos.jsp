<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="menuAtivo" value="planos" scope="request"/>
<c:set var="tituloPagina" value="Planos" scope="request"/>
<c:set var="rotuloLista" value="assinaturas" scope="request"/>
<%@ include file="cabecalho.jspf" %>

    <div class="conteudo" id="conteudo">
        <!-- Título da página -->
        <div class="page-header">
                <h1>Planos</h1>
                <p class="subtitulo-pagina">Assinatura de cada usuário e de cada empresa.</p>
            </div>

        <%@ include file="mensagens.jspf" %>

        <!-- Caixa da tabela -->
        <div class="caixa-tabela">
            <!-- Filtros -->
            <form class="filtros" method="get" action="${ctx}/admin/planos">
                <div class="filtro">
                    <label for="filtro-dono">Dono (nome, CPF ou CNPJ)</label>
                    <input id="filtro-dono" type="text" name="dono" value="<c:out value='${filtroDono}'/>" placeholder="Nome, CPF ou CNPJ">
                </div>
                <div class="filtro">
                    <label for="filtro-plano">Plano</label>
                    <select id="filtro-plano" name="plano">
                        <option value="">Todos</option>
                        <c:forEach items="${planos}" var="p">
                        <option value="${p}" ${filtroPlano == p ? 'selected' : ''}>${p}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="filtro filtro-grande filtro-destaque">
                    <label for="filtro-ordem">Ordenar por</label>
                    <select id="filtro-ordem" name="ord">
                        <option value="dono:asc" ${filtroOrd == 'dono:asc' ? 'selected' : ''}>Ordenar de A a Z</option>
                        <option value="dono:desc" ${filtroOrd == 'dono:desc' ? 'selected' : ''}>Ordenar de Z a A</option>
                        <option value="novo:asc" ${filtroOrd == 'novo:asc' ? 'selected' : ''}>Ordenar da inserção mais antiga</option>
                        <option value="novo:desc" ${filtroOrd == 'novo:desc' ? 'selected' : ''}>Ordenar da inserção mais recente</option>
                    </select>
                </div>
                <button class="botao-limpar" type="button">Limpar filtros</button>
            </form>

            <!-- Tabela -->
            <table class="tabela tabela-plano">
                <thead>
                    <tr>
                        <th>Usuário</th>
                        <th>Empresa</th>
                        <th>Plano</th>
                        <th>Valor mensal</th>
                        
                        <th></th>
                    </tr>
                </thead>

                <!-- Linhas da tabela (uma por registro) -->
                <tbody>
                    <c:forEach items="${assinaturas}" var="a">
                        <c:set var="ehUsuario" value="${not empty a.usuario}"/>
                        <c:set var="dono" value="${ehUsuario ? a.usuario.nome.concat(' ').concat(a.usuario.sobrenome).concat(' · ').concat(a.usuario.email) : a.empresa.nome.concat(' · ').concat(a.empresa.email)}"/>
                        <tr>
                            <td><c:if test="${ehUsuario}"><div class="titulo-cards"><c:out value="${a.usuario.nome} ${a.usuario.sobrenome}"/></div>
                                <div class="texto-pequeno-cards"><c:out value="${a.usuario.email}"/></div></c:if><c:if test="${not ehUsuario}">—</c:if></td>
                            <td><c:if test="${not ehUsuario}"><div class="titulo-cards"><c:out value="${a.empresa.nome}"/></div>
                                <div class="texto-pequeno-cards"><c:out value="${a.empresa.email}"/></div></c:if><c:if test="${ehUsuario}">—</c:if></td>
                            <td><span class="etiqueta-plano">${a.tipoAssinatura}</span></td>
                            <td>R$ ${a.valor}</td>
                            <td class="botoes-tabela">
                                <button class="botao-icone" type="button" aria-label="Visualizar"
                                        data-dono-tipo="${ehUsuario ? 'usuario' : 'empresa'}" data-dono="<c:out value='${dono}'/>" data-plano="${a.tipoAssinatura} · R$ ${a.valor}">
                                    <img src="${ctx}/assets/icones/icone-olho-aberto.svg" alt="">
                                </button>
                                <button class="botao-icone" type="button" aria-label="Editar"
                                        data-id-assinatura="${a.idAssinatura}" data-dono-tipo="${ehUsuario ? 'usuario' : 'empresa'}"
                                        data-cpf="${ehUsuario ? a.usuario.cpf : ''}" data-cnpj="${ehUsuario ? '' : a.empresa.cnpj}" data-plano="${a.tipoAssinatura}">
                                    <img src="${ctx}/assets/icones/icone-caneta.svg" alt="">
                                </button>
                                <button class="botao-icone" type="button" aria-label="Excluir" data-id-assinatura="${a.idAssinatura}">
                                    <img src="${ctx}/assets/icones/icone-lixera.svg" alt="">
                                </button>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty assinaturas}">
                        <tr><td colspan="6">Nenhum registro encontrado com esses filtros.</td></tr>
                    </c:if>
                </tbody>
            </table>

            <!-- Rodapé com contagem e paginação -->
            <%@ include file="rodape-tabela.jspf" %>
        </div>
    </div>

    <!-- ========== POP-UPS ========== -->

    <!-- Editar assinatura -->
    <div class="popup-fundo" id="popup-editar">
        <aside class="gaveta" role="dialog" aria-modal="true" aria-labelledby="popup-editar-titulo">
            <header class="gaveta-topo">
                <div>
                    <h2 class="popup-titulo" id="popup-editar-titulo">Editar assinatura</h2>
                    <p class="popup-texto">Plano e início da assinatura deste usuário ou empresa.</p>
                </div>
                <button class="botao-fechar" type="button" aria-label="Fechar" data-fechar>
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
                </button>
            </header>
            <form class="gaveta-form" method="post" action="${ctx}/admin/planos">
                <input type="hidden" name="action" value="alterar">
                <input type="hidden" name="voltar" value="<c:out value='${pageContext.request.queryString}'/>">
                <input type="hidden" name="idAssinatura" value="">
                <div class="gaveta-corpo">
                    <p class="gaveta-secao">Dono da assinatura</p>
                    <p class="popup-texto">Escolha se o dono é um usuário ou uma empresa.</p>
                    <div class="alternador">
                        <label class="alternador-opcao">
                            <input type="radio" name="donoTipo" value="usuario" checked>
                            <span>Usuário</span>
                        </label>
                        <label class="alternador-opcao">
                            <input type="radio" name="donoTipo" value="empresa">
                            <span>Empresa</span>
                        </label>
                    </div>
                    <div class="filtro" data-opcao="usuario">
                        <label for="editar-usuario">Usuário <span class="obrigatorio">*</span></label>
                        <select id="editar-usuario" name="cpf" required>
                            <option value="">Selecione</option>
                            <c:forEach items="${usuarios}" var="u">
                                <option value="${u.cpf}"><c:out value="${u.nome} ${u.sobrenome} · ${u.email}"/></option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="filtro" data-opcao="empresa" hidden>
                        <label for="editar-empresa">Empresa <span class="obrigatorio">*</span></label>
                        <select id="editar-empresa" name="cnpj" required disabled>
                            <option value="">Selecione</option>
                            <c:forEach items="${empresas}" var="e">
                                <option value="${e.cnpj}"><c:out value="${e.nome} · ${e.email}"/></option>
                            </c:forEach>
                        </select>
                    </div>
                    <hr class="gaveta-divisor">
                    <p class="gaveta-secao">Assinatura</p>
                    <div class="filtro">
                        <label for="editar-plano">Plano <span class="obrigatorio">*</span></label>
                        <select id="editar-plano" name="plano" required>
                            <option value="">Selecione</option>
                            <c:forEach items="${planos}" var="p">
                                <option value="${p}">${p}</option>
                            </c:forEach>
                        </select>
                    </div>
                </div>
                <footer class="gaveta-rodape">
                    <button class="botao-secundario" type="button" data-fechar>Cancelar</button>
                    <button class="botao-principal" type="submit">Salvar alterações</button>
                </footer>
            </form>
        </aside>
    </div>

    <!-- Detalhes da assinatura -->
    <div class="popup-fundo" id="popup-detalhes">
        <aside class="gaveta" role="dialog" aria-modal="true" aria-labelledby="popup-detalhes-titulo">
            <header class="gaveta-topo">
                <div>
                    <h2 class="popup-titulo" id="popup-detalhes-titulo">Detalhes da assinatura</h2>
                    <p class="popup-texto">Plano e início da assinatura deste usuário ou empresa.</p>
                </div>
                <button class="botao-fechar" type="button" aria-label="Fechar" data-fechar>
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
                </button>
            </header>
            <div class="gaveta-form">
                <div class="gaveta-corpo">
                    <p class="gaveta-secao">Dono da assinatura</p>
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
                    <p class="gaveta-secao">Assinatura</p>
                    <div class="filtro">
                        <label for="detalhes-plano">Plano <span class="obrigatorio">*</span></label>
                        <input id="detalhes-plano" name="plano" type="text" readonly>
                    </div>
                </div>
                <footer class="gaveta-rodape">
                    <button class="botao-secundario" type="button" data-fechar>Fechar</button>
                </footer>
            </div>
        </aside>
    </div>

    <!-- Excluir assinatura -->
    <div class="popup-fundo popup-fundo-centro" id="popup-excluir">
        <form class="modal-exclusao" method="post" action="${ctx}/admin/planos" role="alertdialog" aria-modal="true" aria-labelledby="popup-excluir-titulo">
            <input type="hidden" name="action" value="deletar">
            <input type="hidden" name="voltar" value="<c:out value='${pageContext.request.queryString}'/>">
            <input type="hidden" name="idAssinatura" value="">
            <div class="modal-icone">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="3 6 5 6 21 6"></polyline><path d="M19 6l-1 14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2L5 6"></path><path d="M10 11v6"></path><path d="M14 11v6"></path><path d="M9 6V4a1 1 0 0 1 1-1h4a1 1 0 0 1 1 1v2"></path></svg>
            </div>
            <h2 class="popup-titulo" id="popup-excluir-titulo">Excluir "<span id="nome-excluir"></span>"?</h2>
            <p class="popup-texto">A conta ficará sem plano até que uma nova assinatura seja criada. Esta ação não poderá ser desfeita.</p>
            <div class="modal-botoes">
                <button class="botao-secundario" type="button" data-fechar>Cancelar</button>
                <button class="botao-principal botao-perigo" type="submit">Excluir assinatura</button>
            </div>
        </form>
    </div>

<%@ include file="rodape.jspf" %>
