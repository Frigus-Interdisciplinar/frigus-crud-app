<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="menuAtivo" value="configuracoes" scope="request"/>
<c:set var="tituloPagina" value="Configurações" scope="request"/>
<%@ include file="cabecalho.jspf" %>

    <div class="conteudo" id="conteudo">
        <!-- Título da página -->
        <div class="page-header">
                <h1>Configurações</h1>
                <p class="subtitulo-pagina">Perfil e acesso do superadministrador.</p>
            </div>

        <%@ include file="mensagens.jspf" %>

        <!-- Cartões da página -->
        <div class="caixa-configuracao">
            <!-- Perfil -->
            <section class="cartao-config">
                <div class="cartao-config-topo">
                    <span class="avatar-iniciais avatar-grande" aria-hidden="true">${iniciais}</span>
                    <div>
                        <h2 class="cartao-config-nome"><c:out value="${conta.nome}"/></h2>
                        <p class="cartao-config-email"><c:out value="${conta.email}"/></p>
                        <span class="etiqueta-plano etiqueta-escura">Superadministrador</span>
                    </div>
                </div>

                <div class="linha-dado">
                    <span>Nome</span>
                    <strong><c:out value="${conta.nome}"/></strong>
                </div>
                <div class="linha-dado">
                    <span>E-mail</span>
                    <strong><c:out value="${conta.email}"/></strong>
                </div>

                <div class="botoes-config">
                    <button class="botao-principal" type="button" data-abrir-popup="popup-editar-perfil">
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <path d="M12 20h9"></path>
                            <path d="M16.5 3.5a2.121 2.121 0 0 1 3 3L7 19l-4 1 1-4L16.5 3.5z"></path>
                        </svg>
                        Editar perfil
                    </button>
                    <button class="botao-secundario" type="button" data-abrir-popup="popup-alterar-senha">Alterar senha</button>
                </div>
            </section>

            <!-- Sessão -->
            <section class="cartao-config">
                <h2 class="cartao-config-titulo">Sessão</h2>
                <p class="cartao-config-texto">Você é o superadministrador deste sistema e vê os dados de todos os usuários e empresas. Saia da conta quando terminar.</p>
                <a class="botao-secundario" href="${ctx}/logout">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"></path>
                        <polyline points="16 17 21 12 16 7"></polyline>
                        <line x1="21" y1="12" x2="9" y2="12"></line>
                    </svg>
                    Sair da conta
                </a>
            </section>
        </div>
    </div>
    <!-- ========== POP-UPS ========== -->

    <!-- Editar perfil -->
    <div class="popup-fundo" id="popup-editar-perfil">
        <aside class="gaveta" role="dialog" aria-modal="true" aria-labelledby="popup-editar-perfil-titulo">
            <header class="gaveta-topo">
                <div>
                    <h2 class="popup-titulo" id="popup-editar-perfil-titulo">Editar perfil</h2>
                    <p class="popup-texto">Nome e e-mail usados para entrar no painel.</p>
                </div>
                <button class="botao-fechar" type="button" aria-label="Fechar" data-fechar>
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
                </button>
            </header>
            <form class="gaveta-form" method="post" action="${ctx}/admin/conta">
                <input type="hidden" name="action" value="alterar">
                <div class="gaveta-corpo">
                    <p class="gaveta-secao">Dados do perfil</p>
                    <div class="filtro">
                        <label for="perfil-nome">Nome <span class="obrigatorio">*</span></label>
                        <input id="perfil-nome" name="nome" type="text" value="<c:out value='${conta.nome}'/>" required maxlength="20">
                    </div>
                    <div class="filtro">
                        <label for="perfil-email">E-mail <span class="obrigatorio">*</span></label>
                        <input id="perfil-email" name="email" type="text" inputmode="email" value="<c:out value='${conta.email}'/>" required>
                    </div>
                </div>
                <footer class="gaveta-rodape">
                    <button class="botao-secundario" type="button" data-fechar>Cancelar</button>
                    <button class="botao-principal" type="submit">Salvar alterações</button>
                </footer>
            </form>
        </aside>
    </div>

    <!-- Alterar senha -->
    <div class="popup-fundo" id="popup-alterar-senha">
        <aside class="gaveta" role="dialog" aria-modal="true" aria-labelledby="popup-alterar-senha-titulo">
            <header class="gaveta-topo">
                <div>
                    <h2 class="popup-titulo" id="popup-alterar-senha-titulo">Alterar senha</h2>
                    <p class="popup-texto">Escolha uma senha nova para a sua conta.</p>
                </div>
                <button class="botao-fechar" type="button" aria-label="Fechar" data-fechar>
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
                </button>
            </header>
            <form class="gaveta-form" method="post" action="${ctx}/admin/conta">
                <input type="hidden" name="action" value="alterarSenha">
                <div class="gaveta-corpo">
                    <p class="gaveta-secao">Nova senha</p>
                    <div class="filtro">
                        <label for="senha-atual">Senha atual <span class="obrigatorio">*</span></label>
                        <input id="senha-atual" name="senhaAtual" type="password" required autocomplete="current-password">
                    </div>
                    <div class="filtro">
                        <label for="senha-nova">Nova senha <span class="obrigatorio">*</span></label>
                        <input id="senha-nova" name="senhaNova" type="password" placeholder="De 8 a 15 caracteres" required minlength="8" maxlength="15" autocomplete="new-password">
                    </div>
                    <div class="filtro">
                        <label for="senha-confirmar">Confirmar nova senha <span class="obrigatorio">*</span></label>
                        <input id="senha-confirmar" name="senhaConfirma" type="password" required minlength="8" maxlength="15" autocomplete="new-password">
                    </div>
                </div>
                <footer class="gaveta-rodape">
                    <button class="botao-secundario" type="button" data-fechar>Cancelar</button>
                    <button class="botao-principal" type="submit">Alterar senha</button>
                </footer>
            </form>
        </aside>
    </div>

<%@ include file="rodape.jspf" %>
