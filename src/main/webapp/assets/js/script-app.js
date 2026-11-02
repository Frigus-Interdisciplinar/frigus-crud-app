/* ==========================================================================
   1. DROPDOWN
   Troca o select nativo pelo visual do Figma
   ========================================================================== */
document.addEventListener('DOMContentLoaded', () => {
  let dropdownAberto = null; // guarda o dropdown que está aberto agora

  const iconeCheck =
    '<svg viewBox="0 0 24 24" width="14" height="14" fill="none" stroke="currentColor" ' +
    'stroke-width="3" stroke-linecap="round" stroke-linejoin="round"><polyline points="20 6 9 17 4 12"/></svg>';

  function fecharDropdown() {
    if (!dropdownAberto) return;
    dropdownAberto.lista.remove();
    dropdownAberto.caixa.classList.remove('aberto');
    dropdownAberto.botao.setAttribute('aria-expanded', 'false');
    dropdownAberto = null;
  }

  function criarDropdown(select) {
    // esconde o select original
    select.classList.add('select-escondido');
    select.tabIndex = -1;

    // caixa que envolve o select
    const caixa = document.createElement('div');
    caixa.className = 'dropdown';
    select.parentNode.insertBefore(caixa, select);
    caixa.appendChild(select);

    // botão que aparece no lugar do select
    const botao = document.createElement('button');
    botao.type = 'button';
    botao.className = 'dropdown-botao';
    botao.setAttribute('aria-haspopup', 'listbox');
    botao.setAttribute('aria-expanded', 'false');

    const texto = document.createElement('span');
    const seta = document.createElement('span');
    seta.className = 'dropdown-seta';
    botao.append(texto, seta);
    caixa.appendChild(botao);

    function atualizarTexto() {
      texto.textContent = select.options[select.selectedIndex].text;
    }
    atualizarTexto();
    // se o valor mudar por fora (ex.: pop-up de editar preenchendo o select), o texto acompanha
    select.addEventListener('change', atualizarTexto);

    function abrirDropdown() {
      const lista = document.createElement('div');
      lista.className = 'dropdown-lista';
      lista.setAttribute('role', 'listbox');

      Array.from(select.options).forEach((opcao, i) => {
        const item = document.createElement('button');
        item.type = 'button';
        item.className = 'dropdown-item';
        item.setAttribute('role', 'option');
        item.textContent = opcao.text;

        // marca a opção selecionada com o check
        if (i === select.selectedIndex) {
          item.classList.add('selecionado');
          const check = document.createElement('span');
          check.className = 'dropdown-check';
          check.innerHTML = iconeCheck;
          item.appendChild(check);
        }

        item.addEventListener('click', () => {
          select.selectedIndex = i;
          select.dispatchEvent(new Event('change', { bubbles: true }));
          atualizarTexto();
          fecharDropdown();
          botao.focus();
        });

        lista.appendChild(item);
      });

      document.body.appendChild(lista);

      // posiciona embaixo do botão (ou em cima, se não couber)
      const posicao = botao.getBoundingClientRect();
      lista.style.position = 'fixed';
      lista.style.left = posicao.left + 'px';
      lista.style.width = posicao.width + 'px';

      const alturaLista = lista.offsetHeight;
      const espacoEmbaixo = window.innerHeight - posicao.bottom;
      const cabeEmbaixo = espacoEmbaixo >= alturaLista + 8 || espacoEmbaixo >= posicao.top;
      lista.style.top = (cabeEmbaixo ? posicao.bottom + 4 : posicao.top - alturaLista - 4) + 'px';

      caixa.classList.add('aberto');
      botao.setAttribute('aria-expanded', 'true');
      dropdownAberto = { lista, caixa, botao };
    }

    botao.addEventListener('click', e => {
      e.stopPropagation();
      const mesmoBotao = dropdownAberto && dropdownAberto.botao === botao;
      fecharDropdown();
      if (!mesmoBotao) abrirDropdown();
    });
  }

  document.querySelectorAll('.filtro select').forEach(criarDropdown);
  window.criarDropdown = criarDropdown; // usado pelo popup.js nos selects criados depois

  // fecha ao clicar fora, apertar Esc, redimensionar ou rolar a página
  document.addEventListener('click', e => {
    if (dropdownAberto && !dropdownAberto.lista.contains(e.target)) fecharDropdown();
  });
  document.addEventListener('keydown', e => {
    if (e.key === 'Escape') fecharDropdown();
  });
  window.addEventListener('resize', fecharDropdown);
  window.addEventListener('scroll', fecharDropdown, true);
});

