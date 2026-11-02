/* ==========================================================================
   SCRIPT DA TELA DE CARREGAMENTO
   Basta colocar este script e o style-carregando.css em qualquer página:
   a tela é montada aqui mesmo, então todas as páginas usam o mesmo código.
   1. Montagem da tela
   2. Quando mostrar (a pessoa trocou de tela)
   3. Barra de progresso
   4. Friguinho (animação do Rive)
   ========================================================================== */

// guarda quem é este script para descobrir onde ficam as imagens e a animação (funciona em qualquer página)
const SCRIPT_CARREGANDO = document.currentScript;

document.addEventListener('DOMContentLoaded', () => {
  /* ----- 1. Montagem da tela ----- */

  function montarTela() {
    const existente = document.getElementById('carregando');
    if (existente) return existente;

    const raiz = SCRIPT_CARREGANDO ? SCRIPT_CARREGANDO.src.replace(/assets\/js\/script-carregando\.js.*$/, '') : '';
    const flocos = ['carregando-floco--1', 'carregando-floco--2', 'carregando-floco--3', 'carregando-floco--4']
      .map(classe => '<img class="carregando-floco ' + classe + '" src="' + raiz + 'assets/img/erro/floco-claro.svg" alt="">').join('');

    const caixa = document.createElement('div');
    caixa.className = 'carregando';
    caixa.id = 'carregando';
    caixa.hidden = true;
    caixa.setAttribute('role', 'status');
    caixa.setAttribute('aria-live', 'polite');
    caixa.dataset.riv = raiz + 'assets/animacoes/friguinho.riv';
    caixa.innerHTML =
      '<div class="carregando-flocos" aria-hidden="true">' + flocos + '</div>' +
      '<div class="neve-css" aria-hidden="true"></div>' +
      '<div class="carregando-centro">' +
        '<div class="friguinho" aria-hidden="true">' +
          '<div class="friguinho-halo"></div>' +
          '<canvas class="friguinho-fonte" width="500" height="500"></canvas>' +
          '<canvas class="friguinho-tela" width="500" height="500"></canvas>' +
        '</div>' +
        '<h2 class="carregando-titulo">Carregando...</h2>' +
        '<p class="carregando-texto">O Friguinho está organizando a sua geladeira.</p>' +
        '<div class="barra" role="progressbar" aria-label="Progresso do carregamento" aria-valuemin="0" aria-valuemax="100" aria-valuenow="0">' +
          '<div class="barra-cheia"></div>' +
        '</div>' +
        '<p class="carregando-dica"><strong>Dica</strong><span class="dica-texto"></span></p>' +
      '</div>' +
      '<svg class="carregando-monte" viewBox="0 0 1440 140" preserveAspectRatio="none" aria-hidden="true">' +
        '<path class="m2" d="M0 66C180 18 360 22 540 60S900 34 1100 40 1360 70 1440 48V140H0Z"/>' +
        '<path class="m1" d="M0 100C200 62 380 82 600 96S1000 70 1200 86 1400 100 1440 92V140H0Z"/>' +
      '</svg>';
    document.body.appendChild(caixa);
    return caixa;
  }

  const tela = montarTela();

  const barra = tela.querySelector('.barra');
  const barraCheia = tela.querySelector('.barra-cheia');
  const textoDica = tela.querySelector('.dica-texto');

  // a animação (Rive) vem de uma biblioteca na internet; se ela não carregar, a tela funciona sem o Friguinho
  const RIVE_JS = 'https://cdn.jsdelivr.net/npm/@rive-app/canvas@2.49.1/rive.js';
  const GRAFICO_CINZA = 40; // o fundo do desenho do Friguinho é um cinza (40, 40, 40) que vamos apagar

  const dicas = [
    'Os itens perto de vencer aparecem primeiro na sua lista.',
    'Use os filtros para achar um registro mais rápido.',
    'O olhinho abre os detalhes sem sair da lista.',
    'Em Configurações você troca o seu nome e a sua senha.',
    'A visão geral mostra tudo que está cadastrado em um lugar só.'
  ];

  /* ----- 2. Quando mostrar ----- */

  let visivel = false;

  function mostrar() {
    if (visivel) return;
    visivel = true;
    textoDica.textContent = dicas[Math.floor(Math.random() * dicas.length)];
    tela.hidden = false;
    // espera o navegador desenhar a tela antes de ligar o fade
    requestAnimationFrame(() => tela.classList.add('ativo'));
    iniciarBarra();
    iniciarFriguinho();
  }

  function esconder() {
    clearTimeout(adiar);
    if (!visivel) return;
    visivel = false;
    tela.classList.remove('ativo');
    tela.hidden = true;
    pararBarra();
    pararFriguinho();
  }

  // a tela aparece quando a troca demora mais que isso (se a página nova abrir antes, ela nem pisca)
  const ATRASO_MS = 120;
  let adiar = 0;

  function sair() {
    // guarda a hora de saída para medir quanto a próxima página demora
    try { sessionStorage.setItem('carregandoInicio', String(Date.now())); } catch (e) { /* sem armazenamento, segue sem medir */ }
    clearTimeout(adiar);
    adiar = setTimeout(mostrar, ATRASO_MS);
  }

  // clicou num link que leva para outra página
  document.addEventListener('click', e => {
    if (e.defaultPrevented || e.button !== 0 || e.metaKey || e.ctrlKey || e.shiftKey || e.altKey) return;
    const link = e.target.closest('a[href]');
    if (!link || link.hasAttribute('download')) return;
    if (link.target && link.target !== '_self') return;

    const destino = new URL(link.href, window.location.href);
    if (destino.origin !== window.location.origin) return;
    if (link.getAttribute('href').startsWith('#')) return;
    sair();
  });

  // enviou um formulário (cadastro, login, filtros, salvar, excluir)
  document.addEventListener('submit', e => {
    if (e.defaultPrevented) return;
    if (e.target.target && e.target.target !== '_self') return;
    sair();
  });

  // qualquer outra saída (ex.: filtro que envia sozinho pelo script)
  window.addEventListener('beforeunload', sair);

  // voltar pelo botão do navegador pode trazer a página antiga com a tela ainda aberta
  window.addEventListener('pageshow', e => {
    if (e.persisted) esconder();
  });

  // para quem quiser chamar de outro lugar (ex.: testes)
  window.Carregando = { mostrar, esconder };

  /* ----- 3. Barra de progresso ----- */

  // o navegador não avisa quanto falta enquanto o servidor busca no banco;
  // por isso a barra anda pela média do tempo das últimas trocas de tela (sobe rápido e desacelera, sem chegar a 100%)
  const PADRAO_MS = 2500;
  let estimativa = PADRAO_MS;
  let quadroBarra = 0;
  let inicioBarra = 0;

  try {
    const media = Number(localStorage.getItem('carregandoMedia'));
    if (media > 300) estimativa = media;

    // esta página acabou de abrir: mede quanto durou a troca e atualiza a média
    const saida = Number(sessionStorage.getItem('carregandoInicio'));
    if (saida) {
      const duracao = Date.now() - saida;
      if (duracao > 100 && duracao < 30000) {
        estimativa = Math.round(estimativa * 0.6 + duracao * 0.4);
        localStorage.setItem('carregandoMedia', String(estimativa));
      }
      sessionStorage.removeItem('carregandoInicio');
    }
  } catch (e) { /* sem armazenamento, usa o tempo padrão */ }

  function desenharBarra(agora) {
    const decorrido = agora - inicioBarra;
    const porcentagem = 95 * (1 - Math.exp(-2.5 * decorrido / estimativa));
    barraCheia.style.width = porcentagem.toFixed(1) + '%';
    barra.setAttribute('aria-valuenow', String(Math.round(porcentagem)));
    quadroBarra = requestAnimationFrame(desenharBarra);
  }

  function iniciarBarra() {
    barraCheia.style.width = '2%';
    inicioBarra = performance.now();
    quadroBarra = requestAnimationFrame(desenharBarra);
  }

  function pararBarra() {
    cancelAnimationFrame(quadroBarra);
  }

  /* ----- 4. Friguinho ----- */

  const fonte = tela.querySelector('.friguinho-fonte');
  const vista = tela.querySelector('.friguinho-tela');
  const ctxFonte = fonte.getContext('2d', { willReadFrequently: true });
  const ctxVista = vista.getContext('2d');
  let rive = null;
  let carregouRive = false;
  let quadroDesenho = 0;

  function carregarBiblioteca(depois) {
    if (window.rive) return depois();
    const script = document.createElement('script');
    script.src = RIVE_JS;
    script.onload = depois;
    document.head.appendChild(script);
  }

  function prepararFriguinho() {
    if (carregouRive || !tela.dataset.riv) return;
    carregouRive = true;
    carregarBiblioteca(() => {
      rive = new window.rive.Rive({
        src: tela.dataset.riv,
        canvas: fonte,
        animations: 'versao_2',
        autoplay: false,
        onLoad: () => { if (visivel) iniciarFriguinho(); }
      });
    });
  }

  // copia cada quadro do desenho para a tela de cima apagando o cinza do fundo
  function desenharQuadro() {
    // o Rive muda o tamanho do desenho conforme a tela; a de cima acompanha
    if (vista.width !== fonte.width || vista.height !== fonte.height) {
      vista.width = fonte.width;
      vista.height = fonte.height;
    }
    const quadro = ctxFonte.getImageData(0, 0, fonte.width, fonte.height);
    const pixels = quadro.data;
    for (let i = 0; i < pixels.length; i += 4) {
      const distancia = Math.max(
        Math.abs(pixels[i] - GRAFICO_CINZA),
        Math.abs(pixels[i + 1] - GRAFICO_CINZA),
        Math.abs(pixels[i + 2] - GRAFICO_CINZA)
      );
      // perto do cinza vira transparente; nas bordas a transparência vai diminuindo aos poucos
      const opacidade = distancia < 6 ? 0 : (distancia < 28 ? Math.round((distancia - 6) / 22 * 255) : 255);
      pixels[i + 3] = Math.min(pixels[i + 3], opacidade); // o que o Rive ainda não desenhou continua transparente
    }
    ctxVista.putImageData(quadro, 0, 0);
    quadroDesenho = requestAnimationFrame(desenharQuadro);
  }

  function iniciarFriguinho() {
    if (!rive) return prepararFriguinho();
    rive.resizeDrawingSurfaceToCanvas();
    rive.play();
    cancelAnimationFrame(quadroDesenho);
    quadroDesenho = requestAnimationFrame(desenharQuadro);
  }

  function pararFriguinho() {
    cancelAnimationFrame(quadroDesenho);
    if (rive) rive.pause();
  }

  // deixa a animação pronta enquanto a pessoa usa a página, para ela já aparecer na hora da troca
  const quandoOcioso = window.requestIdleCallback || (fn => setTimeout(fn, 1500));
  window.addEventListener('load', () => quandoOcioso(prepararFriguinho));
});
