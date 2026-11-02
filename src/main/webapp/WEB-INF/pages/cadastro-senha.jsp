<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%-- segunda tela do cadastro: senha e confirmação ( o nome e o e-mail ficam guardados na sessão ) --%>
<c:set var="tituloPagina" value="Frigus | Crie uma senha" scope="request"/>
<c:set var="urlVoltar" value="${pageContext.request.contextPath}/cadastro" scope="request"/>
<%@ include file="acesso-topo.jspf" %>
          <h1>Crie uma senha</h1>
          <p class="subtitulo">Mantenha a sua conta segura criando uma senha</p>
          <c:if test="${not empty erro}">
            <p class="erro" role="alert"><c:out value="${erro}"/></p>
          </c:if>
          <form method="post" action="${ctx}/cadastro/senha">
            <div class="campo">
              <label for="senha">Senha</label>
              <div class="caixa">
                <input type="password" id="senha" name="senha" placeholder="Crie uma senha" required minlength="8" maxlength="15" autocomplete="new-password">
                <button class="olho" type="button" aria-label="Mostrar ou ocultar"><img src="${ctx}/assets/img/icone-olho.svg" alt=""></button>
              </div>
            </div>
            <div class="campo">
              <label for="confirmarSenha">Confirmar senha</label>
              <div class="caixa">
                <input type="password" id="confirmarSenha" name="confirmarSenha" placeholder="Confirme sua senha" required minlength="8" maxlength="15" autocomplete="new-password">
                <button class="olho" type="button" aria-label="Mostrar ou ocultar"><img src="${ctx}/assets/img/icone-olho.svg" alt=""></button>
              </div>
            </div>
            <button class="botao" type="submit">Continuar</button>
          </form>
          <p class="troca">Já tem uma conta? <a class="link" href="${ctx}/login">Entrar</a></p>
<%@ include file="acesso-rodape.jspf" %>
