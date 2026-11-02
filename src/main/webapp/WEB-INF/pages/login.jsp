<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="tituloPagina" value="Frigus | Entrar" scope="request"/>
<c:set var="urlVoltar" value="${pageContext.request.contextPath}/index.html" scope="request"/>
<%@ include file="acesso-topo.jspf" %>
          <h1>Bem-vindo de volta</h1>
          <p class="subtitulo">Entre para continuar cuidando melhor da sua rotina.</p>
          <c:if test="${not empty erro}">
            <p class="erro" role="alert"><c:out value="${erro}"/></p>
          </c:if>
          <c:if test="${not empty sucesso}">
            <p class="sucesso" role="status"><c:out value="${sucesso}"/></p>
          </c:if>
          <form method="post" action="${ctx}/login">
            <div class="campo">
              <label for="email">Email</label>
              <div class="caixa">
                <input type="text" inputmode="email" id="email" name="email" value="<c:out value='${param.email}'/>" placeholder="Digite seu email" required>
              </div>
            </div>
            <div class="campo">
              <label for="senha">Senha</label>
              <div class="caixa">
                <input type="password" id="senha" name="senha" placeholder="Digite sua senha" required>
                <button class="olho" type="button" aria-label="Mostrar ou ocultar"><img src="${ctx}/assets/img/icone-olho.svg" alt=""></button>
              </div>
            </div>
            <div class="opcoes">
              <label class="checkbox"><input type="checkbox" id="manter" name="manter"> Manter conectado</label>
            </div>
            <button class="botao" type="submit">Continuar</button>
          </form>
          <p class="troca">Ainda não tem uma conta? <a class="link" href="${ctx}/cadastro">Cadastre-se</a></p>
<%@ include file="acesso-rodape.jspf" %>
