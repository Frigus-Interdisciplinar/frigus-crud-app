<!DOCTYPE html>
<html lang="pt-br">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Criar Conta</title>

    
</head>

<body>
    <img src="assets/images/onda 1.png" class="onda onda1" alt="onda ilustrativa">
    <img src="assets/images/onda 2.png" class="onda onda2" alt="onda ilustrativa">
    <header>
        <img src="assets/images/floco de neve.png" alt="Logo Frigus" class="logo-floco">
        <h1>Frigus</h1>
    </header>


    <main>
        <div class="vidro">
            <div class="card">
                <h2>Crie sua conta</h2>
                <p class="subtitulo">
                    Leva menos de dois minutos para começar.
                </p>


                <form action="/CriarUsuario">
                    <br>
                    <label for="nome">
                        Nome completo
                    </label>
                    <br>
                    <input
                        type="text"
                        name="nome"
                        placeholder="Digite seu nome"
                    >
                    <br>

                    <label for="email">
                        E-mail
                    </label>

                    <br>
                    <input
                        type="email"
                        name="email"
                        placeholder="Digite seu email"
                    >

                    <br>
                    <label for="cpf">
                        CPF
                    </label>
                    <br>
                    <input
                        type="text"
                        name="cpf"
                        placeholder="Digite seu cpf"
                    >

                    <br>
                    <div class="termos">

                        <input
                            type="checkbox"
                            id="privacidade"
                            name="privacidade"
                        >

                        <label for="privacidade">

                            Li e aceito os

                            <a href="#">
                                Termos de uso e a Política de privacidade.
                            </a>

                        </label>

                    </div>


                    <a
                        href="SenhaUsuario.jsp"
                        class="botao"
                    >

                        Continuar

                    </a>

                </form>


                <p class="entrar">

                    Já tem uma conta?

                    <a href="Login.jsp">
                        Entrar
                    </a>

                </p>


            </div>

        </div>

    </main>


    <footer>

        © 2026 Frigus. Todos os direitos reservados.

    </footer>


</body>

</html>