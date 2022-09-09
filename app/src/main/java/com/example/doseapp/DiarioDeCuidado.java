package com.example.doseapp;

public class DiarioDeCuidado{
    private String titulo;
    private String turno;
    private Alimentacao alimentacao;
    private Atividade atividade;
    private String cuidadorResponsavel;

    public DiarioDeCuidado() {
    }

    public DiarioDeCuidado(String titulo, String turno, Alimentacao alimentacao, Atividade atividade, String cuidadorResponsavel) {
        this.titulo = titulo;
        this.turno = turno;
        this.alimentacao = alimentacao;
        this.atividade = atividade;
        this.cuidadorResponsavel = cuidadorResponsavel;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getTurno() {
        return turno;
    }

    public void setTurno(String turno) {
        this.turno = turno;
    }

    public Alimentacao getAlimentacao() {
        return alimentacao;
    }

    public void setAlimentacao(Alimentacao alimentacao) {
        this.alimentacao = alimentacao;
    }

    public Atividade getAtividade() {
        return atividade;
    }

    public void setAtividade(Atividade atividade) {
        this.atividade = atividade;
    }

    public String getCuidadorResponsavel() {
        return cuidadorResponsavel;
    }

    public void setCuidadorResponsavel(String cuidadorResponsavel) {
        this.cuidadorResponsavel = cuidadorResponsavel;
    }
}
