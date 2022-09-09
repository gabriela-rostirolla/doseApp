package com.example.doseapp;

public class Alimentacao {
    private String refeicaoPrincipal;
    private String lanche;
    private String outro;
    private String observacao;

    public Alimentacao() {
    }

    public Alimentacao(String refeicaoPrincipal, String lanche, String outro, String observacao) {
        this.refeicaoPrincipal = refeicaoPrincipal;
        this.lanche = lanche;
        this.outro = outro;
        this.observacao = observacao;
    }

    public String getRefeicaoPrincipal() {
        return refeicaoPrincipal;
    }

    public void setRefeicaoPrincipal(String refeicaoPrincipal) {
        this.refeicaoPrincipal = refeicaoPrincipal;
    }

    public String getLanche() {
        return lanche;
    }

    public void setLanche(String lanche) {
        this.lanche = lanche;
    }

    public String getOutro() {
        return outro;
    }

    public void setOutro(String outro) {
        this.outro = outro;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}
