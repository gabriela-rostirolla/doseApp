package com.example.doseapp.models;

public class Atividade extends DiarioDeCuidado {
    private String sono;
    private String exercicios;
    private String passeio;
    private String cuidadorResp;
    private String saude;
    private String dia;
    private String diarioId;
    private String id;
    private String horario;
    private String outro;
    private String turno;
    private String observacao;

    public Atividade() {
    }

    public String getCuidadorResp() {
        return cuidadorResp;
    }

    public void setCuidadorResp(String cuidadorResp) {
        this.cuidadorResp = cuidadorResp;
    }

    public String getDia() {
        return dia;
    }

    public void setDia(String dia) {
        this.dia = dia;
    }

    public String getDiarioId() {
        return diarioId;
    }

    public void setDiarioId(String diarioId) {
        this.diarioId = diarioId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public String getTurno() {
        return turno;
    }

    public void setTurno(String turno) {
        this.turno = turno;
    }

    public Atividade(String sono, String exercicios, String passeio, String saude, String outro, String observacao) {
        this.sono = sono;
        this.exercicios = exercicios;
        this.passeio = passeio;
        this.saude = saude;
        this.outro = outro;
        this.observacao = observacao;
    }

    public String getSono() {
        return sono;
    }

    public void setSono(String sono) {
        this.sono = sono;
    }

    public String getExercicios() {
        return exercicios;
    }

    public void setExercicios(String exercicios) {
        this.exercicios = exercicios;
    }

    public String getPasseio() {
        return passeio;
    }

    public void setPasseio(String passeio) {
        this.passeio = passeio;
    }

    public String getSaude() {
        return saude;
    }

    public void setSaude(String saude) {
        this.saude = saude;
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
