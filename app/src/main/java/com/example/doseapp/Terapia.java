package com.example.doseapp;

import java.util.List;

public class Terapia {
    private String nome;
    private String profissional;
    private String endereco;
    private String telefone;
    private String horario;
    private List<String> diasSemana;
    private boolean lembre;

    public Terapia() {
    }

    public Terapia(String nome, String profissional, String endereco, String telefone, String horario, List<String> diasSemana, boolean lembre) {
        this.nome = nome;
        this.profissional = profissional;
        this.endereco = endereco;
        this.telefone = telefone;
        this.horario = horario;
        this.diasSemana = diasSemana;
        this.lembre = lembre;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getProfissional() {
        return profissional;
    }

    public void setProfissional(String profissional) {
        this.profissional = profissional;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public List<String> getDiasSemana() {
        return diasSemana;
    }

    public void setDiasSemana(List<String> diasSemana) {
        this.diasSemana = diasSemana;
    }

    public boolean isLembre() {
        return lembre;
    }

    public void setLembre(boolean lembre) {
        this.lembre = lembre;
    }
}
