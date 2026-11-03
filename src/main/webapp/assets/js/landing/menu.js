/* ==========================================================================
   MENU DO SITE (três traços)
   Em telas menores a navegação e os botões ficam num painel que abre e fecha
   ========================================================================== */
document.addEventListener('DOMContentLoaded', () => {
  const header = document.querySelector('header.header');
  const botao = document.querySelector('.botao-menu-site');
  const painel = document.getElementById('menu-site');
  if (!header || !botao || !painel) return;

  function abrir(sim) {
    header.classList.toggle('menu-aberto', sim);
    botao.setAttribute('aria-expanded', sim ? 'true' : 'false');
    botao.setAttribute('aria-label', sim ? 'Fechar menu' : 'Abrir menu');
  }

  botao.addEventListener('click', e => {
    e.stopPropagation();
    abrir(!header.classList.contains('menu-aberto'));
  });

  // clicar num link do painel, clicar fora ou apertar Esc fecha
  painel.addEventListener('click', e => {
    if (e.target.closest('a')) abrir(false);
  });
  document.addEventListener('click', e => {
    if (header.classList.contains('menu-aberto') && !header.contains(e.target)) abrir(false);
  });
  document.addEventListener('keydown', e => {
    if (e.key === 'Escape' && header.classList.contains('menu-aberto')) {
      abrir(false);
      botao.focus();
    }
  });

  // voltou para tela grande: o painel deixa de existir
  window.addEventListener('resize', () => {
    if (window.innerWidth > 960) abrir(false);
  });
});
