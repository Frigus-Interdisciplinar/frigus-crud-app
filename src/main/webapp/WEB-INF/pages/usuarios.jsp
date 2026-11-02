<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="menuAtivo" value="usuarios" scope="request"/>
<c:set var="tituloPagina" value="Usuários" scope="request"/>
<c:set var="rotuloLista" value="usuários" scope="request"/>
<%@ include file="cabecalho.jspf" %>

    <!-- ========== CONTEÚDO DA PÁGINA ========== -->

    <div class="conteudo" id="conteudo">
        <div class="topo-pagina">
            <div class="page-header">
                <h1>Usuários</h1>
                <p class="subtitulo-pagina">Todas as contas de pessoa física cadastradas.</p>
            </div>

            <button class="botao-principal" type="button">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                    <line x1="12" y1="5" x2="12" y2="19"></line>
                    <line x1="5" y1="12" x2="19" y2="12"></line>
                </svg>
                Adicionar usuário
            </button>
        </div>

        <%@ include file="mensagens.jspf" %>

        <div class="caixa-tabela">
            <form class="filtros" method="get" action="${ctx}/admin/usuarios">
                <div class="filtro">
                    <label for="filtro-nome">Nome ou e-mail</label>
                    <input id="filtro-nome" name="busca" type="text" value="<c:out value='${filtroBusca}'/>" placeholder="Buscar por nome ou e-mail...">
                </div>
                <div class="filtro">
                    <label for="f-cpf">CPF</label>
                    <input id="f-cpf" name="cpf" type="text" value="<c:out value='${filtroCpf}'/>" inputmode="numeric" maxlength="14" placeholder="Somente números">
                </div>
                <div class="filtro">
                    <label for="filtro-plano">Plano</label>
                    <select id="filtro-plano" name="plano">
                        <option value="">Todos</option>
                        <c:forEach items="${planos}" var="p">
                            <option value="${p}" ${filtroPlano == p.name() ? 'selected' : ''}><c:out value="${p}"/></option>
                        </c:forEach>
                    </select>
                </div>
                <div class="filtro filtro-grande filtro-destaque">
                    <label for="filtro-ordem">Ordenar por</label>
                    <select id="filtro-ordem" name="ord">
                        <option value="nome:asc" ${filtroOrd == 'nome:asc' ? 'selected' : ''}>Ordenar de A a Z</option>
                        <option value="nome:desc" ${filtroOrd == 'nome:desc' ? 'selected' : ''}>Ordenar de Z a A</option>
                        <option value="novo:desc" ${filtroOrd == 'novo:desc' ? 'selected' : ''}>Mais recentes</option>
                        <option value="novo:asc" ${filtroOrd == 'novo:asc' ? 'selected' : ''}>Mais antigos</option>
                    </select>
                </div>
                <button class="botao-limpar" type="button">Limpar filtros</button>
            </form>

            <table class="tabela ">
                <thead>
                    <tr>
                        <th>Usuário</th>
                        <th>E-mail</th>
                        <th>Plano</th>
                        <th>Geladeiras</th>
                        <th>Receitas</th>
                        <th>Cadastro</th>
                        <th></th>
                    </tr>
                </thead>

                <tbody>
                    <c:forEach items="${usuarios}" var="u">
                        <tr>
                            <td><div class="titulo-cards"><c:out value="${u.nome} ${u.sobrenome}"/></div></td>
                            <td><c:out value="${u.email}"/></td>
                            <td><span class="etiqueta-plano"><c:out value="${planosDosUsuarios[u.cpf]}"/></span></td>
                            <td>${empty geladeirasPorUsuario[u.cpf] ? 0 : geladeirasPorUsuario[u.cpf]}</td>
                            <td>${empty receitasPorUsuario[u.cpf] ? 0 : receitasPorUsuario[u.cpf]}</td>
                            <td><c:out value="${u.dtCadastro}"/></td>
                            <td class="botoes-tabela">
                                <button class="botao-icone" type="button" aria-label="Visualizar"
                                        data-nome="<c:out value='${u.nome}'/>" data-sobrenome="<c:out value='${u.sobrenome}'/>"
                                        data-email="<c:out value='${u.email}'/>" data-plano="<c:out value='${planosDosUsuarios[u.cpf]}'/>">
                                    <img src="${ctx}/assets/icones/icone-olho-aberto.svg" alt="">
                                </button>
                                <button class="botao-icone" type="button" aria-label="Editar"
                                        data-cpf="${u.cpf}" data-nome="<c:out value='${u.nome}'/>" data-sobrenome="<c:out value='${u.sobrenome}'/>"
                                        data-email="<c:out value='${u.email}'/>" data-plano="<c:out value='${planosDosUsuarios[u.cpf]}'/>">
                                    <img src="${ctx}/assets/icones/icone-caneta.svg" alt="">
                                </button>
                                <button class="botao-icone" type="button" aria-label="Excluir" data-cpf="${u.cpf}">
                                    <img src="${ctx}/assets/icones/icone-lixera.svg" alt="">
                                </button>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty usuarios}">
                        <tr><td colspan="7">Nenhum registro encontrado com esses filtros.</td></tr>
                    </c:if>
                </tbody>
            </table>

            <%@ include file="rodape-tabela.jspf" %>
        </div>
    </div>

    <!-- ========== POP-UPS ========== -->

    <!-- Adicionar usuário -->
    <div class="popup-fundo" id="popup-adicionar">
        <aside class="gaveta" role="dialog" aria-modal="true" aria-labelledby="popup-adicionar-titulo">
            <header class="gaveta-topo">
                <div>
                    <h2 class="popup-titulo" id="popup-adicionar-titulo">Adicionar usuário</h2>
                    <p class="popup-texto">Dados de acesso e plano da conta.</p>
                </div>
                <button class="botao-fechar" type="button" aria-label="Fechar" data-fechar>
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
                </button>
            </header>
            <form class="gaveta-form" method="post" action="${ctx}/admin/usuarios">
                <input type="hidden" name="action" value="inserir">
                <input type="hidden" name="voltar" value="<c:out value='${pageContext.request.queryString}'/>">
                <div class="gaveta-corpo">
                    <p class="gaveta-secao">Dados do usuário</p>
                    <div class="filtro">
                        <label for="adicionar-nome">Nome <span class="obrigatorio">*</span></label>
                        <input id="adicionar-nome" name="nome" type="text" placeholder="Ex.: Maria" required maxlength="20">
                    </div>
                    <div class="filtro">
                        <label for="adicionar-sobrenome">Sobrenome <span class="obrigatorio">*</span></label>
                        <input id="adicionar-sobrenome" name="sobrenome" type="text" placeholder="Ex.: Souza" required maxlength="60">
                    </div>
                    <div class="filtro">
                        <label for="us-cpf">CPF <span class="obrigatorio">*</span></label>
                        <input id="us-cpf" name="cpf" type="text" placeholder="000.000.000-00" required inputmode="numeric" maxlength="14">
                    </div>
                    <div class="filtro">
                        <label for="adicionar-email">E-mail <span class="obrigatorio">*</span></label>
                        <input id="adicionar-email" name="email" type="text" inputmode="email" placeholder="nome@email.com" required>
                    </div>
                    <div class="filtro">
                        <label for="adicionar-senha">Senha <span class="obrigatorio">*</span></label>
                        <input id="adicionar-senha" name="senha" type="password" placeholder="De 8 a 15 caracteres" required minlength="8" maxlength="15" autocomplete="new-password">
                    </div>
                    <div class="filtro">
                        <label for="adicionar-plano">Plano <span class="obrigatorio">*</span></label>
                        <select id="adicionar-plano" name="plano" required>
                            <option value="">Selecione</option>
                            <c:forEach items="${planos}" var="p">
                                <option value="${p}"><c:out value="${p}"/></option>
                            </c:forEach>
                        </select>
                    </div>
                </div>
                <footer class="gaveta-rodape">
                    <button class="botao-secundario" type="button" data-fechar>Cancelar</button>
                    <button class="botao-principal" type="submit">Adicionar usuário</button>
                </footer>
            </form>
        </aside>
    </div>

    <!-- Editar usuário -->
    <div class="popup-fundo" id="popup-editar">
        <aside class="gaveta" role="dialog" aria-modal="true" aria-labelledby="popup-editar-titulo">
            <header class="gaveta-topo">
                <div>
                    <h2 class="popup-titulo" id="popup-editar-titulo">Editar usuário</h2>
                    <p class="popup-texto">Dados de acesso e plano da conta.</p>
                </div>
                <button class="botao-fechar" type="button" aria-label="Fechar" data-fechar>
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
                </button>
            </header>
            <form class="gaveta-form" method="post" action="${ctx}/admin/usuarios">
                <input type="hidden" name="action" value="alterar">
                <input type="hidden" name="voltar" value="<c:out value='${pageContext.request.queryString}'/>">
                <input type="hidden" name="cpf" value="">
                <div class="gaveta-corpo">
                    <p class="gaveta-secao">Dados do usuário</p>
                    <div class="filtro">
                        <label for="editar-nome">Nome <span class="obrigatorio">*</span></label>
                        <input id="editar-nome" name="nome" type="text" required maxlength="20">
                    </div>
                    <div class="filtro">
                        <label for="editar-sobrenome">Sobrenome <span class="obrigatorio">*</span></label>
                        <input id="editar-sobrenome" name="sobrenome" type="text" required maxlength="60">
                    </div>
                    <div class="filtro">
                        <label for="editar-email">E-mail <span class="obrigatorio">*</span></label>
                        <input id="editar-email" name="email" type="text" inputmode="email" required>
                    </div>
                    <div class="filtro">
                        <label for="editar-senha">Senha</label>
                        <input id="editar-senha" name="senha" type="password" placeholder="De 8 a 15 caracteres" minlength="8" maxlength="15" autocomplete="new-password">
                        <p class="ajuda-campo">Ao editar, deixe em branco para manter a senha atual.</p>
                    </div>
                    <div class="filtro">
                        <label for="editar-plano">Plano <span class="obrigatorio">*</span></label>
                        <select id="editar-plano" name="plano" required>
                            <option value="">Selecione</option>
                            <c:forEach items="${planos}" var="p">
                                <option value="${p}"><c:out value="${p}"/></option>
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

    <!-- Detalhes do usuário -->
    <div class="popup-fundo" id="popup-detalhes">
        <aside class="gaveta" role="dialog" aria-modal="true" aria-labelledby="popup-detalhes-titulo">
            <header class="gaveta-topo">
                <div>
                    <h2 class="popup-titulo" id="popup-detalhes-titulo">Detalhes do usuário</h2>
                    <p class="popup-texto">Dados de acesso e plano da conta.</p>
                </div>
                <button class="botao-fechar" type="button" aria-label="Fechar" data-fechar>
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
                </button>
            </header>
            <div class="gaveta-form">
                <div class="gaveta-corpo">
                    <p class="gaveta-secao">Dados do usuário</p>
                    <div class="filtro">
                        <label for="detalhes-nome">Nome</label>
                        <input id="detalhes-nome" name="nome" type="text" readonly>
                    </div>
                    <div class="filtro">
                        <label for="detalhes-sobrenome">Sobrenome</label>
                        <input id="detalhes-sobrenome" name="sobrenome" type="text" readonly>
                    </div>
                    <div class="filtro">
                        <label for="detalhes-email">E-mail</label>
                        <input id="detalhes-email" name="email" type="text" readonly>
                    </div>
                    <div class="aviso-info">
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"></circle><line x1="12" y1="16" x2="12" y2="12"></line><line x1="12" y1="8" x2="12.01" y2="8"></line></svg>
                        <p>A senha não é exibida por questões de segurança. Para trocá-la, use a opção Editar.</p>
                    </div>
                    <div class="filtro">
                        <label for="detalhes-plano">Plano</label>
                        <input id="detalhes-plano" name="plano" type="text" readonly>
                    </div>
                </div>
                <footer class="gaveta-rodape">
                    <button class="botao-secundario" type="button" data-fechar>Fechar</button>
                </footer>
            </div>
        </aside>
    </div>

    <!-- Excluir usuário -->
    <div class="popup-fundo popup-fundo-centro" id="popup-excluir">
        <form class="modal-exclusao" method="post" action="${ctx}/admin/usuarios" role="alertdialog" aria-modal="true" aria-labelledby="popup-excluir-titulo">
            <input type="hidden" name="action" value="deletar">
            <input type="hidden" name="voltar" value="<c:out value='${pageContext.request.queryString}'/>">
            <input type="hidden" name="cpf" value="">
            <div class="modal-icone">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="3 6 5 6 21 6"></polyline><path d="M19 6l-1 14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2L5 6"></path><path d="M10 11v6"></path><path d="M14 11v6"></path><path d="M9 6V4a1 1 0 0 1 1-1h4a1 1 0 0 1 1 1v2"></path></svg>
            </div>
            <h2 class="popup-titulo" id="popup-excluir-titulo">Excluir "<span id="nome-excluir"></span>"?</h2>
            <p class="popup-texto">Os registros ligados a este usuário poderão ser afetados. Esta ação não poderá ser desfeita.</p>
            <div class="modal-botoes">
                <button class="botao-secundario" type="button" data-fechar>Cancelar</button>
                <button class="botao-principal botao-perigo" type="submit">Excluir usuário</button>
            </div>
        </form>
    </div>

<%@ include file="rodape.jspf" %>
