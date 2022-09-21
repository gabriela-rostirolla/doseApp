package com.example.doseapp;

public class Consulta {
    private String nome;
    private String profissional;
    private String endereco;
    private String telefone;
    private String data;
    private String horario;
    private boolean lembre;
    private String id;
    public Consulta() {
    }

    public Consulta(String nome, String profissional, String endereco, String telefone, String data, String horario, boolean lembre) {
        this.nome = nome;
        this.profissional = profissional;
        this.endereco = endereco;
        this.telefone = telefone;
        this.data = data;
        this.horario = horario;
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

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public boolean isLembre() {
        return lembre;
    }

    public void setLembre(boolean lembre) {
        this.lembre = lembre;
    }
}
