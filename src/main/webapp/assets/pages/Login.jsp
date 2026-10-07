<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Cadastrar</title>
    <link rel="stylesheet" href="../css/Planos.css">
</head>
<body>
    <header>
        <h1>Frigus</h1>
        <img src="assets/images/floco de neve.png" alt="floco de neve" id="floco">
    </header>
    <main>
            
        <img src="assets/images/onda 1.png" alt="onda para estilizar a pagina" id="onda1">
        <img src="assets/images/onda 2.png" alt="onda para estilizar a pagina" id="onda2">
        
        <div class="Vidro">
            <div class="Preto">
                <h2>Bem-vindo de volta</h2>
                <p>Entre para continuar cuidando melhor da sua rotina.</p>
                <br>
                <form action="/Login">
                    <label for="email">E-mail</label>
                    <br>
                    <input type="email" name="email"  placeholder="Digite seu E-mail">
                    <br>
                    <label for="senha">Senha</label>
                    <br>
                    <input type="password" name="senha"  placeholder="Digite sua senha">
                    <br>
                    <input type="checkbox" id="conectado" name="conectado" value="conectado">
                    <label for="conectado">Manter conectado &emsp; &emsp; &emsp; <a href="../pages/CodigoEmail">Esqueci minha senha</a></label>
                </form>
                <a href="../pages/#">Continuar</a>
                <p>Ainda não tem conta?</a></p>
                <a href="${pageContext.request.contextPath}/assets/Pages/Planos.jsp">Cadastrar-se</a>
            </div>
        </div>
        <br>
    </main>
    <footer>
        <p id="direitos">© 2026 Frigus. Todos os direitos reservados.</p>
    </footer>
</body>
</html>