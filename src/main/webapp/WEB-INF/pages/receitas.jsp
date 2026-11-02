<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="menuAtivo" value="receitas" scope="request"/>
<c:set var="tituloPagina" value="Receitas" scope="request"/>
<c:set var="rotuloLista" value="receitas" scope="request"/>
<%@ include file="cabecalho.jspf" %>

    <div class="conteudo" id="conteudo">
        <!-- Título e botão de adicionar -->
        <div class="topo-pagina">
            <div class="page-header">
                <h1>Receitas</h1>
                <p class="subtitulo-pagina">Receitas de todos os usuários e de todas as empresas.</p>
            </div>

            <button class="botao-principal" type="button">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                    <line x1="12" y1="5" x2="12" y2="19"></line>
                    <line x1="5" y1="12" x2="19" y2="12"></line>
                </svg>
                Nova receita
            </button>
        </div>

        <%@ include file="mensagens.jspf" %>

        <!-- Caixa da tabela -->
        <div class="caixa-tabela">
            <!-- Filtros -->
            <form class="filtros" method="get" action="${ctx}/admin/receitas">
                <div class="filtro">
                    <label for="filtro-receita">Receita</label>
                    <input id="filtro-receita" type="text" name="receita" value="<c:out value='${filtroReceita}'/>" placeholder="Nome da receita">
                </div>
                <div class="filtro">
                    <label for="filtro-dono">Dono</label>
                    <select id="filtro-dono" name="dono">
                        <option value="">Todos</option>
                        <option value="usuario" ${filtroDono == 'usuario' ? 'selected' : ''}>Usuário</option>
                        <option value="empresa" ${filtroDono == 'empresa' ? 'selected' : ''}>Empresa</option>
                    </select>
                </div>
                <div class="filtro filtro-grande filtro-destaque">
                    <label for="filtro-ordem">Ordenar por</label>
                    <select id="filtro-ordem" name="ord">
                        <option value="dono:asc" ${filtroOrd == 'dono:asc' ? 'selected' : ''}>Ordenar por nome do dono de A a Z</option>
                        <option value="dono:desc" ${filtroOrd == 'dono:desc' ? 'selected' : ''}>Ordenar por nome do dono de Z a A</option>
                        <option value="nome:asc" ${filtroOrd == 'nome:asc' ? 'selected' : ''}>Ordenar por nome da receita de A a Z</option>
                        <option value="nome:desc" ${filtroOrd == 'nome:desc' ? 'selected' : ''}>Ordenar por nome da receita de Z a A</option>
                        <option value="novo:asc" ${filtroOrd == 'novo:asc' ? 'selected' : ''}>Ordenar da inserção mais antiga</option>
                        <option value="novo:desc" ${filtroOrd == 'novo:desc' ? 'selected' : ''}>Ordenar da inserção mais recente</option>
                    </select>
                </div>
                <button class="botao-limpar" type="button">Limpar filtros</button>
            </form>

            <!-- Tabela -->
            <table class="tabela tabela-receita">
                <thead>
                    <tr>
                        <th>Receita</th>
                        <th>Usuário</th>
                        <th>Empresa</th>
                        <th>Ingredientes</th>
                        <th>Criada em</th>
                        <th></th>
                    </tr>
                </thead>

                <!-- Linhas da tabela (uma por registro) -->
                <tbody>
                    <c:forEach items="${receitas}" var="r">
                        <c:set var="ehUsuario" value="${not empty r.usuario}"/>
                        <c:set var="qtdIng" value="${empty ingredientesPorReceita[r.idReceita] ? 0 : ingredientesPorReceita[r.idReceita]}"/>
                        <c:set var="dono" value="${ehUsuario ? r.usuario.nome.concat(' ').concat(r.usuario.sobrenome).concat(' · ').concat(r.usuario.email) : r.empresa.nome.concat(' · ').concat(r.empresa.email)}"/>
                        <tr>
                            <td><div class="titulo-cards"><c:out value="${r.nome}"/></div>
                                <div class="texto-pequeno-cards descricao-curta"><c:out value="${r.descricao}"/></div></td>
                            <td><c:if test="${ehUsuario}"><div class="titulo-cards"><c:out value="${r.usuario.nome} ${r.usuario.sobrenome}"/></div>
                                <div class="texto-pequeno-cards"><c:out value="${r.usuario.email}"/></div></c:if><c:if test="${not ehUsuario}">—</c:if></td>
                            <td><c:if test="${not ehUsuario}"><div class="titulo-cards"><c:out value="${r.empresa.nome}"/></div>
                                <div class="texto-pequeno-cards"><c:out value="${r.empresa.email}"/></div></c:if><c:if test="${ehUsuario}">—</c:if></td>
                            <td>${qtdIng}</td>
                            <td>${r.dtReceita}</td>
                            <td class="botoes-tabela">
                                <button class="botao-icone" type="button" aria-label="Visualizar"
                                        data-dono-tipo="${ehUsuario ? 'usuario' : 'empresa'}" data-dono="<c:out value='${dono}'/>" data-nome="<c:out value='${r.nome}'/>"
                                        data-descricao="<c:out value='${r.descricao}'/>" data-passos="<c:out value='${r.modoPreparo}'/>" data-ingredientes="${qtdIng}">
                                    <img src="${ctx}/assets/icones/icone-olho-aberto.svg" alt="">
                                </button>
                                <button class="botao-icone" type="button" aria-label="Editar"
                                        data-id-receita="${r.idReceita}" data-dono-tipo="${ehUsuario ? 'usuario' : 'empresa'}" data-cpf="${ehUsuario ? r.usuario.cpf : ''}" data-cnpj="${ehUsuario ? '' : r.empresa.cnpj}"
                                        data-nome-receita="<c:out value='${r.nome}'/>" data-descricao-receita="<c:out value='${r.descricao}'/>" data-modo-preparo="<c:out value='${r.modoPreparo}'/>">
                                    <img src="${ctx}/assets/icones/icone-caneta.svg" alt="">
                                </button>
                                <button class="botao-icone" type="button" aria-label="Excluir" data-id-receita="${r.idReceita}">
                                    <img src="${ctx}/assets/icones/icone-lixera.svg" alt="">
                                </button>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty receitas}">
                        <tr><td colspan="6">Nenhum registro encontrado com esses filtros.</td></tr>
                    </c:if>
                </tbody>
            </table>

            <!-- Rodapé com contagem e paginação -->
            <%@ include file="rodape-tabela.jspf" %>
        </div>
    </div>

    <!-- ========== POP-UPS ========== -->

    <!-- Nova receita -->
    <div class="popup-fundo" id="popup-adicionar">
        <aside class="gaveta gaveta-larga" role="dialog" aria-modal="true" aria-labelledby="popup-adicionar-titulo">
            <header class="gaveta-topo">
                <div>
                    <h2 class="popup-titulo" id="popup-adicionar-titulo">Nova receita</h2>
                    <p class="popup-texto">Defina o dono, os ingredientes e o modo de preparo.</p>
                </div>
                <button class="botao-fechar" type="button" aria-label="Fechar" data-fechar>
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
                </button>
            </header>
            <form class="gaveta-form" method="post" action="${ctx}/admin/receitas">
                <input type="hidden" name="action" value="inserir">
                <input type="hidden" name="voltar" value="<c:out value='${pageContext.request.queryString}'/>">
                <div class="gaveta-corpo gaveta-corpo-duplo">
                    <div class="gaveta-coluna">
                        <p class="gaveta-secao">Dono da receita</p>
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
                            <label for="adicionar-usuario">Usuário <span class="obrigatorio">*</span></label>
                            <select id="adicionar-usuario" name="cpf" required>
                                <option value="">Selecione</option>
                                <c:forEach items="${usuarios}" var="u">
                                    <option value="${u.cpf}"><c:out value="${u.nome} ${u.sobrenome} · ${u.email}"/></option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="filtro" data-opcao="empresa" hidden>
                            <label for="adicionar-empresa">Empresa <span class="obrigatorio">*</span></label>
                            <select id="adicionar-empresa" name="cnpj" required disabled>
                                <option value="">Selecione</option>
                                <c:forEach items="${empresas}" var="e">
                                    <option value="${e.cnpj}"><c:out value="${e.nome} · ${e.email}"/></option>
                                </c:forEach>
                            </select>
                        </div>
                        <hr class="gaveta-divisor">
                        <p class="gaveta-secao">Informações da receita</p>
                        <div class="filtro">
                            <label for="adicionar-nome">Nome <span class="obrigatorio">*</span></label>
                            <input id="adicionar-nome" name="nomeReceita" maxlength="50" type="text" placeholder="Ex.: Arroz com feijão" required>
                        </div>
                        <div class="filtro">
                            <label for="adicionar-descricao">Descrição</label>
                            <textarea id="adicionar-descricao" name="descricaoReceita" rows="3" placeholder="Uma frase curta sobre a receita"></textarea>
                        </div>
                    </div>
                    <div class="gaveta-coluna">
                        <p class="gaveta-secao">Ingredientes</p>
                        <div class="ingredientes-titulos">
                            <span>Produto</span>
                            <span>Qtd.</span>
                            
                            <span></span>
                        </div>
                        <div class="ingredientes-lista">
                            <div class="ingrediente-linha">
                                <div class="filtro">
                                    <select name="idProduto" required aria-label="Produto">
                                        <option value="">Selecione</option>
                                        <c:forEach items="${produtos}" var="p">
                                            <option value="${p.idProdutos}"><c:out value="${p.nome} ${p.marca} (${p.unidadeMedida})"/></option>
                                        </c:forEach>
                                    </select>
                                </div>
                                <div class="filtro">
                                    <input type="number" name="quantidade" value="1" min="1" step="1" required aria-label="Quantidade">
                                </div>
                                <button class="botao-fechar botao-remover" type="button" aria-label="Remover ingrediente">
                                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
                                </button>
                            </div>
                        </div>
                        <button class="botao-secundario botao-adicionar-ingrediente" type="button">Adicionar ingrediente</button>
                        <hr class="gaveta-divisor">
                        <p class="gaveta-secao">Modo de preparo</p>
                        <div class="filtro">
                            <label for="adicionar-passos">Passo a passo <span class="obrigatorio">*</span></label>
                            <textarea id="adicionar-passos" name="modoPreparo" rows="6" placeholder="Descreva cada passo em uma linha" required></textarea>
                        </div>
                    </div>
                </div>
                <footer class="gaveta-rodape">
                    <button class="botao-secundario" type="button" data-fechar>Cancelar</button>
                    <button class="botao-principal" type="submit">Salvar receita</button>
                </footer>
            </form>
        </aside>
    </div>

    <!-- Editar receita -->
    <div class="popup-fundo" id="popup-editar">
        <aside class="gaveta gaveta-larga" role="dialog" aria-modal="true" aria-labelledby="popup-editar-titulo">
            <header class="gaveta-topo">
                <div>
                    <h2 class="popup-titulo" id="popup-editar-titulo">Editar receita</h2>
                    <p class="popup-texto">Defina o dono, os ingredientes e o modo de preparo.</p>
                </div>
                <button class="botao-fechar" type="button" aria-label="Fechar" data-fechar>
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
                </button>
            </header>
            <form class="gaveta-form" method="post" action="${ctx}/admin/receitas">
                <input type="hidden" name="action" value="alterar">
                <input type="hidden" name="voltar" value="<c:out value='${pageContext.request.queryString}'/>">
                <input type="hidden" name="idReceita" value="">
                <div class="gaveta-corpo gaveta-corpo-duplo">
                    <div class="gaveta-coluna">
                        <p class="gaveta-secao">Dono da receita</p>
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
                        <p class="gaveta-secao">Informações da receita</p>
                        <div class="filtro">
                            <label for="editar-nome">Nome <span class="obrigatorio">*</span></label>
                            <input id="editar-nome" name="nomeReceita" maxlength="50" type="text" placeholder="Ex.: Arroz com feijão" required>
                        </div>
                        <div class="filtro">
                            <label for="editar-descricao">Descrição</label>
                            <textarea id="editar-descricao" name="descricaoReceita" rows="3" placeholder="Uma frase curta sobre a receita"></textarea>
                        </div>
                    </div>
                    <div class="gaveta-coluna">
                        <p class="gaveta-secao">Modo de preparo</p>
                        <div class="filtro">
                            <label for="editar-passos">Passo a passo <span class="obrigatorio">*</span></label>
                            <textarea id="editar-passos" name="modoPreparo" rows="6" placeholder="Descreva cada passo em uma linha" required></textarea>
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

    <!-- Detalhes da receita -->
    <div class="popup-fundo" id="popup-detalhes">
        <aside class="gaveta gaveta-larga" role="dialog" aria-modal="true" aria-labelledby="popup-detalhes-titulo">
            <header class="gaveta-topo">
                <div>
                    <h2 class="popup-titulo" id="popup-detalhes-titulo">Detalhes da receita</h2>
                    <p class="popup-texto">Defina o dono, os ingredientes e o modo de preparo.</p>
                </div>
                <button class="botao-fechar" type="button" aria-label="Fechar" data-fechar>
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
                </button>
            </header>
            <div class="gaveta-form">
                <div class="gaveta-corpo gaveta-corpo-duplo">
                    <div class="gaveta-coluna">
                        <p class="gaveta-secao">Dono da receita</p>
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
                        <p class="gaveta-secao">Informações da receita</p>
                        <div class="filtro">
                            <label for="detalhes-nome">Nome <span class="obrigatorio">*</span></label>
                            <input id="detalhes-nome" name="nome" type="text" readonly>
                        </div>
                        <div class="filtro">
                            <label for="detalhes-descricao">Descrição</label>
                            <textarea id="detalhes-descricao" name="descricao" rows="3" readonly></textarea>
                        </div>
                    </div>
                    <div class="gaveta-coluna">
                        <p class="gaveta-secao">Ingredientes</p>
                        <div class="filtro">
                            <label for="detalhes-ingredientes">Quantidade de ingredientes</label>
                            <input id="detalhes-ingredientes" name="ingredientes" type="text" readonly>
                        </div>
                        <hr class="gaveta-divisor">
                        <p class="gaveta-secao">Modo de preparo</p>
                        <div class="filtro">
                            <label for="detalhes-passos">Passo a passo <span class="obrigatorio">*</span></label>
                            <textarea id="detalhes-passos" name="passos" rows="6" readonly></textarea>
                        </div>
                    </div>
                </div>
                <footer class="gaveta-rodape">
                    <button class="botao-secundario" type="button" data-fechar>Fechar</button>
                </footer>
            </div>
        </aside>
    </div>

    <!-- Excluir receita -->
    <div class="popup-fundo popup-fundo-centro" id="popup-excluir">
        <form class="modal-exclusao" method="post" action="${ctx}/admin/receitas" role="alertdialog" aria-modal="true" aria-labelledby="popup-excluir-titulo">
            <input type="hidden" name="action" value="deletar">
            <input type="hidden" name="voltar" value="<c:out value='${pageContext.request.queryString}'/>">
            <input type="hidden" name="idReceita" value="">
            <div class="modal-icone">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="3 6 5 6 21 6"></polyline><path d="M19 6l-1 14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2L5 6"></path><path d="M10 11v6"></path><path d="M14 11v6"></path><path d="M9 6V4a1 1 0 0 1 1-1h4a1 1 0 0 1 1 1v2"></path></svg>
            </div>
            <h2 class="popup-titulo" id="popup-excluir-titulo">Excluir "<span id="nome-excluir"></span>"?</h2>
            <p class="popup-texto">Os ingredientes e o modo de preparo desta receita serão removidos. Esta ação não poderá ser desfeita.</p>
            <div class="modal-botoes">
                <button class="botao-secundario" type="button" data-fechar>Cancelar</button>
                <button class="botao-principal botao-perigo" type="submit">Excluir receita</button>
            </div>
        </form>
    </div>

    <!-- Modelo de uma linha de ingrediente (o JS copia este modelo ao clicar em "Adicionar ingrediente") -->
    <template id="modelo-ingrediente">
        <div class="ingrediente-linha">
            <div class="filtro">
                <select name="idProduto" required aria-label="Produto">
                    <option value="">Selecione</option>
                    <c:forEach items="${produtos}" var="p">
                                            <option value="${p.idProdutos}"><c:out value="${p.nome} ${p.marca} (${p.unidadeMedida})"/></option>
                                        </c:forEach>
                </select>
            </div>
            <div class="filtro">
                <input type="number" name="quantidade" value="1" min="1" step="1" required aria-label="Quantidade">
            </div>
            <button class="botao-fechar botao-remover" type="button" aria-label="Remover ingrediente">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
            </button>
        </div>
    </template>

<%@ include file="rodape.jspf" %>
