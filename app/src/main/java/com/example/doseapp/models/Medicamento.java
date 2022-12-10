package com.example.doseapp.models;

import android.net.wifi.WifiManager;

import java.util.List;

public class Medicamento {

    private List<String> proxMedicamentos;
    private String nome;
    private String proxMed;
    private String concentracao;
    private String recomendacao;
    private String dose;
    private String intervalo;
    private String unidade_intervalo;
    private String horaInicial;
    private boolean usoContinuo;
    private String dataInicio;
    private String dataFim;
    private String observacoes;
    private boolean lembre;
    private String id;

    public Medicamento() {
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getConcentracao() {
        return concentracao;
    }

    public void setConcentracao(String concentracao) {
        this.concentracao = concentracao;
    }

    public String getRecomendacao() {
        return recomendacao;
    }

    public void setRecomendacao(String recomendacao) {
        this.recomendacao = recomendacao;
    }

    public List<String> getProxMedicamentos() {
        return proxMedicamentos;
    }

    public void setProxMedicamentos(List<String> proxMedicamentos) {
        this.proxMedicamentos = proxMedicamentos;
    }

    public String getDose() {
        return dose;
    }

    public void setDose(String dose) {
        this.dose = dose;
    }

    public String getProxMed() {
        return proxMed;
    }

    public void setProxMed(String proxMed) {
        this.proxMed = proxMed;
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

    public String getHoraInicial() {
        return horaInicial;
    }

    public void setHoraInicial(String horaInicial) {
        this.horaInicial = horaInicial;
    }

    public boolean isUsoContinuo() {
        return usoContinuo;
    }

    public void setUsoContinuo(boolean usoContinuo) {
        this.usoContinuo = usoContinuo;
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

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public boolean isLembre() {
        return lembre;
    }

    public void setLembre(boolean lembre) {
        this.lembre = lembre;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}