/* ==========================================================================
   2. POP-UPS
   Abrir, fechar, alternador (Usuário/Empresa) e ingredientes da receita
   ========================================================================== */
document.addEventListener('DOMContentLoaded', () => {
  let popupAberto = null; // guarda o pop-up que está aberto agora
  let botaoOrigem = null; // botão que abriu o pop-up (o foco volta pra ele ao fechar)

  function abrirPopup(id, origem) {
    const popup = document.getElementById(id);
    if (!popup) return;

    fecharPopup();
    popup.classList.add('aberto');
    popupAberto = popup;
    botaoOrigem = origem;

    // foca no primeiro campo editável (ou no "Cancelar" quando não tem campo)
    const foco = popup.querySelector('input:not([readonly]), .botao-secundario');
    if (foco) foco.focus();
  }

  // copia os dados do registro para os campos do pop-up que têm o mesmo name
  function preencherPopup(id, dados) {
    const popup = document.getElementById(id);
    if (!popup) return;
    Object.keys(dados).forEach(nome => {
      popup.querySelectorAll('[name="' + nome + '"]').forEach(campo => {
        if (campo.type === 'radio') {
          campo.checked = campo.value === dados[nome];
        } else {
          campo.value = dados[nome];
        }
        // avisa o dropdown e o alternador que o valor mudou
        campo.dispatchEvent(new Event('change', { bubbles: true }));
      });
    });
  }

  function fecharPopup() {
    if (!popupAberto) return;
    popupAberto.classList.remove('aberto');
    popupAberto = null;
    if (botaoOrigem) botaoOrigem.focus();
  }

  // botões da página: "Adicionar" e os ícones de cada linha da tabela
  document.addEventListener('click', e => {
    const botaoAbrir = e.target.closest('[data-abrir-popup]');
    if (botaoAbrir) {
      abrirPopup(botaoAbrir.dataset.abrirPopup, botaoAbrir);
      return;
    }

    const botaoAdicionar = e.target.closest('.botao-principal');
    if (botaoAdicionar && botaoAdicionar.closest('.topo-pagina')) {
      abrirPopup('popup-adicionar', botaoAdicionar);
      return;
    }

    const botaoLinha = e.target.closest('.botoes-tabela .botao-icone');
    if (botaoLinha) {
      const acao = botaoLinha.getAttribute('aria-label');

      // os dados do registro vêm nos atributos data-* do botão (data-nome="..." preenche o campo name="nome")
      if (acao === 'Visualizar') { preencherPopup('popup-detalhes', botaoLinha.dataset); abrirPopup('popup-detalhes', botaoLinha); }
      if (acao === 'Editar') { preencherPopup('popup-editar', botaoLinha.dataset); abrirPopup('popup-editar', botaoLinha); }
      if (acao === 'Excluir') {
        preencherPopup('popup-excluir', botaoLinha.dataset);
        // mostra o nome do registro da linha no título da confirmação
        const nome = botaoLinha.closest('tr').querySelector('.titulo-cards');
        document.getElementById('nome-excluir').textContent = nome.textContent.trim();
        abrirPopup('popup-excluir', botaoLinha);
      }
      return;
    }

    // receitas: adiciona uma linha de ingrediente a partir do modelo
    const botaoIngrediente = e.target.closest('.botao-adicionar-ingrediente');
    if (botaoIngrediente) {
      const lista = botaoIngrediente.closest('.gaveta-coluna').querySelector('.ingredientes-lista');
      lista.appendChild(document.getElementById('modelo-ingrediente').content.cloneNode(true));
      // a linha nova tem dois selects (produto e unidade)
      if (window.criarDropdown) lista.lastElementChild.querySelectorAll('select').forEach(window.criarDropdown);
      return;
    }

    // receitas: remove a linha de ingrediente
    const botaoRemover = e.target.closest('.botao-remover');
    if (botaoRemover) {
      botaoRemover.closest('.ingrediente-linha').remove();
      return;
    }

    // fechar: botão X, botão Cancelar ou clique no fundo escuro
    if (popupAberto && (e.target.closest('[data-fechar]') || e.target === popupAberto)) {
      fecharPopup();
    }
  });

  // alternador (Usuário | Empresa, Geladeira | Frigorífico):
  // mostra só o campo da opção escolhida e desativa o outro, pra não travar a validação
  document.addEventListener('change', e => {
    if (!e.target.matches('.alternador input')) return;

    e.target.closest('form').querySelectorAll('[data-opcao]').forEach(campo => {
      const ativo = campo.dataset.opcao === e.target.value;
      campo.hidden = !ativo;
      campo.querySelectorAll('select, input').forEach(item => item.disabled = !ativo);
    });
  });

  // Esc fecha o pop-up (se um dropdown estiver aberto, ele fecha primeiro)
  // o "true" faz este código rodar antes do Esc do dropdown.js
  document.addEventListener('keydown', e => {
    if (e.key === 'Escape' && !document.querySelector('.dropdown-lista')) fecharPopup();
  }, true);
});

