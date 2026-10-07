<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>JSP - Hello World</title>
<style>
    html{
        background-color: black;
        color: aqua;
    }
</style>

</head>
<body>
<h1><%= "Hello World!" %>
</h1>
<br/>
</form>
<a href="hello-servlet">Hello Servlet</a>
<br>
<a href="${pageContext.request.contextPath}/assets/Pages/Login.jsp">Cadastrar usuario</a>
</body>
</html>