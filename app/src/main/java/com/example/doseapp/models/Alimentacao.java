package com.example.doseapp.models;

public class Alimentacao {
    private String refeicaoPrincipal;
    private String lanche;
    private String outro;
    private String cuidadorResponsavel;
    private String id;
    private String DiarioId;
    private String observacao;
    private String horario;
    private String dia;
    private String turno;

    public Alimentacao() {
    }

    public String getTurno() {
        return turno;
    }

    public void setTurno(String turno) {
        this.turno = turno;
    }

    public String getDiarioId() {
        return DiarioId;
    }

    public void setDiarioId(String diarioId) {
        DiarioId = diarioId;
    }

    public String getDia() {
        return dia;
    }

    public void setDia(String dia) {
        this.dia = dia;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCuidadorResponsavel() {
        return cuidadorResponsavel;
    }

    public void setCuidadorResponsavel(String cuidadorResponsavel) {
        this.cuidadorResponsavel = cuidadorResponsavel;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
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
