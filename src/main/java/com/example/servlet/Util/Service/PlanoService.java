package com.example.servlet.Util.Service;

import com.example.servlet.Dao.AssinaturaDAO;
import com.example.servlet.Dao.FrigorificosDAO;
import com.example.servlet.Dao.GeladeiraDAO;
import com.example.servlet.Model.Assinatura;
import com.example.servlet.Model.Frigorificos;
import com.example.servlet.Model.Geladeira;
import com.example.servlet.Util.Enum.TipoAssinatura;

import java.util.List;

public class PlanoService {
    // CLASSE DE REGRA DE NEGÓCIO DOS PLANOS ( o que cada plano pode ter )
    // as regras valem para todo mundo, inclusive para o super administrador:
    //   - usuário tem Free, Plus ou Família / empresa só tem Comercial
    //   - quantas geladeiras o usuário pode ter ( Free 1, Plus 3, Família 3 )
    //   - quantas unidades cabem em cada geladeira ( Free 40, Plus 80, Família e Comercial sem limite )
    // todos os métodos devolvem a mensagem de erro, ou null quando está tudo certo

    private final AssinaturaDAO assinaturaDAO = new AssinaturaDAO();
    private final GeladeiraDAO geladeiraDAO = new GeladeiraDAO();
    private final FrigorificosDAO frigorificoDAO = new FrigorificosDAO();


    // ========== PLANO x TIPO DE DONO ==========
    public String validarPlanoDoDono(TipoAssinatura plano, boolean donoEhUsuario){
        if (donoEhUsuario && plano == TipoAssinatura.COMERCIAL){
            return "O plano Comercial é só para empresas. O usuário pode ter Free, Plus ou Família.";
        }
        if (!donoEhUsuario && plano != TipoAssinatura.COMERCIAL){
            return "A empresa só pode ter o plano Comercial.";
        }
        return null;
    }// ========== PLANO x TIPO DE DONO ==========


    // ========== LIMITE DE GELADEIRAS DO USUARIO ==========
    public String validarNovaGeladeira(String cpf){
        Assinatura assinatura = assinaturaDAO.buscarAssinaturaUsuario(cpf);
        if (assinatura == null){
            return "Esse usuário ainda não tem plano. Defina o plano na tela Planos antes de cadastrar geladeiras.";
        }
        int limite = TipoAssinatura.limiteGeladeiras(assinatura.getTipoAssinatura());
        int atual = geladeiraDAO.totalGeladeirasDoUsuario(cpf);
        if (atual >= limite){
            return "O plano " + assinatura.getTipoAssinatura() + " permite no máximo " + limite
                    + (limite == 1 ? " geladeira" : " geladeiras") + " por usuário, e esse usuário já tem " + atual + ".";
        }
        return null;
    }// ========== LIMITE DE GELADEIRAS DO USUARIO ==========


    // ========== CAPACIDADE DA GELADEIRA ==========
    // quantidadeNova = unidades que vão entrar, quantidadeQueSai = unidades do mesmo item que ja estavam ali ( alterar item )
    public String validarCapacidadeGeladeira(int idGeladeira, String cpfDono, int quantidadeNova, int quantidadeQueSai){
        Assinatura assinatura = assinaturaDAO.buscarAssinaturaUsuario(cpfDono);
        if (assinatura == null){
            return "O dono dessa geladeira ainda não tem plano. Defina o plano na tela Planos antes de guardar produtos.";
        }
        return conferirCapacidade(assinatura, ArmazenamentoService.totalProdutosGeladeira(idGeladeira), quantidadeNova, quantidadeQueSai, "geladeira");
    }// ========== CAPACIDADE DA GELADEIRA ==========


    // ========== CAPACIDADE DO FRIGORIFICO ==========
    // empresa sem plano fica sem limite ( o plano Comercial também não tem )
    public String validarCapacidadeFrigorifico(int idFrigorifico, String cnpjDono, int quantidadeNova, int quantidadeQueSai){
        Assinatura assinatura = assinaturaDAO.buscarAssinaturaEmpresa(cnpjDono);
        if (assinatura == null){
            return null;
        }
        return conferirCapacidade(assinatura, ArmazenamentoService.totalProdutosFrigorificos(idFrigorifico), quantidadeNova, quantidadeQueSai, "frigorífico");
    }// ========== CAPACIDADE DO FRIGORIFICO ==========


