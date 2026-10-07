<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="SenhaUsuario.css">
    <title>Cadastrar</title>
</head>
<body>
    <h1>Frigus</h1>
    <img src="assets/images/floco de neve.png" alt="floco de neve" id="floco">
    <img src="assets/images/onda 1.png" alt="onda para estilizar a pagina" id="onda1">
    <img src="assets/images/onda 2.png" alt="onda para estilizar a pagina" id="onda2">
    
    <div class="Vidro">
        <div class="Preto">
            <h2>Crie uma senha</h2>
            <p>Mantenha a sua conta segura criando uma senha</p>
            <br>
            <form action="/SenhaUsuario">
                <label for="senha">Senha</label>
                <br>
                <input type="password" name="senha" placeholder="Crie uma senha">
                <br>
                <label for="confirmarSenha">Confirmar senha</label>
                <br>
                <input type="password"  name="confirmarSenha" placeholder="Confirme sua senha">
                <br>
            </form>
            <a href="PlanoUsuario.jsp">Continuar</a>
            <p id="conta">Já tem conta? &emsp;<a href="Login.jsp">Entrar</a></p>
        </div>
    </div>
    <br>
    <p id="direitos">© 2026 Frigus. Todos os direitos reservados.</p>
</body>
</html>