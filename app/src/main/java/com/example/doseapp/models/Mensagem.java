package com.example.doseapp.models;

public class Mensagem {
    private String usuarioEnv, usuarioRec, mensagem, data;

    public Mensagem() {
    }

    public String getUsuarioEnv() {
        return usuarioEnv;
    }

    public void setUsuarioEnv(String usuarioEnv) {
        this.usuarioEnv = usuarioEnv;
    }

    public String getUsuarioRec() {
        return usuarioRec;
    }

    public void setUsuarioRec(String usuarioRec) {
        this.usuarioRec = usuarioRec;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
