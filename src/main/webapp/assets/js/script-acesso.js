/* ==========================================================================
   SCRIPT DAS TELAS DE ACESSO
   Só o olhinho que mostra e esconde a senha (a validação dos campos é feita no servidor)
   ========================================================================== */
document.addEventListener('DOMContentLoaded', () => {
  document.querySelectorAll('.olho').forEach(botao => {
    botao.addEventListener('click', () => {
      // o campo da senha é o input que está na mesma caixa do botão
      const campo = botao.closest('.caixa').querySelector('input');
      campo.type = campo.type === 'password' ? 'text' : 'password';
    });
  });
});
