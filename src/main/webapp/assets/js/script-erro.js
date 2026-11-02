/* ==========================================================================
   Script das telas de erro (404, 401, 403 e 500)
   1. Neve caindo
   Desenha flocos num <canvas class="neve"> que fica atrás do conteúdo.
   A cor vem da variável CSS --s-neve de cada tela.
   ========================================================================== */
(function () {
  'use strict';

  var canvas = document.querySelector('.neve');
  if (!canvas || !canvas.getContext) return;

  // Quem prefere menos movimento não vê a animação
  if (window.matchMedia && window.matchMedia('(prefers-reduced-motion: reduce)').matches) return;

  var ctx = canvas.getContext('2d');
  var cor = (getComputedStyle(canvas).getPropertyValue('--s-neve') || '').trim() || 'rgba(255, 255, 255, 0.85)';

  /* ----- Ajustes (mexa aqui para deixar mais calmo ou mais forte) ----- */
  var DENSIDADE = 1 / 7000;   // flocos por pixel² de tela
  var MAXIMO = 220;           // limite de flocos
  var VELOCIDADE = 1;         // 1 = lento; aumente para cair mais rápido
  var VENTO = 22;             // força média do vento lateral (px/s)

  var largura = 0;
  var altura = 0;
  var flocos = [];
  var ultimo = 0;
  var tempo = 0;

  function aleatorio(min, max) {
    return min + Math.random() * (max - min);
  }

  // Cada floco tem uma "profundidade": os perto são maiores, mais rápidos e mais visíveis
  function criarFloco(noTopo) {
    var profundidade = Math.random();
    return {
      x: aleatorio(0, largura),
      y: noTopo ? aleatorio(-40, 0) : aleatorio(0, altura),
      raio: 0.8 + profundidade * 3.2,
      velocidade: 14 + profundidade * 34,
      opacidade: 0.35 + profundidade * 0.65,
      fase: aleatorio(0, Math.PI * 2),
      balanco: aleatorio(0.4, 1.2)
    };
  }

  function ajustarTamanho() {
    var escala = Math.min(window.devicePixelRatio || 1, 2);
    largura = canvas.clientWidth;
    altura = canvas.clientHeight;
    canvas.width = Math.round(largura * escala);
    canvas.height = Math.round(altura * escala);
    ctx.setTransform(escala, 0, 0, escala, 0, 0);

    var total = Math.min(MAXIMO, Math.round(largura * altura * DENSIDADE));
    while (flocos.length < total) flocos.push(criarFloco(false));
    flocos.length = total;
  }

  function desenhar(agora) {
    var dt = Math.min((agora - ultimo) / 1000, 0.05);
    ultimo = agora;
    tempo += dt;

    // Rajadas lentas de vento: vai e volta, como numa tempestade calma
    var vento = VENTO * (1 + 0.9 * Math.sin(tempo * 0.35) + 0.4 * Math.sin(tempo * 0.93 + 1.3));

    ctx.clearRect(0, 0, largura, altura);
    ctx.fillStyle = cor;

    for (var i = 0; i < flocos.length; i++) {
      var f = flocos[i];

      f.y += f.velocidade * VELOCIDADE * dt;
      f.x += (vento * (0.4 + f.raio / 4) + Math.sin(tempo * f.balanco + f.fase) * 12) * dt;

      // Saiu da tela: volta pelo outro lado
      if (f.y - f.raio > altura) {
        f.y = -f.raio;
        f.x = aleatorio(0, largura);
      }
      if (f.x - f.raio > largura) f.x = -f.raio;
      if (f.x + f.raio < 0) f.x = largura + f.raio;

      ctx.globalAlpha = f.opacidade;
      ctx.beginPath();
      ctx.arc(f.x, f.y, f.raio, 0, Math.PI * 2);
      ctx.fill();
    }

    requestAnimationFrame(desenhar);
  }

  ajustarTamanho();
  window.addEventListener('resize', ajustarTamanho);
  requestAnimationFrame(function (agora) {
    ultimo = agora;
    desenhar(agora);
  });
})();

/* ==========================================================================
   2. BOTÕES
   data-acao="voltar" volta uma página; data-acao="recarregar" tenta a página de novo
   ========================================================================== */
document.addEventListener('click', function (e) {
  var botao = e.target.closest('[data-acao]');
  if (!botao) return;
  if (botao.dataset.acao === 'voltar') window.history.back();
  if (botao.dataset.acao === 'recarregar') window.location.reload();
});