/* ==========================================================================
   3. FILTROS DA LISTA
   Aplica o filtro sozinho (select muda ou Enter no campo) e o "Limpar filtros" volta à lista inteira
   ========================================================================== */
document.addEventListener('DOMContentLoaded', () => {
  const filtros = document.querySelector('form.filtros');
  if (!filtros) return;

  filtros.addEventListener('change', e => {
    if (e.target.matches('select')) filtros.submit();
  });

  filtros.addEventListener('keydown', e => {
    if (e.key === 'Enter') { e.preventDefault(); filtros.submit(); }
  });

  filtros.querySelector('.botao-limpar').addEventListener('click', () => {
    window.location.href = window.location.pathname;
  });
});

/* ==========================================================================
   4. ETIQUETAS
   Põe em cada etiqueta a classe etiqueta-<texto> (ex.: "FAMILIA" vira etiqueta-familia),
   e o style-app.css escolhe a cor. Depois deixa o texto legível ("OCUPACAO_BAIXA" vira "Ocupação baixa")
   ========================================================================== */
document.addEventListener('DOMContentLoaded', () => {
  // palavras que precisam de acento
  const acentos = { ocupacao: 'ocupação', atencao: 'atenção', familia: 'família', proximo: 'próximo' };

  document.querySelectorAll('.etiqueta-plano').forEach(etiqueta => {
    const original = etiqueta.textContent.trim();
    const nome = original.toLowerCase()
      .normalize('NFD').replace(/[\u0300-\u036f]/g, '') // tira os acentos
      .replace(/[^a-z0-9]+/g, '-');
    if (!nome) return;
    etiqueta.classList.add('etiqueta-' + nome);

    // só reescreve o que veio em MAIÚSCULAS do servidor (os nomes dos enums)
    if (original === original.toUpperCase() && /[A-Z]/.test(original)) {
      const frase = nome.split('-').map(palavra => acentos[palavra] || palavra).join(' ');
      etiqueta.textContent = frase.charAt(0).toUpperCase() + frase.slice(1);
    }
  });
});
