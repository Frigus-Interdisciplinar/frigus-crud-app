/* ==========================================================================
   GRÁFICOS DA VISÃO GERAL
   Busca os dados no back e desenha os dois gráficos da página
   ========================================================================== */

/* ----- 1. Dados: a JSP escreve os números nos atributos data-* de cada área do gráfico ----- */

/* ----- 2. Cores (as mesmas do style-app.css) ----- */
const corUsuarios = '#2552C8';
const corEmpresas = '#1A1919';
const coresDosPlanos = ['#C9DEF9', '#70A2D7', '#2552C8', '#1A1919'];

const nomesDosMeses = ['Jan', 'Fev', 'Mar', 'Abr', 'Mai', 'Jun', 'Jul', 'Ago', 'Set', 'Out', 'Nov', 'Dez'];
const anoAtual = new Date().getFullYear();

/* ----- 3. Funções de apoio ----- */
function formatarReal(valor) {
  return valor.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
}

function textoAssinaturas(quantidade) {
  return quantidade === 1 ? '1 assinatura' : quantidade + ' assinaturas';
}

// receita: data-total, data-assinaturas e data-planos = "Plano:assinaturas:receita;Plano:assinaturas:receita"
function lerReceita(area) {
  return {
    totalMonthly: Number(area.dataset.total),
    subscriptions: Number(area.dataset.assinaturas),
    plans: area.dataset.planos.split(';').map(texto => {
      const [name, subscribers, revenue] = texto.split(':');
      return { name, subscribers: Number(subscribers), revenue: Number(revenue) };
    })
  };
}

// crescimento: data-crescimento = JSON com total, delta3Months e series (se a JSP não mandar, mostra o aviso de erro)
function lerCrescimento(area) {
  if (!area.dataset.crescimento) throw new Error('sem dados');
  return JSON.parse(area.dataset.crescimento);
}

function mostrarErro(area) {
  area.innerHTML = '<p class="grafico-erro">Não foi possível carregar os dados.</p>';
}

/* ----- 4. Textos desenhados dentro dos gráficos ----- */

// escreve o valor em cima de cada barra
const valoresNasBarras = {
  id: 'valoresNasBarras',
  afterDatasetsDraw(grafico) {
    const ctx = grafico.ctx;
    ctx.save();
    ctx.font = "600 11px 'Sarabun', sans-serif";
    ctx.fillStyle = corEmpresas;
    ctx.textAlign = 'center';
    ctx.textBaseline = 'bottom';

    grafico.getDatasetMeta(0).data.forEach((barra, i) => {
      const valor = grafico.data.datasets[0].data[i];
      ctx.fillText(formatarReal(valor), barra.x, barra.y - 4);
    });

    ctx.restore();
  }
};

// escreve o nome e o valor no fim de cada linha (ex.: "Usuários 6")
const nomesNoFimDasLinhas = {
  id: 'nomesNoFimDasLinhas',
  afterDatasetsDraw(grafico) {
    const ctx = grafico.ctx;
    ctx.save();
    ctx.font = "600 11px 'Sarabun', sans-serif";
    ctx.textAlign = 'left';
    ctx.textBaseline = 'middle';

    grafico.data.datasets.forEach((serie, i) => {
      const pontos = grafico.getDatasetMeta(i).data;
      const ultimoPonto = pontos[pontos.length - 1];
      const ultimoValor = serie.data[serie.data.length - 1];

      ctx.fillStyle = serie.borderColor;
      ctx.fillText(serie.label + ' ' + ultimoValor, ultimoPonto.x + 8, ultimoPonto.y);
    });

    ctx.restore();
  }
};

/* ----- 5. Gráfico de linhas: crescimento de contas ----- */
function montarGraficoCrescimento() {
  const area = document.getElementById('area-grafico-crescimento');

  try {
    const dados = lerCrescimento(area);

    document.getElementById('ano-grafico').textContent = anoAtual;
    document.getElementById('total-contas').textContent = dados.total;
    document.getElementById('variacao-contas').textContent = '+' + dados.delta3Months + ' nos últimos 3 meses';

    const meses = dados.series.map(item => nomesDosMeses[Number(item.month.slice(5)) - 1]);

    new Chart(document.getElementById('grafico-crescimento'), {
      type: 'line',
      data: {
        labels: meses,
        datasets: [
          {
            label: 'Usuários',
            data: dados.series.map(item => item.users),
            borderColor: corUsuarios,
            backgroundColor: corUsuarios,
            borderWidth: 2,
            pointRadius: 3
          },
          {
            label: 'Empresas',
            data: dados.series.map(item => item.companies),
            borderColor: corEmpresas,
            backgroundColor: corEmpresas,
            borderWidth: 2,
            pointRadius: 3
          }
        ]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        layout: { padding: { right: 70 } },
        plugins: { legend: { display: false } },
        scales: {
          x: { grid: { display: false }, border: { display: false } },
          y: {
            beginAtZero: true,
            ticks: { precision: 0 },
            grid: { color: '#EEF1F6' },
            border: { display: false }
          }
        }
      },
      plugins: [nomesNoFimDasLinhas]
    });
  } catch (erro) {
    mostrarErro(area);
  }
}

/* ----- 6. Gráfico de barras: assinaturas e receita por plano ----- */
function montarGraficoReceita() {
  const area = document.getElementById('area-grafico-receita');

  try {
    const dados = lerReceita(area);

    document.getElementById('total-receita').textContent = formatarReal(dados.totalMonthly);
    document.getElementById('total-assinaturas').textContent = textoAssinaturas(dados.subscriptions);

    // o nome do plano e a quantidade de assinaturas ficam em duas linhas embaixo da barra
    const nomesDosPlanos = dados.plans.map(plano => [plano.name, textoAssinaturas(plano.subscribers)]);

    new Chart(document.getElementById('grafico-receita'), {
      type: 'bar',
      data: {
        labels: nomesDosPlanos,
        datasets: [
          {
            data: dados.plans.map(plano => plano.revenue),
            backgroundColor: dados.plans.map((plano, i) => coresDosPlanos[i % coresDosPlanos.length]),
            borderRadius: 4,
            maxBarThickness: 56
          }
        ]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        layout: { padding: { top: 20 } },
        plugins: {
          legend: { display: false },
          tooltip: { callbacks: { label: item => formatarReal(item.parsed.y) } }
        },
        scales: {
          x: { grid: { display: false }, border: { display: false } },
          y: {
            beginAtZero: true,
            ticks: { callback: valor => 'R$ ' + valor },
            grid: { color: '#EEF1F6' },
            border: { display: false }
          }
        }
      },
      plugins: [valoresNasBarras]
    });
  } catch (erro) {
    mostrarErro(area);
  }
}

/* ----- 7. Inicia tudo quando a página carrega ----- */
document.addEventListener('DOMContentLoaded', () => {
  Chart.defaults.font.family = "'Sarabun', sans-serif";
  Chart.defaults.color = '#565E73';

  montarGraficoCrescimento();
  montarGraficoReceita();
});
