package com.example.doseapp.models;

public class Usuario {
    private String email;
    private String senha;
    private String nome;
    private String id;
    private String ultMen;

    private String imagem_perfil;

    public Usuario(String email, String senha, String nome, String imagem_perfil) {
        this.email= email;
        this.senha = senha;
        this.imagem_perfil = imagem_perfil;
        this.nome = nome;
    }

    public Usuario() {
    }

    public String getImagem_perfil() {
        return imagem_perfil;
    }

    public void setImagem_perfil(String imagem_perfil) {
        this.imagem_perfil = imagem_perfil;
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