    private String conferirCapacidade(Assinatura assinatura, int totalAtual, int quantidadeNova, int quantidadeQueSai, String onde){
        int capacidade = TipoAssinatura.quantidadePorPlano(assinatura.getTipoAssinatura());
        // capacidade 0 ou menor = sem limite
        if (capacidade > 0 && totalAtual - quantidadeQueSai + quantidadeNova > capacidade){
            return "Limite do plano " + assinatura.getTipoAssinatura() + ": " + capacidade + " unidades por " + onde
                    + " (já tem " + totalAtual + ").";
        }
        return null;
    }


    // ========== TROCA DE PLANO DO USUARIO ==========
    // o novo plano tem que comportar o que o usuário já tem ( geladeiras e unidades em cada uma )
    public String validarTrocaDePlanoUsuario(String cpf, TipoAssinatura novoPlano){
        int limite = TipoAssinatura.limiteGeladeiras(novoPlano);
        int atual = geladeiraDAO.totalGeladeirasDoUsuario(cpf);
        if (atual > limite){
            return "O plano " + novoPlano + " permite no máximo " + limite + (limite == 1 ? " geladeira" : " geladeiras")
                    + " e esse usuário tem " + atual + ". Apague as geladeiras que passam do limite antes de trocar o plano.";
        }
        int capacidade = TipoAssinatura.quantidadePorPlano(novoPlano);
        if (capacidade > 0){
            for (Geladeira geladeira : geladeirasDoUsuario(cpf)){
                int total = ArmazenamentoService.totalProdutosGeladeira(geladeira.getIdGeladeira());
                if (total > capacidade){
                    return "A geladeira \"" + geladeira.getNome() + "\" tem " + total + " unidades e o plano " + novoPlano
                            + " comporta " + capacidade + " por geladeira. Retire produtos antes de trocar o plano.";
                }
            }
        }
        return null;
    }// ========== TROCA DE PLANO DO USUARIO ==========


    // ========== STATUS ( ocupação ) ==========
    // a ocupação da geladeira depende do plano do dono, então muda quando o plano ou o dono muda
    public void recalcularStatusDaGeladeira(Geladeira geladeira){
        if (geladeira == null) return;
        Assinatura assinatura = assinaturaDAO.buscarAssinaturaUsuario(geladeira.getUsuario().getCpf());
        if (assinatura != null){
            ArmazenamentoService.calcularStatusGeladeira(geladeira, assinatura);
            geladeiraDAO.atualizarStatus(geladeira);
        }
    }

    public void recalcularStatusDoUsuario(String cpf){
        for (Geladeira geladeira : geladeirasDoUsuario(cpf)){
            recalcularStatusDaGeladeira(geladeira);
        }
    }

    public void recalcularStatusDaEmpresa(String cnpj){
        Assinatura assinatura = assinaturaDAO.buscarAssinaturaEmpresa(cnpj);
        if (assinatura == null) return;
        for (Frigorificos frigorifico : frigorificoDAO.buscarTodosFrigorifico()){
            if (frigorifico.getEmpresa().getCnpj().equals(cnpj)){
                ArmazenamentoService.calcularStatusFrigorifico(frigorifico, assinatura);
                frigorificoDAO.atualizarStatus(frigorifico);
            }
        }
    }// ========== STATUS ( ocupação ) ==========


    private List<Geladeira> geladeirasDoUsuario(String cpf){
        List<Geladeira> lista = new java.util.ArrayList<>();
        for (Geladeira geladeira : geladeiraDAO.buscarTodasGeladeira()){
            if (geladeira.getUsuario().getCpf().equals(cpf)){
                lista.add(geladeira);
            }
        }
        return lista;
    }
}
