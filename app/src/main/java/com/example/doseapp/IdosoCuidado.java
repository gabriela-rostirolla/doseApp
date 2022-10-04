package com.example.doseapp;

import java.util.Date;

public class IdosoCuidado {
    private String id;
    private String nome;
    private String endereco;
    private String nascimento;
    private String telParaContato;
    private String genero;
    private String id_usuario;
    private String obs;
    private boolean cuidado;
    public IdosoCuidado() {
    }

    public IdosoCuidado(String nome, String endereco, String nascimento, String telParaContato, String genero, String id_usuario, String obs, String id) {
        this.nome = nome;
        this.endereco=endereco;
        this.telParaContato=telParaContato;
        this.nascimento=nascimento;
        this.obs = obs;
        this.genero=genero;
        this.id_usuario = id_usuario;
        this.id = id;
    }

    public boolean isCuidado() {
        return cuidado;
    }

    public void setCuidado(boolean cuidado) {
        this.cuidado = cuidado;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getObs() {
        return obs;
    }
    public void setObs(String obs) {
        this.obs = obs;
    }

    public String getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(String id_usuario) {
        this.id_usuario = id_usuario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getNascimento() {
        return nascimento;
    }

    public void setNascimento(String nascimento) {
        this.nascimento = nascimento;
    }

    public String getTelParaContato() {
        return telParaContato;
    }

    public void setTelParaContato(String telParaContato) {
        this.telParaContato = telParaContato;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }
}
