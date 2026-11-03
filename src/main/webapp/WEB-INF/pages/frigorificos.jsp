<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="menuAtivo" value="frigorificos" scope="request"/>
<c:set var="tituloPagina" value="Frigoríficos" scope="request"/>
<c:set var="rotuloLista" value="frigoríficos" scope="request"/>
<%@ include file="cabecalho.jspf" %>

    <div class="conteudo" id="conteudo">
        <!-- Título e botão de adicionar -->
        <div class="topo-pagina">
            <div class="page-header">
                <h1>Frigoríficos</h1>
                <p class="subtitulo-pagina">Frigoríficos de todas as empresas, com endereço e validades.</p>
            </div>

            <button class="botao-principal" type="button">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                    <line x1="12" y1="5" x2="12" y2="19"></line>
                    <line x1="5" y1="12" x2="19" y2="12"></line>
                </svg>
                Adicionar frigorífico
            </button>
        </div>

        <%@ include file="mensagens.jspf" %>

        <!-- Caixa da tabela -->
        <div class="caixa-tabela">
            <!-- Filtros -->
            <form class="filtros filtros-cinco-colunas" method="get" action="${ctx}/admin/frigorificos">
                <div class="filtro">
                    <label for="filtro-nome">Nome da unidade</label>
                    <input id="filtro-nome" type="text" name="nome" value="<c:out value='${filtroNome}'/>" placeholder="Ex.: Unidade Paulista">
                </div>
                <div class="filtro">
                    <label for="filtro-cidade">Cidade</label>
                    <select id="filtro-cidade" name="cidade">
                        <option value="">Todos</option>
                        <c:forEach items="${cidades}" var="cidade">
                        <option value="${cidade}" ${filtroCidade == cidade ? 'selected' : ''}><c:out value="${cidade}"/></option>
                        </c:forEach>
                    </select>
                </div>
                <div class="filtro">
                    <label for="filtro-uf">UF</label>
                    <select id="filtro-uf" name="uf">
                        <option value="">Todos</option>
                        <c:forEach items="${ufs}" var="uf">
                        <option value="${uf}" ${filtroUf == uf ? 'selected' : ''}>${uf}</option>
                        </c:forEach>
                    </select>
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
                <div class="filtro filtro-grande filtro-destaque">
                    <label for="filtro-ordem">Ordenar por</label>
                    <select id="filtro-ordem" name="ord">
                        <option value="dono:asc" ${filtroOrd == 'dono:asc' ? 'selected' : ''}>Ordenar por nome da empresa de A a Z</option>
                        <option value="dono:desc" ${filtroOrd == 'dono:desc' ? 'selected' : ''}>Ordenar por nome da empresa de Z a A</option>
                        <option value="nome:asc" ${filtroOrd == 'nome:asc' ? 'selected' : ''}>Ordenar por nome do frigorífico de A a Z</option>
                        <option value="nome:desc" ${filtroOrd == 'nome:desc' ? 'selected' : ''}>Ordenar por nome do frigorífico de Z a A</option>
                        <option value="novo:asc" ${filtroOrd == 'novo:asc' ? 'selected' : ''}>Ordenar da inserção mais antiga</option>
                        <option value="novo:desc" ${filtroOrd == 'novo:desc' ? 'selected' : ''}>Ordenar da inserção mais recente</option>
                    </select>
                </div>
                <button class="botao-limpar" type="button">Limpar filtros</button>
            </form>

            <!-- Tabela -->
            <div class="rolagem-tabela">
                <table class="tabela tabela-frigorifico">
                    <thead>
                        <tr>
                            <th>Frigorífico</th>
                            <th>Empresa</th>
                            <th>Endereço</th>
                            <th>CEP</th>
                            <th>Produtos</th>
                            <th>Itens</th>
                            <th>Alertas</th>
                            <th></th>
                        </tr>
                    </thead>
    
                    <!-- Linhas da tabela (uma por registro) -->
                    <tbody>
                        <c:forEach items="${frigorificos}" var="f">
                            <c:set var="numeros" value="${ocupacao[f.idFrigorifico]}"/>
                            <c:set var="vencidos" value="${empty numeros ? 0 : numeros[2]}"/>
                            <c:set var="aVencer" value="${empty numeros ? 0 : numeros[3]}"/>
                            <tr>
                                <td><div class="titulo-cards"><c:out value="${f.nome}"/></div>
                                    <div class="texto-pequeno-cards"><c:out value="${f.tipo}"/></div></td>
                                <td><div class="titulo-cards"><c:out value="${f.empresa.nome}"/></div>
                                    <div class="texto-pequeno-cards"><c:out value="${f.empresa.email}"/></div></td>
                                <td><div class="titulo-cards"><c:out value="${f.endereco.rua}, ${f.endereco.numero}"/></div>
                                    <div class="texto-pequeno-cards"><c:out value="${f.endereco.bairro} — ${f.endereco.cidade}/${f.endereco.uf}"/></div></td>
                                <td><c:out value="${f.endereco.cep}"/></td>
                                <td>${empty numeros ? 0 : numeros[0]}</td>
                                <td><div>${empty numeros ? 0 : numeros[1]}</div>
                                    <div class="texto-pequeno-cards">itens</div></td>
                                <td>
                                    <c:choose>
                                        <c:when test="${vencidos + aVencer > 0}"><span class="alerta">${vencidos} vencidos · ${aVencer} a vencer</span></c:when>
                                        <c:otherwise><span class="sem-alerta">Sem alertas</span></c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="botoes-tabela">
                                    <button class="botao-icone" type="button" aria-label="Visualizar"
                                            data-empresa="<c:out value='${f.empresa.nome} · ${f.empresa.email}'/>" data-tipo="<c:out value='${f.tipo}'/>" data-unidade="<c:out value='${f.nome}'/>"
                                            data-cep="<c:out value='${f.endereco.cep}'/>" data-pais="<c:out value='${f.endereco.pais}'/>" data-rua="<c:out value='${f.endereco.rua}'/>"
                                            data-numero="<c:out value='${f.endereco.numero}'/>" data-bairro="<c:out value='${f.endereco.bairro}'/>" data-cidade="<c:out value='${f.endereco.cidade}'/>" data-uf="<c:out value='${f.endereco.uf}'/>">
                                        <img src="${ctx}/assets/icones/icone-olho-aberto.svg" alt="">
                                    </button>
                                    <button class="botao-icone" type="button" aria-label="Editar"
                                            data-id-frigorifico="${f.idFrigorifico}" data-cnpj="${f.empresa.cnpj}" data-tipo="<c:out value='${f.tipo}'/>" data-nome-unidade="<c:out value='${f.nome}'/>"
                                            data-cep="<c:out value='${f.endereco.cep}'/>" data-pais="<c:out value='${f.endereco.pais}'/>" data-rua="<c:out value='${f.endereco.rua}'/>"
                                            data-numero="<c:out value='${f.endereco.numero}'/>" data-bairro="<c:out value='${f.endereco.bairro}'/>" data-cidade="<c:out value='${f.endereco.cidade}'/>" data-uf="<c:out value='${f.endereco.uf}'/>">
                                        <img src="${ctx}/assets/icones/icone-caneta.svg" alt="">
                                    </button>
                                    <button class="botao-icone" type="button" aria-label="Excluir" data-id-frigorifico="${f.idFrigorifico}">
                                        <img src="${ctx}/assets/icones/icone-lixera.svg" alt="">
                                    </button>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty frigorificos}">
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

    <!-- Adicionar frigorífico -->
    <div class="popup-fundo" id="popup-adicionar">
        <aside class="gaveta" role="dialog" aria-modal="true" aria-labelledby="popup-adicionar-titulo">
            <header class="gaveta-topo">
                <div>
                    <h2 class="popup-titulo" id="popup-adicionar-titulo">Adicionar frigorífico</h2>
                    <p class="popup-texto">Informe a empresa, os dados da unidade e o endereço.</p>
                </div>
                <button class="botao-fechar" type="button" aria-label="Fechar" data-fechar>
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
                </button>
            </header>
            <form class="gaveta-form" method="post" action="${ctx}/admin/frigorificos">
                <input type="hidden" name="action" value="inserir">
                <input type="hidden" name="voltar" value="<c:out value='${pageContext.request.queryString}'/>">
                <div class="gaveta-corpo">
                    <p class="gaveta-secao">Informações do frigorífico</p>
                    <div class="filtro">
                        <label for="adicionar-empresa">Empresa dona <span class="obrigatorio">*</span></label>
                        <select id="adicionar-empresa" name="cnpj" required>
                            <option value="">Selecione</option>
                            <c:forEach items="${empresas}" var="e">
                                <option value="${e.cnpj}"><c:out value="${e.nome} · ${e.email}"/></option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="filtro">
                        <label for="adicionar-tipo">Tipo <span class="obrigatorio">*</span></label>
                        <input id="adicionar-tipo" name="tipo" maxlength="20" type="text" placeholder="Ex.: Câmara refrigerada" required>
                    </div>
                    <hr class="gaveta-divisor">
                    <p class="gaveta-secao">Endereço da unidade</p>
                    <div class="filtro">
                        <label for="adicionar-unidade">Nome da unidade <span class="obrigatorio">*</span></label>
                        <input id="adicionar-unidade" name="nomeUnidade" maxlength="50" type="text" placeholder="Ex.: Unidade Paulista" required>
                    </div>
                    <div class="gaveta-linha">
                        <div class="filtro">
                            <label for="fr-cep">CEP <span class="obrigatorio">*</span></label>
                            <input id="fr-cep" name="cep" type="text" placeholder="00000-000" required inputmode="numeric" maxlength="9">
                        </div>
                        <div class="filtro">
                            <label for="adicionar-pais">País <span class="obrigatorio">*</span></label>
                            <input id="adicionar-pais" name="pais" type="text" value="Brasil" required>
                        </div>
                    </div>
                    <div class="filtro">
                        <label for="adicionar-rua">Rua <span class="obrigatorio">*</span></label>
                        <input id="adicionar-rua" name="rua" type="text" placeholder="Ex.: Av. Paulista" required>
                    </div>
                    <div class="gaveta-linha">
                        <div class="filtro">
                            <label for="adicionar-numero">Número <span class="obrigatorio">*</span></label>
                            <input id="adicionar-numero" name="numero" maxlength="20" type="text" required>
                        </div>
                        <div class="filtro">
                            <label for="adicionar-bairro">Bairro <span class="obrigatorio">*</span></label>
                            <input id="adicionar-bairro" name="bairro" type="text" required>
                        </div>
                    </div>
                    <div class="gaveta-linha">
                        <div class="filtro">
                            <label for="adicionar-cidade">Cidade <span class="obrigatorio">*</span></label>
                            <input id="adicionar-cidade" name="cidade" type="text" required>
                        </div>
                        <div class="filtro">
                            <label for="adicionar-uf">UF <span class="obrigatorio">*</span></label>
                            <select id="adicionar-uf" name="uf" required>
                                <option value="">Selecione</option>
                                <option>AC</option>
                                <option>AL</option>
                                <option>AP</option>
                                <option>AM</option>
                                <option>BA</option>
                                <option>CE</option>
                                <option>DF</option>
                                <option>ES</option>
                                <option>GO</option>
                                <option>MA</option>
                                <option>MT</option>
                                <option>MS</option>
                                <option>MG</option>
                                <option>PA</option>
                                <option>PB</option>
                                <option>PR</option>
                                <option>PE</option>
                                <option>PI</option>
                                <option>RJ</option>
                                <option>RN</option>
                                <option>RS</option>
                                <option>RO</option>
                                <option>RR</option>
                                <option>SC</option>
                                <option>SP</option>
                                <option>SE</option>
                                <option>TO</option>
                            </select>
                        </div>
                    </div>
                </div>
                <footer class="gaveta-rodape">
                    <button class="botao-secundario" type="button" data-fechar>Cancelar</button>
                    <button class="botao-principal" type="submit">Adicionar frigorífico</button>
                </footer>
            </form>
        </aside>
    </div>

    <!-- Editar frigorífico -->
    <div class="popup-fundo" id="popup-editar">
        <aside class="gaveta" role="dialog" aria-modal="true" aria-labelledby="popup-editar-titulo">
            <header class="gaveta-topo">
                <div>
                    <h2 class="popup-titulo" id="popup-editar-titulo">Editar frigorífico</h2>
                    <p class="popup-texto">Informe a empresa, os dados da unidade e o endereço.</p>
                </div>
                <button class="botao-fechar" type="button" aria-label="Fechar" data-fechar>
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
                </button>
            </header>
            <form class="gaveta-form" method="post" action="${ctx}/admin/frigorificos">
                <input type="hidden" name="action" value="alterar">
                <input type="hidden" name="voltar" value="<c:out value='${pageContext.request.queryString}'/>">
                <input type="hidden" name="idFrigorifico" value="">
                <div class="gaveta-corpo">
                    <p class="gaveta-secao">Informações do frigorífico</p>
                    <div class="filtro">
                        <label for="editar-empresa">Empresa dona <span class="obrigatorio">*</span></label>
                        <select id="editar-empresa" name="cnpj" required>
                            <option value="">Selecione</option>
                            <c:forEach items="${empresas}" var="e">
                                <option value="${e.cnpj}"><c:out value="${e.nome} · ${e.email}"/></option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="filtro">
                        <label for="editar-tipo">Tipo <span class="obrigatorio">*</span></label>
                        <input id="editar-tipo" name="tipo" maxlength="20" type="text" required>
                    </div>
                    <hr class="gaveta-divisor">
                    <p class="gaveta-secao">Endereço da unidade</p>
                    <div class="filtro">
                        <label for="editar-unidade">Nome da unidade <span class="obrigatorio">*</span></label>
                        <input id="editar-unidade" name="nomeUnidade" maxlength="50" type="text" placeholder="Ex.: Unidade Paulista" required>
                    </div>
                    <div class="gaveta-linha">
                        <div class="filtro">
                            <label for="fr-cep-editar">CEP <span class="obrigatorio">*</span></label>
                            <input id="fr-cep-editar" name="cep" type="text" placeholder="00000-000" required inputmode="numeric" maxlength="9">
                        </div>
                        <div class="filtro">
                            <label for="editar-pais">País <span class="obrigatorio">*</span></label>
                            <input id="editar-pais" name="pais" type="text" value="Brasil" required>
                        </div>
                    </div>
                    <div class="filtro">
                        <label for="editar-rua">Rua <span class="obrigatorio">*</span></label>
                        <input id="editar-rua" name="rua" type="text" placeholder="Ex.: Av. Paulista" required>
                    </div>
                    <div class="gaveta-linha">
                        <div class="filtro">
                            <label for="editar-numero">Número <span class="obrigatorio">*</span></label>
                            <input id="editar-numero" name="numero" maxlength="20" type="text" required>
                        </div>
                        <div class="filtro">
                            <label for="editar-bairro">Bairro <span class="obrigatorio">*</span></label>
                            <input id="editar-bairro" name="bairro" type="text" required>
                        </div>
                    </div>
                    <div class="gaveta-linha">
                        <div class="filtro">
                            <label for="editar-cidade">Cidade <span class="obrigatorio">*</span></label>
                            <input id="editar-cidade" name="cidade" type="text" required>
                        </div>
                        <div class="filtro">
                            <label for="editar-uf">UF <span class="obrigatorio">*</span></label>
                            <select id="editar-uf" name="uf" required>
                                <option value="">Selecione</option>
                                <option>AC</option>
                                <option>AL</option>
                                <option>AP</option>
                                <option>AM</option>
                                <option>BA</option>
                                <option>CE</option>
                                <option>DF</option>
                                <option>ES</option>
                                <option>GO</option>
                                <option>MA</option>
                                <option>MT</option>
                                <option>MS</option>
                                <option>MG</option>
                                <option>PA</option>
                                <option>PB</option>
                                <option>PR</option>
                                <option>PE</option>
                                <option>PI</option>
                                <option>RJ</option>
                                <option>RN</option>
                                <option>RS</option>
                                <option>RO</option>
                                <option>RR</option>
                                <option>SC</option>
                                <option>SP</option>
                                <option>SE</option>
                                <option>TO</option>
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

    <!-- Detalhes do frigorífico -->
    <div class="popup-fundo" id="popup-detalhes">
        <aside class="gaveta" role="dialog" aria-modal="true" aria-labelledby="popup-detalhes-titulo">
            <header class="gaveta-topo">
                <div>
                    <h2 class="popup-titulo" id="popup-detalhes-titulo">Detalhes do frigorífico</h2>
                    <p class="popup-texto">Informe a empresa, os dados da unidade e o endereço.</p>
                </div>
                <button class="botao-fechar" type="button" aria-label="Fechar" data-fechar>
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
                </button>
            </header>
            <div class="gaveta-form">
                <div class="gaveta-corpo">
                    <p class="gaveta-secao">Informações do frigorífico</p>
                    <div class="filtro">
                        <label for="detalhes-empresa">Empresa dona <span class="obrigatorio">*</span></label>
                        <input id="detalhes-empresa" name="empresa" type="text" readonly>
                    </div>
                    <div class="filtro">
                        <label for="detalhes-tipo">Tipo <span class="obrigatorio">*</span></label>
                        <input id="detalhes-tipo" name="tipo" type="text" readonly>
                    </div>
                    <hr class="gaveta-divisor">
                    <p class="gaveta-secao">Endereço da unidade</p>
                    <div class="filtro">
                        <label for="detalhes-unidade">Nome da unidade <span class="obrigatorio">*</span></label>
                        <input id="detalhes-unidade" name="unidade" type="text" readonly>
                    </div>
                    <div class="gaveta-linha">
                        <div class="filtro">
                            <label for="detalhes-cep">CEP <span class="obrigatorio">*</span></label>
                            <input id="detalhes-cep" name="cep" type="text" readonly>
                        </div>
                        <div class="filtro">
                            <label for="detalhes-pais">País <span class="obrigatorio">*</span></label>
                            <input id="detalhes-pais" name="pais" type="text" value="Brasil" readonly>
                        </div>
                    </div>
                    <div class="filtro">
                        <label for="detalhes-rua">Rua <span class="obrigatorio">*</span></label>
                        <input id="detalhes-rua" name="rua" type="text" readonly>
                    </div>
                    <div class="gaveta-linha">
                        <div class="filtro">
                            <label for="detalhes-numero">Número <span class="obrigatorio">*</span></label>
                            <input id="detalhes-numero" name="numero" type="text" readonly>
                        </div>
                        <div class="filtro">
                            <label for="detalhes-bairro">Bairro <span class="obrigatorio">*</span></label>
                            <input id="detalhes-bairro" name="bairro" type="text" readonly>
                        </div>
                    </div>
                    <div class="gaveta-linha">
                        <div class="filtro">
                            <label for="detalhes-cidade">Cidade <span class="obrigatorio">*</span></label>
                            <input id="detalhes-cidade" name="cidade" type="text" readonly>
                        </div>
                        <div class="filtro">
                            <label for="detalhes-uf">UF <span class="obrigatorio">*</span></label>
                            <input id="detalhes-uf" name="uf" type="text" readonly>
                        </div>
                    </div>
                </div>
                <footer class="gaveta-rodape">
                    <button class="botao-secundario" type="button" data-fechar>Fechar</button>
                </footer>
            </div>
        </aside>
    </div>

    <!-- Excluir frigorífico -->
    <div class="popup-fundo popup-fundo-centro" id="popup-excluir">
        <form class="modal-exclusao" method="post" action="${ctx}/admin/frigorificos" role="alertdialog" aria-modal="true" aria-labelledby="popup-excluir-titulo">
            <input type="hidden" name="action" value="deletar">
            <input type="hidden" name="voltar" value="<c:out value='${pageContext.request.queryString}'/>">
            <input type="hidden" name="idFrigorifico" value="">
            <div class="modal-icone">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="3 6 5 6 21 6"></polyline><path d="M19 6l-1 14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2L5 6"></path><path d="M10 11v6"></path><path d="M14 11v6"></path><path d="M9 6V4a1 1 0 0 1 1-1h4a1 1 0 0 1 1 1v2"></path></svg>
            </div>
            <h2 class="popup-titulo" id="popup-excluir-titulo">Excluir "<span id="nome-excluir"></span>"?</h2>
            <p class="popup-texto">Os registros ligados a este frigorífico poderão ser afetados. Esta ação não poderá ser desfeita.</p>
            <div class="modal-botoes">
                <button class="botao-secundario" type="button" data-fechar>Cancelar</button>
                <button class="botao-principal botao-perigo" type="submit">Excluir frigorífico</button>
            </div>
        </form>
    </div>

<%@ include file="rodape.jspf" %>
