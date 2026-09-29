<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Cadastro</title>
    <div>
      <form action="LogarUsuario" method="post">
        <label for="cpf"><b>Cpf</b></label>
        <input type="text" id="cpf" name="cpf"
        placeholder="Digite o seu cpf">
        <br>
        <label for="nome"><b>Nome</b></label>
        <input type="text" id="nome" name="nome"
        placeholder="Digite seu nome completo">
        <br>
        <label for="email"><b>E-mail</b></label>
        <input type="email" id="email" name="email"
        placeholder="Digite seu email">
        <br>
        <label for="senha"><b>Senha</b></label>
        <input type="password" id="senha" name="senha"
        placeholder="Digite sua senha">
        <br>
        <label for="telefone"><b>Telefone</b></label>
        <input type="text" id="telefone" name="telefone"
        placeholder="Digite seu telefone">

        
        <br>
        <br>
        <button type="submit" name="botao" value="inserir">Salvar</button>
      
      </form>
    </div>
</head>
<body>

</body>
</html>
