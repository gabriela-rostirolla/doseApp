package com.example.doseapp;

public class Medicamento {
    private String nome;
    private String posologia;
    private String unidade_posologia;
    private String horaInicial;
    private String dose;
    private String unidade_dose;
    private String dataInicio;
    private String dataFim;
    private String finalidade;
    private boolean lembre;

    public Medicamento() {
    }

    public Medicamento(String nome, String posologia, String unidade_posologia, String horaInicial, String dose, String unidade_dose, String dataInicio, String dataFim, String finalidade, boolean lembre) {
        this.nome = nome;
        this.posologia = posologia;
        this.unidade_posologia = unidade_posologia;
        this.horaInicial = horaInicial;
        this.dose = dose;
        this.unidade_dose = unidade_dose;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.finalidade = finalidade;
        this.lembre = lembre;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getPosologia() {
        return posologia;
    }

    public void setPosologia(String posologia) {
        this.posologia = posologia;
    }

    public String getUnidade_posologia() {
        return unidade_posologia;
    }

    public void setUnidade_posologia(String unidade_posologia) {
        this.unidade_posologia = unidade_posologia;
    }

    public String getHoraInicial() {
        return horaInicial;
    }

    public void setHoraInicial(String horaInicial) {
        this.horaInicial = horaInicial;
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

    public String getFinalidade() {
        return finalidade;
    }

    public void setFinalidade(String finalidade) {
        this.finalidade = finalidade;
    }

    public boolean isLembre() {
        return lembre;
    }

    public void setLembre(boolean lembre) {
        this.lembre = lembre;
    }
}

