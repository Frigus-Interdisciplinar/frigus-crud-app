<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, viewport-fit=cover">
    <meta name="robots" content="noindex">
    <title>Erro 401 - Friguinho</title>

    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Fredoka:wght@500;600;700&family=Nunito:wght@400;600;700;800&display=swap" rel="stylesheet">

    <link rel="icon" type="image/svg+xml" href="${ctx}/assets/img/favicon.svg?v=2">
    <link rel="stylesheet" href="${ctx}/assets/css/style-erro.css">
    <link rel="stylesheet" href="${ctx}/assets/css/style-carregando.css">
    <script src="${ctx}/assets/js/script-erro.js" defer></script>
    <script src="${ctx}/assets/js/script-carregando.js" defer></script>
</head>
<body>
    <div class="scene t-ice">
        <!-- Decoração: neve ao fundo -->
        <div class="flakes" aria-hidden="true">
            <img class="sf sf--1" src="${ctx}/assets/img/erro/floco-claro.svg" alt="">
            <img class="sf sf--2" src="${ctx}/assets/img/erro/floco-claro.svg" alt="">
            <img class="sf sf--3" src="${ctx}/assets/img/erro/floco-claro.svg" alt="">
            <img class="sf sf--4" src="${ctx}/assets/img/erro/floco-claro.svg" alt="">
        </div>

        <!-- Neve caindo (animada pelo neve.js) -->
        <canvas class="neve" aria-hidden="true"></canvas>

        <header class="topbar">
            <div class="logo">
                <img class="logo-mark" src="${ctx}/assets/img/erro/floco-azul.svg" alt="Friguinho">
            </div>
        </header>

        <main class="stage">
            <div class="copy">
                <p class="eyebrow">Erro 401</p>
                <h1 class="title">Sessão expirada</h1>
                <p class="lead">O Friguinho bateu na porta e ninguém soube dizer quem era. Seu acesso venceu, então é preciso entrar de novo.</p>

                <div class="actions">
                    <a class="btn btn--primary" href="${ctx}/login">
                        <svg class="ic" viewBox="0 0 24 24" aria-hidden="true">
                            <path d="M4 12h11M11 8l4 4-4 4M15 4h4a1 1 0 0 1 1 1v14a1 1 0 0 1-1 1h-4"/>
                        </svg>
                        Entrar novamente
                    </a>
                </div>

                <p class="tip"><span>Dica</span>Clique em "Entrar novamente" e faça seu login.</p>
            </div>

            <figure class="pg">
                <div class="halo"></div>
                <div class="shadow"></div>
                <div class="ghost" aria-hidden="true">401</div>
                <img src="${ctx}/assets/img/erro/espera.png" alt="Friguinho encolhido, olhando para cima, com um balão de relógio">
            </figure>
        </main>

        <!-- Decoração: montes de neve na base -->
        <svg class="drift" viewBox="0 0 1440 140" preserveAspectRatio="none" aria-hidden="true">
            <path class="d2" d="M0 66C180 18 360 22 540 60S900 34 1100 40 1360 70 1440 48V140H0Z"/>
            <path class="d1" d="M0 100C200 62 380 82 600 96S1000 70 1200 86 1400 100 1440 92V140H0Z"/>
        </svg>
    </div>
</body>
</html>
