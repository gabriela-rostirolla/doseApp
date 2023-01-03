package com.example.doseapp.models;

public class Usuario {
    private String email;
    private String senha;
    private String nome;
    private String id;
    private String ultMen;

    public Usuario(String email, String senha, String nome) {
        this.email= email;
        this.senha = senha;
        this.nome = nome;
    }

    public Usuario() {
    }

    public String getUltMen() {
        return ultMen;
    }

    public void setUltMen(String ultMen) {
        this.ultMen = ultMen;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
