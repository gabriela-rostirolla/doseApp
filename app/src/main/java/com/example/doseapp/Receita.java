package com.example.doseapp;

import com.google.firebase.firestore.Blob;

public class Receita {
    private String nome;
    private String data;
    private Blob foto;
    private String hospital;
    private String telefone;
    private String profissional;
    private String dataRenovar;
    private String id;
    private boolean lembre;

    public Receita() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Receita(String nome, String data, Blob foto, String hospital, String telefone, String profissional, String dataRenovar, boolean lembre) {
        this.nome = nome;
        this.data = data;
        this.foto = foto;
        this.hospital = hospital;
        this.telefone = telefone;
        this.profissional = profissional;
        this.dataRenovar = dataRenovar;
        this.lembre = lembre;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Blob getFoto() {
        return foto;
    }

    public void setFoto(Blob foto) {
        this.foto = foto;
    }

    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getProfissional() {
        return profissional;
    }

    public void setProfissional(String profissional) {
        this.profissional = profissional;
    }

    public String getDataRenovar() {
        return dataRenovar;
    }

    public void setDataRenovar(String dataRenovar) {
        this.dataRenovar = dataRenovar;
    }

    public boolean isLembre() {
        return lembre;
    }

    public void setLembre(boolean lembre) {
        this.lembre = lembre;
    }
}
