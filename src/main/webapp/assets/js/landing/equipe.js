/* ==========================================================================
   CARROSSEL "NOSSA EQUIPE"  (página Sobre nós)
   Carregue no fim do <body>:  <script src="equipe.js"></script>

   Como funciona: os cards ficam todos no mesmo lugar e cada um gira em volta
   de um ponto bem abaixo da seção (ver CSS, seção 17). Aqui só dizemos a
   posição de cada card na roda: 0 = centro, -1 = esquerda, 1 = direita...
   (aceita decimais, por isso a roda gira suave).

   Navegação: clicar no card, setas, bolinhas, teclado (← →), ARRASTAR a roda
   como uma roleta (mouse ou dedo, com inércia) e rolagem horizontal do mouse/touchpad.
   ========================================================================== */
(() => {
  const palco = document.getElementById('equipe-palco');
  if (!palco) return;   // a página não tem o carrossel: não faz nada

  /* ----- Dados da equipe: é só editar/adicionar aqui ------------------- */
  // github e linkedin são opcionais: apague o campo se a pessoa não tiver
  // (o ícone some sozinho). Troque os links de exemplo pelos reais.
  const equipe = [
    { nome: 'Angel', cargo: 'Dev. Full Stack', foto: 'assets/img/landing/equipe/Angella.png',
      desc: 'Responsável pelo conceito e elaboração da base de dados  e pela  implementação do Front-End.',
      tags: ['HTML/CSS', 'Banco de Dados', 'Excel'],
      email: 'angella.20260019@institutojef.org.br' },

    { nome: 'Gabriel', cargo: 'Dev. Full Stack', foto: 'assets/img/landing/equipe/Gabriel.png',
      desc: 'Atuação no desenvolvimento do App Web, BFF e API principal, integrando as funcionalidades do Frigus.',
      tags: ['React', 'TypeScript', 'Arquitetura'],
      email: 'gabriel.goncalves@institutojef.org.br',
      github: 'https://github.com/gabriel03013?utm_source=chatgpt.com', linkedin: 'https://www.linkedin.com/in/gabriel-mendes-goncalves?utm_source=chatgpt.com' },

    { nome: 'Nicholas', cargo: 'Dev. Back-End', foto: 'assets/img/landing/equipe/Nicholas.png',
      desc: 'Responsável pelo Back-End do Frigus, a lógica do sistema, a paginação e a conexão com banco de dados.',
      tags: ['Java', 'SQL', 'Java Servlets'],
      email: 'nicholas.20260224@institutojef.org.br',
      github: 'https://github.com/Code-gamma16?utm_source=chatgpt.com' },

    { nome: 'Mariana', cargo: 'Designer UX', foto: 'assets/img/landing/equipe/Mariana.png',
      desc: 'Fiquei responsável pelo design das telas web e pela prototipação do Frigus.',
      tags: ['Figma', 'Design', 'UX/UI'],
      email: 'mariana.costa@institutojef.org.br',
      github: 'https://github.com/mariitorrs?utm_source=chatgpt.com', linkedin: 'https://www.linkedin.com/in/mariana-torres-aa95453b5?utm_source=chatgpt.com' },

    { nome: 'Matias', cargo: 'Dev. Front-End', foto: 'assets/img/landing/equipe/Miguel.png',
      desc: 'Responsável pelo desenvolvimento do Front-End do Frigus e pela documentação do projeto.',
      tags: ['HTML/CSS', 'Javascript', 'Excel'],
      email: 'miguel.20260214@institutojef.org.br',
      github: 'https://github.com/MiguelMatias2970?utm_source=chatgpt.com', linkedin: 'https://www.linkedin.com/in/miguel-matias-baab693b6?utm_source=chatgpt.com' },

    { nome: 'Gabi', cargo: 'Designer UX/UI', foto: 'assets/img/landing/equipe/Gabriela.png',
      desc: 'Responsável pelo desenvolvimento de Design do aplicativo Frigus.',
      tags: ['Design', 'Figma'],
      email: 'gabriella.silva@institutojef.org.br',
      github: 'https://github.com/Gabzz25?utm_source=chatgpt.com', linkedin: 'https://www.linkedin.com/in/gabriella-reis-814600355?utm_source=chatgpt.com' },

    { nome: 'Davi', cargo: 'Engenheiro de IA', foto: 'assets/img/landing/equipe/Davi.png',
      desc: 'Responsável pela engenharia de IA, também atuando em BI e banco de dados.',
      tags: ['Python', 'Banco de Dados', 'IA', 'Business Inteligence'],
      email: 'davi.franco@institutojef.org.br',
      github: 'https://github.com/DaviAlcanfor?utm_source=chatgpt.com', linkedin: 'https://linkedin.com/in/davi-alcanfor?utm_source=chatgpt.com' },

    { nome: 'Manganelli', cargo: 'Dev. Back-End e Dados', foto: 'assets/img/landing/equipe/Gustavo.png',
      desc: 'Responsável pela modelagem do banco de dados, segurança da API, e criação de telas do mobile.',
      tags: ['Spring boot', 'PostgreSQL', 'Android mobile'],
      email: 'gustavo.manganelli@institutojef.org.br' },

    { nome: 'Erik', cargo: 'Dev. Back-End e Mobile', foto: 'assets/img/landing/equipe/Erik.png',
      desc: 'Responsável pelo desenvolvimento da API e pela integração das funcionalidades no aplicativo mobile.',
      tags: ['Spring boot', 'Engenharia de Software', 'Android'],
      email: 'erik.silva@institutojef.org.br',
      github: 'https://github.com/ErikSaantos?utm_source=chatgpt.com', linkedin: 'https://www.linkedin.com/in/erik-felipe-santos-silva-71b837226?utm_source=chatgpt.com' },

    { nome: 'Amanda', cargo: 'Designer UX/UI', foto: 'assets/img/landing/equipe/Amanda.png',
      desc: 'Responsável pelo design da Landing Page, pelo CRUD e pelos estudos de UX.',
      tags: ['UI/UX', 'Design', 'Figma'],
      email: 'amanda.20260010@institutojef.org.br',
      github: 'https://github.com/amandagturesso-cyber?utm_source=chatgpt.com', linkedin: 'https://www.linkedin.com/in/amanda-guti-06131929a?utm_source=chatgpt.com' },
  ];

  /* ----- Elementos da página ------------------------------------------- */
  const dotsEl = document.getElementById('equipe-dots');
  const carrossel = document.getElementById('equipe-carrossel');
  const detalhes = document.getElementById('equipe-detalhes');
  const n = equipe.length;
  const reduzirMovimento = window.matchMedia('(prefers-reduced-motion: reduce)').matches;

  /* ----- Ajustes da roleta (mexa aqui) --------------------------------- */
  const PX_POR_CARD = 300;       // px de arrasto para girar 1 card (menor = roleta mais "solta")
  const FORCA_DO_GIRO = 0.3;     // quanto um arremesso continua girando depois de soltar
  const MAX_CARDS_NO_GIRO = 4;   // limite de cards que um único arremesso pode passar

  /* ----- Cria os cards e as bolinhas ----------------------------------- */
  const cards = equipe.map((m, i) => {
    const b = document.createElement('button');
    b.type = 'button';
    b.className = 'equipe-card';
    b.setAttribute('aria-label', m.nome);
    b.innerHTML = `
      <span class="equipe-cartao">
        <img src="${m.foto}" alt="Foto de ${m.nome}" draggable="false" onerror="this.remove()">
        <span class="equipe-nome">
          <strong class="subtitulo-b">${m.nome}</strong>
          <span class="titulo-menor-b">${m.cargo}</span>
        </span>
      </span>`;
    b.addEventListener('click', () => ir(i));
    palco.appendChild(b);
    return b;
  });

  const dots = equipe.map((m, i) => {
    const d = document.createElement('button');
    d.type = 'button';
    d.className = 'equipe-dot';
    d.setAttribute('aria-label', `Ver ${m.nome}`);
    d.addEventListener('click', () => ir(i));
    dotsEl.appendChild(d);
    return d;
  });

  /* ----- Estado da roleta ---------------------------------------------- */
  let pos = 0;          // posição contínua da roda, em "cards" (0 = primeiro integrante no centro)
  let mostrando = -1;   // integrante cujo texto está na tela
  let animacao = 0;     // id do requestAnimationFrame do giro em andamento

  const mod = x => ((x % n) + n) % n;
  const limitar = (v, min, max) => Math.max(min, Math.min(max, v));

  /* ----- Desenho: posiciona todos os cards a partir de "pos" ----------- */
  function render() {
    const idx = mod(Math.round(pos));   // integrante mais perto do centro

    cards.forEach((c, i) => {
      let o = mod(i - pos);             // 0..n
      if (o > n / 2) o -= n;            // caminho mais curto até o centro
      o = limitar(o, -3, 3);            // os muito longe ficam parados (e invisíveis) nas pontas
      const ao = Math.abs(o);

      c.style.setProperty('--o', o.toFixed(3));                     // posição na roda
      c.style.setProperty('--a', Math.min(ao, 1).toFixed(3));       // 0 no centro, 1 nas laterais
      c.style.opacity = limitar(3 - ao, 0, 1).toFixed(2);           // visível até ±2, some até ±3
      c.style.zIndex = Math.round(100 - ao * 10);
      c.classList.toggle('visivel', ao < 2.9);
      c.classList.toggle('ativo', i === idx);
    });

    if (idx !== mostrando) {            // trocou de integrante: atualiza o texto de baixo
      mostrando = idx;
      atualizarTexto();
    }
  }

  function atualizarTexto() {
    const m = equipe[mostrando];
    dots.forEach((d, i) => d.classList.toggle('ativo', i === mostrando));

    document.getElementById('equipe-desc').textContent = m.desc;
    document.getElementById('equipe-tags').innerHTML = m.tags.map(t => `<span>${t}</span>`).join('');
    document.getElementById('equipe-email').href = `mailto:${m.email}`;
    document.getElementById('equipe-email-texto').textContent = m.email;
    for (const rede of ['github', 'linkedin']) {          // ícone só aparece se a pessoa tiver o link
      const a = document.getElementById(`equipe-${rede}`);
      a.hidden = !m[rede];
      if (m[rede]) a.href = m[rede];
    }

    // reinicia a animação de entrada do texto
    detalhes.style.animation = 'none';
    detalhes.offsetWidth;
    detalhes.style.animation = '';
  }

  /* ----- Giro suave até uma posição (usado por cliques, setas e soltura) */
  function animarPara(alvo, duracao) {
    cancelAnimationFrame(animacao);
    if (reduzirMovimento || duracao <= 0) { pos = alvo; render(); return; }

    const inicio = pos;
    const t0 = performance.now();
    const passo = agora => {
      const k = Math.min(1, (agora - t0) / duracao);
      pos = inicio + (alvo - inicio) * (1 - Math.pow(1 - k, 3));   // desacelera no fim, como uma roleta
      render();
      animacao = k < 1 ? requestAnimationFrame(passo) : 0;
    };
    animacao = requestAnimationFrame(passo);
  }

  // vai até o integrante i pelo caminho mais curto (de longe, a roda "gira" passando pelos outros)
  function ir(i) {
    const atual = Math.round(pos);
    let d = mod(i - atual);
    if (d > n / 2) d -= n;
    animarPara(atual + d, 450 + Math.abs(d) * 120);
  }

  /* ----- Setas e teclado ------------------------------------------------ */
  document.getElementById('equipe-anterior').addEventListener('click', () => ir(Math.round(pos) - 1));
  document.getElementById('equipe-proximo').addEventListener('click', () => ir(Math.round(pos) + 1));

  carrossel.addEventListener('keydown', e => {
    if (e.key === 'ArrowLeft') ir(Math.round(pos) - 1);
    if (e.key === 'ArrowRight') ir(Math.round(pos) + 1);
  });

  /* ----- Roleta: arrastar para girar (mouse ou dedo) ------------------- */
  let arrasto = null;        // dados do arrasto em andamento
  let suprimirClique = false;

  carrossel.addEventListener('pointerdown', e => {
    if (e.button !== 0) return;
    if (e.target.closest('.equipe-controles, .equipe-contato')) return;   // setas, bolinhas e links não arrastam
    cancelAnimationFrame(animacao);                                        // "segura" a roda que ainda está girando
    arrasto = { id: e.pointerId, x: e.clientX, pos0: pos, ultX: e.clientX, ultT: performance.now(), vel: 0, moveu: false };
  });

  window.addEventListener('pointermove', e => {
    if (!arrasto || e.pointerId !== arrasto.id) return;
    const dx = e.clientX - arrasto.x;
    if (!arrasto.moveu) {
      if (Math.abs(dx) < 6) return;                                        // ainda é um clique
      arrasto.moveu = true;
      carrossel.classList.add('arrastando');
    }
    pos = arrasto.pos0 - dx / PX_POR_CARD;                                 // arrastar para a direita traz o card da esquerda

    // velocidade do arrasto (em cards/segundo), suavizada, para o arremesso
    const agora = performance.now();
    const dt = (agora - arrasto.ultT) / 1000;
    if (dt > 0) arrasto.vel = arrasto.vel * 0.6 + (-(e.clientX - arrasto.ultX) / PX_POR_CARD / dt) * 0.4;
    arrasto.ultX = e.clientX;
    arrasto.ultT = agora;

    render();
  });

  function soltar(e) {
    if (!arrasto || e.pointerId !== arrasto.id) return;
    const a = arrasto;
    arrasto = null;
    carrossel.classList.remove('arrastando');
    if (!a.moveu) return;                                                  // foi só um clique: o clique normal cuida

    suprimirClique = true;                                                 // não deixa o "soltar" virar clique num card
    setTimeout(() => { suprimirClique = false; }, 0);

    const parou = performance.now() - a.ultT > 80;                         // segurou parado antes de soltar: sem arremesso
    const giro = parou ? 0 : limitar(a.vel * FORCA_DO_GIRO, -MAX_CARDS_NO_GIRO, MAX_CARDS_NO_GIRO);
    const alvo = Math.round(pos + giro);                                   // sempre para num integrante, nunca "no meio"
    animarPara(alvo, 400 + Math.abs(alvo - pos) * 140);
  }
  window.addEventListener('pointerup', soltar);
  window.addEventListener('pointercancel', soltar);

  carrossel.addEventListener('click', e => {
    if (suprimirClique) { e.stopPropagation(); e.preventDefault(); }
  }, true);
  carrossel.addEventListener('dragstart', e => e.preventDefault());       // evita arrastar a imagem "fantasma"

  /* ----- Rolagem horizontal do mouse/touchpad gira a roda --------------- */
  // (a rolagem vertical continua rolando a página normalmente)
  let acumulado = 0;
  let travaRolagem = false;
  carrossel.addEventListener('wheel', e => {
    if (Math.abs(e.deltaX) <= Math.abs(e.deltaY)) return;
    e.preventDefault();
    acumulado += e.deltaX;
    if (Math.abs(acumulado) > 50 && !travaRolagem) {
      ir(Math.round(pos) + (acumulado > 0 ? 1 : -1));
      acumulado = 0;
      travaRolagem = true;
      setTimeout(() => { travaRolagem = false; }, 350);
    }
  }, { passive: false });

  render();
})();