// mascara de CPF, CNPJ e CEP enquanto a pessoa digita ( o numero vai sendo separado por pontos, barra e traço )
// o servidor tira a pontuação antes de filtrar ou salvar, então com ou sem mascara dá o mesmo resultado

function mascararCpf(valor) {
  return valor
    .replace(/\D/g, "")
    .replace(/(\d{3})(\d)/, "$1.$2")
    .replace(/(\d{3})(\d)/, "$1.$2")
    .replace(/(\d{3})(\d{1,2})$/, "$1-$2");
}

function mascararCnpj(valor) {
  return valor
    .replace(/\D/g, "")
    .replace(/^(\d{2})(\d)/, "$1.$2")
    .replace(/^(\d{2})\.(\d{3})(\d)/, "$1.$2.$3")
    .replace(/\.(\d{3})(\d)/, ".$1/$2")
    .replace(/(\d{4})(\d)/, "$1-$2");
}

function mascararCep(valor) {
  return valor
    .replace(/\D/g, "")
    .replace(/^(\d{5})(\d)/, "$1-$2");
}

// campos que recebem a mascara: [id do campo, funcao da mascara, quantos numeros o documento tem]
var camposMascara = [
  ["f-cpf", mascararCpf, 11],
  ["us-cpf", mascararCpf, 11],
  ["f-cnpj", mascararCnpj, 14],
  ["em-cnpj", mascararCnpj, 14],
  ["fr-cep", mascararCep, 8]
];

camposMascara.forEach(function (c) {
  var campo = document.getElementById(c[0]);
  if (campo) {
    // corta nos numeros que o documento tem, para não passar do tamanho
    var aplicar = function () {
      campo.value = c[1](campo.value.replace(/\D/g, "").slice(0, c[2]));
    };
    campo.addEventListener("input", aplicar);
    // na tela de editar o campo já vem preenchido só com os numeros
    aplicar();
  }
});
