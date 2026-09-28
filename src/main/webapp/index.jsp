<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>JSP - Hello World</title>
</head>
<body>
<h1><%= "Hello World!" %>
</h1>
<br/>
<form action="/listar">
    <input name="idProduto" placeholder="digite o id do produto">

    <button type="submit">Enviar</button>
</form>
<a href="hello-servlet">Hello Servlet</a>
<br>
<a href="LogarUsuario.jsp">Cadastrar usuario</a>
</body>
</html>