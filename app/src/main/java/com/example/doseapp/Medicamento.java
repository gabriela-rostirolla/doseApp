package com.example.doseapp;

import android.net.wifi.WifiManager;

public class Medicamento {

    private String nome;
    private String recomendacao;
    private String intervalo;
    private String unidade_intervalo;
    private String dose;
    private String unidade_dose;
    private String horaInicial;
    private String dataInicio;
    private String dataFim;
    private boolean lembre;
    private String id;

    public Medicamento() {
    }

    public Medicamento(String nome, String recomendacao, String intervalo, String unidade_intervalo, String dose, String unidade_dose, String horaInicial, String dataInicio, String dataFim, boolean lembre) {
        this.nome = nome;
        this.recomendacao = recomendacao;
        this.intervalo = intervalo;
        this.unidade_intervalo = unidade_intervalo;
        this.dose = dose;
        this.unidade_dose = unidade_dose;
        this.horaInicial = horaInicial;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.lembre = lembre;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getRecomendacao() {
        return recomendacao;
    }

    public void setRecomendacao(String recomendacao) {
        this.recomendacao = recomendacao;
    }

    public String getIntervalo() {
        return intervalo;
    }

    public void setIntervalo(String intervalo) {
        this.intervalo = intervalo;
    }

    public String getUnidade_intervalo() {
        return unidade_intervalo;
    }

    public void setUnidade_intervalo(String unidade_intervalo) {
        this.unidade_intervalo = unidade_intervalo;
    }

    public String getDose() {
        return dose;
    }

    public void setDose(String dose) {
        this.dose = dose;
    }

    public String getUnidade_dose() {
        return unidade_dose;
    }

    public void setUnidade_dose(String unidade_dose) {
        this.unidade_dose = unidade_dose;
    }

    public String getHoraInicial() {
        return horaInicial;
    }

    public void setHoraInicial(String horaInicial) {
        this.horaInicial = horaInicial;
    }

    public String getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(String dataInicio) {
        this.dataInicio = dataInicio;
    }

    public String getDataFim() {
        return dataFim;
    }

    public void setDataFim(String dataFim) {
        this.dataFim = dataFim;
    }

    public boolean isLembre() {
        return lembre;
    }

    public void setLembre(boolean lembre) {
        this.lembre = lembre;
    }
}

