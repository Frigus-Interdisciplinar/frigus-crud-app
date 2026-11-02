<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="menuAtivo" value="visao-geral" scope="request"/>
<c:set var="tituloPagina" value="Visão geral" scope="request"/>
<%@ include file="cabecalho.jspf" %>

    <div class="conteudo" id="conteudo">
        <!-- Título da página -->
        <div class="page-header">
            <h1>Visão geral</h1>
            <p class="subtitulo-pagina">Resumo de tudo que está cadastrado, de todos os usuários e empresas.</p>
        </div>

        <!-- Cards pequenos com os totais -->
        <div class="caixa-cards-pequenos-visao-geral">
            <a class="cards-pequenos-visao-geral" href="${ctx}/admin/usuarios">
                <div class="titulo-cards">Usuários</div>
                <div class="dados-cards">${totais.usuarios}</div>
                <div class="texto-pequeno-cards">${totais.usuariospagos} em plano pago</div>
            </a>

            <a class="cards-pequenos-visao-geral" href="${ctx}/admin/empresas">
                <div class="titulo-cards">Empresas</div>
                <div class="dados-cards">${totais.empresas}</div>
                <div class="texto-pequeno-cards">todas no plano Comercial</div>
            </a>
            <a class="cards-pequenos-visao-geral" href="${ctx}/admin/geladeiras">
                <div class="titulo-cards">Geladeiras</div>
                <div class="dados-cards">${totais.geladeiras}</div>
                    <div class="texto-pequeno-cards">de ${totais.usuarioscomgeladeira} usuários</div>
            </a>
            <a class="cards-pequenos-visao-geral" href="${ctx}/admin/frigorificos">
                <div class="titulo-cards">Frigoríficos</div>
                <div class="dados-cards">${totais.frigorificos}</div>
                <div class="texto-pequeno-cards">de ${totais.empresascomfrigorifico} empresas</div>
            </a>
            <a class="cards-pequenos-visao-geral" href="${ctx}/admin/produtos">
                <div class="titulo-cards">Produtos</div>
                <div class="dados-cards">${totais.itensgeladeira + totais.itensfrigorifico}</div>
                <div class="texto-pequeno-cards">${totais.produtosatencao} vencidos ou com atenção</div>
            </a>
            <a class="cards-pequenos-visao-geral" href="${ctx}/admin/receitas">
                <div class="titulo-cards">Receitas</div>
                <div class="dados-cards">${totais.receitas}</div>
                <div class="texto-pequeno-cards">de usuários e de empresas</div>
            </a>
            <a class="cards-pequenos-visao-geral" href="${ctx}/admin/compras">
                <div class="titulo-cards">Itens em listas</div>
                <div class="dados-cards">${totais.itenslista}</div>
                <div class="texto-pequeno-cards">em ${totais.listas} listas</div>
            </a>
            <a class="cards-pequenos-visao-geral" href="${ctx}/admin/planos">
                <div class="titulo-cards">Assinaturas</div>
                <div class="dados-cards">${totais.assinaturas}</div>
                <div class="texto-pequeno-cards">${totalPlanos} planos disponíveis</div>
            </a>
        </div>

        <!-- Cards grandes com os gráficos -->
        <div class="caixa-cards-grandes-visao-geral">
            <!-- Crescimento de contas (gráfico de linhas) -->
            <div class="cards-grandes-visao-geral card-grafico">
                <div class="grafico-titulo">Crescimento de contas</div>
                <p class="grafico-subtitulo">Usuários e empresas cadastrados, acumulado por mês em <span id="ano-grafico"></span>.</p>

                <div class="grafico-destaque">
                    <div>
                        <span class="grafico-numero" id="total-contas"></span>
                        <span class="grafico-unidade">contas hoje</span>
                    </div>
                    <span class="grafico-variacao" id="variacao-contas"></span>
                </div>

                <div class="grafico-area" id="area-grafico-crescimento" data-crescimento="<c:out value='${crescimento}'/>">
                    <canvas id="grafico-crescimento"></canvas>
                </div>
            </div>

            <!-- Assinaturas e receita mensal (gráfico de barras) -->
            <div class="cards-grandes-visao-geral card-grafico">
                <div class="grafico-titulo">Assinaturas e receita mensal</div>
                <p class="grafico-subtitulo">Valor de cada plano multiplicado pelo número de assinaturas.</p>

                <div class="grafico-destaque">
                    <div>
                        <span class="grafico-numero" id="total-receita"></span>
                        <span class="grafico-unidade">por mês</span>
                    </div>
                    <span class="grafico-variacao" id="total-assinaturas"></span>
                </div>

                <div class="grafico-area" id="area-grafico-receita" data-total="${receitaTotal}" data-assinaturas="${totais.assinaturas}"
                     data-planos="<c:forEach items="${receitaPorPlano}" var="plano" varStatus="s">${plano.key}:${plano.value[0]}:${plano.value[1]}${s.last ? '' : ';'}</c:forEach>">
                    <canvas id="grafico-receita"></canvas>
                </div>

                <a class="botao-ver-planos" href="${ctx}/admin/planos">Ver planos e assinaturas</a>
            </div>
        </div>

    
    </div>

    <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.7/dist/chart.umd.min.js"></script>
    <script src="${ctx}/assets/js/grafico-visao-geral.js"></script>
</body>
</html>
