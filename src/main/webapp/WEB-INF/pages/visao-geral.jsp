<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <title>Visão geral - Frigus Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/painel.css">
</head>
<body>
<c:set var="menuAtivo" value="visao-geral" scope="request"/>
<%@ include file="menu.jspf" %>
<main>
    <header>
        <h1>Visão geral</h1>
        <p>Resumo de tudo que está cadastrado, de todos os usuários e empresas.</p>
    </header>

    <%-- totais por tabela: cada cartão leva para a tela da tabela --%>
    <section>
        <h2>Totais por tabela</h2>
        <ul>
            <li><a href="${pageContext.request.contextPath}/admin/usuarios">Usuários</a>: <strong>${totais.usuarios}</strong> (${totais.usuariosPagos} em plano pago)</li>
            <li><a href="${pageContext.request.contextPath}/admin/empresas">Empresas</a>: <strong>${totais.empresas}</strong> (${totais.empresasComercial} no plano Comercial)</li>
            <li><a href="${pageContext.request.contextPath}/admin/geladeiras">Geladeiras</a>: <strong>${totais.geladeiras}</strong> (de ${totais.usuariosComGeladeira} usuários)</li>
            <li><a href="${pageContext.request.contextPath}/admin/frigorificos">Frigoríficos</a>: <strong>${totais.frigorificos}</strong> (de ${totais.empresasComFrigorifico} empresas)</li>
            <li><a href="${pageContext.request.contextPath}/admin/produtos">Produtos</a>: <strong>${totais.itensGeladeira + totais.itensFrigorifico}</strong> (${totais.produtosAtencao} vencidos ou com atenção)</li>
            <li><a href="${pageContext.request.contextPath}/admin/receitas">Receitas</a>: <strong>${totais.receitas}</strong> (${totais.receitasUsuarios} de usuários e ${totais.receitasEmpresas} de empresas)</li>
            <li><a href="${pageContext.request.contextPath}/admin/compras">Itens em listas</a>: <strong>${totais.itensLista}</strong> (em ${totais.listas} listas)</li>
            <li><a href="${pageContext.request.contextPath}/admin/planos">Assinaturas</a>: <strong>${totais.assinaturas}</strong> (${totalPlanos} planos disponíveis)</li>
        </ul>
    </section>

    <%-- assinaturas e receita mensal: o valor de cada plano multiplicado pelo número de assinaturas --%>
    <section>
        <h2>Assinaturas e receita mensal</h2>
        <p>Valor de cada plano multiplicado pelo número de assinaturas.</p>
        <p>Receita total: <strong>R$ <c:out value="${receitaTotal}"/></strong> por mês, em ${totais.assinaturas} assinaturas.</p>
        <table>
            <thead>
                <tr><th>Plano</th><th>Assinaturas</th><th>Receita por mês (R$)</th></tr>
            </thead>
            <tbody>
                <c:forEach items="${receitaPorPlano}" var="plano">
                    <tr>
                        <td><c:out value="${plano.key}"/></td>
                        <td><c:out value="${plano.value[0]}"/></td>
                        <td><c:out value="${plano.value[1]}"/></td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
        <p><a href="${pageContext.request.contextPath}/admin/planos">Ver planos e assinaturas</a></p>
    </section>
</main>
</body>
</html>
