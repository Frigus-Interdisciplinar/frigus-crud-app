<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%-- primeira tela do cadastro: nome e e-mail ( a senha vem na tela seguinte ) --%>
<c:set var="tituloPagina" value="Frigus | Crie sua conta" scope="request"/>
<c:set var="urlVoltar" value="${pageContext.request.contextPath}/login" scope="request"/>
<%@ include file="acesso-topo.jspf" %>
          <h1>Crie sua conta</h1>
          <p class="subtitulo">Leva menos de dois minutos para começar.</p>
          <c:if test="${not empty erro}">
            <p class="erro" role="alert"><c:out value="${erro}"/></p>
          </c:if>
          <form method="post" action="${ctx}/cadastro">
            <div class="campo">
              <label for="nome">Nome completo</label>
              <div class="caixa">
                <input type="text" id="nome" name="nome" value="<c:out value='${nomeDigitado}'/>" placeholder="Digite seu nome" required maxlength="20">
              </div>
            </div>
            <div class="campo">
              <label for="email">Email</label>
              <div class="caixa">
                <input type="text" inputmode="email" id="email" name="email" value="<c:out value='${emailDigitado}'/>" placeholder="Digite seu email" required>
              </div>
            </div>
            <div class="opcoes">
              <label class="checkbox"><input type="checkbox" id="termos" name="termos" required><span>Li e aceito os <a class="link" href="${ctx}/suporte.html#privacidade" target="_blank" rel="noopener noreferrer">Termos de uso</a> e a <a class="link" href="${ctx}/suporte.html#privacidade" target="_blank" rel="noopener noreferrer">Política de privacidade.</a></span></label>
            </div>
            <button class="botao" type="submit">Continuar</button>
          </form>
          <p class="troca">Já tem uma conta? <a class="link" href="${ctx}/login">Entrar</a></p>
<%@ include file="acesso-rodape.jspf" %>
