/* ==========================================================================
   FUNDO ANIMADO DA PÁGINA INICIAL
   Gradiente azul + flocos de 6 pontas caindo na diagonal (para baixo e para
   a esquerda) + nevasca de risquinhos com vento forte, rajadas e turbulência,
   tudo feito em <canvas>.

   Uso: coloque <canvas class="hero-neve" aria-hidden="true"></canvas>
   como primeiro filho de <section class="hero"> e carregue este arquivo.

   Em páginas que já têm o próprio fundo (como "Sobre nós"), adicione
   data-fundo="transparente" no canvas: ele desenha só a neve, sem o gradiente.
   Com data-neve="suave" a nevasca vira uma neve calma: pontinhos caindo
   devagar e flocos de 6 pontas, sem vento forte.
   ========================================================================== */
(() => {
  const canvas = document.querySelector('.hero-neve');
  if (!canvas) return;

  const hero = canvas.parentElement;
  const ctx = canvas.getContext('2d');
  const reduzirMovimento = window.matchMedia('(prefers-reduced-motion: reduce)').matches;
  const fundoTransparente = canvas.dataset.fundo === 'transparente';
  const neveSuave = canvas.dataset.neve === 'suave';

  /* ----- Ajustes dos FLOCOS (mexa aqui) -------------------------------- */
  const CONFIG = {
    angulo: 22,          // direção inicial dos flocos (graus a partir da vertical, para a esquerda); depois eles seguem o sentido dos risquinhos
    flocos: neveSuave ? 9 : 14,   // quantidade de flocos (em um hero de ~1290x800)
    corFloco: '#CFDFF8',
    velocidade: 1,       // multiplicador geral: 0.5 = mais lento, 2 = mais rápido
  };

  /* ----- Ajustes dos RISQUINHOS (nevasca) ------------------------------ */
  const VELOCIDADE_GERAL = 0.68;       // menor = nevasca mais devagar
  const ALONGAMENTO_RASTRO = neveSuave ? 0.006 : 0.05;   // maior = traços mais compridos
  const ESCALA_ESPESSURA = 1.3;        // maior = risquinhos mais grossos

  // Altura da tela (0 a 1): tempestade forte até ZONA_FORTE_ATE,
  // e calmaria total a partir de ZONA_CALMA_DE.
  const ZONA_FORTE_ATE = 0.6;
  const ZONA_CALMA_DE = 0.82;

  // Tamanho da tela de referência, usado para escalar tudo.
  const LARGURA_REF = 1459;
  const ALTURA_REF = 1024;

  // Camadas, da mais distante para a mais próxima:
  //   quantidade  – risquinhos na tela de referência
  //   raio        – [mín, máx] em px
  //   vento       – [mín, máx] velocidade horizontal (px/s)
  //   queda       – [mín, máx] velocidade vertical (px/s)
  //   opacidade   – [mín, máx]
  //   sensibilidadeRajada – quanto as rajadas afetam essa camada
  const CAMADAS_NEVASCA = [
    { quantidade: 300, raio: [0.5, 1.3], vento: [150, 320],  queda: [50, 130],  opacidade: [0.22, 0.50], sensibilidadeRajada: 0.6 },
    { quantidade: 200, raio: [1.0, 2.4], vento: [300, 620],  queda: [100, 230], opacidade: [0.30, 0.65], sensibilidadeRajada: 0.85 },
    { quantidade: 100, raio: [1.8, 4.0], vento: [520, 1000], queda: [180, 380], opacidade: [0.30, 0.70], sensibilidadeRajada: 1.05 },
    { quantidade: 40,  raio: [3.5, 7.0], vento: [800, 1250], queda: [260, 460], opacidade: [0.15, 0.40], sensibilidadeRajada: 1.2 },
    { quantidade: 22,  raio: [6, 14],    vento: [950, 1600], queda: [320, 560], opacidade: [0.08, 0.22], sensibilidadeRajada: 1.3 },
  ];

  // Neve calma (data-neve="suave"): pontinhos nítidos, queda lenta e quase sem vento
  const CAMADAS_SUAVES = [
    { quantidade: 70, raio: [1.0, 2.0], vento: [20, 50], queda: [40, 80],  opacidade: [0.55, 0.95], sensibilidadeRajada: 0.1 },
    { quantidade: 30, raio: [1.8, 3.0], vento: [25, 60], queda: [55, 100], opacidade: [0.65, 1.00], sensibilidadeRajada: 0.1 },
    { quantidade: 8,  raio: [3.0, 4.5], vento: [30, 70], queda: [70, 120], opacidade: [0.35, 0.65], sensibilidadeRajada: 0.1 },
  ];

  const CAMADAS = neveSuave ? CAMADAS_SUAVES : CAMADAS_NEVASCA;

  /* ----- Cores do gradiente (as mesmas do SVG original) ---------------- */
  const GRADIENTE = [
    [0,    '#131C55'],
    [0.35, '#1F5F97'],
    [0.65, '#4880B7'],
    [1,    '#70A2D7'],
  ];
  // Medidas do SVG original (1436 x 1024), para o gradiente ficar igual
  const SVG = { w: 1436, h: 1024, x1: 573.816, y1: 0, x2: 973.844, y2: 544.954 };

  const dpr = Math.min(window.devicePixelRatio || 1, 2);
  const rad = CONFIG.angulo * Math.PI / 180;
  const dirX = -Math.sin(rad);              // vai para a esquerda
  const dirY = Math.cos(rad);               // vai para baixo

  const rand = (a, b) => a + Math.random() * (b - a);
  const limitar = (v, min, max) => Math.min(max, Math.max(min, v));
  const suavizar = (inicio, fim, v) => {
    const t = limitar((v - inicio) / (fim - inicio), 0, 1);
    return t * t * (3 - 2 * t);
  };

  let W = 0, H = 0;
  let escala = 1;                 // tamanho do hero em relação à tela de referência
  let tempo = 0;                  // segundos desde o início
  let fluxoX = dirX;              // direção média dos risquinhos (vetor unitário)
  let fluxoY = dirY;
  let gradiente = null;
  let flocos = [];
  let risquinhos = [];

  /* ----- Sprites (desenhados uma vez só, já com o desfoque) ------------ */
  function criarSprite(largura, altura, blur, desenhar, cor) {
    const pad = Math.ceil(blur * 3) + 2;
    const cw = largura + pad * 2;
    const ch = altura + pad * 2;
    const c = document.createElement('canvas');
    c.width = Math.ceil(cw * dpr);
    c.height = Math.ceil(ch * dpr);
    const g = c.getContext('2d');
    g.scale(dpr, dpr);

    // Truque do desfoque: desenha a forma fora do canvas e deixa só a
    // sombra (borrada) entrar. Funciona em todos os navegadores.
    const deslocamento = cw + 50;
    g.shadowColor = cor;
    g.shadowBlur = blur * 2 * dpr;
    g.shadowOffsetX = deslocamento * dpr;
    g.translate(cw / 2 - deslocamento, ch / 2);
    g.strokeStyle = cor;
    g.fillStyle = cor;
    g.lineCap = 'round';
    g.lineJoin = 'round';
    desenhar(g);

    return { img: c, w: cw, h: ch };
  }

  // Ponto macio: esticado vira um rastro com pontas suaves
  const spriteRisco = (() => {
    const tamanho = 64;
    const c = document.createElement('canvas');
    c.width = c.height = tamanho;
    const g = c.getContext('2d');
    const grad = g.createRadialGradient(tamanho / 2, tamanho / 2, 0, tamanho / 2, tamanho / 2, tamanho / 2);
    grad.addColorStop(0, 'rgba(255,255,255,1)');
    grad.addColorStop(0.3, 'rgba(255,255,255,0.8)');
    grad.addColorStop(0.65, 'rgba(255,255,255,0.22)');
    grad.addColorStop(1, 'rgba(255,255,255,0)');
    g.fillStyle = grad;
    g.fillRect(0, 0, tamanho, tamanho);
    return c;
  })();

  /* ----- Flocos de 6 pontas -------------------------------------------- */
  // Floco de 6 pontas (hexágono central + braços + bolinha + ramos em "V")
  function desenharFloco(g, R) {
    g.lineWidth = R * 0.054;

    g.beginPath();
    for (let i = 0; i < 6; i++) {
      const a = i * Math.PI / 3 - Math.PI / 2;
      const x = Math.cos(a) * R * 0.205;
      const y = Math.sin(a) * R * 0.205;
      i === 0 ? g.moveTo(x, y) : g.lineTo(x, y);
    }
    g.closePath();
    g.stroke();

    for (let k = 0; k < 6; k++) {
      g.save();
      g.rotate(k * Math.PI / 3);

      g.beginPath();                               // braço
      g.moveTo(0, -R * 0.205);
      g.lineTo(0, -R * 0.92);
      g.stroke();

      g.beginPath();                               // bolinha na ponta
      g.arc(0, -R, R * 0.08, 0, Math.PI * 2);
      g.stroke();

      g.beginPath();                               // ramos em V
      g.moveTo(-R * 0.2, -R * 0.84);
      g.lineTo(0, -R * 0.735);
      g.lineTo(R * 0.2, -R * 0.84);
      g.stroke();

      g.restore();
    }
  }

  function criarFloco() {
    const R = rand(28, 58);                         // raio do floco (mín, máx): tamanho médio-grande
    const blur = 1.2 + R * 0.04;
    const sprite = criarSprite(R * 2.3, R * 2.3, blur, g => desenharFloco(g, R), CONFIG.corFloco);
    const velocidade = (14 + R * 0.55) * CONFIG.velocidade;   // maiores = mais perto = mais rápidos
    return {
      sprite,
      raio: sprite.w / 2,
      velocidade,
      velocidadeX: dirX * velocidade,                    // velocidade atual (persegue o fluxo dos risquinhos)
      velocidadeY: dirY * velocidade,
      giro: rand(-0.35, 0.35),                           // rad/s
      rot: rand(0, Math.PI * 2),
      alpha: rand(0.22, 0.4),
      x: 0, y: 0,
    };
  }

  function sortearPosicaoDoFloco(p, inicial) {
    if (inicial) {
      // espalha por toda a tela
      p.x = rand(-p.raio, W + p.raio);
      p.y = rand(-p.raio, H + p.raio);
      return;
    }

    // renasce fora da tela, por cima ou pela direita, na proporção do fluxo
    // (quanto mais horizontal o vento, mais flocos entram pela direita)
    const entradaPeloTopo = Math.abs(fluxoY) * W;
    const entradaPelaDireita = Math.abs(fluxoX) * H;
    if (Math.random() * (entradaPeloTopo + entradaPelaDireita) < entradaPeloTopo) {
      p.x = rand(0, W * 1.15);
      p.y = -p.raio;
    } else {
      p.x = W + p.raio;
      p.y = rand(-p.raio, H * 0.7);
    }
  }

  // Neve suave: sorteia vários pontos e fica com o que está mais longe dos
  // outros flocos, para eles não nascerem (nem caírem) grudados uns nos outros.
  const TENTATIVAS_DISPERSAO = 24;

  function posicionarFloco(p, inicial) {
    if (!neveSuave) {
      sortearPosicaoDoFloco(p, inicial);
      return;
    }

    let melhorX = 0, melhorY = 0, melhorDistancia = -1;
    for (let i = 0; i < TENTATIVAS_DISPERSAO; i++) {
      sortearPosicaoDoFloco(p, inicial);

      let maisPerto = Infinity;
      for (const outro of flocos) {
        if (outro === p || !outro.posicionado) continue;
        maisPerto = Math.min(maisPerto, Math.hypot(p.x - outro.x, p.y - outro.y));
      }
      if (maisPerto > melhorDistancia) {
        melhorDistancia = maisPerto;
        melhorX = p.x;
        melhorY = p.y;
      }
    }
    p.x = melhorX;
    p.y = melhorY;
    p.posicionado = true;
  }

  /* ----- Vento (risquinhos) -------------------------------------------- */
  // Força global do vento: base + oscilações lentas + rajadas fortes ocasionais
  function forcaDoVento(t) {
    const rajadaForte = Math.pow(Math.max(0, Math.sin(t * 0.17)), 4) * 0.7;
    const forca = 1
      + 0.3 * Math.sin(t * 0.37)
      + 0.18 * Math.sin(t * 0.91 + 1)
      + 0.1 * Math.sin(t * 2.3)
      + rajadaForte;
    return Math.max(0.45, forca);
  }

  // Turbulência suave que depende da posição e do tempo (valores de -1 a 1)
  const turbulencia = { horizontal: 0, vertical: 0 };
  function calcularTurbulencia(x, y, t) {
    turbulencia.horizontal = Math.sin(x * 0.004 + t * 0.9 + Math.sin(y * 0.003 - t * 0.5) * 1.5);
    turbulencia.vertical = Math.cos(y * 0.006 - t * 0.7 + Math.sin(x * 0.002 + t * 0.4) * 1.5);
  }

  // 1 = tempestade plena, 0 = calmaria (depende da altura na tela)
  function intensidadeDaNevasca(y) {
    return 1 - suavizar(H * ZONA_FORTE_ATE, H * ZONA_CALMA_DE, y);
  }

  /* ----- Risquinhos ----------------------------------------------------- */
  // Define (ou redefine) tamanho, velocidade e opacidade de um risquinho.
  // Se "inicial" for falso, ele reaparece fora da tela, por cima ou pela direita.
  function configurarRisquinho(p, inicial) {
    const camada = CAMADAS[p.camada];
    const sorteioTamanho = Math.pow(Math.random(), 1.7);   // muitos pequenos, poucos grandes
    const variacaoVelocidade = rand(0.65, 1.4);            // cada um tem seu ritmo

    p.raio = camada.raio[0] + (camada.raio[1] - camada.raio[0]) * sorteioTamanho;
    p.vento = rand(camada.vento[0], camada.vento[1]) * variacaoVelocidade;
    p.queda = rand(camada.queda[0], camada.queda[1]) * rand(0.75, 1.3) * variacaoVelocidade;
    p.opacidade = rand(camada.opacidade[0], camada.opacidade[1]);
    // altura onde some (menos neve embaixo); na neve suave ela segue até as ondas
    p.yDeSumico = H * (neveSuave ? rand(0.88, 1) : rand(0.7, 1));

    if (inicial) {
      p.x = rand(0, W);
      p.y = rand(0, p.yDeSumico);
      p.velocidadeX = -p.vento;
      p.velocidadeY = p.queda;
      return;
    }

    const margem = (p.raio * 2 + p.vento * ALONGAMENTO_RASTRO + 30) * escala;
    const alturaZonaForte = H * 0.7;
    const entradaPeloTopo = p.queda * W;
    const entradaPelaDireita = p.vento * alturaZonaForte;

    if (Math.random() * (entradaPeloTopo + entradaPelaDireita) < entradaPeloTopo) {
      p.x = rand(0, W * 1.15);
      p.y = -margem;
    } else {
      p.x = W + margem;
      p.y = rand(-margem, alturaZonaForte);
    }
  }

  /* ----- Criação -------------------------------------------------------- */
  function criarParticulas() {
    const fator = Math.min(Math.max((W * H) / (1290 * 800), 0.5), 2);
    const nFlocos = Math.round(CONFIG.flocos * fator);
    flocos = [];
    for (let i = 0; i < nFlocos; i++) flocos.push(criarFloco());
    flocos.forEach(p => posicionarFloco(p, true));

    const fatorArea = limitar((W * H) / (LARGURA_REF * ALTURA_REF), 0.5, 1.6);
    risquinhos = [];
    CAMADAS.forEach((camada, indice) => {
      const total = Math.round(camada.quantidade * fatorArea);
      for (let i = 0; i < total; i++) {
        const p = { camada: indice };
        configurarRisquinho(p, true);
        risquinhos.push(p);
      }
    });
  }

  /* ----- Fundo em gradiente (equivale ao "cover" do SVG original) ------ */
  function criarGradiente() {
    const sc = Math.max(W / SVG.w, H / SVG.h);
    const ox = (W - SVG.w * sc) / 2;
    gradiente = ctx.createLinearGradient(
      ox + SVG.x1 * sc, SVG.y1 * sc,
      ox + SVG.x2 * sc, SVG.y2 * sc
    );
    GRADIENTE.forEach(([pos, cor]) => gradiente.addColorStop(pos, cor));
  }

  /* ----- Desenho ------------------------------------------------------- */
  function desenhar() {
    ctx.setTransform(dpr, 0, 0, dpr, 0, 0);
    ctx.globalAlpha = 1;
    if (fundoTransparente) {
      ctx.clearRect(0, 0, W, H);
    } else {
      ctx.fillStyle = gradiente;
      ctx.fillRect(0, 0, W, H);
    }

    // flocos de 6 pontas
    for (const p of flocos) {
      ctx.globalAlpha = p.alpha;
      ctx.setTransform(
        dpr * Math.cos(p.rot), dpr * Math.sin(p.rot),
        -dpr * Math.sin(p.rot), dpr * Math.cos(p.rot),
        p.x * dpr, p.y * dpr
      );
      ctx.drawImage(p.sprite.img, -p.sprite.w / 2, -p.sprite.h / 2, p.sprite.w, p.sprite.h);
    }

    // risquinhos da nevasca
    for (const p of risquinhos) {
      const velocidade = Math.hypot(p.velocidadeX, p.velocidadeY);
      const espessura = p.raio * 2 * escala * ESCALA_ESPESSURA;
      const comprimento = espessura + velocidade * ALONGAMENTO_RASTRO * escala;

      // o rastro fica deitado exatamente na direção em que o risquinho viaja
      const angulo = Math.atan2(p.velocidadeY, p.velocidadeX);
      const cos = Math.cos(angulo) * dpr;
      const sin = Math.sin(angulo) * dpr;

      // embaixo ficam mais discretos e desaparecem perto de yDeSumico
      const nevasca = intensidadeDaNevasca(p.y);
      const sumindo = 1 - suavizar(p.yDeSumico - H * 0.12, p.yDeSumico, p.y);

      ctx.globalAlpha = p.opacidade * (0.3 + 0.7 * nevasca) * sumindo;
      ctx.setTransform(cos, sin, -sin, cos, p.x * dpr, p.y * dpr);
      ctx.drawImage(spriteRisco, -comprimento / 2, -espessura / 2, comprimento, espessura);
    }

    ctx.globalAlpha = 1;
  }

  function atualizar(dt) {
    tempo += dt;

    const ventoGlobal = forcaDoVento(tempo);
    const suavizacao = 1 - Math.exp(-dt * 2.2);   // inércia: quão rápido acompanha o vento

    // flocos: seguem o mesmo sentido dos risquinhos (fluxo médio, calculado no quadro anterior)
    const suavizacaoFloco = 1 - Math.exp(-dt * 1.2);   // flocos grandes têm mais inércia
    const forcaFloco = 0.6 + 0.4 * ventoGlobal;        // rajadas aceleram um pouco os flocos
    for (const p of flocos) {
      const alvoX = fluxoX * p.velocidade * forcaFloco;
      const alvoY = fluxoY * p.velocidade * forcaFloco;
      p.velocidadeX += (alvoX - p.velocidadeX) * suavizacaoFloco;
      p.velocidadeY += (alvoY - p.velocidadeY) * suavizacaoFloco;
      p.x += p.velocidadeX * dt;
      p.y += p.velocidadeY * dt;
      p.rot += p.giro * dt;
      if (p.y - p.raio > H || p.x + p.raio < 0) posicionarFloco(p, false);
    }

    // risquinhos
    let somaX = 0, somaY = 0, somaPeso = 0;

    for (const p of risquinhos) {
      const camada = CAMADAS[p.camada];
      calcularTurbulencia(p.x / escala, p.y / escala, tempo);

      // embaixo o vento enfraquece e a queda fica suave
      const nevasca = intensidadeDaNevasca(p.y);
      const fatorVento = 0.2 + 0.8 * nevasca;
      const fatorQueda = 0.45 + 0.55 * nevasca;
      const fatorRajada = 1 + (ventoGlobal - 1) * camada.sensibilidadeRajada * nevasca;

      const alvoX = -p.vento * fatorVento * fatorRajada * (1 + 0.25 * turbulencia.horizontal);
      const alvoY = p.queda * fatorQueda * (1 + 0.3 * turbulencia.vertical)
                  + turbulencia.horizontal * 30 * fatorRajada * fatorVento;

      p.velocidadeX += (alvoX - p.velocidadeX) * suavizacao;
      p.velocidadeY += (alvoY - p.velocidadeY) * suavizacao;
      p.x += p.velocidadeX * dt * escala * VELOCIDADE_GERAL;
      p.y += p.velocidadeY * dt * escala * VELOCIDADE_GERAL;

      // soma a direção de cada risquinho (só onde a tempestade é forte)
      somaX += p.velocidadeX * nevasca;
      somaY += p.velocidadeY * nevasca;
      somaPeso += nevasca;

      const margem = (p.raio * 2 + p.vento * ALONGAMENTO_RASTRO + 30) * escala;
      if (p.x < -margem || p.y > p.yDeSumico) configurarRisquinho(p, false);
    }

    // fluxo médio dos risquinhos (direção unitária), suavizado, para os flocos seguirem
    const comprimentoFluxo = Math.hypot(somaX, somaY);
    if (somaPeso > 0 && comprimentoFluxo > 0) {
      const suavizacaoFluxo = 1 - Math.exp(-dt * 1.5);
      fluxoX += (somaX / comprimentoFluxo - fluxoX) * suavizacaoFluxo;
      fluxoY += (somaY / comprimentoFluxo - fluxoY) * suavizacaoFluxo;
    }
  }

  /* ----- Loop ---------------------------------------------------------- */
  let anterior = 0;
  function quadro(t) {
    const dt = Math.min((t - anterior) / 1000, 0.05);
    anterior = t;
    // Quando o hero já foi coberto pelas outras seções, não gasta processamento
    if (window.scrollY < window.innerHeight * 1.5) {
      atualizar(dt);
      desenhar();
    }
    requestAnimationFrame(quadro);
  }

  /* ----- Tamanho ------------------------------------------------------- */
  function redimensionar() {
    const r = hero.getBoundingClientRect();
    W = Math.round(r.width);
    H = Math.round(r.height);
    if (!W || !H) return;
    canvas.width = Math.round(W * dpr);
    canvas.height = Math.round(H * dpr);
    escala = limitar(Math.min(W / LARGURA_REF, H / ALTURA_REF), 0.55, 1.5);
    criarGradiente();
    criarParticulas();
    if (reduzirMovimento) desenhar();
  }

  redimensionar();
  new ResizeObserver(redimensionar).observe(hero);

  if (!reduzirMovimento) requestAnimationFrame(t => { anterior = t; quadro(t); });
})();
