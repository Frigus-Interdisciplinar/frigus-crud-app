/* ==========================================================================
   FRIGUINHO ANIMADO (página "Sobre nós")
   Toca a animação do pinguim (assets/animacoes/friguinho-sobre-nos.riv, feita no Rive) dentro do
   <canvas id="friguinho-animado"> do hero "Quem Somos".

   Precisa da biblioteca do Rive (carregada antes, no <head>) e deste arquivo.
   ========================================================================== */
(() => {
  const canvas = document.getElementById('friguinho-animado');
  if (!canvas || typeof rive === 'undefined') return;

  const reduzirMovimento = window.matchMedia('(prefers-reduced-motion: reduce)').matches;

  /* ----- Ajustes (mexa aqui) ------------------------------------------- */
  const ARQUIVO = 'assets/animacoes/friguinho-sobre-nos.riv';
  const PRANCHETA = 'Frame';        // nome da prancheta (artboard) no Rive
  const ANIMACAO = 'versao_2';      // nome da animação em loop

  // Usa a animação embutida em friguinho-dados.js (funciona até com duplo clique
  // no HTML); se ela não estiver carregada, busca o arquivo .riv normalmente.
  const origem = window.FRIGUINHO_RIV_BASE64
    ? { buffer: Uint8Array.from(atob(window.FRIGUINHO_RIV_BASE64), c => c.charCodeAt(0)).buffer }
    : { src: ARQUIVO };

  const friguinho = new rive.Rive({
    ...origem,
    canvas,
    artboard: PRANCHETA,
    animations: ANIMACAO,
    autoplay: !reduzirMovimento,    // quem pede menos movimento vê só o primeiro quadro
    layout: new rive.Layout({ fit: rive.Fit.Contain }),
    onLoad: ajustarTamanho,
  });

  // Mantém a resolução do canvas nítida em qualquer tamanho de tela
  function ajustarTamanho() {
    friguinho.resizeDrawingSurfaceToCanvas();
  }
  new ResizeObserver(ajustarTamanho).observe(canvas);

  // Pausa quando o pinguim sai da tela, para não gastar processamento
  if (!reduzirMovimento) {
    new IntersectionObserver(([entrada]) => {
      entrada.isIntersecting ? friguinho.play(ANIMACAO) : friguinho.pause();
    }).observe(canvas);
  }
})();
